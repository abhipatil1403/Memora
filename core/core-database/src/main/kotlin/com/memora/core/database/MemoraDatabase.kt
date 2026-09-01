package com.memora.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.memora.core.database.converter.MemoraTypeConverters
import com.memora.core.database.dao.*
import com.memora.core.database.entity.*

@Database(
    entities = [
        MemoryEntity::class,
        MemoryVersionEntity::class,
        ReminderEntity::class,
        SearchHistoryEntity::class,
        CollectionCacheEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(MemoraTypeConverters::class)
abstract class MemoraDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun memoryVersionDao(): MemoryVersionDao
    abstract fun reminderDao(): ReminderDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun collectionDao(): CollectionDao
    abstract fun notificationDao(): NotificationDao
}
