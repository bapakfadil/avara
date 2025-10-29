package com.project.avara

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.project.avara.MainActivity

class ReminderBroadcastReceiver : BroadcastReceiver() {

    // --- ADD A COMPANION OBJECT for constants ---
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "avara_reminder_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Avara Reminders"
        const val EXTRA_NOTE_TITLE = "NOTE_TITLE"
        const val EXTRA_NOTE_CONTENT = "NOTE_CONTENT"
        const val EXTRA_NOTE_ID = "NOTE_ID"
    }
    // ------------------------------------------

    override fun onReceive(context: Context, intent: Intent) {
        val noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE) ?: "Note Reminder"
        val noteContent = intent.getStringExtra(EXTRA_NOTE_CONTENT) ?: "You have a reminder."
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID) ?: ""

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // --- ADD THIS: Create Notification Channel for Android 8+ ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Avara note reminders"
            }
            manager.createNotificationChannel(channel)
        }
        // -----------------------------------------------------------

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_note_id", noteId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId.hashCode(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(noteTitle)
            .setContentText(noteContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // The permission check is done at runtime before scheduling,
        // but the channel and manifest declaration are essential.
        manager.notify(noteId.hashCode(), notification)
    }
}
