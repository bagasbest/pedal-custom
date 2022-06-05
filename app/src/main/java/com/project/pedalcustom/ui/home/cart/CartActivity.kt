package com.project.pedalcustom.ui.home.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {

    private var binding: ActivityCartBinding? = null
    private var adapter: CartAdapter? = null
    private var cartList = ArrayList<CartModel>()
    private var uid = FirebaseAuth.getInstance().currentUser!!.uid
    private var isCheckedAccessories = false
    private var isCheckedBikes = false
    private var isCheckedSparePart = false
    private var isCheckedCustomBike = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.rvBikes?.isNestedScrollingEnabled = false
        binding?.rvAccessories?.isNestedScrollingEnabled = false
        binding?.rvSparePart?.isNestedScrollingEnabled = false
        binding?.rvCustom?.isNestedScrollingEnabled = false
        initViewModel()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        val bikesList: ArrayList<CartModel> =
            cartList.filter { cartModel -> cartModel.category == "bikes" } as ArrayList<CartModel>
        if (bikesList.isNotEmpty()) {
            binding?.llBikes?.visibility = View.VISIBLE
            binding?.rvBikes?.layoutManager =
                LinearLayoutManager(this)
            adapter = CartAdapter(
                bikesList,
                binding?.llBikes,
                binding?.llAccessories,
                binding?.llSparePart,
                binding?.llCustom,
                "bikes",
                isCheckedBikes,
            )
            binding?.rvBikes?.adapter = adapter
        }

        val accessoriesList: ArrayList<CartModel> =
            cartList.filter { cartModel -> cartModel.category == "accessories" } as ArrayList<CartModel>
        if (accessoriesList.isNotEmpty()) {
            binding?.llAccessories?.visibility = View.VISIBLE
            binding?.rvAccessories?.layoutManager =
                LinearLayoutManager(this)
            adapter = CartAdapter(
                accessoriesList,
                binding?.llBikes,
                binding?.llAccessories,
                binding?.llSparePart,
                binding?.llCustom,
                "accessories",
                isCheckedAccessories,
            )
            binding?.rvAccessories?.adapter = adapter
        }

        val sparePartList: ArrayList<CartModel> =
            cartList.filter { cartModel -> cartModel.category == "spare part" } as ArrayList<CartModel>
        if (sparePartList.isNotEmpty()) {
            binding?.llSparePart?.visibility = View.VISIBLE
            binding?.rvSparePart?.layoutManager =
                LinearLayoutManager(this)
            adapter = CartAdapter(
                sparePartList,
                binding?.llBikes,
                binding?.llAccessories,
                binding?.llSparePart,
                binding?.llCustom,
                "spare part",
                isCheckedSparePart,
            )
            binding?.rvSparePart?.adapter = adapter
        }

        val customBikeList: ArrayList<CartModel> =
            cartList.filter { cartModel -> cartModel.category == "custom bike" } as ArrayList<CartModel>
        if (customBikeList.isNotEmpty()) {
            binding?.llCustom?.visibility = View.VISIBLE
            binding?.rvCustom?.layoutManager =
                LinearLayoutManager(this)
            adapter = CartAdapter(
                customBikeList,
                binding?.llBikes,
                binding?.llAccessories,
                binding?.llSparePart,
                binding?.llCustom,
                "custom bike",
                isCheckedCustomBike,
            )
            binding?.rvCustom?.adapter = adapter
        }

    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setCartList(uid)
        viewModel.getCart().observe(this) { cl ->
            if (cl.size > 0) {
                cartList.clear()
                cartList.addAll(cl)
                initRecyclerView()
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.cbAllAccessories -> {
                    isCheckedAccessories = checked
                    initRecyclerView()
                }
                R.id.cbAllBikes -> {
                    isCheckedBikes = checked
                    initRecyclerView()
                }
                R.id.cbAllCustomBike -> {
                   isCheckedCustomBike = checked
                    initRecyclerView()
                }
                R.id.cbAllSparePart -> {
                   isCheckedSparePart = checked
                    initRecyclerView()
                }

            }
        }
    }
}