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


class NotificationReceiver (
    firebaseViewModel:FirebaseViewModel,
): BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {

        // Check notification permission for Android 13+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
//                != PackageManager.PERMISSION_GRANTED
//            ) return
//        }


        val today = LocalDate.now().toString()

        val userId = Firebase.auth.currentUser?.uid ?: return

        Firebase.firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val birthday = doc.getString("birthday")

                if (birthday != null && birthday == today) {
                    sendBirthdayNotification(context)
                }
            }

        sendNotification(context)
    }
}
