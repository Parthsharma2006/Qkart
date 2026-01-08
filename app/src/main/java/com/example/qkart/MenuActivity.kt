package com.example.qkart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MenuActivity : AppCompatActivity() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var cartBar: LinearLayout
    private lateinit var tvCartInfo: TextView

    private val menuList = mutableListOf<MenuItem>()
    private lateinit var canteenId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // ---------------- Toolbar with visible back arrow ----------------
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = "Menu"

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // ---------------- Views ----------------
        rvMenu = findViewById(R.id.rvMenu)
        cartBar = findViewById(R.id.cartBar)
        tvCartInfo = findViewById(R.id.tvCartInfo)

        rvMenu.layoutManager = LinearLayoutManager(this)

        canteenId = intent.getStringExtra("canteenId") ?: return

        adapter = MenuAdapter(
            menuList = menuList,
            canteenId = canteenId
        ) {
            updateCartBar()
        }

        rvMenu.adapter = adapter

        cartBar.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // ---------------- Swiggy-style cart replacement check ----------------
        if (CartManager.isDifferentCanteen(canteenId)) {
            AlertDialog.Builder(this)
                .setTitle("Replace cart?")
                .setMessage(
                    "Your cart contains items from another canteen. " +
                            "If you continue, those items will be removed."
                )
                .setPositiveButton("Yes") { _, _ ->
                    CartManager.clearCart()
                    loadMenu()
                }
                .setNegativeButton("No") { _, _ ->
                    finish()
                }
                .setCancelable(false)
                .show()
        } else {
            loadMenu()
        }

        updateCartBar()
    }

    // ---------------- Load menu from Firebase ----------------
    private fun loadMenu() {
        FirebaseFirestore.getInstance()
            .collection("canteens")
            .document(canteenId) // 🔥 main_canteen
            .collection("menu")
            .get()
            .addOnSuccessListener { result ->
                menuList.clear()
                for (doc in result) {
                    menuList.add(
                        MenuItem(
                            name = doc.getString("name") ?: "",
                            price = doc.getDouble("price") ?: 0.0, // ✅ DOUBLE
                            imageUrl = doc.getString("imageUrl") ?: "" // ✅ REQUIRED
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
    }

    // ---------------- Floating cart bar update ----------------
    private fun updateCartBar() {
        if (CartManager.isEmpty()) {
            cartBar.visibility = View.GONE
        } else {
            cartBar.visibility = View.VISIBLE
            tvCartInfo.text =
                "${CartManager.getTotalItems()} items | ₹${CartManager.getTotalPrice()}"
        }
    }
}
