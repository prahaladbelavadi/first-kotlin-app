package com.example.helloworld

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.helloworld.databinding.ActivityMainBinding
import com.example.helloworld.ui.EightFragment
import com.example.helloworld.ui.HomeFragment
import com.example.helloworld.ui.PostsFragment
import com.example.helloworld.ui.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentPageId: Int = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.drawer_open, R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { item ->
            val pageId = when (item.itemId) {
                R.id.drawer_home -> R.id.nav_home
                R.id.drawer_posts -> R.id.nav_posts
                R.id.drawer_profile -> R.id.nav_profile
                R.id.drawer_eight -> R.id.drawer_eight
                else -> null // Settings/About: no dedicated page yet.
            }
            if (pageId != null) {
                navigateTo(pageId)
            }
            binding.drawerLayout.closeDrawers()
            pageId != null
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            navigateTo(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            navigateTo(R.id.nav_home)
        }
    }

    // Single source of truth for switching pages: updates the fragment, syncs
    // the bottom nav's checked state (without re-triggering its own listener,
    // which previously caused a duplicate fragment to be created), and skips
    // redundant work if we're already on that page.
    private fun navigateTo(pageId: Int) {
        if (pageId == currentPageId && binding.bottomNav.selectedItemId == pageId) return
        currentPageId = pageId

        val fragment: Fragment = when (pageId) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_posts -> PostsFragment()
            R.id.nav_profile -> ProfileFragment()
            R.id.drawer_eight -> EightFragment()
            else -> return
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        val menuItem = binding.bottomNav.menu.findItem(pageId)
        if (menuItem != null && !menuItem.isChecked) {
            menuItem.isChecked = true
        }
    }
}
