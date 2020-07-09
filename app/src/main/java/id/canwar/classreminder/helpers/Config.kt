package id.canwar.classreminder.helpers

import android.content.Context
import kotlin.collections.HashSet

class Config(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var notificationScheduleStatus: Boolean
        get() = prefs.getBoolean(NOTIFICATION_SCHEDULE_STATUS, true)
        set(notificationStatus) = prefs.edit().putBoolean(NOTIFICATION_SCHEDULE_STATUS, notificationStatus).apply()

    var scheduleReminderMinute: Int
        get() = prefs.getInt(SCHEDULE_REMINDER_MINUTE, 60)
        set(timeInMinute) = prefs.edit().putInt(SCHEDULE_REMINDER_MINUTE, timeInMinute).apply()

    var lastDayScheduleSelect: Int
        get() = prefs.getInt(LAST_DAY_SCHEDULE_SELECT, 1)
        set(day) = prefs.edit().putInt(LAST_DAY_SCHEDULE_SELECT, day).apply()

    var lastScheduleEndTime: Int
        get() = prefs.getInt(LAST_SCHEDULE_END_MINUTE, 480)
        set(timeInMinute) = prefs.edit().putInt(LAST_SCHEDULE_END_MINUTE, timeInMinute).apply()

    var intervalStartEnd: Int
        get() = prefs.getInt(INTERVAL_START_END_MINUTE, 100)
        set(timeInMinute) = prefs.edit().putInt(INTERVAL_START_END_MINUTE, timeInMinute).apply()

    var notificationTaskStatus: Boolean
        get() = prefs.getBoolean(NOTIFICATION_TASK_STATUS, true)
        set(notificationStatus) = prefs.edit().putBoolean(NOTIFICATION_TASK_STATUS, notificationStatus).apply()

    var taskReminderMinute: Int
        get() = prefs.getInt(TASK_REMINDER_MINUTE, 1440)
        set(timeInMinute) = prefs.edit().putInt(TASK_REMINDER_MINUTE, timeInMinute).apply()

    var displayTitles: Set<String>
        get() = prefs.getStringSet(DISPLAY_TITLE, HashSet<String>())!!
        set(displayTitle) = prefs.edit().remove(DISPLAY_TITLE).putStringSet(DISPLAY_TITLE, displayTitle).apply()


    fun addDisplayTitle(title: String) {
        addDisplayTitles(HashSet<String>(listOf(title)))
    }

    private fun addDisplayTitles(titles: Set<String>) {
        val curDisplayTitles = HashSet<String>(displayTitles)
        curDisplayTitles.addAll(titles)
        displayTitles = curDisplayTitles
    }

    fun removeDisplayTitle(title: String) {
        removeDisplayTitles(HashSet<String>(listOf(title)))
    }

    private fun removeDisplayTitles(titles: Set<String>) {
        val curDisplayTitles = HashSet<String>(displayTitles)
        curDisplayTitles.removeAll(titles)
        displayTitles = curDisplayTitles
    }
}