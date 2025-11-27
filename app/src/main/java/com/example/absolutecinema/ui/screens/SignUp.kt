package com.example.absolutecinema.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.absolutecinema.R
import com.example.absolutecinema.ui.theme.Animation
import com.example.absolutecinema.ui.theme.darkBlue
import com.example.absolutecinema.ui.theme.red
import com.example.absolutecinema.viewmodel.FirebaseViewModel

@Composable
fun SignUP(
    goToApp: () -> Unit,
    haveAnAccount: () -> Unit,
    viewModel: FirebaseViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var firstname by remember { mutableStateOf("") }
    var secondName by remember { mutableStateOf("") }
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { startAnimation = true }

    if (isLoading) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 4.dp)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(darkBlue, Color.Black))),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Animation(startAnimation) { animModifier ->
                    Text(
                        text = "Create Account",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .then(animModifier)
                    )
                }

                Animation(startAnimation, initialOffsetY = 30.dp) { animModifier ->
                    Text(
                        text = "Join us to explore movies and shows!",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 40.dp)
                            .then(animModifier)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Animation(startAnimation, initialOffsetY = 40.dp) { animModifier ->
                        OutlinedTextField(
                            value = firstname,
                            onValueChange = { firstname = it },
                            singleLine = true,
                            label = { Text("First name", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = red,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).then(animModifier)
                        )
                    }

                    Animation(startAnimation, initialOffsetY = 40.dp) { animModifier ->
                        OutlinedTextField(
                            value = secondName,
                            onValueChange = { secondName = it },
                            singleLine = true,
                            label = { Text("Second name", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = red,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).then(animModifier)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Animation(startAnimation, initialOffsetY = 40.dp) { animModifier ->
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        label = { Text("Email") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = red,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().then(animModifier),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Animation(startAnimation, initialOffsetY = 40.dp) { animModifier ->
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        label = { Text("Password") },
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
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().then(animModifier),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Animation(startAnimation, initialOffsetY = 40.dp) { animModifier ->
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        singleLine = true,
                        label = { Text("Confirm password") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (confirmPasswordVisible) painterResource(R.drawable.visibility)
                            else painterResource(R.drawable.visibility_off)
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(painter = image, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = red,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().then(animModifier),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Animation(startAnimation, initialOffsetY = 50.dp) { animModifier ->
                    Button(
                        onClick = {
                            viewModel.signUp(
                                firstname,
                                secondName,
                                email,
                                password,
                                confirmPassword,
                                context,
                                goToApp
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = red),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .then(animModifier)
                    ) {
                        Text("SIGN UP", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Animation(startAnimation, initialOffsetY = 50.dp) { animModifier ->
                    TextButton(onClick = haveAnAccount, modifier = Modifier.then(animModifier)) {
                        Text("Already have an account? Log in", color = red)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
