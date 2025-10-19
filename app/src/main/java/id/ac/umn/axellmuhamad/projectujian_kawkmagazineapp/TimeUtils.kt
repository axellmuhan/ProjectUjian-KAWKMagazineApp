package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp // Sesuaikan

import com.google.firebase.Timestamp
import java.util.Date
import java.util.concurrent.TimeUnit

fun formatTimeAgo(timestamp: Timestamp): String {
    val now = Date()
    val past = timestamp.toDate()
    val diffInMillis = now.time - past.time

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
    val weeks = days / 7

    return when {
        weeks > 0 -> "${weeks}w"
        days > 0 -> "${days}d"
        hours > 0 -> "${hours}h"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}