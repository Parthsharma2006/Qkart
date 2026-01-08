package com.example.qkart

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private val canteenList = mutableListOf<Canteen>()
    private val allCanteens = mutableListOf<Canteen>()
    private lateinit var adapter: CanteenAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ---------------- CART ICON ----------------
        val ivCart = view.findViewById<ImageView>(R.id.ivCart)
        val tvCartBadge = view.findViewById<TextView>(R.id.tvCartBadge)

        val cartCount = CartManager.getTotalItems()
        if (cartCount > 0) {
            tvCartBadge.text = cartCount.toString()
            tvCartBadge.visibility = View.VISIBLE
        } else {
            tvCartBadge.visibility = View.GONE
        }

        ivCart.setOnClickListener {
            if (CartManager.isEmpty()) {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(requireContext(), CartActivity::class.java))
            }
        }

        // ---------------- RECYCLER VIEW ----------------
        val rv = view.findViewById<RecyclerView>(R.id.rvCanteens)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = CanteenAdapter(requireContext(), canteenList)
        rv.adapter = adapter

        // ---------------- SEARCH ----------------
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCanteens(s.toString())
            }
        })

        // ---------------- FETCH CANTEENS ----------------
        FirebaseFirestore.getInstance()
            .collection("canteens")
            .get()
            .addOnSuccessListener { result ->

                canteenList.clear()
                allCanteens.clear()

                for (doc in result) {

                    val canteen = Canteen(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        rating = doc.getDouble("rating") ?: 0.0,
                        category = doc.getString("category") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )

                    allCanteens.add(canteen)

                    // 🔥 FETCH MENU SUBCOLLECTION
                    FirebaseFirestore.getInstance()
                        .collection("canteens")
                        .document(doc.id)
                        .collection("menu")
                        .get()
                        .addOnSuccessListener { menuSnap ->

                            canteen.menuItems.clear()
                            for (menuDoc in menuSnap) {
                                canteen.menuItems.add(
                                    menuDoc.getString("name") ?: ""
                                )
                            }

                            // Update visible list after menu loads
                            canteenList.clear()
                            canteenList.addAll(allCanteens)
                            adapter.notifyDataSetChanged()
                        }
                }
            }
    }

    // ---------------- SEARCH LOGIC ----------------
    private fun filterCanteens(query: String) {
        val q = query.trim().lowercase()

        canteenList.clear()

        if (q.isEmpty()) {
            canteenList.addAll(allCanteens)
        } else {
            allCanteens.forEach { canteen ->

                val matchInMenu = canteen.menuItems.any {
                    it.lowercase().contains(q)
                }

                val match =
                    canteen.name.lowercase().contains(q) ||
                            canteen.category.lowercase().contains(q) ||
                            matchInMenu

                if (match) {
                    canteenList.add(canteen)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }
}
