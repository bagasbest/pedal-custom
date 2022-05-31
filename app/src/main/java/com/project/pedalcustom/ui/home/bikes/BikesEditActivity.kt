package com.project.pedalcustom.ui.home.bikes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.pedalcustom.databinding.ActivityBikesEditBinding

class BikesEditActivity : AppCompatActivity() {

    private var binding : ActivityBikesEditBinding ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBikesEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}