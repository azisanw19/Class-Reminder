package id.canwar.classreminder.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.view.ViewGroup
import id.canwar.classreminder.R
import id.canwar.classreminder.helpers.*
import kotlinx.android.synthetic.main.dialog_custom_reminder.view.*

class CustomReminderDialog(val activity: Activity, val selectMinute: Int = 0, val callback: (minute: Int) -> Unit) {
    var dialog: AlertDialog
    var view = activity.layoutInflater.inflate(R.layout.dialog_custom_reminder, null) as ViewGroup

    init {
        view.apply {
            when {
                selectMinute == 0 -> dialog_radio_view.check(R.id.dialog_radio_minute)
                selectMinute % DAY_MINUTE == 0 -> {
                    dialog_radio_view.check(R.id.dialog_radio_day)
                    dialog_custom_reminder_interval.setText((selectMinute / DAY_MINUTE).toString())
                }
                selectMinute % HOUR_MINUTE == 0 -> {
                    dialog_radio_view.check(R.id.dialog_radio_hour)
                    dialog_custom_reminder_interval.setText((selectMinute / HOUR_MINUTE).toString())
                }
                else -> {
                    dialog_radio_view.check(R.id.dialog_radio_minute)
                    dialog_custom_reminder_interval.setText(selectMinute.toString())
                }
            }
        }

        dialog = AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setView(view)
            .setPositiveButton(R.string.ok) { dialogInterface, which -> confirmRepeatInterval()}
            .setNegativeButton(R.string.cancel, null)
            .create()
        dialog.show()
    }

    private fun confirmRepeatInterval() {
        val value = Integer.valueOf(if (view.dialog_custom_reminder_interval.text.toString() == "") "0" else view.dialog_custom_reminder_interval.text.toString())
        val multiplier = getMultiplier(view.dialog_radio_view.checkedRadioButtonId)
        callback(value * multiplier)
        dialog.dismiss()
    }

    private fun getMultiplier(id: Int) = when (id) {
        R.id.dialog_radio_day -> DAY_MINUTE
        R.id.dialog_radio_hour -> HOUR_MINUTE
        else -> 1
    }
}