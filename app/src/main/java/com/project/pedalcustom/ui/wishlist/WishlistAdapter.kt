package com.project.pedalcustom.ui.wishlist

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemWishlistBinding
import com.project.pedalcustom.ui.home.bike_custom.CustomSparePartModel
import com.project.pedalcustom.ui.home.cart.CartActivity
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import java.text.DecimalFormat

class WishlistAdapter(private val wishList: ArrayList<WishlistModel>, private val myUid: String) :
    RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: WishlistModel) {
            with(binding) {
                val formatter = DecimalFormat("#,###")
                val listColor: ArrayList<String> = model.color?.split(",")!!.map { it.trim() } as ArrayList<String>
                
                name.text = model.name
                price.text = "Rp." + formatter.format(model.price)
                Glide.with(itemView.context)
                    .load(model.image!![0])
                    .into(image)

                cartBtn.setOnClickListener {
                    if(listColor.size > 1) {
                        showPopupColorProductPicker(itemView.context, listColor, model)
                    } else {
                        val cartId = System.currentTimeMillis().toString()
                        val userId = FirebaseAuth.getInstance().currentUser!!.uid
                        val customSparePartList = ArrayList<CustomSparePartModel>()
                        val data = mapOf(
                            "uid" to cartId,
                            "productId" to model.uid,
                            "userId" to userId,
                            "name" to model.name,
                            "totalPrice" to model.price,
                            "customSparePartList" to customSparePartList,
                            "isAssembled" to false,
                            "category" to "bikes",
                            "type" to model.type,
                            "color" to model.color,
                            "image" to model.image!![0],
                            "qty" to 1,
                        )

                        FirebaseFirestore
                            .getInstance()
                            .collection("cart")
                            .document(cartId)
                            .set(data)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    showSuccessDialog(itemView.context)
                                } else {
                                    showFailureDialog(itemView.context)
                                }
                            }
                    }
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

    @SuppressLint("SetTextI18n")
    private fun showPopupColorProductPicker(
        context: Context,
        listColor: ArrayList<String>,
        model: WishlistModel
    ) {
        val spinner: SearchableSpinner
        val saveBtn: Button
        val title: TextView
        val option: TextView
        val discardBtn: Button
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_color_product)

        spinner = dialog.findViewById(R.id.spinner)
        title = dialog.findViewById(R.id.editText)
        option = dialog.findViewById(R.id.option)
        saveBtn = dialog.findViewById(R.id.save)
        discardBtn = dialog.findViewById(R.id.discard)

        title.text = "Choose Product Color"
        option.text = "Color"

        val adapter = ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, listColor)
        spinner?.adapter = adapter

        discardBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            dialog.dismiss()
            
            val cartId = System.currentTimeMillis().toString()
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val customSparePartList = ArrayList<CustomSparePartModel>()
            val data = mapOf(
                "uid" to cartId,
                "userId" to userId,
                "productId" to model.uid,
                "name" to model.name,
                "totalPrice" to model.price,
                "customSparePartList" to customSparePartList,
                "isAssembled" to false,
                "category" to "bikes",
                "type" to model.type,
                "color" to spinner?.selectedItem.toString(),
                "image" to model.image!![0],
                "qty" to 1,
            )

            FirebaseFirestore
                .getInstance()
                .collection("cart")
                .document(cartId)
                .set(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showSuccessDialog(context)
                    } else {
                        showFailureDialog(context)
                    }
                }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showSuccessDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Success Add Product To Cart")
            .setMessage("You can see product on cart page for transaction")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                context.startActivity(Intent(context, CartActivity::class.java))
            }
            .show()
    }

    private fun showFailureDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Failure Add Product To Cart")
            .setMessage("Ups there problem with your internet connection, please try again later!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
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