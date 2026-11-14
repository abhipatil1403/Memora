# Memora reminder function

Deploy from the repository root after logging into the Firebase CLI:

```sh
cd functions
npm install
cd ..
firebase deploy --only functions,firestore:indexes
```

The function runs every minute in `asia-south1`. It reads reminders stored at
`users/{uid}/reminders/{reminderId}` and requires `dateTime` (epoch milliseconds),
`isCompleted: false`, `notifiedAt: null`, and a title. It sends to the user's private
`deviceTokens` subcollection, removes invalid tokens, and marks a delivered reminder
with `notifiedAt`.
