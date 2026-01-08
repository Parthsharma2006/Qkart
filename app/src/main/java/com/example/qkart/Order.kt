package com.example.qkart

data class Order(
    val id: String = "",
    val canteenId: String = "",
    val status: String = "",
    val totalAmount: Int = 0,
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val name: String = "",
    val qty: Int = 0,
    val price: Int = 0
)
