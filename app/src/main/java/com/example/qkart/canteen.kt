package com.example.qkart

data class Canteen(
    val id: String,
    val name: String,
    val rating: Double,
    val category: String,
    val imageUrl: String,
    val menuItems: MutableList<String> = mutableListOf()
)
