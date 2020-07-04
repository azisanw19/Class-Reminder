package id.canwar.classreminder.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.NoteActivity
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.models.Note
import kotlinx.android.synthetic.main.fragment_note_item_holder.view.*
import kotlin.collections.ArrayList

class NoteAdapter(val context: Context, val notes: ArrayList<Note>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fragment_note_item_holder, parent, false)
    )

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteAdapter.ViewHolder, position: Int) {
        holder.bind(context, notes[position])
    }

    open inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        private val view = view

        fun bind(context: Context, note: Note) {

            view.note_item_title.text = note.title
            view.note_item_content.text = note.content

            view.setOnClickListener {
                Intent(it.context,  NoteActivity::class.java).apply {
                    val bundle = Bundle().apply {
                        putExtra(NOTE_ID, note.id)
                        putExtra(NOTE_TITLE, note.title)
                        putExtra(NOTE_CONTENT, note.content)
                    }
                    action = UPDATE_ACTION
                    putExtras(bundle)
                    it.context.startActivity(this)
                }
            }
        }
    }

}