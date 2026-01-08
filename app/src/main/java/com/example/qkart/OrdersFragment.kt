package com.example.qkart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrdersFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var adapter: OrdersAdapter
    private val orders = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rvOrders = view.findViewById(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(requireContext())

        adapter = OrdersAdapter(orders)
        rvOrders.adapter = adapter

        fetchOrders()
    }

    private fun fetchOrders() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("orders")
            .whereEqualTo("userId", userId)
            // 🔥 THIS LINE MAKES RECENT ORDERS COME FIRST
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e("ORDERS", "Firestore error", error)
                    return@addSnapshotListener
                }

                orders.clear()

                snapshot?.documents?.forEach { doc ->

                    val itemsList = mutableListOf<OrderItem>()
                    val items = doc.get("items") as? List<Map<String, Any>>

                    items?.forEach { item ->
                        val name = item["name"] as? String ?: ""

                        val qty =
                            (item["qty"] as? Number)?.toInt()
                                ?: (item["quantity"] as? Number)?.toInt()
                                ?: 0

                        val price = (item["price"] as? Number)?.toInt() ?: 0

                        itemsList.add(
                            OrderItem(
                                name = name,
                                qty = qty,
                                price = price
                            )
                        )
                    }

                    val totalAmount =
                        (doc.get("totalAmount") as? Number)?.toInt()
                            ?: (doc.get("totalPrice") as? Number)?.toInt()
                            ?: 0

                    orders.add(
                        Order(
                            id = doc.id,
                            canteenId = doc.getString("canteenId") ?: "",
                            status = doc.getString("status") ?: "",
                            totalAmount = totalAmount,
                            items = itemsList
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }
}
