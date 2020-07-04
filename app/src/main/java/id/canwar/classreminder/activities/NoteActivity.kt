package id.canwar.classreminder.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import id.canwar.classreminder.R
import id.canwar.classreminder.extensions.dbHelper
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.models.Note
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {

    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        if (intent.action == UPDATE_ACTION) {
            id = intent.extras?.getInt(NOTE_ID)
            val title = intent.extras?.getString(NOTE_TITLE)
            val content = intent.extras?.getString(NOTE_CONTENT)

            note_title.setText(title)
            note_content.setText(content)
        }

        /** App bar **/
        initAppbar()
    }

    private fun initAppbar() {

        if (intent.action == UPDATE_ACTION)
            supportActionBar?.title = resources.getString(R.string.note_update)
        else
            supportActionBar?.title = resources.getString(R.string.note_new)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_update_delete, menu)
        if (intent.action != UPDATE_ACTION) {
            val item = menu?.findItem(R.id.delete_action)
            item?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_action -> saveNote()
            R.id.delete_action -> deleteNote()
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }

        finish()
        return true
    }

    private fun saveNote() {
        val title = note_title.text.toString()
        val content = note_content.text.toString()

        val note = Note(id, title, content)

        if (intent.action == UPDATE_ACTION) {
            dbHelper.updateNote(note)
            Toast.makeText(baseContext, resources.getText(R.string.note_update_action), Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.insertNote(note)
            Toast.makeText(baseContext, resources.getText(R.string.note_add_action), Toast.LENGTH_SHORT).show()
        }

    }

    private fun deleteNote() {
        val title = note_title.text.toString()
        val content = note_content.text.toString()

        val note = Note(id, title, content)

        dbHelper.deleteNote(note)
        Toast.makeText(baseContext, resources.getText(R.string.note_delete_action), Toast.LENGTH_SHORT).show()
    }

}