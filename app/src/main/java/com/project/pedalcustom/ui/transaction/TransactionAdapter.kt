package com.project.pedalcustom.ui.transaction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemTransactionBinding
import java.text.DecimalFormat

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val transactionList = ArrayList<TransactionModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<TransactionModel>) {
        transactionList.clear()
        transactionList.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: TransactionModel) {
            with(binding) {
                val formatter = DecimalFormat("#,###")
                if(model.category != "custom bike") {
                    Glide.with(itemView.context)
                        .load(model.image)
                        .into(image)

                    color.visibility = View.VISIBLE
                    type.visibility = View.VISIBLE
                    qty.visibility = View.VISIBLE

                    type.text = "Type : ${model.type}"
                    color.text = "Color : ${model.color}"
                    qty.text = "Qty : ${model.qty}"
                } else {
                    Glide.with(itemView.context)
                        .load(R.drawable.bike)
                        .into(image)

                    color.visibility = View.GONE
                    qty.visibility = View.GONE
                    type.visibility = View.GONE
                }

                status.text = model.status
                name.text = model.productName
                date.text = model.date
                price.text = "Rp${formatter.format(model.totalPrice)}"

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactionList[position])
    }

    override fun getItemCount(): Int = transactionList.size
}