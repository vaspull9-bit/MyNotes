

package com.example.mynotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynotes.adapter.NotesAdapter
import com.example.mynotes.data.NotesDatabase
import com.example.mynotes.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notesAdapter: NotesAdapter
    private val database by lazy { NotesDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadNotes()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "MyNotes"

        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_about -> {
                    showAboutDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(emptyList()) { note ->
            openNoteEditor(note)
        }

        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = notesAdapter
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            createNewNote()
        }
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            val notes = database.noteDao().getAllNotes()
            notesAdapter.updateNotes(notes)
        }
    }

    private fun createNewNote() {
        lifecycleScope.launch {
            val newNote = com.example.mynotes.data.NoteEntity(
                title = "Новая заметка",
                content = ""
            )
            val noteId = database.noteDao().insertNote(newNote)
            val savedNote = newNote.copy(id = noteId)
            openNoteEditor(savedNote)
        }
    }

    private fun openNoteEditor(note: com.example.mynotes.data.NoteEntity) {
        val intent = Intent(this, NoteEditorActivity::class.java).apply {
            putExtra("note", note)
        }
        startActivity(intent)
    }

    private fun showAboutDialog() {
        val aboutMessage = """
            VShargin (C) 2025
            vaspull9@gmail.com
            MyNotes v1.0
            
            Программа мои записки
        """.trimIndent()

        android.app.AlertDialog.Builder(this)
            .setTitle("О компании")
            .setMessage(aboutMessage)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }
}