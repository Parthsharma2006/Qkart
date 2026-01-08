package com.example.qkart

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class CanteenAdapter(
    canteens1: Context,
    private val canteens: List<Canteen>
) : RecyclerView.Adapter<CanteenAdapter.CanteenViewHolder>() {

    inner class CanteenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCanteen: ImageView = itemView.findViewById(R.id.imgCanteen)
        val tvCanteenName: TextView = itemView.findViewById(R.id.tvCanteenName)
        val tvCanteenMeta: TextView = itemView.findViewById(R.id.tvCanteenMeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanteenViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_canteen, parent, false)
        return CanteenViewHolder(view)
    }

    override fun onBindViewHolder(holder: CanteenViewHolder, position: Int) {
        val canteen = canteens[position]

        // 🔥 LOAD IMAGE
        Glide.with(holder.itemView.context)
            .load(canteen.imageUrl)
            .placeholder(R.drawable.ic_canteen)
            .error(R.drawable.ic_canteen)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.imgCanteen)

        // Name
        holder.tvCanteenName.text = canteen.name

        // Rating + Category
        holder.tvCanteenMeta.text = "⭐ ${canteen.rating} · ${canteen.category}"

        // Navigate to menu
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MenuActivity::class.java)
            intent.putExtra("canteenId", canteen.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = canteens.size
}
