package id.canwar.classreminder.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.canwar.classreminder.R
import id.canwar.classreminder.activities.TaskActivity
import id.canwar.classreminder.adapters.TaskAdapter
import id.canwar.classreminder.extensions.dbHelper
import kotlinx.android.synthetic.main.fragment_task.view.*

class TaskFragment : Fragment() {

    lateinit var view: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = (inflater.inflate(R.layout.fragment_task, container, false) as ViewGroup).apply {

            task_fab.setOnClickListener {
                Intent(activity, TaskActivity::class.java).apply {
                    startActivity(this)
                }
            }

            initRecyclerView(this)

        }

        return view
    }

    override fun onResume() {
        initRecyclerView(view)
        super.onResume()
    }

    private fun initRecyclerView(view: View) {
        view.apply {

            val tasks = context.dbHelper.getTask()

            if (tasks.isEmpty()) {
                task_recycler_view_list.visibility = View.GONE
                task_empty_layout.visibility = View.VISIBLE
            } else {
                task_empty_layout.visibility = View.GONE
                task_recycler_view_list.visibility = View.VISIBLE

                val taskAdapter = TaskAdapter(context, tasks)

                task_recycler_view_list.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = taskAdapter
                }
            }

        }
    }

}