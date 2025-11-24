package com.example.absolutecinema.data.helpers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.absolutecinema.viewmodel.FirebaseViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.time.LocalDate


class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {

        val today = LocalDate.now().toString()

        val prefs=context.getSharedPreferences("user_birthday",Context.MODE_PRIVATE)
        val birthday = prefs.getString("birthday","")
        if (today == birthday)
        {
            sendBirthdayNotification(context)
        }
        sendNotification(context)
        scheduleNextDay(context)
    }
}
