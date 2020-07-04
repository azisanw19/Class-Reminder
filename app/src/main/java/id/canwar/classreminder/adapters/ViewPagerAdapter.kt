package id.canwar.classreminder.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.canwar.classreminder.fragments.TaskFragment
import id.canwar.classreminder.fragments.NoteFragment
import id.canwar.classreminder.fragments.ScheduleFragment
import id.canwar.classreminder.helpers.TABS_COUNT
import java.lang.RuntimeException

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = HashMap<Int, Fragment>()

    override fun createFragment(position: Int): Fragment {
        val fragment = getFragment(position)
        fragments[position] = fragment
        return fragment
    }

    private fun getFragment(position: Int) = when (position) {
        0 -> ScheduleFragment()
        1 -> TaskFragment()
        2 -> NoteFragment()
        else -> throw RuntimeException("Trying to fetch unknown fragment id $position")
    }

    override fun getItemCount(): Int = TABS_COUNT

}
