package com.project.avara

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
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
import com.project.avara.model.Note
import com.project.avara.ui.theme.AvaraTheme
import com.project.avara.ui.theme.WelcomeSubtext
import com.project.avara.ui.theme.WelcomeText
import com.project.avara.viewmodel.NotesViewModel

@Composable
fun NoteDetailScreen(
    note: Note,
    onBackClick: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var noteTitle by remember { mutableStateOf(note.title) }
    var noteContent by remember { mutableStateOf(note.content) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var actionClicked by remember { mutableStateOf(false) }

    val notesViewModel: NotesViewModel = viewModel()
    val notesState by notesViewModel.notesState.collectAsState()

    // Handle system back button
    BackHandler(enabled = true) {
        onBackClick()
    }

    // Handle delete success
    LaunchedEffect(notesState.notes) {
        if (actionClicked && notesState.notes.none { it.id == note.id } && !notesState.isLoading) {
            onDeleteSuccess()
        }
    }

    // Handle update success
    LaunchedEffect(notesState) {
        if (actionClicked && !notesState.isLoading && notesState.error == null) {
            isEditing = false
            actionClicked = false
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

                // Centered Title
                Text(
                    text = "Avara",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WelcomeText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
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

            // Note Detail title
            Text(
                text = if (isEditing) "Edit Note" else "Note Detail",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = WelcomeText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Title Input Field
            OutlinedTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Note Title", color = WelcomeSubtext) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    disabledBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    disabledTextColor = WelcomeText
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
                value = noteContent,
                onValueChange = { noteContent = it },
                enabled = isEditing,
                placeholder = { Text(text = "Note Content", color = WelcomeSubtext) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    disabledBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    disabledTextColor = WelcomeText
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = WelcomeText,
                    lineHeight = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            notesState.error?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Edit/Save button
                Button(
                    onClick = {
                        if (isEditing) {
                            actionClicked = true
                            val updatedNote = note.copy(
                                title = noteTitle,
                                content = noteContent
                            )
                            notesViewModel.updateNote(updatedNote)
                        } else {
                            isEditing = true
                        }
                    },
                    enabled = !notesState.isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) Color.Black else Color(0xFFFFEB3B) // Yellow for Edit
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    if (notesState.isLoading && isEditing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = if (isEditing) "Save" else "Edit",
                            color = if (isEditing) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                // Delete button
                Button(
                    onClick = { showDeleteDialog = true },
                    enabled = !notesState.isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336) // Red for Delete
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Menu dropdown
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
                    colors = CardDefaults.cardColors(containerColor = Color.White)
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

        // Delete confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Note", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            actionClicked = true
                            showDeleteDialog = false
                            notesViewModel.deleteNote(note.id)
                        }
                    ) {
                        Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
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
fun NoteDetailScreenPreview() {
    AvaraTheme {
        NoteDetailScreen(
            note = Note(
                id = "1",
                title = "Sample Note Title",
                content = "This is some sample content for the note detail screen to see how it looks.",
                userId = "user1"
            )
        )
    }
}
