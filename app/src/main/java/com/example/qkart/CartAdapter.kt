package com.example.qkart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvItemPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQuantity)
        val btnPlus: Button = view.findViewById(R.id.btnPlus)
        val btnMinus: Button = view.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        holder.tvName.text = item.name
        holder.tvPrice.text = "₹${item.price}"
        holder.tvQty.text = item.quantity.toString()

        // ➕ PLUS
        holder.btnPlus.setOnClickListener {
            CartManager.updateQuantity(item, +1)
            notifyItemChanged(position)
            onCartUpdated()
        }

        // ➖ MINUS
        holder.btnMinus.setOnClickListener {
            CartManager.updateQuantity(item, -1)

            if (item.quantity <= 0) {
                val pos = holder.adapterPosition
                cartItems.removeAt(pos)
                notifyItemRemoved(pos)
            } else {
                notifyItemChanged(position)
            }

            onCartUpdated()
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
