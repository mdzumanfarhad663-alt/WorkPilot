package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DailyPlanEntity
import com.example.data.local.FocusSessionEntity
import com.example.data.model.AppBackupData
import com.example.data.model.DailyScoreResult
import com.example.data.model.UserSettings
import com.example.util.BackupHelper
import com.example.util.DateUtil
import com.example.util.SpecificityCoach
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context verifies app name FocusLock`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FocusLock", appName)
  }

  @Test
  fun `daily score calculation verifies all 5 points and ratings`() {
    // 5 points: Excellent
    val perfectScore = DailyScoreResult.evaluate(
      plannedThreeTasks = true,
      startedOnTime = true,
      completedTargetSessions = true,
      completedMoneyTask = true,
      plannedTomorrow = true
    )
    assertEquals(5, perfectScore.totalScore)
    assertEquals("Excellent", perfectScore.ratingLabel)
    assertTrue(perfectScore.isSuccessful)

    // 4 points: Successful
    val goodScore = DailyScoreResult.evaluate(
      plannedThreeTasks = true,
      startedOnTime = false,
      completedTargetSessions = true,
      completedMoneyTask = true,
      plannedTomorrow = true
    )
    assertEquals(4, goodScore.totalScore)
    assertEquals("Successful", goodScore.ratingLabel)
    assertTrue(goodScore.isSuccessful)

    // 3 points: Needs improvement
    val okScore = DailyScoreResult.evaluate(
      plannedThreeTasks = true,
      startedOnTime = false,
      completedTargetSessions = true,
      completedMoneyTask = true,
      plannedTomorrow = false
    )
    assertEquals(3, okScore.totalScore)
    assertEquals("Needs improvement", okScore.ratingLabel)
    assertFalse(okScore.isSuccessful)

    // <= 2 points: Failed day
    val lowScore = DailyScoreResult.evaluate(
      plannedThreeTasks = false,
      startedOnTime = false,
      completedTargetSessions = false,
      completedMoneyTask = true,
      plannedTomorrow = false
    )
    assertEquals(1, lowScore.totalScore)
    assertEquals("Failed day", lowScore.ratingLabel)
    assertFalse(lowScore.isSuccessful)
  }

  @Test
  fun `specificity coach catches vague tasks accurately`() {
    assertTrue(SpecificityCoach.isVague("work"))
    assertTrue(SpecificityCoach.isVague("Work on website"))
    assertTrue(SpecificityCoach.isVague("coding"))
    assertTrue(SpecificityCoach.isVague("emails"))
    assertTrue(SpecificityCoach.isVague("fix bugs"))

    assertFalse(SpecificityCoach.isVague("Complete and test the checkout page"))
    assertFalse(SpecificityCoach.isVague("Send five customized client proposals"))
  }

  @Test
  fun `backup serializer and deserializer preserves data roundtrip`() {
    val sampleSettings = UserSettings(
      name = "Freelancer Alex",
      normalWorkStartHour = 9,
      normalWorkStartMinute = 30,
      dailyFocusTargetSessions = 3,
      defaultFocusDurationMinutes = 50,
      isFirstTimeSetupCompleted = true,
      currentStreak = 7
    )
    val samplePlan = DailyPlanEntity(
      date = "2026-08-18",
      moneyTaskTitle = "Build checkout page",
      moneyTaskCompleted = true,
      growthTaskTitle = "Send 5 pitches",
      growthTaskCompleted = true,
      maintenanceTaskTitle = "Reply to emails",
      maintenanceTaskCompleted = true,
      isWorkdayFinished = true,
      calculatedScore = 5
    )
    val sampleSession = FocusSessionEntity(
      id = 1L,
      date = "2026-08-18",
      taskType = "MONEY",
      taskTitle = "Build checkout page",
      plannedDurationMinutes = 50,
      startTimeEpochMillis = 100000L,
      targetEndTimeEpochMillis = 103000000L,
      isCompleted = true
    )

    val backup = AppBackupData(
      userSettings = sampleSettings,
      dailyPlans = listOf(samplePlan),
      focusSessions = listOf(sampleSession)
    )

    val json = BackupHelper.serializeToJson(backup)
    assertTrue(json.contains("Freelancer Alex"))
    assertTrue(json.contains("Build checkout page"))

    val restored = BackupHelper.deserializeFromJson(json)
    assertEquals("Freelancer Alex", restored.userSettings.name)
    assertEquals(9, restored.userSettings.normalWorkStartHour)
    assertEquals(7, restored.userSettings.currentStreak)
    assertEquals(1, restored.dailyPlans.size)
    assertEquals("Build checkout page", restored.dailyPlans[0].moneyTaskTitle)
    assertEquals(1, restored.focusSessions.size)
    assertEquals("MONEY", restored.focusSessions[0].taskType)
  }
}
