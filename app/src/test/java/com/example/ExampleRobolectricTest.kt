package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DailyPlanEntity
import com.example.data.local.FocusSessionEntity
import com.example.data.model.AppBackupData
import com.example.data.model.DailyScoreResult
import com.example.data.model.UserSettings
import com.example.util.BackupHelper
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
  fun `read string from context verifies app name WorkPilot`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WorkPilot", appName)
  }

  @Test
  fun `daily score and reward points calculation verifies all points and negative ratings`() {
    // 3 tasks completed: +30 points, 0 missed
    val perfectScore = DailyScoreResult.evaluate(
      plannedThreeTasks = true,
      startedOnTime = true,
      completedTargetSessions = true,
      completedTasksCount = 3,
      totalTasksCount = 3,
      plannedTomorrow = true
    )
    assertEquals(5, perfectScore.totalScore)
    assertEquals("Excellent", perfectScore.ratingLabel)
    assertTrue(perfectScore.isSuccessful)
    assertEquals(30, perfectScore.todayRewardPointsDelta)

    // 1 task completed, 2 tasks missed: +10 - 20 = -10 points
    val partialScore = DailyScoreResult.evaluate(
      plannedThreeTasks = true,
      startedOnTime = false,
      completedTargetSessions = true,
      completedTasksCount = 1,
      totalTasksCount = 3,
      plannedTomorrow = true
    )
    assertEquals(4, partialScore.totalScore)
    assertEquals("Successful", partialScore.ratingLabel)
    assertTrue(partialScore.isSuccessful)
    assertEquals(-10, partialScore.todayRewardPointsDelta)

    // 0 tasks completed, 3 missed: 0 - 30 = -30 points
    val zeroTaskScore = DailyScoreResult.evaluate(
      plannedThreeTasks = true,
      startedOnTime = false,
      completedTargetSessions = false,
      completedTasksCount = 0,
      totalTasksCount = 3,
      plannedTomorrow = false
    )
    assertEquals(1, zeroTaskScore.totalScore)
    assertEquals("Failed day", zeroTaskScore.ratingLabel)
    assertFalse(zeroTaskScore.isSuccessful)
    assertEquals(-30, zeroTaskScore.todayRewardPointsDelta)
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
  fun `backup serializer and deserializer preserves reward points and custom task durations`() {
    val sampleSettings = UserSettings(
      name = "Freelancer Alex",
      normalWorkStartHour = 9,
      normalWorkStartMinute = 30,
      dailyFocusTargetSessions = 3,
      defaultFocusDurationMinutes = 35,
      isFirstTimeSetupCompleted = true,
      currentStreak = 7,
      totalRewardPoints = 40
    )
    val samplePlan = DailyPlanEntity(
      date = "2026-08-18",
      task1Title = "Build checkout page",
      task1DurationMinutes = 20,
      task1Completed = true,
      task2Title = "Send 5 pitches",
      task2DurationMinutes = 35,
      task2Completed = true,
      task3Title = "Reply to emails",
      task3DurationMinutes = 15,
      task3Completed = false,
      isWorkdayFinished = true,
      calculatedScore = 4,
      rewardPointsEarned = 10
    )
    val sampleSession = FocusSessionEntity(
      id = 1L,
      date = "2026-08-18",
      taskType = "TASK_1",
      taskTitle = "Build checkout page",
      plannedDurationMinutes = 20,
      startTimeEpochMillis = 100000L,
      targetEndTimeEpochMillis = 101200000L,
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
    assertTrue(json.contains("totalRewardPoints"))

    val restored = BackupHelper.deserializeFromJson(json)
    assertEquals("Freelancer Alex", restored.userSettings.name)
    assertEquals(9, restored.userSettings.normalWorkStartHour)
    assertEquals(7, restored.userSettings.currentStreak)
    assertEquals(40, restored.userSettings.totalRewardPoints)
    assertEquals(1, restored.dailyPlans.size)
    assertEquals("Build checkout page", restored.dailyPlans[0].task1Title)
    assertEquals(20, restored.dailyPlans[0].task1DurationMinutes)
    assertEquals(35, restored.dailyPlans[0].task2DurationMinutes)
    assertEquals(15, restored.dailyPlans[0].task3DurationMinutes)
    assertEquals(1, restored.focusSessions.size)
    assertEquals("TASK_1", restored.focusSessions[0].taskType)
  }
}
