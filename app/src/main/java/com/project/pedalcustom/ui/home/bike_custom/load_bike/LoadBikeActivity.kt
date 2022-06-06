package com.project.pedalcustom.ui.home.bike_custom.load_bike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityLoadBikeBinding

class LoadBikeActivity : AppCompatActivity() {

    private var binding : ActivityLoadBikeBinding? = null
    private var adapter : LoadBikeAdapter ? = null
    private var loadDataList = ArrayList<LoadBikeModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadBikeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Glide.with(this)
            .load(R.drawable.bike)
            .into(binding!!.imageView2)

        initViewModel()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        binding?.recyclerView?.layoutManager =
            LinearLayoutManager(this)
        adapter = LoadBikeAdapter(loadDataList)
        binding?.recyclerView?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[LoadBikeViewModel::class.java]

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListSaveData(uid)
        viewModel.getSaveData().observe(this) { savesDataList ->
            if (savesDataList.size > 0) {
                binding?.noData?.visibility = View.GONE
                loadDataList.clear()
                loadDataList.addAll(savesDataList)
                initRecyclerView()
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}