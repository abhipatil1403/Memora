package com.memora.core.firebase.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.memora.core.common.result.Result
import com.memora.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth?
) {
    private val _authState = MutableStateFlow<Result<User?>>(Result.Success(null))
    val authState: Flow<Result<User?>> = _authState.asStateFlow()

    init {
        try {
            firebaseAuth?.addAuthStateListener { auth ->
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    val user = mapFirebaseUser(firebaseUser)
                    _authState.value = Result.Success(user)
                } else {
                    _authState.value = Result.Success(null)
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Could not attach AuthStateListener")
        }
    }

    fun getCurrentUser(): User? {
        val auth = firebaseAuth ?: return null
        val firebaseUser = auth.currentUser ?: return null
        return mapFirebaseUser(firebaseUser)
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        val auth = firebaseAuth ?: return Result.Error(Exception("Firebase Auth not initialized. Check google-services.json."))
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(Exception("User payload is null after sign in"))
            Result.Success(mapFirebaseUser(firebaseUser))
        } catch (e: Exception) {
            Timber.e(e, "Error signing in with email")
            Result.Error(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User> {
        val auth = firebaseAuth ?: return Result.Error(Exception("Firebase Auth not initialized. Check google-services.json."))
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(Exception("User payload is null after sign up"))

            if (displayName.isNotBlank()) {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                firebaseUser.updateProfile(profileUpdate).await()
            }

            Result.Success(mapFirebaseUser(firebaseUser))
        } catch (e: Exception) {
            Timber.e(e, "Error signing up with email")
            Result.Error(e)
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential): Result<User> {
        val auth = firebaseAuth ?: return Result.Error(Exception("Firebase Auth not initialized. Check google-services.json."))
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(Exception("User payload is null after credential sign in"))
            Result.Success(mapFirebaseUser(firebaseUser))
        } catch (e: Exception) {
            Timber.e(e, "Error signing in with credential")
            Result.Error(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        val auth = firebaseAuth ?: return Result.Error(Exception("Firebase Auth not initialized. Check google-services.json."))
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error sending password reset email")
            Result.Error(e)
        }
    }

    suspend fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Timber.e(e, "Error signing out")
        }
    }

    private fun mapFirebaseUser(user: com.google.firebase.auth.FirebaseUser): User {
        return User(
            id = user.uid,
            email = user.email ?: "",
            displayName = user.displayName ?: "",
            photoUrl = user.photoUrl?.toString(),
            createdAt = Instant.ofEpochMilli(user.metadata?.creationTimestamp ?: System.currentTimeMillis()),
            updatedAt = Instant.ofEpochMilli(user.metadata?.lastSignInTimestamp ?: System.currentTimeMillis())
        )
    }
}
