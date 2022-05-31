package com.project.pedalcustom.ui.home.bikes

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemBikeBinding
import java.text.DecimalFormat

class BikesAdapter(private val myUid: String) : RecyclerView.Adapter<BikesAdapter.ViewHolder>() {

    private val bikesList = ArrayList<BikesModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<BikesModel>) {
        bikesList.clear()
        bikesList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemBikeBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: BikesModel) {
            with(binding) {
                val formatter = DecimalFormat("#,###")
                name.text = model.name
                price.text = "Rp." + formatter.format(model.price)
                Glide.with(itemView.context)
                    .load(model.image!![0])
                    .into(image)



                if(model.favoriteBy!!.contains(myUid)) {
                    favorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_baseline_favorite_24))
                } else {
                    favorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_baseline_favorite_border_24))
                }

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, BikesDetailActivity::class.java)
                    intent.putExtra(BikesDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bikesList[position])
    }

    override fun getItemCount(): Int = bikesList.size
}