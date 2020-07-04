package id.canwar.classreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import id.canwar.classreminder.extensions.getNextNotificationSchedule
import id.canwar.classreminder.extensions.getNextNotificationTask

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            context?.getNextNotificationSchedule()
            context?.getNextNotificationTask()
        }

    }

}