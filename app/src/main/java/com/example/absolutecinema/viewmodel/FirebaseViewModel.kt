package com.example.absolutecinema.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import android.util.Base64



class FirebaseViewModel : ViewModel() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var isLoading = mutableStateOf(false)

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile
    private val _firstName = MutableLiveData<String>("")
    val firstName: LiveData<String> = _firstName

    // Add LiveData for auth state
    private val _isLoggedIn = MutableLiveData<Boolean>(auth.currentUser != null)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        // Listen to auth state changes
        auth.addAuthStateListener {
            _isLoggedIn.value = it.currentUser != null
        }
    }
    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Sign out
    fun signOut(context: Context, onSuccess: () -> Unit) {
        auth.signOut()
        _isLoggedIn.value = false //Update LiveData
        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
        onSuccess()
    }

    // Fetch user profile
    fun fetchUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    val profile = UserProfile(
                        firstName = document.getString("firstName") ?: "",
                        secondName = document.getString("secondName") ?: "",
                        email = document.getString("email") ?: currentUser.email ?: "",
                        birthday = document.getString("birthday") ?: "",
                        profileImageUrl = document.getString("profileImageUrl") ?: ""
                    )
                    _userProfile.value = profile
                    _firstName.value = profile.firstName
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseViewModel", "Error fetching profile", e)
                }
        }
    }

    // Update birthday
    fun updateBirthday(birthday: String, context: Context) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .update("birthday", birthday)
                .addOnSuccessListener {
                    Toast.makeText(context, "Birthday updated", Toast.LENGTH_SHORT).show()
                    fetchUserProfile() // Refresh profile
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Upload profile picture - Using Firestore Only (No Storage needed)
    fun uploadProfilePicture(uri: Uri, context: Context) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading.value = true

        try {
            // ✅ Read image from URI
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // ✅ Compress and resize image to reduce size
            val resizedBitmap = resizeBitmap(bitmap, 400, 400) // Resize to 400x400

            // ✅ Convert to Base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // ✅ Save to Firestore
            val userRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)

            val profileData = hashMapOf(
                "profileImageUrl" to base64Image, // Store Base64 string
                "uid" to currentUser.uid,
                "email" to (currentUser.email ?: "")
            )

            userRef.set(profileData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    isLoading.value = false
                    Log.d("ProfileUpload", "Profile picture saved to Firestore")
                    Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                    fetchUserProfile()
                }
                .addOnFailureListener { e ->
                    isLoading.value = false
                    Log.e("ProfileUpload", "Failed to save", e)
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } catch (e: Exception) {
            isLoading.value = false
            Log.e("ProfileUpload", "Error processing image", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Helper function to resize bitmap
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }
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
                                    "birthday" to "", // ✅ Added
                                    "profileImageUrl" to "", // ✅ Added
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
                                        _isLoggedIn.value = true
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
                    _firstName.value = firstName  // Update LiveData
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
                                _isLoggedIn.value = true // Update auth state
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

