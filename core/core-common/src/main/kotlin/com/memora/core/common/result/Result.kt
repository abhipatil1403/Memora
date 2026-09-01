package com.memora.core.common.result

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(
        val exception: Throwable? = null,
        val message: String = exception?.localizedMessage ?: "Unknown error"
    ) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(exception, message)
        is Result.Loading -> Result.Loading
    }
}
