package com.example.absolutecinema.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.absolutecinema.ui.theme.darkBlue
import com.example.absolutecinema.viewmodel.FirebaseViewModel
import com.google.firebase.annotations.concurrent.Background
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: FirebaseViewModel,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.observeAsState()
    val isLoading by viewModel.isLoading

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfilePicture(it, context)
        }
    }

    // Fetch profile on load
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    // ✅ Material3 DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val formattedDate = dateFormat.format(calendar.time)
                            val prefs= context.getSharedPreferences("user_birthday", Context.MODE_PRIVATE)
                            prefs.edit().putString("birthday",formattedDate).apply()
                            viewModel.updateBirthday(formattedDate, context)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(darkBlue)
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Picture
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (userProfile?.profileImageUrl?.isNotEmpty() == true) {
                        // ✅ Check if it's Base64 or URL
                        val imageUrl = userProfile?.profileImageUrl ?: ""

                        if (imageUrl.startsWith("data:image") || imageUrl.length > 100) {
                            // It's Base64
                                val base64String = if (imageUrl.startsWith("data:image")) {
                                    imageUrl.substring(imageUrl.indexOf(",") + 1)
                                } else {
                                    imageUrl
                                }

                                val imageBytes = android.util.Base64.decode(
                                    base64String,
                                    android.util.Base64.DEFAULT
                                )
                                val bitmap = android.graphics.BitmapFactory.decodeByteArray(
                                    imageBytes,
                                    0,
                                    imageBytes.size
                                )

                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Profile Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    // Fallback icon
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Default Profile",
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    )
                                }
                        } else {
                            // It's a URL (from Firebase Storage if enabled)
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }

                // Camera icon
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Picture",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Cards
            ProfileInfoCard(
                icon = Icons.Default.Person,
                title = "Name",
                value = "${userProfile?.firstName ?: ""} ${userProfile?.secondName ?: ""}".trim(),
                color = Color(0xFF6200EA)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                icon = Icons.Default.Email,
                title = "Email",
                value = userProfile?.email ?: "",
                color = Color(0xFF03DAC5)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                icon = Icons.Default.Cake,
                title = "Birthday",
                value = userProfile?.birthday?.ifEmpty { "Not set" } ?: "Not set",
                color = Color(0xFFFF6B6B),
                onClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Out Button
            Button(
                onClick = { viewModel.signOut(context, onSignOut) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White // children will use this color
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sign Out",
                    modifier = Modifier.size(24.dp)
                    // no explicit tint needed — Icon will use LocalContentColor (white)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sign Out",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                    // no explicit color needed because contentColor is set on the Button
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun ProfileInfoCard(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    cardElevation: Dp = 6.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0E1216),
            contentColor = Color(0xFFFFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color(0xFFFFFFFF).copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFFFFF)
                )
            }
        }
    }
}