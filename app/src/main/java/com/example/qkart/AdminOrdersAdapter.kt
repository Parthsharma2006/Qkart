package com.example.qkart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminOrdersAdapter(
    private val orders: MutableList<AdminOrder>
) : RecyclerView.Adapter<AdminOrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvOrderId = itemView.findViewById<TextView>(R.id.tvOrderId)
        private val tvItems = itemView.findViewById<TextView>(R.id.tvItems) // 🔥 ADDED
        private val tvTotal = itemView.findViewById<TextView>(R.id.tvTotal)
        private val tvPayment = itemView.findViewById<TextView>(R.id.tvPayment)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        private val btnUpdateStatus = itemView.findViewById<Button>(R.id.btnUpdateStatus)

        fun bind(order: AdminOrder) {

            tvOrderId.text = "Order ID: ${order.orderId.take(8)}"

            // 🔥 SHOW ORDERED ITEMS
            tvItems.text = order.items.joinToString("\n")

            tvTotal.text = "Total: ₹${order.totalPrice.toInt()}"

            tvPayment.text = "Payment: ${order.paymentMode} | ${order.paymentStatus}"
            tvStatus.text = "Status: ${order.status}"

            btnUpdateStatus.visibility = View.GONE
            btnUpdateStatus.setOnClickListener(null)

            when (order.status) {
                "Pending" -> {
                    btnUpdateStatus.visibility = View.VISIBLE
                    btnUpdateStatus.text = "Move to Preparing"
                    btnUpdateStatus.setOnClickListener {
                        updateOrderStatus(order.orderId, "Preparing")
                    }
                }

                "Preparing" -> {
                    btnUpdateStatus.visibility = View.VISIBLE
                    btnUpdateStatus.text = "Mark as Ready"
                    btnUpdateStatus.setOnClickListener {
                        updateOrderStatus(order.orderId, "Ready")
                    }
                }

                "Ready" -> {
                    btnUpdateStatus.visibility = View.VISIBLE
                    btnUpdateStatus.text = "Complete Order"
                    btnUpdateStatus.setOnClickListener {
                        updateOrderStatus(order.orderId, "Completed")
                    }
                }

                "Completed" -> {
                    btnUpdateStatus.visibility = View.GONE
                }
            }
        }

        private fun updateOrderStatus(orderId: String, newStatus: String) {

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {

                // 🔥 UPDATE LOCAL UI STATE
                val updatedPaymentStatus =
                    if (newStatus == "Completed") "DONE" else orders[position].paymentStatus

                orders[position] = orders[position].copy(
                    status = newStatus,
                    paymentStatus = updatedPaymentStatus
                )

                notifyItemChanged(position)
            }

            // 🔥 UPDATE FIRESTORE
            val updates = mutableMapOf<String, Any>(
                "status" to newStatus
            )

            if (newStatus == "Completed") {
                updates["paymentStatus"] = "DONE"
            }

            FirebaseFirestore.getInstance()
                .collection("orders")
                .document(orderId)
                .update(updates)
        }
    }
}
