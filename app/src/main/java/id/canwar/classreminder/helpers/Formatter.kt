package id.canwar.classreminder.helpers

import java.text.SimpleDateFormat
import java.util.*

object Formatter {

    private const val FORMAT_TIME = "HH:mm"
    private const val WEEKLY_NAME = "EEEE"
    private const val WEEKLY_INT = "u"
    private const val TIME_TASK = "MMMM dd yyyy (EEE) HH:mm"

    fun getMinuteFromTime(string: String): Int {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat(FORMAT_TIME, Locale.US).parse(string)
        val hourOfMinute = calendar.get(Calendar.HOUR_OF_DAY) * 60
        val minute = calendar.get(Calendar.MINUTE)
        return hourOfMinute + minute
    }

    fun getTimeFromMinute(int: Int): String? {
        val calendar = Calendar.getInstance()

        val hour = int / 60
        val minute = int % 60

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        return SimpleDateFormat(FORMAT_TIME, Locale.US).format(calendar.time)
    }

    fun getDayFromString(string: String): Int {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat(WEEKLY_NAME, Locale.US).parse(string)
        val day = SimpleDateFormat(WEEKLY_INT, Locale.US).format(calendar.time)
        return day.toInt()
    }

    fun getDayFromInt(int: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat(WEEKLY_INT, Locale.US).parse(int.toString())
        return SimpleDateFormat(WEEKLY_NAME, Locale.US).format(calendar.time)
    }

    fun getCurrentDay(): Int {
        val calendar = Calendar.getInstance()
        val day = SimpleDateFormat(WEEKLY_INT, Locale.US).format(calendar.time)
        return day.toInt()
    }

    fun getTimeInMills(time: String): String {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat(TIME_TASK, Locale.US).parse(time)

        return calendar.timeInMillis.toString()
    }

    fun getStringTimeTask(timeInMills: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMills
        return SimpleDateFormat(TIME_TASK, Locale.US).format(calendar.time)
    }

}