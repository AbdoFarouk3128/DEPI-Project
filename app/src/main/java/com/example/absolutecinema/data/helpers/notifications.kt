package com.example.absolutecinema.data.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.absolutecinema.MainActivity
import com.example.absolutecinema.R
import java.time.LocalTime
import java.util.Calendar

fun scheduleDailyNotification(context: Context) {
//    val now =LocalTime.now().hour
//    val later =LocalTime.now().minute+1
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)

        // If time already passed today, schedule for tomorrow
        if (before(Calendar.getInstance())) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        100,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}
fun scheduleNextDay(context: Context) {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        100,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}

@SuppressLint("MissingPermission")//android lint
fun sendNotification(context: Context) {

    val i = Intent(
        context,
        MainActivity::class.java
    )

    val pendingIntent = PendingIntent.getActivity(
        context,
        101,
        i,
        PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(context, "1")
        .setSmallIcon(R.drawable.placeholder)
        .setContentTitle("Daily reminder")
        .setContentText("New day, new story. See today’s featured film.")
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()
    NotificationManagerCompat.from(context).notify(102, builder)
}
fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(

        "1",
        "General",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    channel.description = "you can control your notification from here"

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    manager.createNotificationChannel(channel)

}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun sendBirthdayNotification(context: Context) {

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context, 200, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "1")
        .setSmallIcon(R.drawable.placeholder)
        .setContentTitle("🎉 Happy Birthday!")
        .setContentText(" we wish you a Happy birthday. Make your day special with a good movie")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(201, notification)
}
