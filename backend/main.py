import os
import json
import base64
import io
from pathlib import Path

from dotenv import load_dotenv
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from PIL import Image
from groq import Groq


# Robust .env file location resolution
BASE_DIR = Path(__file__).resolve().parent
ENV_PATH = BASE_DIR / ".env"

if ENV_PATH.exists():
    load_dotenv(dotenv_path=ENV_PATH)
else:
    load_dotenv()

# Read environment variables
API_KEY = os.getenv("GROQ_API_KEY", "").strip()
MODEL = os.getenv("GROQ_MODEL", "qwen/qwen3.6-27b").strip()
DEBUG = os.getenv("DEBUG", "false").lower() in ("true", "1", "yes")

# Validate API key
if not API_KEY or API_KEY == "your_groq_api_key_here":
    print("⚠️  WARNING: GROQ_API_KEY is not set or using placeholder in .env")

# Initialize Groq client
client = None
if API_KEY:
    try:
        client = Groq(api_key=API_KEY)
        print(f"✅ Groq Vision AI Client initialized with model: {MODEL}")
    except Exception as e:
        print(f"⚠️ Groq client init notice: {e}")


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

Return ONLY valid JSON.

Use exactly these keys:

DATE
EMAIL
EVENT
LOCATION
MEDICINE
MERCHANT
MONEY
ORGANIZATION
PERSON
PHONE
PRODUCT
TIME
URL

Rules:

1. Extract only meaningful entities, not random OCR fragments.

2. MERCHANT:
   - The business, store, restaurant, or seller associated with the document.
   - If a recognizable brand logo clearly identifies the merchant, use the full brand name.
   - Do NOT use slogans, taglines, logo letters, or logo fragments as MERCHANT.
   - Example: "M" logo + "I'm lovin' it" → MERCHANT: ["McDonald's"]

3. ORGANIZATION:
   - Companies, institutions, schools, government bodies, etc.
   - Do not duplicate the merchant when the organization is simply the same business.

4. PRODUCT:
   - Extract actual named products or clearly identified products.
   - Do not extract slogans, ingredients, generic descriptive phrases, or random words.
   - Example: "all-beef patties" is not a PRODUCT.
   - "MAC" can be a PRODUCT if it clearly refers to the advertised product.

5. Ignore:
   - slogans
   - taglines
   - decorative text
   - isolated logo letters
   - meaningless OCR errors
   - generic descriptive phrases

6. Do not invent information.
7. Use [] when a category is absent.
8. Maximum 5 values per category.
9. Return JSON only.
10. Preserve visible text where appropriate, but normalize obvious brand names when a logo clearly identifies the brand.

Format:

{
  "DATE": [],
  "EMAIL": [],
  "EVENT": [],
  "LOCATION": [],
  "MEDICINE": [],
  "MERCHANT": [],
  "MONEY": [],
  "ORGANIZATION": [],
  "PERSON": [],
  "PHONE": [],
  "PRODUCT": [],
  "TIME": [],
  "URL": []
}
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
        "has_api_key": bool(API_KEY and API_KEY != "your_groq_api_key_here")
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

        image_base64 = base64.b64encode(
            buffer.getvalue()
        ).decode("utf-8")

        if client is None:
            raise RuntimeError("Groq client not initialized")

        # Groq Vision call (cannot use json_object because Qwen <think> tags cause Groq validation to fail with HTTP 400)
        # We must rely on extremely strict prompt instructions instead.
        response = client.chat.completions.create(
            model=MODEL,
            messages=[
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": PROMPT + "\n\nCRITICAL: You must output ONLY valid JSON. Begin your response with { and end with }. Do not add any conversational text outside the JSON."
                        },
                        {
                            "type": "image_url",
                            "image_url": {
                                "url": "data:image/jpeg;base64," + image_base64
                            }
                        }
                    ]
                }
            ],
            temperature=0,
            max_completion_tokens=8000
        )

        raw_result = response.choices[0].message.content
        
        # Strip out <think>...</think> blocks if Qwen includes them
        import re
        raw_result = re.sub(r'<think>.*?</think>', '', raw_result, flags=re.DOTALL).strip()
        
        # Clean markdown if present
        if "```json" in raw_result:
            parts = raw_result.split("```json")
            if len(parts) > 1:
                raw_result = parts[1].split("```")[0].strip()
        elif "```" in raw_result:
            parts = raw_result.split("```")
            if len(parts) > 1:
                raw_result = parts[1].strip()
            
        # Fallback to finding the first { and last }
        start_idx = raw_result.find('{')
        end_idx = raw_result.rfind('}')
        if start_idx != -1 and end_idx != -1 and end_idx >= start_idx:
            raw_result = raw_result[start_idx:end_idx+1]
        else:
            raise ValueError(f"No JSON object found. Raw output snippet: {raw_result[:200]}...")

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
        print(f"Groq Vision call exception: {error_detail}")
        traceback.print_exc()

        # Return error with details so we can debug
        return {
            "success": False,
            "filename": file.filename if file else "unknown",
            "error": error_detail,
            "entities": {
                "DATE": [],
                "EMAIL": [],
                "EVENT": [],
                "LOCATION": [],
                "MEDICINE": [],
                "MERCHANT": [],
                "MONEY": [],
                "ORGANIZATION": [],
                "PERSON": [],
                "PHONE": [],
                "PRODUCT": [],
                "TIME": [],
                "URL": []
            }
        }