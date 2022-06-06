package com.project.pedalcustom.ui.home.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemCartBinding
import java.text.DecimalFormat

class CheckoutAdapter(
    private val cartList: ArrayList<CartModel>,
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: CartModel) {
            with(binding) {
                deleteBtn.visibility = View.GONE
                checkbox.visibility = View.GONE
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

                name.text = model.name
                price.text = "Rp${formatter.format(model.totalPrice)}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    override fun getItemCount(): Int = cartList.size
}