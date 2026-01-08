package com.example.qkart

data class AdminOrder(
    val orderId: String,
    val items: List<String>,      // 🔥 item names with qty
    val totalPrice: Double,
    val paymentMode: String,
    val paymentStatus: String,
    val status: String,
    val timestamp: Long
)
