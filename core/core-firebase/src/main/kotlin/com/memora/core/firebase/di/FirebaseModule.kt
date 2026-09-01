package com.memora.core.firebase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.memora.core.firebase.auth.FirebaseAuthManager
import com.memora.core.firebase.firestore.FirestoreManager
import com.memora.core.firebase.storage.FirebaseStorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth? {
        return try {
            Firebase.auth
        } catch (e: Exception) {
            Timber.w(e, "FirebaseAuth unavailable or not initialized yet")
            null
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore? {
        return try {
            Firebase.firestore
        } catch (e: Exception) {
            Timber.w(e, "FirebaseFirestore unavailable or not initialized yet")
            null
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage? {
        return try {
            Firebase.storage
        } catch (e: Exception) {
            Timber.w(e, "FirebaseStorage unavailable or not initialized yet")
            null
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthManager(firebaseAuth: FirebaseAuth?): FirebaseAuthManager =
        FirebaseAuthManager(firebaseAuth)

    @Provides
    @Singleton
    fun provideFirestoreManager(firestore: FirebaseFirestore?): FirestoreManager =
        FirestoreManager(firestore)

    @Provides
    @Singleton
    fun provideFirebaseStorageManager(storage: FirebaseStorage?): FirebaseStorageManager =
        FirebaseStorageManager(storage)
}
