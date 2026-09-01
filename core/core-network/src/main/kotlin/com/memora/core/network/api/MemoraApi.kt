package com.memora.core.network.api

import com.memora.core.network.dto.ExtractEntitiesResponseDto
import com.memora.core.network.dto.MemoryDto
import com.memora.core.network.dto.ProcessImageRequest
import com.memora.core.network.dto.SyncMemoriesRequest
import com.memora.core.network.dto.SyncMemoriesResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MemoraApi {
    @Multipart
    @POST("api/extract")
    suspend fun extractEntities(
        @Part file: MultipartBody.Part
    ): Response<ExtractEntitiesResponseDto>

    @POST("v1/process")
    suspend fun processImage(
        @Body request: ProcessImageRequest
    ): Response<MemoryDto>

    @POST("v1/sync")
    suspend fun syncMemories(
        @Body request: SyncMemoriesRequest
    ): Response<SyncMemoriesResponse>

    @GET("v1/memories")
    suspend fun getMemories(
        @Query("since") sinceTimestamp: Long? = null,
        @Query("limit") limit: Int = 100
    ): Response<List<MemoryDto>>

    @GET("v1/memories/{id}")
    suspend fun getMemory(
        @Path("id") id: String
    ): Response<MemoryDto>
}
