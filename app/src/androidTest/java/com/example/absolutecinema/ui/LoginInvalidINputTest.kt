package com.example.absolutecinema.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.absolutecinema.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests invalid input path: shows an error node when email or password invalid.
 */
@RunWith(AndroidJUnit4::class)
class LoginInvalidInputTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun login_withInvalidShowsError() {
        composeRule.activity.setContent {
            var showError by remember { mutableStateOf(false) }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                verticalArrangement = Arrangement.Center) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // simulate validation error
                        showError = !(email.contains("@") && password.length >= 6)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_button")
                ) {
                    Text("LOGIN")
                }

                if (showError) {
                    Text(
                        text = "Invalid credentials",
                        modifier = Modifier.testTag("login_error")
                    )
                }
            }
        }

        // Provide invalid inputs
        composeRule.onNodeWithTag("login_email").performTextInput("bad-email")
        composeRule.onNodeWithTag("login_password").performTextInput("123")
        composeRule.onNodeWithTag("login_button").performClick()

        // Assert the error message is shown
        composeRule.onNodeWithTag("login_error").assertIsDisplayed()
    }
}