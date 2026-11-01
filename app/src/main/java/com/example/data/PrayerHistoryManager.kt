package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class CompletedPrayerRecord(
    val id: String,
    val timestamp: Long,
    val dateFormatted: String,
    val mysterySetKey: String,
    val mysteryTitle: String,
    val durationSeconds: Long,
    val intention: String?
)

data class DayOfWeekStat(
    val dayOfWeek: DayOfWeek,
    val dayNameShort: String,
    val dayNameFull: String,
    val mysteryName: String,
    val countAllTime: Int,
    val countThisWeek: Int,
    val isToday: Boolean
)

data class RosaryStatistics(
    val dayStats: List<DayOfWeekStat>,
    val totalAllTime: Int,
    val totalThisWeek: Int,
    val activeDaysThisWeek: Int,
    val streakDays: Int,
    val bestDayName: String?,
    val totalDurationSeconds: Long,
    val mysteryCounts: Map<String, Int>
)

class PrayerHistoryManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("rosary_history_prefs", Context.MODE_PRIVATE)

    fun saveCompletedPrayer(
        mysterySetKey: String,
        mysteryTitle: String,
        durationSeconds: Long,
        intention: String?
    ) {
        val list = getCompletedPrayers().toMutableList()
        val now = System.currentTimeMillis()
        val localDate = LocalDate.now()
        val dateStr = "${localDate.dayOfMonth}/${localDate.monthValue}/${localDate.year}"

        val record = CompletedPrayerRecord(
            id = now.toString(),
            timestamp = now,
            dateFormatted = dateStr,
            mysterySetKey = mysterySetKey,
            mysteryTitle = mysteryTitle,
            durationSeconds = durationSeconds,
            intention = intention
        )
        list.add(0, record)

        // Keep last 50 records
        val trimmed = if (list.size > 50) list.take(50) else list
        val jsonArr = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("timestamp", item.timestamp)
            obj.put("dateFormatted", item.dateFormatted)
            obj.put("mysterySetKey", item.mysterySetKey)
            obj.put("mysteryTitle", item.mysteryTitle)
            obj.put("durationSeconds", item.durationSeconds)
            obj.put("intention", item.intention ?: "")
            jsonArr.put(obj)
        }
        prefs.edit().putString("completed_list", jsonArr.toString()).apply()
    }

    fun getCompletedPrayers(): List<CompletedPrayerRecord> {
        val str = prefs.getString("completed_list", null) ?: return emptyList()
        return try {
            val jsonArr = JSONArray(str)
            val result = mutableListOf<CompletedPrayerRecord>()
            for (i in 0 until jsonArr.length()) {
                val obj = jsonArr.getJSONObject(i)
                result.add(
                    CompletedPrayerRecord(
                        id = obj.optString("id", ""),
                        timestamp = obj.optLong("timestamp", 0L),
                        dateFormatted = obj.optString("dateFormatted", ""),
                        mysterySetKey = obj.optString("mysterySetKey", ""),
                        mysteryTitle = obj.optString("mysteryTitle", ""),
                        durationSeconds = obj.optLong("durationSeconds", 0L),
                        intention = obj.optString("intention", "").ifEmpty { null }
                    )
                )
            }
            result
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun calculateStreakDays(): Int {
        val prayers = getCompletedPrayers()
        if (prayers.isEmpty()) return 0

        val prayerDates = prayers.map {
            Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        }.distinct().sortedDescending()

        val today = LocalDate.now()
        if (prayerDates.first() != today && prayerDates.first() != today.minusDays(1)) {
            return 0
        }

        var streak = 0
        var expectedDate = prayerDates.first()
        for (date in prayerDates) {
            if (ChronoUnit.DAYS.between(date, expectedDate) <= 1L) {
                streak++
                expectedDate = date
            } else {
                break
            }
        }
        return streak
    }

    fun saveIntention(intention: String?) {
        prefs.edit().putString("prayer_intention", intention ?: "").apply()
    }

    fun getSavedIntention(): String? {
        val str = prefs.getString("prayer_intention", "")
        return if (str.isNullOrBlank()) null else str
    }

    fun getStatistics(): RosaryStatistics {
        val records = getCompletedPrayers()
        val today = LocalDate.now()
        val daysSinceSunday = today.dayOfWeek.value % 7
        val startOfThisWeek = today.minusDays(daysSinceSunday.toLong())
        val endOfThisWeek = startOfThisWeek.plusDays(6)

        val daysOrder = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )

        val dayStats = daysOrder.map { day ->
            val shortName = when (day) {
                DayOfWeek.SUNDAY -> "أحد"
                DayOfWeek.MONDAY -> "اثنين"
                DayOfWeek.TUESDAY -> "ثلاثاء"
                DayOfWeek.WEDNESDAY -> "أربعاء"
                DayOfWeek.THURSDAY -> "خميس"
                DayOfWeek.FRIDAY -> "جمعة"
                DayOfWeek.SATURDAY -> "سبت"
            }
            val fullName = when (day) {
                DayOfWeek.SUNDAY -> "الأحد"
                DayOfWeek.MONDAY -> "الاثنين"
                DayOfWeek.TUESDAY -> "الثلاثاء"
                DayOfWeek.WEDNESDAY -> "الأربعاء"
                DayOfWeek.THURSDAY -> "الخميس"
                DayOfWeek.FRIDAY -> "الجمعة"
                DayOfWeek.SATURDAY -> "السبت"
            }
            val mystery = when (day) {
                DayOfWeek.SUNDAY -> "أسرار المجد"
                DayOfWeek.MONDAY -> "أسرار الفرح"
                DayOfWeek.TUESDAY -> "أسرار الحزن"
                DayOfWeek.WEDNESDAY -> "أسرار المجد"
                DayOfWeek.THURSDAY -> "أسرار النور"
                DayOfWeek.FRIDAY -> "أسرار الحزن"
                DayOfWeek.SATURDAY -> "أسرار الفرح"
            }

            val countAll = records.count { r ->
                val rDate = Instant.ofEpochMilli(r.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                rDate.dayOfWeek == day
            }
            val countWeek = records.count { r ->
                val rDate = Instant.ofEpochMilli(r.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                rDate.dayOfWeek == day && !rDate.isBefore(startOfThisWeek) && !rDate.isAfter(endOfThisWeek)
            }

            DayOfWeekStat(
                dayOfWeek = day,
                dayNameShort = shortName,
                dayNameFull = fullName,
                mysteryName = mystery,
                countAllTime = countAll,
                countThisWeek = countWeek,
                isToday = (day == today.dayOfWeek)
            )
        }

        val totalAllTime = records.size
        val totalThisWeek = records.count { r ->
            val rDate = Instant.ofEpochMilli(r.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            !rDate.isBefore(startOfThisWeek) && !rDate.isAfter(endOfThisWeek)
        }
        val activeDaysThisWeek = dayStats.count { it.countThisWeek > 0 }
        val streak = calculateStreakDays()
        val bestDay = dayStats.maxByOrNull { it.countAllTime }?.takeIf { it.countAllTime > 0 }?.dayNameFull
        val totalDurationSeconds = records.sumOf { it.durationSeconds }

        val mysteryCounts = records.groupingBy { it.mysterySetKey }.eachCount()

        return RosaryStatistics(
            dayStats = dayStats,
            totalAllTime = totalAllTime,
            totalThisWeek = totalThisWeek,
            activeDaysThisWeek = activeDaysThisWeek,
            streakDays = streak,
            bestDayName = bestDay,
            totalDurationSeconds = totalDurationSeconds,
            mysteryCounts = mysteryCounts
        )
    }
}
