package com.project.avara.viewmodel

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

data class NotesState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val error: String? = null
)

class NotesViewModel : ViewModel() {
    private val notesRepository = NotesRepository(
        FirebaseFirestore.getInstance(),
        FirebaseAuth.getInstance()
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
    
    fun createNote(title: String, content: String) {
        viewModelScope.launch {
            _notesState.value = _notesState.value.copy(isLoading = true, error = null)
            
            val note = Note(
                title = title.ifBlank { "Untitled" },
                content = content
            )
            
            notesRepository.createNote(note)
                .onSuccess { createdNote ->
                    val updatedNotes = listOf(createdNote) + _notesState.value.notes
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        notes = updatedNotes,
                        error = null
                    )
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
                .onSuccess { updatedNote ->
                    val updatedNotes = _notesState.value.notes.map { 
                        if (it.id == updatedNote.id) updatedNote else it 
                    }
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        notes = updatedNotes,
                        error = null
                    )
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
                    val updatedNotes = _notesState.value.notes.filter { it.id != noteId }
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        notes = updatedNotes,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _notesState.value = _notesState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to delete note"
                    )
                }
        }
    }
    
    fun clearError() {
        _notesState.value = _notesState.value.copy(error = null)
    }
}
