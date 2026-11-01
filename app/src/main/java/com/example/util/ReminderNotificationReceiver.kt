package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val manager = NotificationReminderManager(context)

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule reminder upon reboot if enabled
            if (manager.isReminderEnabled()) {
                val (hour, minute) = manager.getReminderTime()
                manager.scheduleReminder(hour, minute)
            }
        } else {
            // Alarm triggered: show the daily reminder notification
            if (manager.isReminderEnabled()) {
                manager.showReminderNotification()
                // Schedule next occurrence for tomorrow
                val (hour, minute) = manager.getReminderTime()
                manager.scheduleReminder(hour, minute)
            }
        }
    }
}
