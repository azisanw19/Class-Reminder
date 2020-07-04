package id.canwar.classreminder.services

import android.content.Intent
import android.widget.RemoteViewsService
import id.canwar.classreminder.adapters.ScheduleListWidgetAdapter

class WidgetScheduleService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory = ScheduleListWidgetAdapter(applicationContext, intent)

}