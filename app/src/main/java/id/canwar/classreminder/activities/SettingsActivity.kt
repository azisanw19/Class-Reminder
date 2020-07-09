package id.canwar.classreminder.activities

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import id.canwar.classreminder.R
import id.canwar.classreminder.dialogs.CustomReminderDialog
import id.canwar.classreminder.extensions.*
import id.canwar.classreminder.helpers.DAY_MINUTE
import id.canwar.classreminder.helpers.HOUR_MINUTE
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        /** app bar **/
        supportActionBar?.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        setupSettingsItems()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupSettingsItems() {
        setupDeleteSchedule()
        setupDeleteTask()
        setupDeleteNote()
        setupNotificationStatusSchedule()
        setupNotificationStatusTask()
        setupScheduleReminder()
        setupTaskReminder()
    }

    private fun setupTaskReminder() {
        settings_task_reminder.text = reminderText(config.taskReminderMinute)
        settings_task_reminder_layout.setOnClickListener{
            CustomReminderDialog(this, config.taskReminderMinute) {
                config.taskReminderMinute = it
                settings_task_reminder.text = reminderText(it)
                getNextNotificationTask()
            }
        }
    }

    private fun setupScheduleReminder() {
        settings_schedule_reminder.text = reminderText(config.scheduleReminderMinute)
        settings_schedule_reminder_layout.setOnClickListener {
            CustomReminderDialog(this, config.scheduleReminderMinute) {
                config.scheduleReminderMinute = it
                settings_schedule_reminder.text = reminderText(it)
                getNextNotificationSchedule()
            }
        }
    }

    private fun reminderText(minute: Int) = when {
        minute % DAY_MINUTE == 0 -> "${minute / DAY_MINUTE} day"
        minute % HOUR_MINUTE == 0 -> "${minute / HOUR_MINUTE} hour"
        else -> "$minute minute"
    }

    private fun setupNotificationStatusTask() {
        settings_task_notification.isChecked = config.notificationTaskStatus
        settings_task_notification.setOnClickListener {
            settings_task_notification.splitTrack
            if (settings_task_notification.isChecked)
                getNextNotificationTask()
            else
                cancelNotificationTask()
            config.notificationTaskStatus = settings_task_notification.isChecked
            settings_task_reminder_layout.isClickable = config.notificationTaskStatus
        }
    }

    private fun setupNotificationStatusSchedule() {
        settings_schedule_notification.isChecked = config.notificationScheduleStatus
        settings_schedule_notification.setOnClickListener {
            settings_schedule_notification.splitTrack
            if (settings_schedule_notification.isChecked)
                getNextNotificationSchedule()
            else
                cancelNotificationSchedule()
            config.notificationScheduleStatus = settings_schedule_notification.isChecked
            settings_schedule_reminder_layout.isClickable = config.notificationScheduleStatus
        }
    }

    private fun setupDeleteSchedule() {
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
    }

    private fun setupDeleteTask() {
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
    }

    private fun setupDeleteNote() {
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