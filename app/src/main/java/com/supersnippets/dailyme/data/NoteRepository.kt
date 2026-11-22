package com.supersnippets.dailyme.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class NoteRepository(context: Context) {
    private val file = File(context.filesDir, "notes.ser")
    private val _notesFlow = MutableStateFlow<List<Note>>(emptyList())
    val allNotes: Flow<List<Note>> = _notesFlow.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        if (file.exists()) {
            try {
                ObjectInputStream(file.inputStream()).use { stream ->
                    @Suppress("UNCHECKED_CAST")
                    val list = stream.readObject() as? List<Note>
                    if (list != null) {
                        _notesFlow.value = list
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveNotes(notes: List<Note>) {
        try {
            ObjectOutputStream(file.outputStream()).use { stream ->
                stream.writeObject(notes)
            }
            _notesFlow.value = notes
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insert(note: Note) {
        val current = _notesFlow.value.toMutableList()
        current.add(0, note)
        saveNotes(current)
    }

    suspend fun update(note: Note) {
        val current = _notesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == note.id }
        if (index != -1) {
            current[index] = note
            saveNotes(current)
        }
    }

    suspend fun delete(note: Note) {
        val current = _notesFlow.value.toMutableList()
        current.removeAll { it.id == note.id }
        saveNotes(current)
    }
}
