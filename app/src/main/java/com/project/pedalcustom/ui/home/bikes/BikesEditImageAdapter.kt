package com.project.pedalcustom.ui.home.bikes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ItemImageEditBinding

class BikesEditImageAdapter(private val imageList: ArrayList<String>) : RecyclerView.Adapter<BikesEditImageAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemImageEditBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(imageData: String) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(imageData)
                    .into(image)

                delete.setOnClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("Confirm Delete Image")
                        .setMessage("Are you sure want to delete this image")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("YES") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            imageList.removeAt(adapterPosition)
                            notifyDataSetChanged()
                        }
                        .setNegativeButton("NO", null)
                        .show()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}