package com.project.pedalcustom.ui.home.bikes

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemBikeBinding
import java.text.DecimalFormat

class BikesAdapter(private val myUid: String, private val role: String) : RecyclerView.Adapter<BikesAdapter.ViewHolder>() {

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
                var isFavorite: Boolean
                name.text = model.name
                price.text = "Rp." + formatter.format(model.price)
                Glide.with(itemView.context)
                    .load(model.image!![0])
                    .into(image)

                if(myUid == "" || role == "admin") {
                    favorite.visibility = View.GONE
                }


                if(model.favoriteBy!!.contains(myUid)) {
                    isFavorite = true
                    favorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_baseline_favorite_24))
                } else {
                    isFavorite = false
                    favorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_baseline_favorite_border_24))
                }

                favorite.setOnClickListener {
                    if(isFavorite) {
                        changeFavorite(model, false)
                        isFavorite = false
                        favorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_baseline_favorite_border_24))
                    } else {
                        changeFavorite(model, true)
                        isFavorite = true
                        favorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_baseline_favorite_24))
                    }
                }

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, BikesDetailActivity::class.java)
                    intent.putExtra(BikesDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    private fun changeFavorite(model: BikesModel, isFavorite: Boolean) {
        if(isFavorite) {
            model.favoriteBy?.add(myUid)

            val wishList = mapOf(
                "uid" to model.uid+myUid,
                "userId" to myUid,
                "productId" to model.uid,
                "collection" to "bikes",
                "name" to model.name,
                "code" to model.code,
                "type" to model.type,
                "color" to model.color,
                "description" to model.description,
                "specification" to model.specification,
                "price" to model.price,
                "sold" to model.sold,
                "image" to model.image,
                "favoriteBy" to model.favoriteBy,
            )
            FirebaseFirestore
                .getInstance()
                .collection("wishlist")
                .document(model.uid!!+myUid)
                .set(wishList)
        } else {
            model.favoriteBy?.remove(myUid)

            FirebaseFirestore
                .getInstance()
                .collection("wishlist")
                .document(model.uid!!+myUid)
                .delete()
        }

        FirebaseFirestore
            .getInstance()
            .collection("bikes")
            .document(model.uid!!)
            .update("favoriteBy", model.favoriteBy)

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