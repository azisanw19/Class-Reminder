package id.canwar.classreminder.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.ScheduleActivity
import id.canwar.classreminder.adapters.ScheduleAdapter
import id.canwar.classreminder.extensions.dbHelper
import kotlinx.android.synthetic.main.fragment_schedule.view.*

class ScheduleFragment : Fragment() {

    lateinit var view: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view = (inflater.inflate(R.layout.fragment_schedule, container, false) as ViewGroup).apply {

            schedule_fab.setOnClickListener {
                Intent(activity, ScheduleActivity::class.java).apply {
                    startActivity(this)
                }
            }

            initRecylerView(this)

        }

        return view
    }

    override fun onResume() {
        initRecylerView(view)
        super.onResume()
    }

    private fun initRecylerView(view: View) {
        view.apply {

            val schedules = context.dbHelper.getSchedule()

            if (schedules.isEmpty()) {
                schedule_recycler_view_list.visibility = View.GONE
                schedule_empty_layout.visibility = View.VISIBLE
            } else {
                schedule_recycler_view_list.visibility = View.VISIBLE
                schedule_empty_layout.visibility = View.GONE

                val scheduleAdapter = ScheduleAdapter(context, schedules)
                schedule_recycler_view_list.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = scheduleAdapter
                }
            }
        }
    }

}