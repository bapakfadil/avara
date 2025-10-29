package com.project.avara.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.avara.model.Note
import com.project.avara.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import java.util.Calendar

data class NotesState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val error: String? = null
)

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val notesRepository = NotesRepository(
        FirebaseFirestore.getInstance(),
        FirebaseAuth.getInstance(),
        application.applicationContext // <-- This line will now work correctly
    )

    private val _notesState = MutableStateFlow(NotesState())
    val notesState: StateFlow<NotesState> = _notesState.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _notesState.value = _notesState.value.copy(isLoading = true, error = null)

            notesRepository.getNotes()
                .onSuccess { notes ->
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        notes = notes,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        notes = emptyList(),
                        error = exception.message ?: "Failed to load notes"
                    )
                }
        }
    }

    fun createNote(title: String, content: String, reminderTime: Calendar?) { // <-- MODIFICATION 1
        viewModelScope.launch {
            _notesState.value = _notesState.value.copy(isLoading = true, error = null)

            // MODIFICATION 2: Convert Calendar to Timestamp if it exists
            val reminderTimestamp = reminderTime?.let {
                Timestamp(it.time)
            }

            // MODIFICATION 3: Pass the reminder to the Note object
            val note = Note(
                title = title.ifBlank { "Untitled" },
                content = content,
                reminderTime = reminderTimestamp
            )

            notesRepository.createNote(note)
                .onSuccess {
                    // On success, just reload the list from the source of truth
                    loadNotes()
                }
                .onFailure { exception ->
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to create note"
                    )
                }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            _notesState.value = _notesState.value.copy(isLoading = true, error = null)
            notesRepository.updateNote(note)
                .onSuccess {
                    // On success, just reload the list
                    loadNotes()
                }
                .onFailure { exception ->
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to update note"
                    )
                }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _notesState.value = _notesState.value.copy(isLoading = true, error = null)
            notesRepository.deleteNote(noteId)
                .onSuccess {
                    // On success, just reload the list
                    loadNotes()
                }
                .onFailure { exception ->
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to delete note"
                    )
                }
        }
    }

    fun logout() {
        // Clear the user-specific data from the state
        _notesState.value = NotesState()
    }

    fun clearError() {
        _notesState.value = _notesState.value.copy(error = null)
    }
}
