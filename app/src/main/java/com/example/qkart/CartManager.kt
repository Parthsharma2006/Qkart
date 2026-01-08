package com.example.qkart

object CartManager {

    private var canteenId: String? = null
    private val cartItems = mutableListOf<CartItem>()

    // ---------------------------
    // CHECK IF CART IS EMPTY
    // ---------------------------
    fun isEmpty(): Boolean {
        return cartItems.isEmpty()
    }

    // ---------------------------
    // CHECK IF DIFFERENT CANTEEN
    // ---------------------------
    fun isDifferentCanteen(newCanteenId: String): Boolean {
        return canteenId != null && canteenId != newCanteenId
    }

    // ---------------------------
    // ADD ITEM TO CART
    // ---------------------------
    fun addItem(newCanteenId: String, item: MenuItem) {

        if (canteenId == null) {
            canteenId = newCanteenId
        }

        if (canteenId != newCanteenId) {
            clearCart()
            canteenId = newCanteenId
        }

        val existingItem = cartItems.find { it.name == item.name }

        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            cartItems.add(
                CartItem(
                    name = item.name,
                    price = item.price, // ✅ DOUBLE (NO toInt)
                    quantity = 1
                )
            )
        }
    }

    // ---------------------------
    // GET CART ITEMS
    // ---------------------------
    fun getItems(): MutableList<CartItem> {
        return cartItems
    }

    // ---------------------------
    // TOTAL ITEM COUNT
    // ---------------------------
    fun getTotalItems(): Int {
        var total = 0
        for (item in cartItems) {
            total += item.quantity
        }
        return total
    }

    // ---------------------------
    // TOTAL PRICE (DOUBLE)
    // ---------------------------
    fun getTotalPrice(): Double {
        var total = 0.0
        for (item in cartItems) {
            total += item.price * item.quantity
        }
        return total
    }

    // Alias (for Razorpay compatibility)
    fun getTotalAmount(): Double {
        return getTotalPrice()
    }

    // ---------------------------
    // UPDATE QUANTITY
    // ---------------------------
    fun updateQuantity(item: CartItem, delta: Int) {
        item.quantity += delta
        if (item.quantity <= 0) {
            cartItems.remove(item)
        }
        if (cartItems.isEmpty()) {
            canteenId = null
        }
    }

    // ---------------------------
    // CLEAR CART
    // ---------------------------
    fun clearCart() {
        cartItems.clear()
        canteenId = null
    }

    // ---------------------------
    // GET CURRENT CANTEEN
    // ---------------------------
    fun getCanteenId(): String? {
        return canteenId
    }
}
