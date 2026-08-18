package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusLockDao {
    @Query("SELECT * FROM daily_plans WHERE date = :date")
    fun getDailyPlan(date: String): Flow<DailyPlanEntity?>

    @Query("SELECT * FROM daily_plans WHERE date = :date")
    suspend fun getDailyPlanSync(date: String): DailyPlanEntity?

    @Query("SELECT * FROM daily_plans ORDER BY date DESC")
    fun getAllDailyPlans(): Flow<List<DailyPlanEntity>>

    @Query("SELECT * FROM daily_plans ORDER BY date DESC")
    suspend fun getAllDailyPlansSync(): List<DailyPlanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyPlan(plan: DailyPlanEntity)

    @Update
    suspend fun updateDailyPlan(plan: DailyPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateFocusSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun getFocusSessionById(id: Long): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY startTimeEpochMillis ASC")
    fun getFocusSessionsForDate(date: String): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY startTimeEpochMillis ASC")
    suspend fun getFocusSessionsForDateSync(date: String): List<FocusSessionEntity>

    @Query("SELECT * FROM focus_sessions ORDER BY startTimeEpochMillis DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY startTimeEpochMillis DESC")
    suspend fun getAllFocusSessionsSync(): List<FocusSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlans(plans: List<DailyPlanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSessions(sessions: List<FocusSessionEntity>)

    @Query("DELETE FROM daily_plans")
    suspend fun deleteAllDailyPlans()

    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllFocusSessions()
}
