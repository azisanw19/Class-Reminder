package id.canwar.classreminder.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.MainActivity
import id.canwar.classreminder.extensions.getNextNotificationSchedule
import id.canwar.classreminder.extensions.getNextNotificationTask
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.helpers.Formatter.getStringTimeTask

class NotificationReceiver : BroadcastReceiver() {

    private val NOTIFICATION_SCHEDULE_NAME = "Schedule"
    private val NOTIFICATION_TASK_NAME = "Task"

    override fun onReceive(context: Context?, intent: Intent?) {

        when(intent?.action) {
            NOTIFICATION_SCHEDULE -> notificationScheduleBuilder(context, intent)
            NOTIFICATION_TASK -> notificationTaskBuilder(context, intent)
        }
    }

    private fun notificationTaskBuilder(context: Context?, intent: Intent?) {
        val bundle = intent?.extras

//        val id = bundle?.getInt(TASK_ID)
        val title = bundle?.getString(TASK_TITLE)
        val description = bundle?.getString(TASK_DESCRIPTION)
        val time = bundle?.getString(TASK_TIME_IN_MILLS)

        val intentMainActivity = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_UPDATE_CURRENT)

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bigContentText = "Reminder task $title at ${getStringTimeTask(time!!.toLong())}. $description"

        // channel id 1
        val builder = NotificationCompat.Builder(context!!, "1")
            .setContentTitle(title)
            .setContentText("Reminder task $title at ${getStringTimeTask(time.toLong())}.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(bigContentText))
            .setSmallIcon(R.drawable.ic_reminder_vector)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setSound(sound)
            .setColor(Color.WHITE)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val notificationChannel = NotificationChannel("1", NOTIFICATION_TASK_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                enableLights(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000)
                lightColor = Color.WHITE
                setSound(sound, audioAttributes)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(1, builder)

        /** call next task **/
        context.getNextNotificationTask()
    }

    private fun notificationScheduleBuilder(context: Context?, intent: Intent?) {

        val bundle = intent?.extras

//        val id = bundle?.getInt(SCHEDULE_ID)
        val title = bundle?.getString(SCHEDULE_TITLE)
        val location = bundle?.getString(SCHEDULE_LOCATION)
        val info = bundle?.getString(SCHEDULE_INFO)
//        val day = bundle?.getInt(SCHEDULE_DAY)
        val timeStart = bundle?.getInt(SCHEDULE_TIME_START)
        val timeEnd = bundle?.getInt(SCHEDULE_TIME_END)

        val intentMainActivity = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_UPDATE_CURRENT)

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var bigContentText = "Reminder class $title at ${Formatter.getTimeFromMinute(timeStart!!)}. "
        if (info != "")
            bigContentText += "$info. "
        if (location != "")
            bigContentText += "location in $location "
        bigContentText += "end time at ${Formatter.getTimeFromMinute(timeEnd!!)}"

        // id channel 0
        val builder = NotificationCompat.Builder(context!!, "0")
            .setContentTitle(title)
            .setContentText("Reminder class $title at ${Formatter.getTimeFromMinute(timeStart)}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(bigContentText))
            .setSmallIcon(R.drawable.ic_reminder_vector)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setSound(sound)
            .setColor(Color.WHITE)
            .build()


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val notificationChannel = NotificationChannel("0", NOTIFICATION_SCHEDULE_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                enableLights(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000)
                lightColor = Color.WHITE
                setSound(sound, audioAttributes)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder)

        /** call next schedule **/
        context.getNextNotificationSchedule()

    }

}