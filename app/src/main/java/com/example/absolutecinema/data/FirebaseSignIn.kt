package com.example.absolutecinema.data

import com.google.firebase.auth.FirebaseAuth


fun signInAnonymously(onDone: (String) -> Unit) {

    FirebaseAuth.getInstance()
        .signInAnonymously()
        .addOnSuccessListener { result ->
            val userId = result.user?.uid ?: "guest_user"
            println("✅ Signed in as $userId")
            onDone(userId)
        }
        .addOnFailureListener {
            println("❌ Sign-in failed: ${it.message}")
        }
}