package com.example.qkart

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ FIX: intent is nullable, so use safe call
        intent?.let {
            handleNavigation(it)
        } ?: run {
            openFragment(HomeFragment())
        }

        findViewById<TextView>(R.id.navHome).setOnClickListener {
            openFragment(HomeFragment())
        }

        findViewById<TextView>(R.id.navOrders).setOnClickListener {
            openFragment(OrdersFragment())
        }

        findViewById<TextView>(R.id.navProfile).setOnClickListener {
            openFragment(ProfileFragment())
        }
    }

    // Works because launchMode="singleTop"
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // ✅ FIX: unwrap nullable Intent safely
        intent?.let {
            handleNavigation(it)
        }
    }

    // Non-null Intent ONLY
    private fun handleNavigation(intent: Intent) {
        if (intent.getBooleanExtra("openOrders", false)) {
            openFragment(OrdersFragment())
        } else {
            openFragment(HomeFragment())
        }
    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
