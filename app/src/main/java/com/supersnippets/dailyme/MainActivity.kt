package com.supersnippets.dailyme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.supersnippets.dailyme.data.Note
import com.supersnippets.dailyme.ui.NoteDetailScreen
import com.supersnippets.dailyme.ui.NoteListScreen
import com.supersnippets.dailyme.ui.NoteViewModel
import com.supersnippets.dailyme.ui.theme.DailyMeTheme

sealed interface Screen {
    object List : Screen
    data class Detail(val note: Note? = null) : Screen
}

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyMeTheme {
                val notes by viewModel.allNotes.collectAsState()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val screen = currentScreen) {
                        is Screen.List -> {
                            NoteListScreen(
                                notes = notes,
                                onNoteClick = { note -> currentScreen = Screen.Detail(note) },
                                onAddNoteClick = { currentScreen = Screen.Detail(null) },
                                onDeleteNote = { note -> viewModel.deleteNote(note) }
                            )
                        }
                        is Screen.Detail -> {
                            NoteDetailScreen(
                                note = screen.note,
                                onSave = { title, content ->
                                    val existing = screen.note
                                    if (existing == null) {
                                        viewModel.addNote(title, content)
                                    } else {
                                        viewModel.updateNote(existing, title, content)
                                    }
                                    currentScreen = Screen.List
                                },
                                onBack = { currentScreen = Screen.List }
                            )
                        }
                    }
                }
            }
        }
    }
}
