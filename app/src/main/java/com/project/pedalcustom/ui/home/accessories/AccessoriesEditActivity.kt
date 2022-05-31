package com.project.pedalcustom.ui.home.accessories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.pedalcustom.databinding.ActivityAccessoriesEditBinding

class AccessoriesEditActivity : AppCompatActivity() {

    private var binding : ActivityAccessoriesEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessoriesEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}