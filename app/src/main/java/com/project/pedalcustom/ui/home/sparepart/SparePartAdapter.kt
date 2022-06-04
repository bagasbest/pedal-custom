package com.project.pedalcustom.ui.home.sparepart

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemBikeBinding
import java.text.DecimalFormat

class SparePartAdapter(private val myUid: String) : RecyclerView.Adapter<SparePartAdapter.ViewHolder>() {

    private val sparePartList = ArrayList<SparePartModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<SparePartModel>) {
        sparePartList.clear()
        sparePartList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemBikeBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: SparePartModel) {
            with(binding) {
                val formatter = DecimalFormat("#,###")
                var isFavorite: Boolean
                name.text = model.name
                price.text = "Rp." + formatter.format(model.price)
                Glide.with(itemView.context)
                    .load(model.image!![0])
                    .into(image)



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
                    val intent = Intent(itemView.context, SparePartDetailActivity::class.java)
                    intent.putExtra(SparePartDetailActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    private fun changeFavorite(model: SparePartModel, isFavorite: Boolean) {
        if(isFavorite) {
            model.favoriteBy?.add(myUid)

            val wishList = mapOf(
                "uid" to model.uid+myUid,
                "userId" to myUid,
                "productId" to model.uid,
                "collection" to "spare_parts",
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
            .collection("spare_parts")
            .document(model.uid!!)
            .update("favoriteBy", model.favoriteBy)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sparePartList[position])
    }

    override fun getItemCount(): Int = sparePartList.size
}