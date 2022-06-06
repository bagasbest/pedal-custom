package com.project.pedalcustom.ui.home.cart

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemCartBinding
import java.text.DecimalFormat

class CartAdapter(
    private val cartList: ArrayList<CartModel>,
    private val llBikes: LinearLayout?,
    private val llAccessories: LinearLayout?,
    private val llSparePart: LinearLayout?,
    private val llCustom: LinearLayout?,
    private val option: String,
    private val isCheckedCategory: Boolean?,
    private var cartToCheckoutList: MutableSet<CartModel>,
    private var cbValidator: String,
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: CartModel) {
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

                name.text = model.name
                price.text = "Rp${formatter.format(model.totalPrice)}"

                if(isCheckedCategory == true) {
                    if (model.category == option) {
                        if (!checkbox.isChecked) {
                            checkbox.isChecked = true
                            cartToCheckoutList.add(model)
                        }
                    }
                } else {
                    if(model.category == cbValidator) {
                        cartToCheckoutList.remove(model)
                    }
                }

                checkbox.setOnClickListener {
                    if(checkbox.isChecked) {
                        checkbox.isChecked = true
                        cartToCheckoutList.add(model)
                    } else {
                        checkbox.isChecked = false
                        cartToCheckoutList.remove(model)
                    }
                }

                deleteBtn.setOnClickListener {
                    FirebaseFirestore
                        .getInstance()
                        .collection("cart")
                        .document(model.uid!!)
                        .delete()
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                cartList.removeAt(adapterPosition)
                                notifyDataSetChanged()

                                if(cartList.size == 0) {
                                    when (option) {
                                        "bikes" -> {
                                            llBikes?.visibility = View.GONE
                                        }
                                        "accessories" -> {
                                            llAccessories?.visibility = View.GONE
                                        }
                                        "spare part" -> {
                                            llSparePart?.visibility = View.GONE
                                        }
                                        "custom bike" -> {
                                            llCustom?.visibility = View.GONE
                                        }
                                    }
                                }

                                Toast.makeText(itemView.context, "Successfully to delete ${model.name} from cart!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(itemView.context, "Failure to delete product from wishlist!", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
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