package com.project.avara.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.avara.model.Note
import kotlinx.coroutines.tasks.await

class NotesRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val notesCollection = firestore.collection("notes")
    
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    suspend fun createNote(note: Note): Result<Note> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val noteWithUserId = note.copy(userId = userId)
            val docRef = notesCollection.add(noteWithUserId).await()
            val createdNote = noteWithUserId.copy(id = docRef.id)
            Result.success(createdNote)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNotes(): Result<List<Note>> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val snapshot = notesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val notes = snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Note::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { note ->
                note.createdAt?.toDate()?.time ?: 0L
            }
            
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateNote(note: Note): Result<Note> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            if (note.id.isEmpty()) {
                return Result.failure(Exception("Note ID is required for update"))
            }
            
            val noteWithUserId = note.copy(userId = userId)
            notesCollection.document(note.id).set(noteWithUserId).await()
            Result.success(noteWithUserId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            if (noteId.isEmpty()) {
                return Result.failure(Exception("Note ID is required for deletion"))
            }
            
            notesCollection.document(noteId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNoteById(noteId: String): Result<Note?> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            if (noteId.isEmpty()) {
                return Result.failure(Exception("Note ID is required"))
            }
            
            val document = notesCollection.document(noteId).get().await()
            if (document.exists()) {
                val note = document.toObject(Note::class.java)?.copy(id = document.id)
                if (note?.userId == userId) {
                    Result.success(note)
                } else {
                    Result.failure(Exception("Note not found or access denied"))
                }
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
