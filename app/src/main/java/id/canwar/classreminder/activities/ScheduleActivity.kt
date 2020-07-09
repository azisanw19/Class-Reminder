package id.canwar.classreminder.activities

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import id.canwar.classreminder.R
import id.canwar.classreminder.extensions.*
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.models.Schedule
import kotlinx.android.synthetic.main.activity_schedule.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleActivity : AppCompatActivity() {

    private var id: Int? = null
    private lateinit var historyTitles: Set<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        /** parsing data intent */
        if (intent.action == UPDATE_ACTION) {
            getDataIntent(intent.extras!!)
        } else {
            schedule_day_text.text = weekOfDay(config.lastDayScheduleSelect)
            schedule_start_time_text.text = minuteToTime(config.lastScheduleEndTime)
            schedule_end_time_text.text = minuteToTime(config.lastScheduleEndTime + config.intervalStartEnd)
        }

        historyTitles = config.displayTitles

        /** Auto Complete Text View **/
        val suggestText = ArrayAdapter<String>(this, R.layout.custom_select_dialog, ArrayList<String>(historyTitles))
        schedule_title.threshold = 1
        schedule_title.setAdapter(suggestText)

        /** App bar **/
        initAppbar()

        /** click handle **/
        initUi()

    }

    private fun getDataIntent(bundle: Bundle) {

        val id  = bundle.getInt(SCHEDULE_ID)
        val title = bundle.getString(SCHEDULE_TITLE)
        val location = bundle.getString(SCHEDULE_LOCATION)
        val info = bundle.getString(SCHEDULE_INFO)
        val day = bundle.getInt(SCHEDULE_DAY)
        val timeStart = bundle.getInt(SCHEDULE_TIME_START)
        val timeEnd = bundle.getInt(SCHEDULE_TIME_END)

        this.id = id

        schedule_title.setText(title)
        schedule_location.setText(location)
        schedule_info.setText(info)
        schedule_day_text.text = weekOfDay(day)
        schedule_start_time_text.text = minuteToTime(timeStart)
        schedule_end_time_text.text = minuteToTime(timeEnd)
    }

    private fun initAppbar() {
        if (intent.action == UPDATE_ACTION)
            supportActionBar?.title = getString(R.string.schedule_update)
        else
            supportActionBar?.title = getString(R.string.schedule_new)
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
            R.id.save_action -> saveSchedule()
            R.id.delete_action -> deleteSchedule()
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }

        config.lastDayScheduleSelect = dayOfWeek(schedule_day_text.text.toString())
        config.lastScheduleEndTime = timeToMinute(schedule_end_time_text.text.toString())
        val minute = timeToMinute(schedule_end_time_text.text.toString()) - timeToMinute(schedule_start_time_text.text.toString())
        config.intervalStartEnd = minute

        /** Trigger Widget & Notifications **/
        triggerWidgetSchedule()
        if (config.notificationScheduleStatus)
            getNextNotificationSchedule()

        finish()
        return true
    }

    private fun saveSchedule() {

        val title = schedule_title.text.toString()
        val location = schedule_location.text.toString()
        val info: String? = schedule_info.text.toString()
        val day = dayOfWeek(schedule_day_text.text.toString())
        val start = timeToMinute(schedule_start_time_text.text.toString())
        val end = timeToMinute(schedule_end_time_text.text.toString())

        val schedule = Schedule(id, title, location, info, day, start, end)

        if (intent.action == UPDATE_ACTION) {

            if (!historyTitles.contains(title)) {
                config.addDisplayTitle(title)
                val oldTitle = intent.extras!!.getString(SCHEDULE_TITLE)
                if (historyTitles.contains(oldTitle))
                    config.removeDisplayTitle(oldTitle!!)
            }

            dbHelper.updateSchedule(schedule)
            Toast.makeText(baseContext, getString(R.string.schedule_update_action), Toast.LENGTH_SHORT).show()
        }  else {

            if (!historyTitles.contains(title))
                config.addDisplayTitle(title)

            dbHelper.insertSchedule(schedule)
            Toast.makeText(baseContext, getString(R.string.schedule_add_action), Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteSchedule() {

        val title = schedule_title.text.toString()
        val location = schedule_location.text.toString()
        val info: String? = schedule_info.text.toString()
        val day = dayOfWeek(schedule_day_text.text.toString())
        val start = timeToMinute(schedule_start_time_text.text.toString())
        val end = timeToMinute(schedule_end_time_text.text.toString())

        val scheduleInsert = Schedule(id, title, location, info, day, start, end)

        if (historyTitles.contains(title))
            config.removeDisplayTitle(title)

        val oldTitle = intent.extras!!.getString(SCHEDULE_TITLE)

        if (historyTitles.contains(oldTitle))
            config.removeDisplayTitle(oldTitle!!)

        dbHelper.deleteSchedule(scheduleInsert)
        Toast.makeText(baseContext, getString(R.string.schedule_delete_action), Toast.LENGTH_SHORT).show()
    }

    private fun initUi() {

        schedule_day_text.setOnClickListener {
            val day = resources.getStringArray(R.array.day_array)
            setupDialog(day, schedule_day_text)
        }

        schedule_start_time_text.setOnClickListener {
            setupTimePicker(schedule_start_time_text)
        }

        schedule_end_time_text.setOnClickListener {
            setupTimePicker(schedule_end_time_text)
        }

    }

    private fun setupTimePicker(textView: AppCompatTextView) {

        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("HH:mm").parse(textView.text.toString())
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            textView.text = SimpleDateFormat("HH:mm").format(calendar.time)
        }

        TimePickerDialog(this, TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }


    private fun setupDialog(array: Array<String>, textView: AppCompatTextView) {

        val choice = array.indexOf(textView.text)

        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setSingleChoiceItems(array, choice) { dialog, which ->
                textView.text = array[which]
                dialog.dismiss()
            }
            .create()
            .show()

    }

}
