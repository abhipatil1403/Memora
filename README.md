# Memora

Memora is an Android document-memory app. Capture or choose an image, extract useful information with Groq Vision, keep the result locally, and optionally sync signed-in users' memories, reminders, and device tokens through Firebase.

The Android app calls the hosted FastAPI service at `https://memora-wbna.onrender.com/`. The backend sends images to Groq; API keys never belong in the APK or any Android source file.

## What the project includes

- **Image capture and extraction** — CameraX captures images and the app uploads them as multipart form data to `POST /api/extract`.
- **Entity extraction** — Groq Vision returns structured JSON for documents, receipts, screenshots, medicine labels, handwritten notes, invitations, QR codes, and ordinary photos.
- **Memory management** — Save, search, favorite, archive, view, and delete memories.
- **On-device data** — Room stores memories, versions, reminders, search history, collections, and notifications locally.
- **Cloud support** — Firebase Authentication, Firestore, Cloud Storage, and Firebase Cloud Messaging are integrated for signed-in users.
- **Reminders** — Local WorkManager scheduling works on-device. A Firebase scheduled function can also send due-reminder notifications through FCM.
- **Modern Android UI** — Kotlin, Jetpack Compose, Navigation 3, Hilt, CameraX, Room, Retrofit, and Coil.

## Architecture

```text
Android app (Kotlin / Compose)
  │
  ├── Camera or selected image
  ├── POST https://memora-wbna.onrender.com/api/extract
  │     multipart/form-data field: file
  │
  ▼
FastAPI backend (Render)
  ├── Validates, converts, and resizes image to JPEG
  ├── Sends image + extraction prompt to Groq Vision
  └── Returns JSON entity map

Android local data
  ├── Room database
  ├── internal image storage
  └── WorkManager reminders

Optional Firebase cloud services
  ├── Authentication
  ├── Firestore metadata sync
  ├── Cloud Storage images / profile images
  └── FCM reminder delivery
```

## Repository layout

```text
app/                         Android application entry point and manifest
backend/                     FastAPI + Groq Vision backend
core/
  core-common/               shared utilities, local storage, reminders, constants
  core-database/             Room entities, DAOs, mappers, repository
  core-datastore/            settings persistence
  core-firebase/             Firebase Auth, Firestore, and Storage adapters
  core-model/                domain models
  core-network/              Retrofit API client and DTOs
  core-testing/              shared test support
  core-ui/                   theme and reusable UI
feature/
  feature-auth/              sign-in and sign-up
  feature-capture/           CameraX capture
  feature-processing/        upload and entity extraction workflow
  feature-result/            extraction results
  feature-home/              home dashboard
  feature-library/           memory list
  feature-memory-detail/     memory detail and edits
  feature-collections/       collections
  feature-search/            search
  feature-reminders/         reminder management
  feature-profile/           user profile
  feature-settings/          settings
  feature-splash/            app startup
functions/                   Firebase scheduled reminder function
render.yaml                  Render web-service definition
firestore.rules              Firestore authorization rules
storage.rules                Cloud Storage authorization rules
```

## Technology stack

| Area | Technology |
| --- | --- |
| Android | Kotlin, Jetpack Compose, Material 3, Navigation 3 |
| Dependency injection | Hilt + KSP |
| Camera | CameraX |
| Networking | Retrofit, OkHttp, Kotlinx Serialization |
| Local data | Room, DataStore, WorkManager |
| Images | Coil |
| Cloud | Firebase Auth, Firestore, Storage, FCM, Cloud Functions |
| Backend | Python, FastAPI, Uvicorn, Pillow, OpenAI-compatible Groq client |
| AI extraction | Groq Vision, configured through `GROQ_MODEL` |
| Hosting | Render |

## Requirements

### Android development

- JDK 17
- Android SDK with API 36 installed
- An Android device or emulator running Android 8.0 / API 26 or newer
- Git

Android Studio is optional. The Gradle wrapper can build the APK from a terminal.

### Backend development

- Python 3.10 or newer
- A Groq API key

### Optional Firebase features

- Firebase project with Android app package name `com.memora`
- `app/google-services.json` from that Firebase project
- Firebase CLI and Node.js 20 for deploying `functions/`

## Configure the backend locally

1. Copy the example file:

   ```powershell
   Copy-Item backend/.env.example backend/.env
   ```

2. Edit `backend/.env` and set the following values. Keep the key server-side only.

   ```dotenv
   GROQ_API_KEY_1=
   GROQ_API_KEY_2=your_groq_api_key
   GROQ_MODEL=qwen/qwen3.8-27b
   HOST=0.0.0.0
   PORT=8000
   DEBUG=true
   ```

   `GROQ_API_KEY_2` is the key used by the extraction service. `GROQ_API_KEY_1` is reserved for future use. Change models by changing `GROQ_MODEL`; do not put a Groq key or model value in Android code.

3. Install dependencies and run the API:

   ```powershell
   cd backend
   python -m pip install -r requirements.txt
   python -m uvicorn main:app --reload --host 0.0.0.0 --port 8000
   ```

   Or run `start_backend.bat` from the repository root.

4. Verify the service:

   ```powershell
   Invoke-RestMethod http://127.0.0.1:8000/health
   ```

   A configured backend returns a `200` response with `provider: "groq"`, a non-empty `model`, and `has_api_key: true`.

## Configure Render deployment

The Render service definition is in [`render.yaml`](render.yaml). It uses `backend/` as the root directory, runs Uvicorn, checks `/health`, and deploys on commits.

In the Render dashboard for **memora-backend**, add these environment variables:

| Variable | Required value |
| --- | --- |
| `GROQ_API_KEY_2` | Your Groq API key |
| `GROQ_MODEL` | `qwen/qwen3.8-27b` |

Do not add `backend/.env` to Git and do not expose either variable to the Android app. The `sync: false` declarations in `render.yaml` document the required secrets but do not populate values for an already-existing Render service.

After saving variables, deploy the latest `main` commit. Confirm deployment with:

```text
https://memora-wbna.onrender.com/health
```

If the endpoint returns `503` with an empty `model`, `GROQ_MODEL` is missing in the Render environment. If it returns `has_api_key: false`, `GROQ_API_KEY_2` is missing or empty.

## Android backend URL

The production API base URL is defined in:

[`core/core-common/src/main/kotlin/com/memora/core/common/constant/Constants.kt`](core/core-common/src/main/kotlin/com/memora/core/common/constant/Constants.kt)

```kotlin
const val API_BASE_URL = "https://memora-wbna.onrender.com/"
```

The Retrofit endpoint is `POST api/extract`, so the full production endpoint is:

```text
https://memora-wbna.onrender.com/api/extract
```

For local emulator-only development, temporarily change the base URL to `http://10.0.2.2:8000/`. Do not use that address in a production build or on a physical phone.

## Build and install the Android app

From the repository root:

```powershell
.\gradlew.bat :app:assembleDebug
```

The debug APK is created at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

To install it with Android Debug Bridge:

```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Useful Gradle commands:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :core:core-common:compileDebugKotlin
.\gradlew.bat clean
```

On memory-constrained machines, use:

```powershell
.\gradlew.bat :app:assembleDebug --no-daemon --max-workers=1
```

## Backend API

### `GET /`

Returns the service message, selected model, and whether a local `backend/.env` was found.

### `GET /health`

Returns `200` when both `GROQ_API_KEY_2` and `GROQ_MODEL` are available. Returns `503` when either is missing.

Example successful response:

```json
{
  "status": "healthy",
  "model": "qwen/qwen3.8-27b",
  "provider": "groq",
  "has_api_key": true
}
```

### `POST /api/extract`

Accepts `multipart/form-data` with one required field named `file`. The server validates the image, converts it to RGB JPEG, constrains its dimensions to 1024 × 1024, and sends it to Groq Vision.

Example request:

```bash
curl -X POST "https://memora-wbna.onrender.com/api/extract" \
  -F "file=@receipt.jpg"
```

Example response:

```json
{
  "success": true,
  "filename": "receipt.jpg",
  "entities": {
    "MERCHANT": ["Example Store"],
    "MONEY": ["₹499"],
    "DATE": ["29 September 2026"],
    "IMAGE_TYPE": ["receipt"],
    "DOCUMENT_TYPE": ["retail receipt"],
    "TITLE": ["Example Store receipt"],
    "SUMMARY": ["Retail receipt for ₹499."],
    "ACTION_ITEM": [],
    "REMINDER": [],
    "MEETING": [],
    "EMAIL": [],
    "EVENT": [],
    "LOCATION": [],
    "MEDICINE": [],
    "ORGANIZATION": [],
    "PERSON": [],
    "PHONE": [],
    "PRODUCT": [],
    "QR_CONTENT": [],
    "REFERENCE_NUMBER": [],
    "ACCOUNT_NUMBER": [],
    "SOCIAL_HANDLE": [],
    "TIME": [],
    "URL": []
  }
}
```

The API always normalizes returned entity arrays and removes blank values. Each category is capped at ten values.

### Entity categories

| Category | Purpose |
| --- | --- |
| `IMAGE_TYPE`, `DOCUMENT_TYPE` | Visual and document classification |
| `TITLE`, `SUMMARY` | User-facing context |
| `ACTION_ITEM`, `REMINDER`, `MEETING` | Tasks, timed actions, and meeting data |
| `DATE`, `TIME`, `EVENT` | Temporal information |
| `EMAIL`, `PHONE`, `URL`, `SOCIAL_HANDLE` | Contact and link data |
| `PERSON`, `ORGANIZATION`, `LOCATION` | Identified people, organizations, and places |
| `MERCHANT`, `PRODUCT`, `MONEY`, `MEDICINE` | Commerce and medicine data |
| `QR_CONTENT`, `REFERENCE_NUMBER`, `ACCOUNT_NUMBER` | Explicitly visible codes and identifiers |

### Error handling

| Situation | HTTP status | API behavior |
| --- | --- | --- |
| Empty or invalid image | `400` | FastAPI validation error with an explanatory `detail` |
| Missing Groq server configuration | `503` | `Groq is not configured on the server.` |
| Groq request rejected | `400` | Clean JSON error; upstream details are not exposed |
| Groq authentication failure | `401` | Clean JSON error |
| Groq rate limit | `429` | Clean JSON error asking the user to retry shortly |
| Groq internal error | `500` | Clean JSON error |
| Groq temporary unavailability or connection failure | `503` | Clean JSON error asking the user to try again |

## Firebase setup

Firebase is optional for the local-only experience, but required for sign-in, cloud metadata sync, Cloud Storage, and push notification delivery.

1. Create or select a Firebase project.
2. Register Android package `com.memora`.
3. Download `google-services.json` to `app/google-services.json`.
4. Enable Email/Password authentication if using the built-in auth screen.
5. Create Firestore and Cloud Storage.
6. Deploy the supplied rules and indexes:

   ```bash
   firebase deploy --only firestore:rules,firestore:indexes,storage
   ```

The current rules limit each user's Firestore documents and storage files to that user's Firebase UID. Storage writes accept image content smaller than 10 MB.

### Reminder Cloud Function

The function in `functions/index.js` runs every minute in `asia-south1`. It claims due reminders with a Firestore lease, sends FCM notifications to the signed-in user's registered device tokens, deletes invalid tokens, and marks successfully delivered reminders.

Deploy it from the repository root:

```bash
cd functions
npm install
cd ..
firebase deploy --only functions,firestore:indexes
```

The expected reminder document fields are:

```text
dateTime: epoch milliseconds
isCompleted: false
notifiedAt: null
title: non-empty string
description: optional string
memoryId: optional string
```

## Security and secrets

- Never commit `backend/.env`, Groq keys, Firebase service-account keys, or production credentials.
- Keep `GROQ_API_KEY_2` in the Render environment or local `backend/.env` only.
- `GROQ_MODEL` is server configuration. Updating it requires a backend restart/redeploy, not an Android APK update.
- The APK contains only the public backend URL; it does not receive the Groq key.
- Verify Firebase rules before enabling production data.

## Current implementation notes

- The backend implements `/`, `/health`, and `/api/extract`.
- The Android Retrofit interface also declares `v1/process`, `v1/sync`, and `v1/memories` paths. These are not implemented by the current FastAPI service and should not be treated as live backend endpoints.
- Render must have both `GROQ_API_KEY_2` and `GROQ_MODEL` configured for image extraction to work.
- The Android manifest includes `usesCleartextTraffic="true"` for local HTTP development. Production traffic uses the Render HTTPS URL.

## Troubleshooting

### The app says Groq is not configured

Open `https://memora-wbna.onrender.com/health`.

- Empty `model`: add `GROQ_MODEL=qwen/qwen3.8-27b` in Render.
- `has_api_key: false`: add or correct `GROQ_API_KEY_2` in Render.
- `provider` is not `groq`: deploy the latest backend commit from `main`.

### Render is still serving old backend code

Confirm the Render service is connected to this repository's `main` branch and its Auto-Deploy setting is **On Commit**. Then push the latest commit or use **Manual Deploy → Deploy latest commit** in Render.

### Groq returns 429 or 503

The API returns a retry-friendly JSON response. Wait briefly and retry. Check Render logs for the HTTP status without copying keys into logs or issue reports.

### Android cannot reach local backend

`10.0.2.2` works only from the Android emulator. Physical devices require your computer's LAN address and appropriate firewall/network access. The checked-in configuration uses Render HTTPS instead.

### Gradle build runs out of resources

Use a single worker:

```powershell
.\gradlew.bat :app:assembleDebug --no-daemon --max-workers=1
```

## Contributing workflow

```powershell
git checkout main
git pull origin main
# make and test a focused change
git add <files>
git commit -m "Describe the change"
git push origin main
```

Pushing to the branch connected to Render triggers the configured Render deployment. Check `/health` after a backend deployment before testing extraction in the Android app.

## License

No license file is currently included. Add an explicit license before distributing the source or binaries outside the project team.
