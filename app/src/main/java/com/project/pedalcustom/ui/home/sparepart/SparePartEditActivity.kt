package com.project.pedalcustom.ui.home.sparepart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.pedalcustom.databinding.ActivitySparePartEditBinding

class SparePartEditActivity : AppCompatActivity() {
    private var binding : ActivitySparePartEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySparePartEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}