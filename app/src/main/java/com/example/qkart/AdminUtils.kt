package com.example.qkart

import com.google.firebase.auth.FirebaseAuth

object AdminUtils {

    // 🔐 Admin email → canteenId mapping
    private val adminMap = mapOf(
        "fresspresso@qkart.com" to "fresspresso",
        "ramdhani@qkart.com" to "ramdhani_tea_stall"
        // add more canteens here later
    )

    // 🔍 Check if given email is admin
    fun isAdmin(email: String?): Boolean {
        if (email.isNullOrEmpty()) return false
        return adminMap.containsKey(email.lowercase())
    }

    // 🔥 USED BY AdminDashboardActivity
    // Gets canteenId of currently logged-in admin
    fun getAdminCanteenId(): String? {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email.isNullOrEmpty()) return null
        return adminMap[email.lowercase()]
    }

    // (Optional helper if needed elsewhere)
    fun getCanteenId(email: String?): String? {
        if (email.isNullOrEmpty()) return null
        return adminMap[email.lowercase()]
    }
}
