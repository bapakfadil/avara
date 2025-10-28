package com.project.avara.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Note(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val userId: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    // Helper function to get formatted date
    fun getFormattedDate(): String {
        // Prefer updatedAt, but fall back to createdAt. If both are null, use current time as a temporary placeholder.
        val date = updatedAt?.toDate() ?: createdAt?.toDate() ?: return "Just now"

        val now = System.currentTimeMillis()
        val diff = now - date.time

        // Use TimeUnit for accurate and readable conversions
        val seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diff)
        val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            days == 1L -> "Yesterday"
            days < 7 -> {
                // E.g., "Tuesday"
                val sdf = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
                sdf.format(date)
            }
            else -> {
                // E.g., "May 22, 2024"
                val sdf = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
                sdf.format(date)
            }
        }
    }
}
