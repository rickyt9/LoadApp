package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 1
const val DOWNLOAD_ID = "downloadId"

fun NotificationManager.sendNotification(context: Context, messageBody: String, downloadId : Long) {

    val detailIntent = Intent(context, DetailActivity::class.java)
    detailIntent.putExtra(DOWNLOAD_ID, downloadId)

    val detailPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_download_done)
        .setContentTitle("Repository Download")
        .setContentText(messageBody)
//        .setContentIntent(detailPendingIntent)
//        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_download_done,
            "Details",
            detailPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}