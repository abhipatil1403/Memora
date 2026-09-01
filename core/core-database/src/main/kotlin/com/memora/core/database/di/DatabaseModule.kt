package com.memora.core.database.di

import android.content.Context
import androidx.room.Room
import com.memora.core.database.MemoraDatabase
import com.memora.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MemoraDatabase {
        return Room.databaseBuilder(
            context,
            MemoraDatabase::class.java,
            "memora.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideMemoryDao(db: MemoraDatabase): MemoryDao = db.memoryDao()

    @Provides
    fun provideMemoryVersionDao(db: MemoraDatabase): MemoryVersionDao = db.memoryVersionDao()

    @Provides
    fun provideReminderDao(db: MemoraDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun provideSearchHistoryDao(db: MemoraDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    fun provideCollectionDao(db: MemoraDatabase): CollectionDao = db.collectionDao()

    @Provides
    fun provideNotificationDao(db: MemoraDatabase): NotificationDao = db.notificationDao()
}
