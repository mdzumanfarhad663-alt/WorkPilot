package com.example.data.model

import com.example.data.local.DailyPlanEntity
import com.example.data.local.FocusSessionEntity

data class AppBackupData(
    val app: String = "FocusLock",
    val exportedAt: Long = System.currentTimeMillis(),
    val userSettings: UserSettings,
    val dailyPlans: List<DailyPlanEntity>,
    val focusSessions: List<FocusSessionEntity>
)
