package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.mysterySetFor
import java.time.LocalDate
import java.util.Calendar

class NotificationReminderManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("rosary_reminder_prefs", Context.MODE_PRIVATE)

    companion object {
        const val CHANNEL_ID = "rosary_daily_reminder"
        const val NOTIFICATION_ID = 1001
        const val TEST_NOTIFICATION_ID = 1002
        const val DEFAULT_HOUR = 20 // 8:00 PM
        const val DEFAULT_MINUTE = 0
    }

    init {
        createNotificationChannel()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "تذكير صلاة الوردية"
            val descriptionText = "تنبيه يومي لموعد تلاوة المسبحة الوردية المقدسة"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun isReminderEnabled(): Boolean = prefs.getBoolean("reminder_enabled", false)

    fun getReminderTime(): Pair<Int, Int> {
        val hour = prefs.getInt("reminder_hour", DEFAULT_HOUR)
        val minute = prefs.getInt("reminder_minute", DEFAULT_MINUTE)
        return hour to minute
    }

    fun scheduleReminder(hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now) || timeInMillis <= now.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

            prefs.edit()
                .putBoolean("reminder_enabled", true)
                .putInt("reminder_hour", hour)
                .putInt("reminder_minute", minute)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        prefs.edit().putBoolean("reminder_enabled", false).apply()
    }

    fun showReminderNotification() {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return
        }

        val todayMystery = mysterySetFor(LocalDate.now())
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "وقت صلاة الوردية المقدسة"
        val message = "«السلام عليك يا مريم...» - حان موعد صلاة ${todayMystery.titleAr} ونيل بركات الوردية."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun sendTestNotification(): Boolean {
        createNotificationChannel()

        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return false
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("تجربة تذكير صلاة الوردية")
            .setContentText("تم ضبط وتفعيل نظام التنبيهات بنجاح. سنذكرك يومياً في موعد الصلاة.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("تم ضبط وتفعيل نظام التنبيهات بنجاح. سنذكرك يومياً في موعد الصلاة لرفع قلبك وفكرك بالصلاة مع العذراء مريم.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(TEST_NOTIFICATION_ID, notification)
            true
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }

    fun formatTimeAr(hour: Int, minute: Int): String {
        val period = if (hour < 12) "ص" else "م"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minuteStr = String.format("%02d", minute)
        return String.format("%02d:%s %s", displayHour, minuteStr, period)
    }
}
