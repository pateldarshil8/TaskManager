package org.taskflow.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import dagger.hilt.android.AndroidEntryPoint
import org.taskflow.app.databinding.ActivityLoginBinding
import org.taskflow.app.ui.auth.EntryFragment

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var navigationSettings: AppBarConfiguration
    private lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        setSupportActionBar(loginBinding.toolbar)

        loginBinding.toolbar.setNavigationOnClickListener {
            displayFragment(EntryFragment())
        }

        displayFragment(EntryFragment())
    }

    private fun displayFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
