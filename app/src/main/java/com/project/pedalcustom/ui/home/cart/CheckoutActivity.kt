package com.project.pedalcustom.ui.home.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.pedalcustom.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {

    private var binding : ActivityCheckoutBinding ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}