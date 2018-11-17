package com.smartlife_solutions.android.navara_store.Notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat

import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.Statics

object NewsNotifications {

    private val NOTIFICATION_TAG = "News"
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private val channelID = "com.smartlife_solutions.android.navara_store.Notifications"
    private val description = "Navara Store"

    fun notify(context: Context, number: Int, title: String, body: String, about: String,intent: Intent, myToken: String) {

        val res = context.resources
        val picture = BitmapFactory.decodeResource(res, R.drawable.navara_logo)

        notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!

        Statics.myToken = myToken
        intent.putExtra(Statics.fromNotification, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            val builder = NotificationCompat.Builder(context, channelID)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_stat_newss)
                    .setContentTitle(title)
                    .setContentText(body).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setLargeIcon(picture)
                    .setTicker(context.getString(R.string.app_name))
                    .setNumber(number)
                    .setContentIntent(
                            PendingIntent.getActivity(context, 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT))

                    .setStyle(NotificationCompat.BigTextStyle().bigText(body)
                            .setBigContentTitle(title)
                            .setSummaryText(about))
                    .setAutoCancel(true)

            notificationManager.notify(number, builder.build())

        } else {
            val builder = NotificationCompat.Builder(context)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_stat_newss)
                    .setContentTitle(title)
                    .setContentText(body).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setLargeIcon(picture)
                    .setTicker(context.getString(R.string.app_name))
                    .setNumber(number)
                    .setContentIntent(
                            PendingIntent.getActivity(context, 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT))

                    .setStyle(NotificationCompat.BigTextStyle().bigText(body)
                            .setBigContentTitle(title)
                            .setSummaryText(about))
                    .setAutoCancel(true)

            notificationManager.notify(number, builder.build())

        }

     }

}
