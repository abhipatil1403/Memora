package com.memora.core.database.di

import com.memora.core.database.repository.MemoryRepository
import com.memora.core.database.repository.MemoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMemoryRepository(
        impl: MemoryRepositoryImpl
    ): MemoryRepository
}
