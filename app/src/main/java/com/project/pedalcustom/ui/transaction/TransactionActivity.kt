package com.project.pedalcustom.ui.transaction

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityTransactionBinding
import java.text.DecimalFormat

class TransactionActivity : AppCompatActivity() {

    private var binding : ActivityTransactionBinding? = null
    private var model : TransactionModel ? = null
    private var adapter : TransactionDetailAdapter ? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)

        if(model?.category == "custom bike"){
            binding?.customBikeRv?.visibility = View.VISIBLE
            Glide.with(this)
                .load(R.drawable.bike)
                .into(binding!!.image)

            val isAssembled = model?.isAssembled
            if(isAssembled == true) {
                binding?.assembled?.text = "Assembled ON"
            } else {
                binding?.assembled?.text = "Assembled OFF"
            }

            initRecyclerView()
        } else {
            Glide.with(this)
                .load(model?.image)
                .into(binding!!.image)
        }

        binding?.address?.text = model?.userAddress
        binding?.userName?.text = "Name : ${model?.userName}"
        binding?.userPhone?.text = "Phone Number : ${model?.userPhone}"
        binding?.qty?.text = "Qty : ${model?.qty}"
        binding?.productName?.text = model?.productName
        binding?.date?.text = "Date : " + model?.date
        binding?.type?.text = "Type : " + model?.type


        Glide.with(this)
            .load(model?.paymentProof)
            .into(binding!!.paymentProof)

        val formatter = DecimalFormat("#,###")
        binding?.price?.text = "Price : Rp${formatter.format(model?.totalPrice)}"

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }


    }

    private fun initRecyclerView() {
        binding?.rvSparePart?.layoutManager =
            LinearLayoutManager(this)
        adapter = TransactionDetailAdapter()
        binding?.rvSparePart?.adapter = adapter
        adapter!!.setData(model?.customSparePartList!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}