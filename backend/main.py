import os
import json
import base64
import io
from pathlib import Path

from dotenv import load_dotenv
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from PIL import Image
from google import genai
from google.genai import types

# Robust .env file location resolution
BASE_DIR = Path(__file__).resolve().parent
ENV_PATH = BASE_DIR / ".env"

if ENV_PATH.exists():
    load_dotenv(dotenv_path=ENV_PATH)
else:
    load_dotenv()

# Read environment variables
API_KEY = os.getenv("GEMINI_API_KEY", "").strip()
MODEL = os.getenv("GEMINI_MODEL", "gemini-3.6-flash").strip()
DEBUG = os.getenv("DEBUG", "false").lower() in ("true", "1", "yes")

# Validate API key
if not API_KEY or API_KEY == "your_gemini_api_key_here":
    print("⚠️  WARNING: GEMINI_API_KEY is not set or using placeholder in .env")

# Initialize Gemini client
client = None
if API_KEY:
    try:
        client = genai.Client(api_key=API_KEY)
        print(f"✅ Google Gemini AI Client initialized with model: {MODEL}")
    except Exception as e:
        print(f"⚠️ Gemini client init notice: {e}")


app = FastAPI(
    title="Memora AI API",
    description="Gemini Vision AI image entity extraction service for Memora",
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
    "TIME",
    "URL"
]


PROMPT = """
Analyze this image and extract meaningful entities for a document-memory app.

Use exactly these keys:
DATE, EMAIL, EVENT, LOCATION, MEDICINE, MERCHANT, MONEY, ORGANIZATION, PERSON, PHONE, PRODUCT, TIME, URL.

Rules:
1. Extract only meaningful entities, not random OCR fragments.
2. MERCHANT:
   - The business, store, restaurant, or seller associated with the document.
   - If a recognizable brand logo clearly identifies the merchant, use the full brand name.
   - Do NOT use slogans, taglines, logo letters, or logo fragments as MERCHANT.
3. ORGANIZATION:
   - Companies, institutions, schools, government bodies, etc.
   - Do not duplicate the merchant when the organization is simply the same business.
4. PRODUCT:
   - Extract actual named products or clearly identified products.
   - Do not extract slogans, ingredients, generic descriptive phrases, or random words.
5. Ignore slogans, taglines, decorative text, isolated logo letters, and meaningless OCR errors.
6. Do not invent information.
7. Use empty arrays when a category is absent.
8. Maximum 5 values per category.
9. Preserve visible text where appropriate, but normalize obvious brand names when a logo clearly identifies the brand.
"""


def normalize_result(data):
    result = {}
    for key in ENTITY_KEYS:
        values = data.get(key, [])
        if not isinstance(values, list):
            values = [values]
        result[key] = values[:5]
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
    return {
        "status": "healthy",
        "model": MODEL,
        "has_api_key": client is not None
    }


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

        if client is None:
            raise RuntimeError("Gemini client not initialized. Check GEMINI_API_KEY.")

        # Gemini Vision Call with Structured Outputs
        response = client.models.generate_content(
            model=MODEL,
            contents=[
                types.Part.from_bytes(
                    data=buffer.getvalue(),
                    mime_type="image/jpeg"
                ),
                PROMPT
            ],
            config=types.GenerateContentConfig(
                temperature=0.0,
                response_mime_type="application/json",
                response_schema={
                    "type": "OBJECT",
                    "properties": {
                        key: {
                            "type": "ARRAY",
                            "items": {"type": "STRING"}
                        } for key in ENTITY_KEYS
                    },
                    "required": ENTITY_KEYS
                }
            )
        )

        raw_result = response.text
        extracted = json.loads(raw_result)
        extracted = normalize_result(extracted)

        return {
            "success": True,
            "filename": file.filename,
            "entities": extracted
        }

    except HTTPException:
        raise

    except Exception as e:
        import traceback
        error_detail = f"{type(e).__name__}: {str(e)}"
        print(f"Gemini Vision call exception: {error_detail}")
        traceback.print_exc()

        # Return error with details so we can debug
        return {
            "success": False,
            "filename": file.filename if file else "unknown",
            "error": error_detail,
            "entities": { key: [] for key in ENTITY_KEYS }
        }