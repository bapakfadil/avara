package com.project.avara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.avara.model.Note
import com.project.avara.ui.theme.AvaraTheme
import com.project.avara.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvaraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    
    var currentScreen by remember { mutableStateOf("welcome") }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    
    // Check authentication state on startup
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            currentScreen = "home"
        } else {
            currentScreen = "welcome"
        }
    }
    
    when (currentScreen) {
        "welcome" -> WelcomeScreen(
            onLoginClick = { currentScreen = "login" },
            onRegisterClick = { currentScreen = "register" }
        )
        "login" -> LoginScreen(
            onBackClick = { currentScreen = "welcome" },
            onLoginSuccess = { currentScreen = "home" },
            onRegisterClick = { currentScreen = "register" }
        )
        "register" -> RegisterScreen(
            onBackClick = { currentScreen = "welcome" },
            onRegisterSuccess = { currentScreen = "home" },
            onLoginClick = { currentScreen = "login" }
        )
        "home" -> HomeScreen(
            onLogoutClick = { 
                authViewModel.logout()
                currentScreen = "welcome"
            },
            onCreateNoteClick = { currentScreen = "create_notes" },
            onNoteClick = { note -> 
                selectedNote = note
                currentScreen = "note_detail"
            }
        )
        "create_notes" -> CreateNotesScreen(
            onBackClick = { currentScreen = "home" },
            onSaveSuccess = { currentScreen = "home" },
            onLogoutClick = { 
                authViewModel.logout()
                currentScreen = "welcome"
            }
        )
        "note_detail" -> selectedNote?.let { note ->
            NoteDetailScreen(
                note = note,
                onBackClick = { currentScreen = "home" },
                onDeleteSuccess = { currentScreen = "home" },
                onLogoutClick = { 
                    authViewModel.logout()
                    currentScreen = "welcome"
                }
            )
        } ?: run {
            // Fallback if no note is selected
            HomeScreen(
                onLogoutClick = { 
                    authViewModel.logout()
                    currentScreen = "welcome"
                },
                onCreateNoteClick = { currentScreen = "create_notes" },
                onNoteClick = { note -> 
                    selectedNote = note
                    currentScreen = "note_detail"
                }
            )
        }
    }
}