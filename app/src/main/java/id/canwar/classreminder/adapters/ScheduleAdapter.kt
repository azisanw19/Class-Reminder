package id.canwar.classreminder.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.ScheduleActivity
import id.canwar.classreminder.extensions.minuteToTime
import id.canwar.classreminder.extensions.weekOfDay
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.models.Schedule
import kotlinx.android.synthetic.main.fragment_schedule_item_holder.view.*

class ScheduleAdapter(val context: Context, private val schedules: ArrayList<Schedule>) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fragment_schedule_item_holder, parent, false)
    )

    override fun getItemCount(): Int = schedules.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, schedules[position])
    }

    open inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val view = view

        fun bind(context: Context, schedule: Schedule) {
            view.schedule_list_title.text = schedule.title
            view.schedule_list_day.text = context.weekOfDay(schedule.day)
            view.schedule_list_time.text = "${context.minuteToTime(schedule.timeStart)} - ${context.minuteToTime(schedule.timeEnd)}"
            view.schedule_list_location.text = schedule.location

            view.setOnClickListener {
                Intent(it.context, ScheduleActivity::class.java).apply {
                    val bundle = Bundle().apply {
                        putExtra(SCHEDULE_ID, schedule.id)
                        putExtra(SCHEDULE_TITLE, schedule.title)
                        putExtra(SCHEDULE_LOCATION, schedule.location)
                        putExtra(SCHEDULE_INFO, schedule.info)
                        putExtra(SCHEDULE_DAY, schedule.day)
                        putExtra(SCHEDULE_TIME_START, schedule.timeStart)
                        putExtra(SCHEDULE_TIME_END, schedule.timeEnd)
                    }
                    action = UPDATE_ACTION
                    putExtras(bundle)
                    it.context.startActivity(this)
                }
            }
        }
    }

}