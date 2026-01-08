package com.example.qkart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (user != null) {
            if (AdminUtils.isAdmin(email)) {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
            return
        }

        // 🔥 2. ONLY SHOW LOGIN UI IF NOT LOGGED IN
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignup = findViewById<TextView>(R.id.tvSignup)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val user = FirebaseAuth.getInstance().currentUser
                val userEmail = user?.email

                // 🔥 Save FCM token
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { token ->
                        user?.uid?.let { uid ->
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .set(mapOf("fcmToken" to token))
                        }
                    }

                // 🔥 ADMIN vs USER ROUTING
                if (AdminUtils.isAdmin(userEmail)) {

                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    intent.putExtra(
                        "canteenId",
                        AdminUtils.getCanteenId(userEmail)
                    )
                    startActivity(intent)

                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }

                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
    }
}
