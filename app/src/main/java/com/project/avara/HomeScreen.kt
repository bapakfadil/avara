package com.project.avara

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.avara.model.Note
import com.project.avara.ui.theme.AvaraTheme
import com.project.avara.ui.theme.WelcomeSubtext
import com.project.avara.ui.theme.WelcomeText
import com.project.avara.viewmodel.NotesViewModel
import com.project.avara.viewmodel.QuoteViewModel

@Composable
fun HomeScreen(
    onLogoutClick: () -> Unit = {},
    onCreateNoteClick: () -> Unit = {},
    onNoteClick: (Note) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showCloseAppDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val notesViewModel: NotesViewModel = viewModel()
    val notesState by notesViewModel.notesState.collectAsState()

    val quoteViewModel: QuoteViewModel = viewModel()
    val quoteState by quoteViewModel.quoteState

    BackHandler(enabled = true) {
        showCloseAppDialog = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile icon
                    Image(
                        painter = painterResource(id = R.drawable.profile_icon),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color.Black, CircleShape)
                    )

                    // App title
                    Text(
                        text = "Avara",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = WelcomeText
                    )

                    // Menu button
                    IconButton(
                        onClick = { showMenu = !showMenu }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = WelcomeText
                        )
                    }
                }
            }

            // Quote of the Day section
            item {
                Column {
                    Text(
                        text = "Quote of The Day",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WelcomeText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            if (quoteState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else if (quoteState.error != null) {
                                Text(
                                    text = quoteState.error!!,
                                    color = Color.Red
                                )
                            } else {
                                quoteState.quote?.let { quote ->
                                    Text(
                                        text = quote.quote,
                                        fontSize = 14.sp,
                                        color = WelcomeText,
                                        lineHeight = 20.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "- ${quote.author}",
                                        fontSize = 12.sp,
                                        color = WelcomeSubtext,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Notes section
            item {
                Column {
                    Text(
                        text = "Recent Notes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WelcomeText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (notesState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = WelcomeText)
                        }
                    } else if (notesState.notes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No notes yet. Create your first note!",
                                color = WelcomeSubtext,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.height(400.dp)
                        ) {
                            items(notesState.notes) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = { onNoteClick(note) }
                                )
                            }
                        }
                    }

                    // Error message
                    notesState.error?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onCreateNoteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp, end = 24.dp, bottom = 80.dp)
                .border(1.5.dp, Color.Black, CircleShape),
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Color.Black,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Note"
            )
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
                        .padding(top = 60.dp, end = 16.dp)
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

        val activity = LocalActivity.current

        if (showCloseAppDialog) {
            AlertDialog(
                onDismissRequest = { showCloseAppDialog = false },
                title = { Text("Close App", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to close the app?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCloseAppDialog = false
                            activity?.finish() // Closes the app
                        }
                    ) {
                        Text("Yes", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCloseAppDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log out", fontWeight = FontWeight.Bold) },
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

@Composable
fun NoteCard(note: Note, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = note.title.ifBlank { "Untitled" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WelcomeText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = note.content,
                fontSize = 12.sp,
                color = WelcomeSubtext,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = note.getFormattedDate(),
                fontSize = 10.sp,
                color = WelcomeSubtext,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AvaraTheme {
        HomeScreen()
    }
}
