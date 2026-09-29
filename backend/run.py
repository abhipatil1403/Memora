import os
from pathlib import Path
from dotenv import load_dotenv
import uvicorn

BASE_DIR = Path(__file__).resolve().parent
ENV_PATH = BASE_DIR / ".env"

load_dotenv(dotenv_path=ENV_PATH, override=True)

HOST = os.getenv("HOST", "0.0.0.0").strip()
PORT = int(os.getenv("PORT", "8000").strip())
DEBUG = os.getenv("DEBUG", "true").lower() in ("true", "1", "yes")

if __name__ == "__main__":
    uvicorn.run("main:app", host=HOST, port=PORT, reload=DEBUG)
