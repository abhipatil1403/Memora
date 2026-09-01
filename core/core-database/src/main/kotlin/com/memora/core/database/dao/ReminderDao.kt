package com.memora.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.memora.core.database.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND dateTime > :now ORDER BY dateTime ASC")
    fun getUpcomingReminders(now: Long = System.currentTimeMillis()): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 1 OR dateTime <= :now ORDER BY dateTime DESC")
    fun getPastReminders(now: Long = System.currentTimeMillis()): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE memoryId = :memoryId")
    fun getRemindersForMemory(memoryId: String): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isCompleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun completeReminder(id: String, now: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
}
