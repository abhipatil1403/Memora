package com.memora

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MemoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        try {
            FirebaseApp.initializeApp(this)
            FirebaseAuth.getInstance().addAuthStateListener { auth ->
                val userId = auth.currentUser?.uid ?: return@addAuthStateListener
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .collection("deviceTokens").document(token).set(
                        mapOf(
                            "token" to token,
                            "platform" to "android",
                            "updatedAt" to System.currentTimeMillis()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Safe FirebaseApp initialization notice")
        }
    }
}
