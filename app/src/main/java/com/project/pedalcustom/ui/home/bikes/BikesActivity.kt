package com.project.pedalcustom.ui.home.bikes

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
import com.project.pedalcustom.authentication.LoginActivity
import com.project.pedalcustom.databinding.ActivityBikesBinding
import com.project.pedalcustom.ui.home.cart.CartActivity

class BikesActivity : AppCompatActivity() {


    private var binding: ActivityBikesBinding? = null
    private var adapter: BikesAdapter? = null
    private var user : FirebaseUser? = null
    private var uid = ""

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBikesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Glide.with(this)
            .load(R.drawable.bike)
            .into(binding!!.imageView2)

        user = FirebaseAuth.getInstance().currentUser

        checkRole()
        binding?.backButton?.setOnClickListener { onBackPressed() }
        binding?.addBikeBtn?.setOnClickListener {
            startActivity(Intent(this, BikesAddActivity::class.java))
        }

        binding?.cartBtn?.setOnClickListener {
            if(user != null) {
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
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
                        binding?.addBikeBtn?.visibility = View.VISIBLE
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
        adapter = BikesAdapter(uid)
        binding?.recyclerView?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[BikesViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListBikes()
        viewModel.getBikes().observe(this) { bikesList ->
            if (bikesList.size > 0) {
                adapter?.setData(bikesList)
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