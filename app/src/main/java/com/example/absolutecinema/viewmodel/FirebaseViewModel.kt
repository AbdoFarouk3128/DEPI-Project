package com.example.absolutecinema.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseViewModel : ViewModel() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var isLoading = mutableStateOf(false)
    private val _firstName = MutableLiveData<String>("")
    val firstName: LiveData<String> = _firstName
    fun signUp(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        confirmPassword: String,
        context: Context,
        goToApp: () -> Unit,
    ) {
        when {
            firstName.isBlank() || secondName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                Toast.makeText(context, "Missing fields", Toast.LENGTH_SHORT).show()

            password.length < 6 ->
                Toast.makeText(context, "Short Password", Toast.LENGTH_SHORT).show()

            password != confirmPassword ->
                Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT)
                    .show()

            else -> {
                isLoading.value = true
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading.value = false
                        if (task.isSuccessful) {
                            val user = auth.currentUser


                            if (user != null) {
                                val userData = hashMapOf(
                                    "firstName" to firstName,
                                    "secondName" to secondName,
                                    "email" to email,
                                    "uid" to user.uid,
                                    "createdAt" to System.currentTimeMillis()
                                )

                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(user.uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Log.d("SignUp", "User data saved to Firestore")

                                        // Send verification email
                                        user.sendEmailVerification()
                                            .addOnCompleteListener { verifyTask ->
                                                if (verifyTask.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Verification email sent",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to send verification email",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                        Toast.makeText(
                                            context,
                                            "Account created successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        goToApp()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("SignUp", "Error saving user data", e)
                                        Toast.makeText(
                                            context,
                                            "Failed to save user data: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                task.exception?.message ?: "Authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
    fun fetchUserFirstName() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    val firstName = document.getString("firstName") ?: ""
                    _firstName.value = firstName  // ✅ Update LiveData
                    Log.d("FirebaseViewModel", "Fetched firstName: $firstName")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseViewModel", "Error fetching firstName", e)
                    _firstName.value = ""
                }
        } else {
            _firstName.value = ""
        }
    }

    fun forgotPassword(
        email: String,
        context: Context,
    ) {
        if (email.isBlank()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT)
                .show()
        } else {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Reset email sent", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            task.exception?.message ?: "Failed to send reset email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    fun logIn(
        email: String,
        password: String,
        context: Context,
        goToApp: () -> Unit,
    ) {
        when {
            email.isBlank() || password.isBlank() ->
                Toast.makeText(context, "Missing fields", Toast.LENGTH_SHORT).show()

            else -> {
                isLoading.value = true
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading.value = false
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null && user.isEmailVerified) {
                                Toast.makeText(
                                    context,
                                    "Login successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                goToApp()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please verify your email first",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                task.exception?.message ?: "Authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}

