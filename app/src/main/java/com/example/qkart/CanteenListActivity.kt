package com.example.qkart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CanteenListActivity : AppCompatActivity() {

    private lateinit var rvCanteens: RecyclerView
    private lateinit var canteenAdapter: CanteenAdapter

    // ✅ Adapter expects ONLY List<Canteen>
    private val canteenList = mutableListOf<Canteen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canteen_list)

        rvCanteens = findViewById(R.id.rvCanteens)
        rvCanteens.layoutManager = LinearLayoutManager(this)

        // ✅ IMPORTANT: pass ONLY the list
        canteenAdapter = CanteenAdapter(requireContext(return), canteenList)
        rvCanteens.adapter = canteenAdapter

        loadCanteensFromFirebase()
    }

    private fun loadCanteensFromFirebase() {
        FirebaseFirestore.getInstance()
            .collection("canteens")
            .get()
            .addOnSuccessListener { snapshot ->

                canteenList.clear()

                for (doc in snapshot) {
                    canteenList.add(
                        Canteen(
                            id = doc.id,                     // documentId = canteenId
                            name = doc.getString("name") ?: "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            category = doc.getString("category") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: ""
                        )
                    )
                }

                // 🔄 Refresh RecyclerView
                canteenAdapter.notifyDataSetChanged()
            }
    }
}
