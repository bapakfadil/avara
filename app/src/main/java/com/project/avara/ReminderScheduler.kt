package com.project.avara

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.project.avara.model.Note
import java.util.*

object ReminderScheduler {

    fun scheduleReminder(context: Context, note: Note) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Ensure the note has a reminder time and the time is in the future.
        val reminderTime = note.reminderTime?.toDate()?.time
        if (reminderTime == null || reminderTime < System.currentTimeMillis()) {
            return // Don't schedule if no time or if time is in the past.
        }

        // Check for exact alarm permissions on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Optionally, you can guide the user to the settings screen.
                // For now, we just won't schedule.
                // You could show a Toast message here to inform the user.
                return
            }
        }

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            // Pass note data to the receiver
            putExtra(ReminderBroadcastReceiver.EXTRA_NOTE_ID, note.id)
            putExtra(ReminderBroadcastReceiver.EXTRA_NOTE_TITLE, note.title)
            putExtra(ReminderBroadcastReceiver.EXTRA_NOTE_CONTENT, note.content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id.hashCode(), // Use a unique request code for each alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the exact alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, noteId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
