package com.example.absolutecinema.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.absolutecinema.R
import com.example.absolutecinema.ui.theme.Animation
import com.example.absolutecinema.ui.theme.darkBlue
import com.example.absolutecinema.ui.theme.red
import com.example.absolutecinema.viewmodel.FirebaseViewModel

@Composable
fun Login(
    goToApp: () -> Unit,
    noAccount: () -> Unit,
    viewModel: FirebaseViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { startAnimation = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(darkBlue, Color.Black))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {

            Animation(startAnimation) { animModifier ->
                Text(
                    text = "Welcome Back!",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(20.dp)
                        .then(animModifier)
                )
            }

            Animation(startAnimation, initialOffsetY = 30.dp) { animModifier ->
                Text(
                    text = "Sign In To Continue",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .then(animModifier)
                )
            }

            Animation(startAnimation, initialOffsetY = 40.dp) { animModifier ->
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(animModifier),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Animation(startAnimation, initialOffsetY = 40.dp) { animModifier ->
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) painterResource(R.drawable.visibility)
                        else painterResource(R.drawable.visibility_off)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(animModifier),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Animation(startAnimation, initialOffsetY = 40.dp) { animModifier ->
                TextButton(
                    onClick = { viewModel.forgotPassword(email, context) },
                    modifier = Modifier
                        .align(Alignment.End)
                        .then(animModifier)
                ) {
                    Text(
                        text = "Forgot your password?",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Animation(startAnimation, initialOffsetY = 50.dp) { animModifier ->
                Button(
                    onClick = { viewModel.logIn(email, password, context, goToApp) },
                    colors = ButtonDefaults.buttonColors(containerColor = red),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .then(animModifier)
                ) {
                    Text("LOGIN", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Animation(startAnimation, initialOffsetY = 50.dp) { animModifier ->
                TextButton(
                    onClick = noAccount,
                    modifier = Modifier.then(animModifier)
                ) {
                    Text("Don't have an account? Sign up", color = red)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
