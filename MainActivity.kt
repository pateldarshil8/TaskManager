package org.taskflow.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.taskflow.app.databinding.ActivityMainBinding
import org.taskflow.app.ui.account.ProfileFragment
import org.taskflow.app.ui.home.DashboardFragment
import androidx.navigation.ui.AppBarConfiguration

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var navigationConfig: AppBarConfiguration
    private val activeUser = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbar)

        if (activeUser != null) {
            showFragment(DashboardFragment())
        } else {
            launchLoginScreen()
        }

        setupBottomNavigation()
    }

    private fun launchLoginScreen() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun setupBottomNavigation() {
        viewBinding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            val nextFragment = when (menuItem.itemId) {
                R.id.home -> DashboardFragment()
                R.id.account -> ProfileFragment()
                else -> DashboardFragment()
            }
            showFragment(nextFragment)
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
