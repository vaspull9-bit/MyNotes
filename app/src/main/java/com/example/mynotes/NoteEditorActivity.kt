package com.example.mynotes

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mynotes.data.NotesDatabase
import com.example.mynotes.databinding.ActivityNoteEditorBinding
import java.util.Date

class NoteEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteEditorBinding
    private var currentNote: com.example.mynotes.data.NoteEntity? = null
    private var isEdited = false
    private val database by lazy { NotesDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        getNoteFromIntent()
        setupTextListeners()
        setupFormattingButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Редактор заметки"
    }

    private fun getNoteFromIntent() {
        currentNote = intent.getParcelableExtra("note")
        currentNote?.let { note ->
            binding.titleEditText.setText(note.title)
            binding.contentEditText.setText(note.content)
        } ?: run {
            currentNote = com.example.mynotes.data.NoteEntity(title = "Новая заметка", content = "")
            binding.titleEditText.setText("")
            binding.contentEditText.setText("")
        }
    }

    private fun setupTextListeners() {
        binding.titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { isEdited = true }
        })

        binding.contentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { isEdited = true }
        })
    }

    private fun setupFormattingButtons() {
        binding.boldButton.setOnClickListener { applyFormatting("<b>", "</b>") }
        binding.italicButton.setOnClickListener { applyFormatting("<i>", "</i>") }
        binding.underlineButton.setOnClickListener { applyFormatting("<u>", "</u>") }
        binding.strikeButton.setOnClickListener { applyFormatting("<s>", "</s>") }
    }

    private fun applyFormatting(startTag: String, endTag: String) {
        val content = binding.contentEditText
        val start = content.selectionStart
        val end = content.selectionEnd

        if (start == end) {
            // No selection, insert tags at cursor
            val text = content.text.insert(start, "$startTag$endTag")
            content.setText(text)
            content.setSelection(start + startTag.length)
        } else {
            // Text selected, wrap with tags
            val selectedText = content.text.substring(start, end)
            val newText = content.text.replaceRange(start, end, "$startTag$selectedText$endTag")
            content.setText(newText)
            content.setSelection(start, end + startTag.length + endTag.length)
        }
        isEdited = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_note_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_save -> {
                saveNote()
                true
            }
            R.id.menu_export -> {
                showExportOptions()
                true
            }
            R.id.menu_format_bold -> {
                applyFormatting("<b>", "</b>")
                true
            }
            R.id.menu_format_italic -> {
                applyFormatting("<i>", "</i>")
                true
            }
            R.id.menu_format_underline -> {
                applyFormatting("<u>", "</u>")
                true
            }
            R.id.menu_format_strike -> {
                applyFormatting("<s>", "</s>")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = binding.titleEditText.text.toString().trim()
        val content = binding.contentEditText.text.toString()

        if (title.isEmpty()) {
            Toast.makeText(this, "Введите заголовок", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedNote = currentNote?.copy(
            title = title,
            content = content,
            updatedAt = Date()
        ) ?: com.example.mynotes.data.NoteEntity(
            title = title,
            content = content,
            updatedAt = Date()
        )

        Thread {
            try {
                if (updatedNote.id == 0L) {
                    database.noteDao().insertNote(updatedNote)
                } else {
                    database.noteDao().updateNote(updatedNote)
                }
                runOnUiThread {
                    Toast.makeText(this, "Заметка сохранена", Toast.LENGTH_SHORT).show()
                    isEdited = false
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun showExportOptions() {
        val options = arrayOf("TXT", "PDF", "PNG", "XLS", "DOCX")

        AlertDialog.Builder(this)
            .setTitle("Экспорт в формате")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> exportToTxt()
                    1 -> exportToPdf()
                    2 -> exportToPng()
                    3 -> exportToXls()
                    4 -> exportToDocx()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun exportToTxt() {
        Toast.makeText(this, "Экспорт в TXT", Toast.LENGTH_SHORT).show()
    }

    private fun exportToPdf() {
        Toast.makeText(this, "Экспорт в PDF", Toast.LENGTH_SHORT).show()
    }

    private fun exportToPng() {
        Toast.makeText(this, "Экспорт в PNG", Toast.LENGTH_SHORT).show()
    }

    private fun exportToXls() {
        Toast.makeText(this, "Экспорт в XLS", Toast.LENGTH_SHORT).show()
    }

    private fun exportToDocx() {
        Toast.makeText(this, "Экспорт в DOCX", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (isEdited) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showSaveDialog() {
        AlertDialog.Builder(this)
            .setTitle("Сохранение")
            .setMessage("Сохранить изменения?")
            .setPositiveButton("Сохранить") { _, _ -> saveNote() }
            .setNegativeButton("Не сохранять") { _, _ -> finish() }
            .setNeutralButton("Отмена", null)
            .show()
    }
}