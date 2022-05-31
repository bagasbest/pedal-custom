package com.project.pedalcustom.ui.home.accessories

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemBikeBinding
import com.project.pedalcustom.ui.home.bikes.BikesDetailActivity
import com.project.pedalcustom.ui.home.bikes.BikesModel
import java.text.DecimalFormat

class AccessoriesAdapter(private val myUid: String) : RecyclerView.Adapter<AccessoriesAdapter.ViewHolder>() {

    private val accessoriesList = ArrayList<AccessoriesModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<AccessoriesModel>) {
        accessoriesList.clear()
        accessoriesList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemBikeBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: AccessoriesModel) {
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
                    val intent = Intent(itemView.context, AccessoriesDetailActivity::class.java)
                    intent.putExtra(AccessoriesDetailActivity.EXTRA_DATA, model)
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
        holder.bind(accessoriesList[position])
    }

    override fun getItemCount(): Int = accessoriesList.size
}