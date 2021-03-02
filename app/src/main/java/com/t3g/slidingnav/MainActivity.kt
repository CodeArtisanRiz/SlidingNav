package com.t3g.slidingnav

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.t3g.slidingnav.MainActivity
import com.t3g.slidingnav.fragment.BlankFragment
import com.t3g.slidingnav.fragment.HomeFragment
import com.t3g.slidingnav.menu.DrawerAdapter
import com.t3g.slidingnav.menu.DrawerItem
import com.t3g.slidingnav.menu.SimpleItem
import com.t3g.slidingnav.menu.SpaceItem
import com.t3g.slidingnav.sample.R
import java.util.*

@Suppress("INACCESSIBLE_TYPE")
class MainActivity : AppCompatActivity(), DrawerAdapter.OnItemSelectedListener {
    private lateinit var screenTitles: Array<String>
    private lateinit var screenIcons: Array<Drawable?>
    private var slidingNav: SlidingRootNav? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        slidingNav = SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject()
        screenIcons = loadScreenIcons()
        screenTitles = loadScreenTitles()
        val adapter = DrawerAdapter(listOf(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_SHARE),
                createItemFor(POS_PRIVACY),
                createItemFor(POS_TERMS),
                SpaceItem(48),
                createItemFor(POS_LOGOUT)))
        adapter.setListener(this)
        val list = findViewById<RecyclerView>(R.id.list)
        list.isNestedScrollingEnabled = false
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        adapter.setSelected(POS_HOME)
    }

    override fun onItemSelected(position: Int) {
        if (position == POS_HOME) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment, HomeFragment())
            transaction.commit()
        } else if (position == POS_SHARE) {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                val text = "Android Sliding Nav by t3g"
                intent.putExtra(Intent.EXTRA_TEXT, text)
                startActivity(Intent.createChooser(intent, "Share with"))
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Install apps to share", Toast.LENGTH_LONG).show()
            }
        } else if (position == POS_PRIVACY) {
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment, BlankFragment())
//            transaction.commit()
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://studentsguide.page.link/privacy_policy"))
            startActivity(i)
        } else if (position == POS_TERMS) {
            val j = Intent(Intent.ACTION_VIEW, Uri.parse("https://studentsguide.page.link/tnc"))
            startActivity(j)
        } else if (position == POS_LOGOUT) {
            finish()
        }
        slidingNav!!.closeMenu()
    }

    private fun createItemFor(position: Int): DrawerItem<*> {
        return SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent))
    }

    private fun loadScreenTitles(): Array<String> {
        return resources.getStringArray(R.array.ld_activityScreenTitles)
    }

    private fun loadScreenIcons(): Array<Drawable?> {
        val ta = resources.obtainTypedArray(R.array.ld_activityScreenIcons)
        val icons = arrayOfNulls<Drawable>(ta.length())
        for (i in 0 until ta.length()) {
            val id = ta.getResourceId(i, 0)
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id)
            }
        }
        ta.recycle()
        return icons
    }

    @ColorInt
    private fun color(@ColorRes res: Int): Int {
        return ContextCompat.getColor(this, res)
    }

    companion object {
        private const val POS_HOME = 0
        private const val POS_SHARE = 1
        private const val POS_PRIVACY = 2
        private const val POS_TERMS = 3
        private const val POS_LOGOUT = 5
    }
}