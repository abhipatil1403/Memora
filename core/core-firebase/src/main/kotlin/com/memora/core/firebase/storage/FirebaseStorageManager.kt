package com.memora.core.firebase.storage

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.memora.core.common.result.Result
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageManager @Inject constructor(
    private val storage: FirebaseStorage?
) {
    suspend fun uploadMemoryImage(
        userId: String,
        memoryId: String,
        imageBytes: ByteArray,
        mimeType: String = "image/jpeg"
    ): Result<String> {
        val st = storage ?: return Result.Error(Exception("Firebase Storage disabled/not initialized"))
        return try {
            val ref = st.reference
                .child("users")
                .child(userId)
                .child("memories")
                .child(memoryId)
                .child("original.jpg")

            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            ref.putBytes(imageBytes, metadata).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Timber.e(e, "Error uploading memory image to Firebase Storage")
            Result.Error(e)
        }
    }

    suspend fun uploadProfileAvatar(
        userId: String,
        imageBytes: ByteArray,
        mimeType: String = "image/jpeg"
    ): Result<String> {
        val st = storage ?: return Result.Error(Exception("Firebase Storage disabled/not initialized"))
        return try {
            val ref = st.reference
                .child("users")
                .child(userId)
                .child("profile")
                .child("avatar.jpg")

            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            ref.putBytes(imageBytes, metadata).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Timber.e(e, "Error uploading profile avatar to Firebase Storage")
            Result.Error(e)
        }
    }

    suspend fun deleteMemoryImage(userId: String, memoryId: String): Result<Unit> {
        val st = storage ?: return Result.Success(Unit)
        return try {
            val ref = st.reference
                .child("users")
                .child(userId)
                .child("memories")
                .child(memoryId)
                .child("original.jpg")

            ref.delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting memory image from Firebase Storage")
            Result.Error(e)
        }
    }
}
