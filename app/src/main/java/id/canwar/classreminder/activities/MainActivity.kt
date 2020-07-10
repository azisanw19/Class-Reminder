package id.canwar.classreminder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import id.canwar.classreminder.R
import id.canwar.classreminder.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragments()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun initFragments() {
        view_pager.adapter = ViewPagerAdapter(this)

        /** Binding view_pager and TabLayout **/
        TabLayoutMediator(main_tabs_holder, view_pager) { tab, position -> tab.icon = when (position) {
            0 -> getDrawable(R.drawable.ic_schedule_vector)
            1 -> getDrawable(R.drawable.ic_assignment_vector)
            2 -> getDrawable(R.drawable.ic_note_vector)
            else -> throw RuntimeException("Trying to fetch unknown fragment id $position")
        } }.attach()
    }

}
