package com.project.pedalcustom.ui.transaction

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemTransactionBinding
import com.project.pedalcustom.ui.home.bike_custom.CustomSparePartModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TransactionAdapter(private val role: String) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val transactionList = ArrayList<TransactionModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<TransactionModel>) {
        transactionList.clear()
        transactionList.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(
            model: TransactionModel,
            transactionList: ArrayList<TransactionModel>,
            position: Int
        ) {
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

                if(role == "admin") {
                    if(model.status == "On Process") {
                        deliver.visibility = View.VISIBLE
                        cancel.visibility = View.VISIBLE
                    }
                } else {
                    if(model.status == "On Delivery" && model.rating == 0.0) {
                        cancel.visibility = View.VISIBLE
                        cancel.text = "Done"
                    }
                }

                status.text = model.status
                name.text = model.productName
                date.text = model.date
                price.text = "Rp${formatter.format(model.totalPrice)}"

                deliver.setOnClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("Confirm Deliver Transaction")
                        .setMessage("Are you sure want to deliver ${model.productName} ?")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("YES") { dialogInterface, _ ->
                            dialogInterface.dismiss()

                            if(model.status == "On Process") {
                                updateStatus("On Delivery", model, itemView.context, transactionList, position)
                            }
                        }
                        .setNegativeButton("NO", null)
                        .show()
                }

                cancel.setOnClickListener {
                    if(role == "admin") {
                        AlertDialog.Builder(itemView.context)
                            .setTitle("Confirm Cancel Transaction")
                            .setMessage("Are you sure want to cancel ${model.productName} ?")
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setPositiveButton("YES") { dialogInterface, _ ->
                                dialogInterface.dismiss()

                                FirebaseFirestore
                                    .getInstance()
                                    .collection("transaction")
                                    .document(model.uid!!)
                                    .delete()
                                    .addOnCompleteListener {
                                        if(it.isSuccessful) {
                                            transactionList.removeAt(adapterPosition)
                                            notifyDataSetChanged()
                                            Toast.makeText(itemView.context, "Successfully cancel transaction, this item will be delete!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                            .setNegativeButton("NO", null)
                            .show()
                    } else {
                        val btnSubmitRating: Button
                        val btnDismiss: Button
                        val pb: ProgressBar
                        val ratingBar: RatingBar

                        val dialog = Dialog(itemView.context)

                        dialog.setContentView(R.layout.popup_rating)
                        dialog.setCanceledOnTouchOutside(false)

                        btnSubmitRating = dialog.findViewById(R.id.submit)
                        btnDismiss = dialog.findViewById(R.id.skip)
                        pb = dialog.findViewById(R.id.progressBar)
                        ratingBar = dialog.findViewById(R.id.ratingBar)


                        btnSubmitRating.setOnClickListener { _ ->
                            if (ratingBar.rating.toDouble() == 0.0) {
                                Toast.makeText(
                                    itemView.context,
                                    "Sorry, you must give rating from 1 - 5",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }  else {
                                pb.visibility = View.VISIBLE
                                updateStatus("Delivered", model, itemView.context, transactionList, position)
                                updateSellProduct(model.productId, model.category, model.customSparePartList)
                                createLogTransaction(model.productName, model.category)

                                Handler().postDelayed({
                                    FirebaseFirestore
                                        .getInstance()
                                        .collection("transaction")
                                        .document(model.uid!!)
                                        .update("rating", ratingBar.rating.toDouble())
                                        .addOnCompleteListener {
                                            if(it.isSuccessful) {
                                                dialog.dismiss()
                                                pb.visibility = View.GONE
                                                deliver.visibility = View.GONE
                                                Toast.makeText(itemView.context, "Successfully give rating", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                },2000)
                            }
                        }

                        btnDismiss.setOnClickListener { dialog.dismiss() }
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.show()
                    }
                }

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, TransactionActivity::class.java)
                    intent.putExtra(TransactionActivity.EXTRA_DATA, model)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    private fun createLogTransaction(productName: String?, category: String?) {
        val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val date = Date()
        val month: String = monthFormat.format(date)
        val year: String = yearFormat.format(date)
        val dates: String = dateFormat.format(date)
        val timeInMillis = date.time

        val data = mapOf(
            "timeInMillis" to timeInMillis,
            "date" to dates.toLong(),
            "month" to month.toLong(),
            "year" to year.toLong(),
            "productName" to productName,
            "category" to category,
        )

        FirebaseFirestore
            .getInstance()
            .collection("log_transaction")
            .document(timeInMillis.toString())
            .set(data)
    }

    private fun updateSellProduct(
        productId: String?,
        category: String?,
        customSparePartList: ArrayList<CustomSparePartModel>?
    ) {
        var categories = category
        if(categories != "custom bike") {
            if(categories == "spare part") {
                categories = "spare_parts"
            }
            FirebaseFirestore
                .getInstance()
                .collection(categories!!)
                .document(productId!!)
                .get()
                .addOnSuccessListener {
                    val sold = it.data?.get("sold") as Long

                    FirebaseFirestore
                        .getInstance()
                        .collection(categories)
                        .document(productId)
                        .update("sold", sold + 1)
                }

        } else {
            for(i in customSparePartList?.indices!!) {
                FirebaseFirestore
                    .getInstance()
                    .collection("spare_parts")
                    .document(customSparePartList[i].productId!!)
                    .get()
                    .addOnSuccessListener {
                        FirebaseFirestore
                            .getInstance()
                            .collection("spare_parts")
                            .document(customSparePartList[i].productId!!)
                            .update("sold", it.data?.get("sold") as Long + 1)
                    }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateStatus(
        status: String,
        model: TransactionModel,
        context: Context?,
        transactionList: ArrayList<TransactionModel>,
        position: Int
    ) {
        FirebaseFirestore
            .getInstance()
            .collection("transaction")
            .document(model.uid!!)
            .update("status", status)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    transactionList.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(context, "Successfully update status into $status", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failure update status into $status", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactionList[position], transactionList, position)
    }

    override fun getItemCount(): Int = transactionList.size
}