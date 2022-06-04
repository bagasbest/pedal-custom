package com.project.pedalcustom.ui.home.bike_custom.load_bike

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.pedalcustom.databinding.ItemLoadBikeBinding
import com.project.pedalcustom.ui.home.bike_custom.CustomActivity
import java.text.DecimalFormat

class LoadBikeAdapter : RecyclerView.Adapter<LoadBikeAdapter.ViewHolder>() {

    private val loadBikeList = ArrayList<LoadBikeModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<LoadBikeModel>) {
        loadBikeList.clear()
        loadBikeList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemLoadBikeBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: LoadBikeModel) {
            with(binding) {
                val formatter = DecimalFormat("#,###")
                name.text = model.saveName
                bikeType.text = "Bike Type = ${model.bikeType}"
                totalPrice.text = "Total = Rp${formatter.format(model.totalPrice)}"

                number.text = "${adapterPosition+1}"

                loadBtn.setOnClickListener {
                    val intent = Intent(itemView.context, CustomActivity::class.java)
                    intent.putExtra(CustomActivity.EXTRA_DATA, model)
                    intent.putExtra(CustomActivity.OPTION, "load")
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoadBikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(loadBikeList[position])
    }

    override fun getItemCount(): Int = loadBikeList.size
}