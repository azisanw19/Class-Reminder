package id.canwar.classreminder.adapters

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import id.canwar.classreminder.R
import id.canwar.classreminder.extensions.dbHelper
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.helpers.WidgetScheduleProvider.Companion.ACTION_REFRESH
import id.canwar.classreminder.models.Schedule

class ScheduleListWidgetAdapter(val context: Context, val intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private val schedules = ArrayList<Schedule>()

    private fun getDataList() {
        schedules.clear()
        schedules.addAll(context.dbHelper.getSchedule(Formatter.getCurrentDay()))
    }

    override fun onCreate() {}

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onDataSetChanged() {
        getDataList()
    }

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(position: Int): RemoteViews {

        val fillIntent = Intent().apply {
            val bundle = Bundle().apply {
                putExtra(SCHEDULE_ID, schedules[position].id)
                putExtra(SCHEDULE_TITLE, schedules[position].title)
                putExtra(SCHEDULE_LOCATION, schedules[position].location)
                putExtra(SCHEDULE_INFO, schedules[position].info)
                putExtra(SCHEDULE_DAY, schedules[position].day)
                putExtra(SCHEDULE_TIME_START, schedules[position].timeStart)
                putExtra(SCHEDULE_TIME_END, schedules[position].timeEnd)
            }
            putExtra(ACTION_REFRESH, intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID))
            putExtras(bundle)
        }

        return RemoteViews(context.packageName, R.layout.widget_schedule_item_holder).apply {
            setTextViewText(R.id.schedule_item_start, Formatter.getTimeFromMinute(schedules[position].timeStart))
            setTextViewText(R.id.schedule_item_title, schedules[position].title)
            setTextViewText(R.id.schedule_item_end, Formatter.getTimeFromMinute(schedules[position].timeEnd))
            setTextViewText(R.id.schedule_item_info, schedules[position].info)

            setOnClickFillInIntent(R.id.schedule_item_holder, fillIntent)
        }
    }

    override fun getCount(): Int = schedules.size

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {
        // close data source
    }
    
}