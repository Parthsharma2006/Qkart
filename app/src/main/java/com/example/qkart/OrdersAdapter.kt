package com.example.qkart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(
    private val orders: List<Order>
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCanteen: TextView = view.findViewById(R.id.tvCanteen)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val itemsContainer: LinearLayout = view.findViewById(R.id.itemsContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvCanteen.text =
            if (order.canteenId.isNotEmpty()) "Canteen: ${order.canteenId}"
            else "Canteen"

        holder.tvStatus.text = order.status
        holder.tvTotal.text = "₹${order.totalAmount}"

        // 🔥 STATUS COLOR
        when (order.status) {
            "Pending", "Preparing", "Ready" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
            }
            "Completed" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed)
            }
        }

        // 🔥 ITEMS LIST
        holder.itemsContainer.removeAllViews()
        order.items.forEach { item ->
            val tv = TextView(holder.itemView.context)
            tv.text = "• ${item.name} × ${item.qty}"
            tv.textSize = 14f
            holder.itemsContainer.addView(tv)
        }
    }

    override fun getItemCount(): Int = orders.size
}
