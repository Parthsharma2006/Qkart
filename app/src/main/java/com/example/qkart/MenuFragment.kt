package com.example.qkart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MenuFragment : Fragment() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private val menuList = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvMenu = view.findViewById(R.id.rvMenu)
        rvMenu.layoutManager = LinearLayoutManager(requireContext())

        menuAdapter = MenuAdapter(menuList)
        rvMenu.adapter = menuAdapter

        val canteenId = arguments?.getString("canteenId") ?: return
        loadMenu(canteenId)
    }

    private fun loadMenu(canteenId: String) {
        FirebaseFirestore.getInstance()
            .collection("canteens")
            .document(canteenId)
            .collection("menu")
            .get()
            .addOnSuccessListener { result ->
                menuList.clear()

                for (doc in result) {
                    menuList.add(
                        MenuItem(
                            name = doc.getString("name") ?: "",
                            price = (doc.get("price") as? Number)?.toDouble() ?: 0.0,
                            imageUrl = doc.getString("imageUrl") ?: ""
                        )
                    )
                }

                menuAdapter.notifyDataSetChanged()
            }
    }
}
