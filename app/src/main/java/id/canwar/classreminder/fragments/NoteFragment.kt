package id.canwar.classreminder.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.NoteActivity
import id.canwar.classreminder.adapters.NoteAdapter
import id.canwar.classreminder.extensions.dbHelper
import kotlinx.android.synthetic.main.fragment_note.view.*

class NoteFragment : Fragment() {

    lateinit var view: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = (inflater.inflate(R.layout.fragment_note, container, false) as ViewGroup).apply {

            note_fab.setOnClickListener {
                Intent(activity, NoteActivity::class.java).apply {
                    startActivity(this)
                }
            }

            initRecylerView(this)

        }

        return view
    }

    override fun onResume() {
        initRecylerView(view)
        super.onResume()
    }

    private fun initRecylerView(view: View) {
        view.apply {

            val notes = context.dbHelper.getNote()

            if (notes.isEmpty()) {
                note_recycler_view_list.visibility = View.GONE
                note_empty_layout.visibility = View.VISIBLE
            } else {
                note_recycler_view_list.visibility = View.VISIBLE
                note_empty_layout.visibility = View.GONE

                val noteAdapter = NoteAdapter(context, notes)

                note_recycler_view_list.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = noteAdapter
                }
            }

        }
    }
}