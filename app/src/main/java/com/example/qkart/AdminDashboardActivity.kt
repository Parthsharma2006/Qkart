package com.example.qkart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var tvCanteenName: TextView
    private lateinit var btnLogout: Button
    private lateinit var adapter: AdminOrdersAdapter

    private val ordersList = mutableListOf<AdminOrder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // 🔥 TOOLBAR WITH LOGO
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        rvOrders = findViewById(R.id.rvAdminOrders)
        tvCanteenName = findViewById(R.id.tvCanteenName)
        btnLogout = findViewById(R.id.btnLogout)

        val canteenId = AdminUtils.getAdminCanteenId()
        if (canteenId == null) {
            logout()
            return
        }

        tvCanteenName.text = "Orders for ${canteenId.replace('_', ' ').uppercase()}"

        adapter = AdminOrdersAdapter(ordersList)
        rvOrders.layoutManager = LinearLayoutManager(this)
        rvOrders.adapter = adapter

        fetchOrders(canteenId)

        // 🔐 LOGOUT ACTION
        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // 🔥 REAL-TIME ORDERS LISTENER
    private fun fetchOrders(canteenId: String) {
        FirebaseFirestore.getInstance()
            .collection("orders")
            .whereEqualTo("canteenId", canteenId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) return@addSnapshotListener

                ordersList.clear()

                for (doc in snapshot.documents) {

                    // 🔥 READ ITEMS
                    val itemsList = mutableListOf<String>()
                    val items = doc.get("items") as? List<Map<String, Any>> ?: emptyList()

                    for (item in items) {
                        val name = item["name"] as? String ?: ""
                        val qty = (item["quantity"] as? Long)?.toInt() ?: 1
                        itemsList.add("$name x$qty")
                    }

                    val order = AdminOrder(
                        orderId = doc.id,
                        items = itemsList,
                        totalPrice = doc.getDouble("totalPrice") ?: 0.0,
                        paymentMode = doc.getString("paymentMode") ?: "",
                        paymentStatus = doc.getString("paymentStatus") ?: "",
                        status = doc.getString("status") ?: "Pending",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )

                    ordersList.add(order)
                }

                adapter.notifyDataSetChanged()
            }
    }

}


