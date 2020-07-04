package id.canwar.classreminder.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.TaskActivity
import id.canwar.classreminder.helpers.*
import id.canwar.classreminder.models.Task
import kotlinx.android.synthetic.main.fragment_task_item_holder.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskAdapter(val context: Context, val tasks: ArrayList<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fragment_task_item_holder, parent, false)
    )

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, tasks[position])
    }

    open inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        private val view = view

        fun bind(context: Context, task: Task) {

            view.task_item_title.text = task.title
            view.task_item_description.text = task.description

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.time.toLong()
            val time = SimpleDateFormat("MMMM dd yyyy (EEE) HH:mm").format(calendar.time)

            view.task_item_calendar.text = time

            view.setOnClickListener {
                Intent(it.context, TaskActivity::class.java).apply {
                    val bundle = Bundle().apply {
                        putExtra(TASK_ID, task.id)
                        putExtra(TASK_TITLE, task.title)
                        putExtra(TASK_DESCRIPTION, task.description)
                        putExtra(TASK_TIME_IN_MILLS, task.time)
                    }
                    action = UPDATE_ACTION
                    putExtras(bundle)
                    it.context.startActivity(this)
                }
            }
        }
    }


}