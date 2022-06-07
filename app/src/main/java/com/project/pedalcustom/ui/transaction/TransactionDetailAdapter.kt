package com.project.pedalcustom.ui.transaction

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.pedalcustom.databinding.ItemSparePartBinding
import com.project.pedalcustom.ui.home.bike_custom.CustomSparePartModel
import com.project.pedalcustom.ui.home.sparepart.SparePartDetailActivity
import java.text.DecimalFormat

class TransactionDetailAdapter : RecyclerView.Adapter<TransactionDetailAdapter.ViewHolder>() {

    private val customSparePartList = ArrayList<CustomSparePartModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<CustomSparePartModel>) {
        customSparePartList.clear()
        customSparePartList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemSparePartBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: CustomSparePartModel) {
            with(binding) {
                val formatter = DecimalFormat("#,###")

                name.text = model.name
                qty.text = "Qty : " + model.qty
                price.text = "Price : Rp" + formatter.format(model.price)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSparePartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customSparePartList[position])
    }

    override fun getItemCount(): Int = customSparePartList.size
}