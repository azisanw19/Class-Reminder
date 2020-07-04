package id.canwar.classreminder.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.MainActivity
import id.canwar.classreminder.extensions.getNextNotificationSchedule
import id.canwar.classreminder.extensions.getNextNotificationTask
import id.canwar.classreminder.helpers.*

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {


        when(intent?.action) {
            NOTIFICATION_SCHEDULE -> notificationScheduleBuilder(context, intent)
            NOTIFICATION_TASK -> notificationTaskBuilder(context, intent)
        }
    }

    private fun notificationTaskBuilder(context: Context?, intent: Intent?) {
        val bundle = intent?.extras

        val id = bundle?.getInt(TASK_ID)
        val title = bundle?.getString(TASK_TITLE)
        val description = bundle?.getString(TASK_DESCRIPTION)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context!!, id.toString())
            .setContentTitle(title)
            .setContentText("Reminder 1 day before task $title.")
            .setSmallIcon(R.drawable.ic_reminder_vector)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND or Notification.FLAG_AUTO_CANCEL)
            .setChannelId(id!!.toString())
            .setPriority(Notification.PRIORITY_MAX)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id!!, builder)

        /** call next task **/
        context?.getNextNotificationTask()
    }

    private fun notificationScheduleBuilder(context: Context?, intent: Intent?) {

        val bundle = intent?.extras

        val id = bundle?.getInt(SCHEDULE_ID)
        val title = bundle?.getString(SCHEDULE_TITLE)
        val location = bundle?.getString(SCHEDULE_LOCATION)
        val info = bundle?.getString(SCHEDULE_INFO)
        val day = bundle?.getInt(SCHEDULE_DAY)
        val timeStart = bundle?.getInt(SCHEDULE_TIME_START)
        val timeEnd = bundle?.getInt(SCHEDULE_TIME_END)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context!!, id.toString())
            .setContentTitle(title)
            .setContentText("Reminder 60 minute before class $title.")
            .setSmallIcon(R.drawable.ic_reminder_vector)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND or Notification.FLAG_AUTO_CANCEL)
            .setChannelId(id!!.toString())
            .setPriority(Notification.PRIORITY_MAX)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id!!, builder)

        /** call next schedule **/
        context?.getNextNotificationSchedule()

    }

}