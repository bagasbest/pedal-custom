package com.project.pedalcustom.ui.home.accessories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityAccessoriesBinding

class AccessoriesActivity : AppCompatActivity() {

    private var binding : ActivityAccessoriesBinding ? = null
    private var adapter: AccessoriesAdapter? = null
    private var uid = ""
    private var user : FirebaseUser? = null


    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessoriesBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        Glide.with(this)
            .load(R.drawable.bike_accesories)
            .into(binding!!.imageView2)

        user = FirebaseAuth.getInstance().currentUser

        checkRole()
        binding?.backButton?.setOnClickListener { onBackPressed() }
        binding?.addAccessoriesBtn?.setOnClickListener {
            startActivity(Intent(this, AccessoriesAddActivity::class.java))
        }

    }

    private fun checkRole() {
        if(user != null) {
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user!!.uid)
                .get()
                .addOnSuccessListener {
                    val role = "" + it.data!!["role"]
                    if(role == "admin") {
                        binding?.addAccessoriesBtn?.visibility = View.VISIBLE
                    }
                }
        }
    }

    private fun initRecyclerView() {

        if (user != null) {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
        }

        binding?.recyclerView?.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        adapter = AccessoriesAdapter(uid)
        binding?.recyclerView?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[AccessoriesViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListAccessories()
        viewModel.getAccessories().observe(this) { accessoriesList ->
            if (accessoriesList.size > 0) {
                adapter?.setData(accessoriesList)
                binding?.noData?.visibility = View.GONE
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