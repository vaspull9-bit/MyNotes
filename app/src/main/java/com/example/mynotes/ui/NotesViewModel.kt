package com.example.mynotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.NoteEntity
import com.example.mynotes.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NotesRepository) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes.asStateFlow()

    private val _searchResults = MutableStateFlow<List<NoteEntity>>(emptyList())
    val searchResults: StateFlow<List<NoteEntity>> = _searchResults.asStateFlow()

    init {
        loadAllNotes()
    }

    private fun loadAllNotes() {
        viewModelScope.launch {
            repository.getAllNotes().collect { notesList ->
                _notes.value = notesList
            }
        }
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            repository.searchNotes(query).collect { results ->
                _searchResults.value = results
            }
        }
    }

    suspend fun getNoteById(id: Long): NoteEntity? {
        return repository.getNoteById(id)
    }

    suspend fun saveNote(note: NoteEntity) {
        if (note.id == 0L) {
            repository.insertNote(note)
        } else {
            repository.updateNote(note)
        }
    }

    suspend fun deleteNote(note: NoteEntity) {
        repository.deleteNote(note)
    }
}