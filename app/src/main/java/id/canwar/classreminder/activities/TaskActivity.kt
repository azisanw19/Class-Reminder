package id.canwar.classreminder.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import id.canwar.classreminder.R
import id.canwar.classreminder.extensions.*
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.models.Task
import kotlinx.android.synthetic.main.activity_task.*
import java.text.SimpleDateFormat
import java.util.*

class TaskActivity : AppCompatActivity() {

    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        if (intent.action == UPDATE_ACTION) {
            id = intent.extras?.getInt(TASK_ID)
            val title = intent.extras?.getString(TASK_TITLE)
            val description = intent.extras?.getString(TASK_DESCRIPTION)
            val timeInMills = intent.extras?.getString(TASK_TIME_IN_MILLS)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMills!!.toLong()

            val date = SimpleDateFormat("MMMM dd yyyy (EEE)").format(calendar.time)
            val time = SimpleDateFormat("HH:mm").format(calendar.time)

            task_title.setText(title)
            task_description.setText(description)
            task_date.text = date
            task_time.text = time
        } else {

            val calendar = Calendar.getInstance()
            val date = SimpleDateFormat("MMMM dd yyyy (EEE)").format(calendar.time)

            task_date.text = date
        }

        /** App bar **/
        initAppbar()

        /** click handle **/
        initUi()
    }

    private fun initAppbar() {

        if (intent.action == UPDATE_ACTION)
            supportActionBar?.title = resources.getString(R.string.task_update)
        else
            supportActionBar?.title = resources.getString(R.string.task_new)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun initUi() {
        task_date.setOnClickListener {
            setupDatePicker(task_date)
        }

        task_time.setOnClickListener {
            setupTimePicker(task_time)
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

        TimePickerDialog(this, TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, timeSetListener, calendar.get(
            Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun setupDatePicker(textView: AppCompatTextView) {

        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("MMMM dd yyyy (EEE)").parse(textView.text.toString())
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.apply {
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
            }
            textView.text = SimpleDateFormat("MMMM dd yyyy (EEE)").format(calendar.time)
        }

        DatePickerDialog(this, DatePickerDialog.THEME_DEVICE_DEFAULT_DARK, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()

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
            R.id.save_action -> saveTask()
            R.id.delete_action -> deleteTask()
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }

        /** trigger notifications **/
        if (config.notificationTaskStatus)
            getNextNotificationTask()

        finish()
        return true
    }

    private fun saveTask() {
        val title = task_title.text.toString()
        val description: String? = task_description.text.toString()
        Log.d("taskActivity", "tohere")
        val time = getTimeInMills()


        val task = Task(id, title, description, time)

        if (intent.action == UPDATE_ACTION) {
            dbHelper.updateTask(task)
            Toast.makeText(baseContext, resources.getText(R.string.task_update_action), Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.insertTask(task)
            Toast.makeText(baseContext, resources.getText(R.string.task_add_action), Toast.LENGTH_SHORT).show()
        }

    }

    private fun deleteTask() {
        val title = task_title.text.toString()
        val description: String? = task_description.text.toString()
        val time = getTimeInMills()

        val task = Task(id, title, description, time)
        Log.d("methodDelet", id.toString())

        dbHelper.deleteTask(task)
        Toast.makeText(baseContext, resources.getText(R.string.task_delete_action), Toast.LENGTH_SHORT).show()
    }

    private fun getTimeInMills(): String {

        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("MMMM dd yyyy (EEE) HH:mm").parse("${task_date.text} ${task_time.text}")

        return calendar.timeInMillis.toString()
    }

}