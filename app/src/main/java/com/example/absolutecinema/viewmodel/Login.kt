package com.example.absolutecinema.viewmodel
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.absolutecinema.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Login(navController: NavController, auth: FirebaseAuth) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val red = Color(0xFFE50914)
    val darkBlue = Color(0xFF0B131D)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(darkBlue, Color.Black)
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // محتوى صفحة تسجيل الدخول
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = "Welcome Back!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp),
            )

            Text(
                text = "Sign In To Continue",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = red,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.visibility)
                    else
                        painterResource(id = R.drawable.visibility_off)

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, contentDescription = null)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = red,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
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
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Forgot your password?",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        email.isBlank() || password.isBlank() ->
                            Toast.makeText(context, "Missing fields", Toast.LENGTH_SHORT).show()

                        else -> {
                            isLoading = true
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        if (user != null && user.isEmailVerified) {
                                            Toast.makeText(
                                                context,
                                                "Login successful",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = red),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("LOGIN", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don't have an account? Sign up", color = red)
            }


            Spacer(modifier = Modifier.height(16.dp))


            AnimatedVisibility(visible = isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(50.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
