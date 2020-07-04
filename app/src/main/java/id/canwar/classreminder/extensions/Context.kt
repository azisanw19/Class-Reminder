package id.canwar.classreminder.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import id.canwar.classreminder.activities.MainActivity
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.models.Schedule
import id.canwar.classreminder.models.Task
import id.canwar.classreminder.receivers.NotificationReceiver
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

val Context.dbHelper: DBHelper get() = DBHelper.newInstance(applicationContext)

fun Context.timeToMinute(string: String): Int {
    val calendar = Calendar.getInstance()
    calendar.time = SimpleDateFormat("HH:mm").parse(string)
    val hourOfMinute = calendar.get(Calendar.HOUR_OF_DAY) * 60
    val minute = calendar.get(Calendar.MINUTE)
    return hourOfMinute + minute
}

fun Context.minuteToTime(int: Int): String? {
    val calendar = Calendar.getInstance()

    val hour = int / 60
    val minute = int % 60

    calendar.apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }

    return SimpleDateFormat("HH:mm").format(calendar.time)
}

fun Context.dayOfWeek(string: String): Int {
    val calendar = Calendar.getInstance()
    calendar.time = SimpleDateFormat("EEEE").parse(string)
    val day = SimpleDateFormat("u").format(calendar.time)
    return day.toInt()
}

fun Context.weekOfDay(int: Int): String {
    val calendar = Calendar.getInstance()
    calendar.time = SimpleDateFormat("u").parse(int.toString())
    val day = SimpleDateFormat("EEEE").format(calendar.time)
    return day
}

fun Context.getDayInt(): Int {
    val calendar = Calendar.getInstance()
    val day = SimpleDateFormat("u").format(calendar.time)
    return day.toInt()
}

private fun calendarNextSchedule(day: Int, time: Int): Calendar {

    val calendar = Calendar.getInstance()
    val thisDay = SimpleDateFormat("u").format(calendar.time).toInt()
    if (thisDay != day) {
        val dayAdd = (day + 7 - thisDay) % 7
        calendar.add(Calendar.DAY_OF_MONTH, dayAdd)
    } else {
        val minuteOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        if (minuteOfDay >= time) {
            calendar.add(Calendar.DAY_OF_MONTH, 7)
        }
    }

    val hour = time / 60
    val minute = time % 60
    calendar.apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return calendar

}

fun Context.getNextNotificationSchedule() {
    val calendar = Calendar.getInstance()
    val day = SimpleDateFormat("u").format(calendar.time).toInt()
    val time = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE) + 60
    val schedule = this.dbHelper.getNextSchedule(day, time)

    if (schedule != null) {
        val scheduleCalendar = calendarNextSchedule(schedule.day, schedule.timeStart - 60)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getNotificationIntentSchedule(this, schedule)

        try {
            alarmManager.set(AlarmManager.RTC_WAKEUP, scheduleCalendar.timeInMillis, pendingIntent)
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    } else {
        /** cancel pending intent **/
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            action = NOTIFICATION_SCHEDULE
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntent?.cancel()
    }
}

/** request code schedule 0 **/
private fun getNotificationIntentSchedule(context: Context, schedule: Schedule): PendingIntent {

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        val bundle = Bundle().apply {
            putExtra(SCHEDULE_ID, schedule.id)
            putExtra(SCHEDULE_TITLE, schedule.title)
            putExtra(SCHEDULE_LOCATION, schedule.location)
            putExtra(SCHEDULE_INFO, schedule.info)
            putExtra(SCHEDULE_DAY, schedule.day)
            putExtra(SCHEDULE_TIME_START, schedule.timeStart)
            putExtra(SCHEDULE_TIME_END, schedule.timeEnd)
        }
        action = NOTIFICATION_SCHEDULE
        putExtras(bundle)
    }

    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.getNextNotificationTask() {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, 1)
    val task = dbHelper.getTask(calendar.timeInMillis.toString())

    if (task != null) {
        calendar.timeInMillis = task.time.toLong()
        calendar.add(Calendar.DAY_OF_MONTH, -1)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getNotificationIntentTask(this, task)

        try {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    } else {
        /** Cancel PendingIntent **/
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            action = NOTIFICATION_TASK
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntent?.cancel()
    }

}

/** request code task 1 **/
private fun getNotificationIntentTask(context: Context, task: Task): PendingIntent {

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        val bundle = Bundle().apply {
            putExtra(TASK_ID, task.id)
            putExtra(TASK_TITLE, task.title)
            putExtra(TASK_DESCRIPTION, task.description)
            putExtra(TASK_TIME_IN_MILLS, task.time)
        }
        action = NOTIFICATION_TASK
        putExtras(bundle)
    }

    return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.triggerWidgetSchedule() {
    try {
        val intent = Intent(this, WidgetScheduleProvider::class.java)
        intent.action = WidgetScheduleProvider.ACTION_CLICK_REFRESH
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntent.send()
    } catch (e: PendingIntent.CanceledException) {
        Log.e("Error", e.toString())

    }
}