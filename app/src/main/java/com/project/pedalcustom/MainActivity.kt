package com.project.pedalcustom

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bumptech.glide.Glide
import com.project.pedalcustom.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Glide.with(this)
            .load(R.drawable.splash_black)
            .into(binding!!.splash
            )
        Handler().postDelayed({
            val prefs = getSharedPreferences(
                "role", Context.MODE_PRIVATE
            )
            val role = prefs.getString("role", "user")
            if(role == "user") {
                startActivity(Intent(this, Homepage::class.java))
            } else {
                startActivity(Intent(this, HomepageAdmin::class.java))
            }
            finish()
        }, 3000)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}