package com.example.helloworld

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.helloworld.databinding.ActivityMainBinding
import com.example.helloworld.ui.HomeFragment
import com.example.helloworld.ui.PostsFragment
import com.example.helloworld.ui.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
            val handled = when (item.itemId) {
                R.id.drawer_home -> {
                    showFragment(HomeFragment())
                    binding.bottomNav.selectedItemId = R.id.nav_home
                    true
                }
                R.id.drawer_posts -> {
                    showFragment(PostsFragment())
                    binding.bottomNav.selectedItemId = R.id.nav_posts
                    true
                }
                R.id.drawer_profile -> {
                    showFragment(ProfileFragment())
                    binding.bottomNav.selectedItemId = R.id.nav_profile
                    true
                }
                R.id.drawer_settings, R.id.drawer_about -> {
                    // Placeholder items: no dedicated page yet.
                    true
                }
                else -> false
            }
            binding.drawerLayout.closeDrawers()
            handled
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showFragment(HomeFragment())
                    true
                }
                R.id.nav_posts -> {
                    showFragment(PostsFragment())
                    true
                }
                R.id.nav_profile -> {
                    showFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            showFragment(HomeFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
