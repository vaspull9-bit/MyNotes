package com.example.mynotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import java.text.SimpleDateFormat
import java.util.Locale

class NotesAdapter(
    private var notes: List<com.example.mynotes.data.NoteEntity>,
    private val onNoteClick: (com.example.mynotes.data.NoteEntity) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.note_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.note_date)
        private val contentPreviewTextView: TextView = itemView.findViewById(R.id.note_content_preview)

        fun bind(note: com.example.mynotes.data.NoteEntity) {
            titleTextView.text = note.title
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            dateTextView.text = dateFormat.format(note.updatedAt)
            contentPreviewTextView.text = note.content.take(100) + if (note.content.length > 100) "..." else ""

            itemView.setOnClickListener { onNoteClick(note) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    fun updateNotes(newNotes: List<com.example.mynotes.data.NoteEntity>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}