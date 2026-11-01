package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.MysterySet
import com.example.domain.buildSequence
import com.example.domain.mysterySetFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("المسبحة الوردية", appName)
  }

  @Test
  fun `verify mystery day mapping`() {
    // 2026-11-01 was Sunday -> GLORIOUS
    val sunday = LocalDate.of(2026, 11, 1)
    assertEquals(MysterySet.GLORIOUS, mysterySetFor(sunday))

    // Monday -> JOYFUL
    val monday = LocalDate.of(2026, 11, 2)
    assertEquals(MysterySet.JOYFUL, mysterySetFor(monday))

    // Tuesday -> SORROWFUL
    val tuesday = LocalDate.of(2026, 11, 3)
    assertEquals(MysterySet.SORROWFUL, mysterySetFor(tuesday))

    // Thursday -> LUMINOUS
    val thursday = LocalDate.of(2026, 11, 5)
    assertEquals(MysterySet.LUMINOUS, mysterySetFor(thursday))
  }

  @Test
  fun `verify full rosary sequence count`() {
    val sequence = buildSequence(MysterySet.JOYFUL, includeChain = true)
    // 1 opening + 5 decades * (1 header + 1 our_father + 10 hail_mary + 1 glory_be = 13) + 1 salve + 1 hearts + 1 chain + 1 closing
    // 1 + 5 * 13 + 4 = 1 + 65 + 4 = 70 steps
    assertEquals(70, sequence.size)
  }

  @Test
  fun `verify today mystery auto-detection and explanations`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.RosaryRepository(context)
    val sunday = LocalDate.of(2026, 11, 1)
    val info = repository.getTodayMysteryInfo(sunday)
    assertEquals("الأحد", info.dayNameAr)
    assertEquals(MysterySet.GLORIOUS, info.mysterySet)
    assertEquals(5, info.mysteries.size)
    assertEquals("النهوض من الخطيئة", info.mysteries[0].fruit)
    assertTrue(info.introText.contains("المجد"))
  }

  @Test
  fun `verify timer formatting`() {
    val durationAr = com.example.ui.formatDurationAr(754L)
    assertTrue(durationAr.contains("12 دقيقة"))
    assertTrue(durationAr.contains("34 ثانية"))

    val stopwatch = com.example.ui.formatStopwatch(754L)
    assertEquals("12:34", stopwatch)
  }

  @Test
  fun `verify prayer history statistics calculation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val historyManager = com.example.data.PrayerHistoryManager(context)
    historyManager.saveCompletedPrayer(
      mysterySetKey = "glorious",
      mysteryTitle = "أسرار المجد",
      durationSeconds = 600L,
      intention = "من أجل السلام"
    )

    val stats = historyManager.getStatistics()
    assertEquals(7, stats.dayStats.size)
    assertTrue(stats.totalAllTime >= 1)
    assertTrue(stats.totalThisWeek >= 1)
    assertTrue(stats.activeDaysThisWeek >= 1)
    assertEquals("أحد", stats.dayStats[0].dayNameShort)
    assertEquals("الأحد", stats.dayStats[0].dayNameFull)
    assertEquals("سبت", stats.dayStats[6].dayNameShort)
  }

  @Test
  fun `verify hail mary text contains our lord jesus christ`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.RosaryRepository(context)
    val hailMary = repository.getPrayer("hail_mary") ?: ""
    assertTrue(hailMary.contains("سيدنا يسوع المسيح"))
  }

  @Test
  fun `verify daily reminder scheduling and formatting`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val reminderManager = com.example.util.NotificationReminderManager(context)
    reminderManager.scheduleReminder(21, 30)
    assertTrue(reminderManager.isReminderEnabled())
    val (hour, minute) = reminderManager.getReminderTime()
    assertEquals(21, hour)
    assertEquals(30, minute)

    val formatted = reminderManager.formatTimeAr(hour, minute)
    assertTrue(formatted.contains("09:30"))
    assertTrue(formatted.contains("م"))

    reminderManager.cancelReminder()
    org.junit.Assert.assertFalse(reminderManager.isReminderEnabled())
  }
}
