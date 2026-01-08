package com.example.qkart

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MenuAdapter(
    private val menuList: List<MenuItem>,
    private val canteenId: String = "",
    private val onCartUpdated: () -> Unit = {}
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)

        // 🔥 FIX: match XML ID
        val tvItemPrice: TextView = view.findViewById(R.id.tvPrice)

        val btnAdd: Button = view.findViewById(R.id.btnAddToCart)
        val imgItem: ImageView = view.findViewById(R.id.imgItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuList[position]

        holder.tvItemName.text = item.name
        holder.tvItemPrice.text = "₹${item.price.toInt()}"


        Log.d("MENU_IMAGE", "Item=${item.name}, URL=${item.imageUrl}")

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)

            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.imgItem)


        holder.btnAdd.setOnClickListener {
            if (canteenId.isNotEmpty()) {
                CartManager.addItem(canteenId, item)
            }
            onCartUpdated()
        }
    }

    override fun getItemCount(): Int = menuList.size
}
