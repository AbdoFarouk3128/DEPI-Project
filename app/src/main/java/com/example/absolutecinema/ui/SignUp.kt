package com.example.absolutecinema.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.absolutecinema.ui.theme.darkBlue
import com.example.absolutecinema.ui.theme.red
import com.example.absolutecinema.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun SignUP(
    goToApp:()->Unit,
    haveAnAccount:()->Unit,
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


    if(isLoading){
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 4.dp,

            )
        }
    }else{


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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Create Account",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Join us to explore movies and shows!",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = firstname,
                    onValueChange = { firstname = it },
                    singleLine = true,
                    label = { Text(text = "First name", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = red,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    modifier = Modifier
                        .weight(1f)
                )

                OutlinedTextField(
                    value = secondName,
                    onValueChange = { secondName = it },
                    singleLine = true,
                    label = { Text(text = "Second name", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = red,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    modifier = Modifier
                        .weight(1f)
                )

            }

            Spacer(modifier = Modifier.height(16.dp))



            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                label = { Text(text = "Email") },
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
                singleLine = true,
                label = { Text(text = "Password") },
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

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                singleLine = true,
                label = { Text(text = "Confirm password") },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        painterResource(id = R.drawable.visibility)
                    else
                        painterResource(id = R.drawable.visibility_off)

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
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.signUp(firstname,secondName,email,password,confirmPassword,context,goToApp)
                },
                colors = ButtonDefaults.buttonColors(containerColor = red),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "SIGN UP", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = haveAnAccount) {
                Text("Already have an account? Log in", color = red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ ProgressBar بنفس تنسيق XML
//            AnimatedVisibility(visible = isLoading) {
//                CircularProgressIndicator(
//                    color = Color.White,
//                    strokeWidth = 4.dp,
//                    modifier = Modifier
//                        .padding(top = 10.dp)
//                        .size(50.dp)
//                        .align(Alignment.CenterHorizontally)
//                )
//            }

        }
    }
        }
}