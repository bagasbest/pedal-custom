package com.project.pedalcustom.ui.home.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.pedalcustom.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private var binding : ActivityProfileBinding ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}