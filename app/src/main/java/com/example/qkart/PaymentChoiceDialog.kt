package com.example.qkart

import android.app.AlertDialog
import android.content.Context

object PaymentChoiceDialog {

    fun show(
        context: Context,
        onCash: () -> Unit,
        onOnline: () -> Unit
    ) {
        val options = arrayOf("Cash on Delivery", "Pay Online")

        AlertDialog.Builder(context)
            .setTitle("Choose Payment Method")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> onCash()
                    1 -> onOnline()
                }
            }
            .setCancelable(true)
            .show()
    }
}
