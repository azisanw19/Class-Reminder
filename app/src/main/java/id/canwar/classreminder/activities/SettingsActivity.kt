package id.canwar.classreminder.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import id.canwar.classreminder.R
import id.canwar.classreminder.extensions.dbHelper
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        /** app bar **/
        supportActionBar?.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handleClick()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun handleClick() {
        settings_schedules_delete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert).create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setMessage(resources.getString(R.string.settings_schedule_delete_all_action))
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getText(R.string.no), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getText(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
                dbHelper.deleteSchedule()
                dialog.dismiss()
            })
            alertDialog.show()
        }

        settings_tasks_delete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert).create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setMessage(resources.getString(R.string.settings_task_delete_all_action))
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getText(R.string.no), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getText(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
                dbHelper.deleteTask()
                dialog.dismiss()
            })
            alertDialog.show()
        }

        settings_notes_delete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert).create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setMessage(resources.getString(R.string.settings_note_delete_all_action))
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getText(R.string.no), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getText(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
                dbHelper.deleteNote()
                dialog.dismiss()
            })
            alertDialog.show()
        }
    }

}