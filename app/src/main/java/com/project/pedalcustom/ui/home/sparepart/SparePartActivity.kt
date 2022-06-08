package com.project.pedalcustom.ui.home.sparepart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.project.pedalcustom.databinding.ActivitySparepartBinding
import com.project.pedalcustom.ui.home.cart.CartActivity
import java.util.*

class SparePartActivity : AppCompatActivity() {
    private var binding : ActivitySparepartBinding? = null
    private var adapter: SparePartAdapter? = null
    private var user : FirebaseUser? = null
    private var uid = ""

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel("all")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySparepartBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        Glide.with(this)
            .load(R.drawable.bike_part)
            .into(binding!!.imageView2)

        user = FirebaseAuth.getInstance().currentUser

        checkRole()
        binding?.backButton?.setOnClickListener { onBackPressed() }
        binding?.addSparePartBtn?.setOnClickListener {
            startActivity(Intent(this, SparePartAddActivity::class.java))
        }

        binding?.cartBtn?.setOnClickListener {
            if(user != null) {
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding?.search?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString().isNotEmpty()) {
                    val query = p0.toString().toLowerCase(Locale.ROOT)
                    initRecyclerView()
                    initViewModel(query)
                } else {
                    initRecyclerView()
                    initViewModel("all")
                }
            }

        })

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
                        binding?.addSparePartBtn?.visibility = View.VISIBLE
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
        adapter = SparePartAdapter(uid)
        binding?.recyclerView?.adapter = adapter
    }

    private fun initViewModel(query : String) {
        val viewModel = ViewModelProvider(this)[SparePartViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        if(query == "all") {
            viewModel.setListSparePart()
        } else {
            viewModel.setListSparePartByQuery(query)
        }
        viewModel.getSparePart().observe(this) { sparePartList ->
            if (sparePartList.size > 0) {
                adapter?.setData(sparePartList)
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