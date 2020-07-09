package id.canwar.classreminder.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import id.canwar.classreminder.models.Note
import id.canwar.classreminder.models.Schedule
import id.canwar.classreminder.models.Task
import java.lang.Exception

class DBHelper private constructor(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    /** Schedule Table **/
    private val SCHEDULE_TABLE_NAME = "schedule"
    private val SCHEDULE_COL_ID = "id_schedule"
    private val SCHEDULE_COL_TITLE = "title_schedule"
    private val SCHEDULE_COL_LOCATION = "location_schedule"
    private val SCHEDULE_COL_INFO = "info_schedule"
    private val SCHEDULE_COL_DAY = "day_schedule"
    private val SCHEDULE_COL_TIME_START = "time_start_schedule"
    private val SCHEDULE_COL_TIME_END = "time_end_schedule"

    /** Task Table **/
    private val TASK_TABLE_NAME = "task"
    private val TASK_COL_ID = "id_task"
    private val TASK_COL_TITLE = "title_task"
    private val TASK_COL_DESCRIPTION = "description_task"
    private val TASK_COL_TIME_IN_MILLS = "time_in_mills_task"

    /** Note Table **/
    private val NOTE_TABLE_NAME = "note"
    private val NOTE_COL_ID = "id_note"
    private val NOTE_COL_TITLE = "title_note"
    private val NOTE_COL_CONTENT = "content_note"

    /** Handling DB **/
    private val mDb = writableDatabase

    companion object {
        private const val DB_VERSION = 1
        const val DB_NAME = "schedule"
        var dbInstance: DBHelper? = null

        fun newInstance(context: Context): DBHelper {
            if (dbInstance == null)
                dbInstance = DBHelper(context)

            return dbInstance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE $SCHEDULE_TABLE_NAME (" +
                "$SCHEDULE_COL_ID INTEGER," +
                "$SCHEDULE_COL_TITLE TEXT," +
                "$SCHEDULE_COL_LOCATION TEXT," +
                "$SCHEDULE_COL_INFO TEXT," +
                "$SCHEDULE_COL_DAY INTEGER," +
                "$SCHEDULE_COL_TIME_START INTEGER," +
                "$SCHEDULE_COL_TIME_END INTEGER," +
                "PRIMARY KEY ($SCHEDULE_COL_ID))")

        db?.execSQL("""CREATE TABLE $TASK_TABLE_NAME (
            |$TASK_COL_ID INTEGER,
            |$TASK_COL_TITLE TEXT,
            |$TASK_COL_DESCRIPTION TEXT,
            |$TASK_COL_TIME_IN_MILLS TEXT,
            |PRIMARY KEY ($TASK_COL_ID)
            |)""".trimMargin())

        db?.execSQL("""CREATE TABLE $NOTE_TABLE_NAME (
            |$NOTE_COL_ID INTEGER,
            |$NOTE_COL_TITLE TEXT,
            |$NOTE_COL_CONTENT TEXT,
            |PRIMARY KEY ($NOTE_COL_ID)
            |)""".trimMargin())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    fun insertNote(note: Note, db: SQLiteDatabase = mDb) {
        val noteValues = fillNoteContentValues(note)
        db.insertOrThrow(NOTE_TABLE_NAME, null, noteValues).toInt()
    }

    fun getNote(): ArrayList<Note> {
        val notes = ArrayList<Note>()
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(NOTE_TABLE_NAME, null, null, null, null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    try {

                        val note = extractCursorNote(cursor)
                        notes.add(note)
                    } catch (e : Exception) {
                        continue
                    }
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }

        return notes
    }

    fun deleteNote(note: Note): Boolean {
        val selectionArgs = arrayOf(note.id.toString())
        val selection = "$NOTE_COL_ID = ?"
        return mDb.delete(NOTE_TABLE_NAME, selection, selectionArgs) == 1
    }

    fun deleteNote(): Boolean {
        return mDb.delete(NOTE_TABLE_NAME, null, null) == 1
    }

    fun updateNote(note: Note): Boolean {
        val selectionArgs = arrayOf(note.id.toString())
        val selection = "$NOTE_COL_ID = ?"
        val noteValues = fillNoteContentValues(note)
        return mDb.update(NOTE_TABLE_NAME, noteValues, selection, selectionArgs) == 1
    }

    private fun extractCursorNote(cursor: Cursor): Note {
        val positionId = cursor.getColumnIndex(NOTE_COL_ID)
        val positionTitle = cursor.getColumnIndex(NOTE_COL_TITLE)
        val positionContent = cursor.getColumnIndex(NOTE_COL_CONTENT)

        val id = cursor.getInt(positionId)
        val title = cursor.getString(positionTitle)
        val content = cursor.getString(positionContent)

        return Note(id, title, content)
    }

    private fun fillNoteContentValues(note: Note) = ContentValues().apply {
        put(NOTE_COL_TITLE, note.title)
        put(NOTE_COL_CONTENT, note.content)
    }

    fun insertTask(task: Task, db: SQLiteDatabase = mDb) {
        val taskValues = fillTaskContentValues(task)
        db.insertOrThrow(TASK_TABLE_NAME, null, taskValues).toInt()
    }

    fun getTask(timeInMills: String): Task? {
        var cursor: Cursor? = null
        var task: Task? = null
        try {
            val selectionArgs = arrayOf(timeInMills)
            val selection = "$TASK_COL_TIME_IN_MILLS > ?"
            val limit =  "1"
            cursor = mDb.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, TASK_COL_TIME_IN_MILLS, limit)
            if (cursor.moveToFirst()) {
                try {
                    task = extractCursorTask(cursor)
                } catch (e: Exception) {
                    Log.d("error", e.toString())
                }
            }
        } finally {
            cursor?.close()
        }

        return task
    }

    fun getTask(): ArrayList<Task> {
        val tasks = ArrayList<Task>()
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(TASK_TABLE_NAME, null, null, null, null, null, TASK_COL_TIME_IN_MILLS)
            if (cursor.moveToFirst()) {
                do {
                    try {
                        val task = extractCursorTask(cursor)
                        tasks.add(task)
                    } catch (e: Exception) {
                        continue
                    }
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }

        return tasks
    }

    fun updateTask(task: Task): Boolean {
        val selectionArgs = arrayOf(task.id.toString())
        val selection = "$TASK_COL_ID = ?"
        val taskValues = fillTaskContentValues(task)
        return mDb.update(TASK_TABLE_NAME, taskValues, selection, selectionArgs) == 1
    }

    fun deleteTask(task: Task): Boolean {
        val selectionArgs = arrayOf(task.id.toString())
        val selection = "$TASK_COL_ID = ?"
        return mDb.delete(TASK_TABLE_NAME, selection, selectionArgs) == 1
    }

    fun deleteTask(): Boolean {
        return mDb.delete(TASK_TABLE_NAME, null, null) == 1
    }

    private fun extractCursorTask(cursor: Cursor): Task {
        val positionId = cursor.getColumnIndex(TASK_COL_ID)
        val positionTitle = cursor.getColumnIndex(TASK_COL_TITLE)
        val positionDescription = cursor.getColumnIndex(TASK_COL_DESCRIPTION)
        val positionTime = cursor.getColumnIndex(TASK_COL_TIME_IN_MILLS)

        val id = cursor.getInt(positionId)
        val title = cursor.getString(positionTitle)
        val description = cursor.getString(positionDescription)
        val time = cursor.getString(positionTime)

        return Task(id, title, description, time)
    }

    private fun fillTaskContentValues(task: Task) = ContentValues().apply {
        put(TASK_COL_TITLE, task.title)
        put(TASK_COL_DESCRIPTION, task.description)
        put(TASK_COL_TIME_IN_MILLS, task.time)
    }

    fun insertSchedule(schedule: Schedule, db: SQLiteDatabase = mDb) {
        val scheduleValues = fillScheduleContentValues(schedule)
        db.insertOrThrow(SCHEDULE_TABLE_NAME, null, scheduleValues).toInt()
    }

    private fun fillScheduleContentValues(schedule: Schedule) = ContentValues().apply {
        put(SCHEDULE_COL_TITLE, schedule.title)
        put(SCHEDULE_COL_LOCATION, schedule.location)
        put(SCHEDULE_COL_INFO, schedule.info)
        put(SCHEDULE_COL_DAY, schedule.day)
        put(SCHEDULE_COL_TIME_START, schedule.timeStart)
        put(SCHEDULE_COL_TIME_END, schedule.timeEnd)
    }

    fun getSchedule(day: Int): ArrayList<Schedule> {
        val schedules = ArrayList<Schedule>()
        var cursor: Cursor? = null
        try {
            val order = "$SCHEDULE_COL_DAY, $SCHEDULE_COL_TIME_START"
            val selection = "$SCHEDULE_COL_DAY == ?"
            val selectionArgs = arrayOf(day.toString())
            cursor = mDb.query(SCHEDULE_TABLE_NAME, null, selection, selectionArgs, null, null, order)
            if (cursor.moveToFirst()) {
                do {
                    try {
                        val schedule = extractCursorSchedule(cursor)
                        schedules.add(schedule)
                    } catch (e: Exception) {
                        continue
                    }
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }

        return schedules
    }

    fun getSchedule(): ArrayList<Schedule> {
        val schedules = ArrayList<Schedule>()
        val order = "$SCHEDULE_COL_DAY, $SCHEDULE_COL_TIME_START"
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(SCHEDULE_TABLE_NAME, null, null, null, null, null, order)
            if (cursor?.moveToFirst() == true) {
                do {
                    try {
                        val schedule = extractCursorSchedule(cursor)
                        schedules.add(schedule)
                    } catch (e: Exception) {
                        continue
                    }
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }

        return schedules
    }

    fun updateSchedule(schedule: Schedule): Boolean {
        val selectionArgs = arrayOf(schedule.id.toString())
        val selection = "$SCHEDULE_COL_ID = ?"
        val scheduleValues = fillScheduleContentValues(schedule)
        return mDb.update(SCHEDULE_TABLE_NAME, scheduleValues, selection, selectionArgs) == 1
    }

    fun deleteSchedule(schedule: Schedule): Boolean {
        val selectionArgs = arrayOf(schedule.id.toString())
        val selection = "$SCHEDULE_COL_ID = ?"
        return mDb.delete(SCHEDULE_TABLE_NAME, selection, selectionArgs) == 1
    }

    fun deleteSchedule(): Boolean {
        return mDb.delete(SCHEDULE_TABLE_NAME, null, null) == 1
    }

    fun extractCursorSchedule(cursor: Cursor): Schedule {
        val positionId = cursor.getColumnIndex(SCHEDULE_COL_ID)
        val positionTitle = cursor.getColumnIndex(SCHEDULE_COL_TITLE)
        val positionLocation = cursor.getColumnIndex(SCHEDULE_COL_LOCATION)
        val positionInfo = cursor.getColumnIndex(SCHEDULE_COL_INFO)
        val positionDay = cursor.getColumnIndex(SCHEDULE_COL_DAY)
        val positionTimeStart = cursor.getColumnIndex(SCHEDULE_COL_TIME_START)
        val positionTimeEnd = cursor.getColumnIndex(SCHEDULE_COL_TIME_END)

        val id = cursor.getInt(positionId)
        val title = cursor.getString(positionTitle)
        val location = cursor.getString(positionLocation)
        val info = cursor.getString(positionInfo)
        val day = cursor.getInt(positionDay)
        val timeStart = cursor.getInt(positionTimeStart)
        val timeEnd = cursor.getInt(positionTimeEnd)

        return Schedule(id, title, location, info, day, timeStart, timeEnd)
    }

    /** get Next Schedule **/
    fun sameDayNextTime(day: Int, time: Int): Schedule? {
        var schedule: Schedule? = null
        var cursor: Cursor? = null
        try {
            val selection = "$SCHEDULE_COL_DAY == ? AND $SCHEDULE_COL_TIME_START > ?"
            val selectionArgs = arrayOf(day.toString(), time.toString())
            val order = "$SCHEDULE_COL_DAY, $SCHEDULE_COL_TIME_START"
            val limit = "1"
            cursor = mDb.query(SCHEDULE_TABLE_NAME, null, selection, selectionArgs, null, null, order, limit)

            if (cursor?.moveToFirst() == true) {
                schedule = extractCursorSchedule(cursor)
            }
        } finally {
            cursor?.close()
        }

        return schedule
    }

    fun nextDay(day: Int): Schedule? {
        var schedule: Schedule? = null
        var cursor: Cursor? = null
        try {
            val selection =  "$SCHEDULE_COL_DAY > ?"
            val selectionArgs = arrayOf(day.toString())
            val order = "$SCHEDULE_COL_DAY, $SCHEDULE_COL_TIME_START"
            val limit = "1"
            cursor = mDb.query(SCHEDULE_TABLE_NAME, null, selection, selectionArgs, null, null, order, limit)

            if (cursor?.moveToFirst() == true) {
                schedule = extractCursorSchedule(cursor)
            }
        } finally {
            cursor?.close()
        }

        return schedule
    }

    fun nextWeek(): Schedule? {
        var schedule: Schedule? = null
        var cursor: Cursor? = null
        try {
            val order = "$SCHEDULE_COL_DAY, $SCHEDULE_COL_TIME_START"
            val limit = "1"
            cursor = mDb.query(SCHEDULE_TABLE_NAME, null, null, null, null, null, order, limit)
            if (cursor?.moveToFirst() == true) {
                schedule = extractCursorSchedule(cursor)
            }
        } finally {
            cursor?.close()
        }

        return schedule
    }

    fun oneSchedule(cursor: Cursor): Schedule? {
        var schedule: Schedule? = null
        if (cursor.moveToFirst() == true) {
            schedule = extractCursorSchedule(cursor)
        }

        return schedule
    }

    fun moreThenOne(day: Int, time: Int): Schedule? = sameDayNextTime(day, time) ?: nextDay(day) ?: nextWeek()

    fun getNextSchedule(day: Int, time: Int): Schedule? {
        var schedule: Schedule? = null
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(SCHEDULE_TABLE_NAME, null, null, null, null, null, null)
            val count = cursor.count
            schedule = when (count) {
                0 -> null
                1 -> oneSchedule(cursor)
                else -> moreThenOne(day, time)
            }
        } finally {
            cursor?.close()
        }

        return schedule
    }

}