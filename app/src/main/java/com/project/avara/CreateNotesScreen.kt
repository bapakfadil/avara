package com.project.avara

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.avara.ui.theme.AvaraTheme
import com.project.avara.ui.theme.WelcomeSubtext
import com.project.avara.ui.theme.WelcomeText
import com.project.avara.viewmodel.NotesViewModel

@Composable
fun CreateNotesScreen(
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    // Handle the system back button press
    BackHandler(enabled = true) {
        onBackClick()
    }

    var noteTitle by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

    // Prompt Log Out
    var showLogoutDialog by remember { mutableStateOf(false) }
    var saveClicked by remember { mutableStateOf(false) }

    // Reminder Function
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<Calendar?>(null) }

    val notesViewModel: NotesViewModel = viewModel()
    val notesState by notesViewModel.notesState.collectAsState()

    // Handle navigation after a successful save
    LaunchedEffect(notesState.isLoading) {
        if (saveClicked && !notesState.isLoading && notesState.error == null) {
            onSaveSuccess()
        }
    }

    // --- ADD THIS DIALOG LOGIC ---
    val context = LocalContext.current

    // --- ADD THIS PERMISSION LAUNCHER ---
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission was granted, now show the date picker
                showDatePicker = true
            } else {
                // The user denied the permission. Show a message.
                Toast.makeText(context, "Notification permission is required to set reminders.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // 1. Date Picker Dialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                reminderTime = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                showDatePicker = false
                showTimePicker = true // Chain the time picker to show next
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Prevent setting reminders for past dates
            datePicker.minDate = System.currentTimeMillis()
            setOnCancelListener { showDatePicker = false } // Handle dismissing the dialog
            show()
        }
    }

    // 2. Time Picker Dialog
    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                // Time has been selected, update our reminderTime state
                reminderTime?.set(Calendar.HOUR_OF_DAY, hourOfDay)
                reminderTime?.set(Calendar.MINUTE, minute)
                reminderTime?.set(Calendar.SECOND, 0) // Ensure seconds are zero
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // Use 12-hour format (true for 24-hour)
        ).apply {
            setOnCancelListener { showTimePicker = false } // Handle dismissing the dialog
            show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color.Black, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Reminder button
                IconButton(
                    onClick = {
                        // Check if the Android version is 13 (TIRAMISU) or higher
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            // Request the notification permission
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            // For older Android versions, permission is granted by default, so just show the picker
                            showDatePicker = true
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Set Reminder",
                        tint = Color.Black
                    )
                }

                // Centered Title
                Text(
                    text = "Avara",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WelcomeText,
                    textAlign = TextAlign.Center
                    // modifier = Modifier.weight(1f)
                )

                // Menu Button
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Create Note Title
            Text(
                text = "Create Note",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = WelcomeText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Title Input Field
            OutlinedTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                placeholder = { Text(text = "Enter note title", color = WelcomeSubtext) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = WelcomeText
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content Input Field
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = {
                    Text(
                        text = "Enter note body",
                        color = WelcomeSubtext,
                        lineHeight = 24.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp), // Takes up remaining vertical space
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = WelcomeText,
                    lineHeight = 24.sp
                )
            )

            // --- ADD THIS TO DISPLAY THE REMINDER ---
            reminderTime?.let {
                val sdf = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault())
                Text(
                    text = "Reminder: ${sdf.format(it.time)}",
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Error message display
            notesState.error?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Save Button
            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        saveClicked = true
                        notesViewModel.createNote(noteTitle, noteText, reminderTime)
                    }
                },
                enabled = !notesState.isLoading && noteText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(50), // Fully rounded corners
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.White,
                    disabledContentColor = WelcomeSubtext
                ),
                border = ButtonDefaults.outlinedButtonBorder // Use default outlined border
            ) {
                if (notesState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Save",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Menu dropdown overlay
        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { showMenu = false }
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 100.dp, end = 24.dp)
                        .width(150.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        TextButton(
                            onClick = {
                                showMenu = false
                                showLogoutDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp), // Add some padding
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout, // The new logout icon
                                    contentDescription = "Logout",
                                    tint = WelcomeText,
                                    modifier = Modifier.size(20.dp) // Adjust size as needed
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                                Text(
                                    text = "Logout",
                                    color = WelcomeText,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            onLogoutClick() // Perform the actual logout
                        }
                    ) {
                        Text("Yes", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun CreateNotesScreenPreview() {
    AvaraTheme {
        CreateNotesScreen()
    }
}
