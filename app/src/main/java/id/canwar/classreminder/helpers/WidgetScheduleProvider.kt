package id.canwar.classreminder.helpers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.ScheduleActivity
import id.canwar.classreminder.services.WidgetScheduleService
import java.text.SimpleDateFormat
import java.util.*

class WidgetScheduleProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_CLICK = "actionClick"
        const val ACTION_REFRESH = "actionRefresh"
        const val ACTION_CLICK_REFRESH = "actionClcickRefresh"
    }

    private fun thisDate(): String{
        val calendar = Calendar.getInstance()
        return SimpleDateFormat("MMMM dd yyyy (EEEE)").format(calendar.time)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        appWidgetIds.forEach {
            val intent = Intent(context, ScheduleActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val serviceIntent = Intent(context, WidgetScheduleService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, it)
                data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
            }

            val clickIntent = Intent(context, WidgetScheduleProvider::class.java).apply {
                action = ACTION_CLICK
            }
            val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0)

            val views = RemoteViews(context.packageName, R.layout.widget_schedule).apply{
                setOnClickPendingIntent(R.id.widget_add_image, pendingIntent)
                setTextViewText(R.id.widget_date_label, thisDate())
                setRemoteAdapter(R.id.widget_schedule_list, serviceIntent)
                setEmptyView(R.id.widget_schedule_list, R.id.widget_schedule_empty)
                setPendingIntentTemplate(R.id.widget_schedule_list, clickPendingIntent)
            }

            appWidgetManager.apply {
                updateAppWidget(it, views)
                notifyAppWidgetViewDataChanged(it, R.id.widget_schedule_list)
            }
        }

    }

    private fun getComponentName(context: Context) = ComponentName(context, WidgetScheduleProvider::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        when {
            ACTION_CLICK == intent!!.action -> {

                val scheduleActivityIntent = Intent(context, ScheduleActivity::class.java).apply {
                    action = UPDATE_ACTION
                    putExtras(intent.extras!!)
                }

                context?.startActivity(scheduleActivityIntent)
            }
            ACTION_REFRESH == intent.action -> {

                val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

                AppWidgetManager.getInstance(context).apply {
                    notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_schedule_list)
                }

            }
            ACTION_CLICK_REFRESH == intent.action -> {

                val appWidgetManager = AppWidgetManager.getInstance(context)

                val appWidgetIds = appWidgetManager.getAppWidgetIds(getComponentName(context!!))
                appWidgetIds.forEach {
                    appWidgetManager.notifyAppWidgetViewDataChanged(it, R.id.widget_schedule_list)
                }
            }
        }

        super.onReceive(context, intent)
    }

}