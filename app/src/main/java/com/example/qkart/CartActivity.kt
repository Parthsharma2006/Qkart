package com.example.qkart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class CartActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPlaceOrder: Button

    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(androidx.appcompat.R.drawable.abc_ic_ab_back_material)

        rvCart = findViewById(R.id.rvCart)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        rvCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(CartManager.getItems()){
            updateTotal()
        }
        rvCart.adapter = cartAdapter

        updateTotal()

        Checkout.preload(applicationContext)

        btnPlaceOrder.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            PaymentChoiceDialog.show(
                this,
                onCash = {
                    placeOrder(
                        paymentMode = "CASH",
                        paymentStatus = "PENDING"
                    )
                },
                onOnline = {
                    startRazorpayPayment()
                }
            )
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun updateTotal() {
        tvTotalAmount.text = "Total: ₹${CartManager.getTotalAmount().toInt()}"
    }

    // ---------------------------
    // RAZORPAY PAYMENT
    // ---------------------------

    private fun startRazorpayPayment() {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_XXXXXXXXXXXXXXXX") // TEST KEY

        val options = JSONObject()
        options.put("name", "QKart")
        options.put("description", "Canteen Food Order")
        options.put("currency", "INR")
        options.put("amount", (CartManager.getTotalAmount() * 100).toInt())

        try {
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Payment error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        placeOrder(
            paymentMode = "ONLINE",
            paymentStatus = "PAID"
        )
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
    }

    // ---------------------------
    // PLACE ORDER (🔥 FIXED FIELDS)
    // ---------------------------

    private fun placeOrder(
        paymentMode: String,
        paymentStatus: String
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val canteenId = CartManager.getCanteenId()

        if (canteenId == null) {
            Toast.makeText(this, "Canteen not found", Toast.LENGTH_SHORT).show()
            return
        }

        val orderData = hashMapOf(
            "userId" to userId,
            "canteenId" to canteenId,

            // 🔥 FIXED: qty instead of quantity
            "items" to CartManager.getItems().map {
                mapOf(
                    "name" to it.name,
                    "price" to it.price,
                    "qty" to it.quantity
                )
            },

            // 🔥 FIXED: totalAmount instead of totalPrice
            "totalAmount" to CartManager.getTotalAmount(),

            "paymentMode" to paymentMode,
            "paymentStatus" to paymentStatus,
            "status" to "Pending",

            // 🔥 Matches OrdersFragment
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("orders")
            .add(orderData)
            .addOnSuccessListener {

                CartManager.clearCart()

                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("openOrders", true)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
            }
    }
}
