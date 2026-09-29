import os
import json
import base64
import io
import logging
from pathlib import Path

from dotenv import load_dotenv
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from PIL import Image
from openai import APIConnectionError, APIStatusError, OpenAI

# Robust .env file location resolution
BASE_DIR = Path(__file__).resolve().parent
ENV_PATH = BASE_DIR / ".env"

load_dotenv(dotenv_path=ENV_PATH, override=True)

# Read environment variables
GROQ_API_KEY = os.getenv("GROQ_API_KEY_2", "").strip()
MODEL = os.getenv("GROQ_MODEL", "").strip()
DEBUG = os.getenv("DEBUG", "false").lower() in ("true", "1", "yes")

logger = logging.getLogger(__name__)

groq_client = None
if not MODEL:
    logger.warning("GROQ_MODEL is not configured")
if GROQ_API_KEY:
    groq_client = OpenAI(api_key=GROQ_API_KEY, base_url="https://api.groq.com/openai/v1")
else:
    logger.warning("GROQ_API_KEY_2 is not configured")


app = FastAPI(
    title="Memora AI API",
    description="Groq Vision AI image entity extraction service for Memora",
    version="1.0.0"
)


app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


ENTITY_KEYS = [
    "IMAGE_TYPE",
    "DOCUMENT_TYPE",
    "TITLE",
    "SUMMARY",
    "ACTION_ITEM",
    "REMINDER",
    "MEETING",
    "DATE",
    "EMAIL",
    "EVENT",
    "LOCATION",
    "MEDICINE",
    "MERCHANT",
    "MONEY",
    "ORGANIZATION",
    "PERSON",
    "PHONE",
    "PRODUCT",
    "QR_CONTENT",
    "REFERENCE_NUMBER",
    "ACCOUNT_NUMBER",
    "SOCIAL_HANDLE",
    "TIME",
    "URL"
]


PROMPT = """
Analyze this image completely for a document-memory app. It may be a receipt, a
poster, timetable, business card, medicine label, screenshot, handwritten note,
whiteboard, QR code, invitation, certificate, calendar, document, or a normal photo.
First understand what the image is; then extract every useful, visible detail that
could help the user search, act on, or remember it.

Use exactly these keys:
IMAGE_TYPE, DOCUMENT_TYPE, TITLE, SUMMARY, ACTION_ITEM, REMINDER, MEETING, DATE,
EMAIL, EVENT, LOCATION, MEDICINE, MERCHANT, MONEY, ORGANIZATION, PERSON, PHONE,
PRODUCT, QR_CONTENT, REFERENCE_NUMBER, ACCOUNT_NUMBER, SOCIAL_HANDLE, TIME, URL.

Rules:
1. IMAGE_TYPE is a short visual classification (for example "receipt", "screenshot",
   "handwritten note", "business card", "event poster", or "photo"). DOCUMENT_TYPE
   is a more specific subtype when applicable. TITLE is a concise user-facing title.
   SUMMARY is one short factual summary. Return a single value for each of these keys.
2. Extract only meaningful entities, not random OCR fragments. Preserve useful visible
   OCR text in ACTION_ITEM or SUMMARY if it does not fit another category.
3. ACTION_ITEM contains actionable tasks or instructions. REMINDER contains time-bound
   tasks in human-readable form. When a meeting link exists, each MEETING value must include
   that complete URL so it can be opened directly; it must also appear in URL.
4. Extract QR_CONTENT when a QR/barcode payload is visibly available or clearly decoded.
   Extract reference/account/order/booking numbers only when they are explicitly shown.
   Never infer sensitive values.
5. MERCHANT:
   - The business, store, restaurant, or seller associated with the document.
   - If a recognizable brand logo clearly identifies the merchant, use the full brand name.
   - Do NOT use slogans, taglines, logo letters, or logo fragments as MERCHANT.
6. ORGANIZATION:
   - Companies, institutions, schools, government bodies, etc.
   - Do not duplicate the merchant when the organization is simply the same business.
7. PRODUCT:
   - Extract actual named products or clearly identified products.
   - Do not extract slogans, ingredients, generic descriptive phrases, or random words.
8. Ignore decorative fragments and meaningless OCR errors. Do not invent information.
9. Use empty arrays when a category is absent. Maximum 10 values per category.
10. URLs must be complete, opening-safe URLs. Recognize Google Meet, Zoom, Teams and
    other meeting links. Dates and times must preserve the visible text; do not assume a year.
"""


def normalize_result(data):
    result = {}
    for key in ENTITY_KEYS:
        values = data.get(key, [])
        if not isinstance(values, list):
            values = [values]
        result[key] = [str(value).strip() for value in values if str(value).strip()][:10]
    return result


@app.get("/")
def root():
    return {
        "message": "Memora AI API is running",
        "model": MODEL,
        "env_loaded": ENV_PATH.exists()
    }


@app.get("/health")
def health():
    ready = groq_client is not None and bool(MODEL)
    return JSONResponse(status_code=200 if ready else 503, content={
        "status": "healthy" if ready else "unhealthy",
        "model": MODEL,
        "provider": "groq",
        "has_api_key": groq_client is not None
    })


@app.post("/api/extract")
async def extract_entities(
    file: UploadFile = File(...)
):
    try:
        # Read uploaded image
        image_bytes = await file.read()

        if not image_bytes:
            raise HTTPException(
                status_code=400,
                detail="Empty image file"
            )

        # Validate image
        try:
            image = Image.open(
                io.BytesIO(image_bytes)
            ).convert("RGB")
            
            # Resize image to prevent massive payloads (max 1024x1024)
            image.thumbnail((1024, 1024), Image.Resampling.LANCZOS)
        except Exception:
            raise HTTPException(
                status_code=400,
                detail="Invalid image file"
            )

        # Convert to JPEG
        buffer = io.BytesIO()
        image.save(
            buffer,
            format="JPEG",
            quality=85
        )

        if groq_client is None or not MODEL:
            raise HTTPException(status_code=503, detail="Groq is not configured on the server.")
        image_data_url = "data:image/jpeg;base64," + base64.b64encode(buffer.getvalue()).decode("ascii")
        response = groq_client.chat.completions.create(
            model=MODEL,
            temperature=0,
            response_format={"type": "json_object"},
            messages=[{"role": "system", "content": "Return valid JSON."}, {"role": "user", "content": [
                {"type": "text", "text": PROMPT},
                {"type": "image_url", "image_url": {"url": image_data_url}}
            ]}]
        )
        raw_result = response.choices[0].message.content
        extracted = json.loads(raw_result)
        extracted = normalize_result(extracted)

        return {
            "success": True,
            "filename": file.filename,
            "entities": extracted
        }

    except HTTPException:
        raise

    except APIStatusError as exc:
        status = exc.status_code
        logger.error("Groq API request failed: HTTP %s (%s)", status, type(exc).__name__)
        messages = {
            400: "Groq could not process this image. Please try another image.",
            401: "Groq authentication failed. Please check the server configuration.",
            429: "Groq rate limit reached. Please try again shortly.",
            500: "Groq service encountered an error. Please try again.",
            503: "Groq service is temporarily unavailable. Please try again.",
        }
        return JSONResponse(status_code=status, content={
            "success": False,
            "filename": file.filename,
            "error": messages.get(status, "Groq request failed. Please try again."),
            "entities": {key: [] for key in ENTITY_KEYS},
        })

    except APIConnectionError as exc:
        logger.error("Groq API connection failed (%s)", type(exc).__name__)
        return JSONResponse(status_code=503, content={
            "success": False,
            "filename": file.filename,
            "error": "Groq service is temporarily unavailable. Please try again.",
            "entities": {key: [] for key in ENTITY_KEYS},
        })

    except Exception as e:
        logger.error("Groq extraction failed (%s)", type(e).__name__)

        return {
            "success": False,
            "filename": file.filename if file else "unknown",
            "error": "Image extraction failed. Please try again.",
            "entities": { key: [] for key in ENTITY_KEYS }
        }
