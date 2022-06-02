package com.project.pedalcustom.ui.wishlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.databinding.ItemWishlistBinding
import java.text.DecimalFormat

class WishlistAdapter(private val wishList: ArrayList<WishlistModel>, private val myUid: String) :
    RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: WishlistModel) {
            with(binding) {
                val formatter = DecimalFormat("#,###")
                name.text = model.name
                price.text = "Rp." + formatter.format(model.price)
                Glide.with(itemView.context)
                    .load(model.image!![0])
                    .into(image)

                cartBtn.setOnClickListener {

                }

                deleteBtn.setOnClickListener {
                    FirebaseFirestore
                        .getInstance()
                        .collection("wishlist")
                        .document(model.uid!!)
                        .delete()
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                model.favoriteBy?.remove(myUid)

                                FirebaseFirestore
                                    .getInstance()
                                    .collection(model.collection!!)
                                    .document(model.productId!!)
                                    .update("favoriteBy", model.favoriteBy)
                                    .addOnCompleteListener {
                                        if(it.isSuccessful) {
                                            wishList.removeAt(adapterPosition)
                                            notifyDataSetChanged()
                                            Toast.makeText(itemView.context, "Success delete product from wishlist!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(itemView.context, "Failure to delete product from wishlist!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(itemView.context, "Failure to delete product from wishlist!", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wishList[position])
    }

    override fun getItemCount(): Int = wishList.size
}