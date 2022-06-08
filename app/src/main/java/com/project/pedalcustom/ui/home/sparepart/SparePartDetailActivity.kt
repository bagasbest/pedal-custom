package com.project.pedalcustom.ui.home.sparepart

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.authentication.LoginActivity
import com.project.pedalcustom.databinding.ActivitySparePartDetailBinding
import com.project.pedalcustom.ui.home.bike_custom.CustomSparePartModel
import com.project.pedalcustom.ui.home.cart.CartActivity
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import java.text.DecimalFormat

class SparePartDetailActivity : AppCompatActivity() {
    private var binding : ActivitySparePartDetailBinding? = null
    private var model: SparePartModel? = null
    private var user : FirebaseUser? = null
    private var listColor = ArrayList<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySparePartDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        model = intent.getParcelableExtra(EXTRA_DATA)
        user = FirebaseAuth.getInstance().currentUser

        initSlider()
        getSparePartColor()
        checkRole()
        val formatter = DecimalFormat("#,###")

        binding?.price?.text = "Rp." + formatter.format(model?.price)
        binding?.code?.text = "#${model?.code}"
        binding?.name?.text = model?.name
        binding?.description?.text = model?.description
        binding?.specification?.text = model?.specification
        binding?.type?.text = "Type: ${model?.type}"
        binding?.sold?.text = "Sold: ${model?.sold}"




        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.cartBtn?.setOnClickListener {
            if(user != null) {
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding?.addToCartBtn?.setOnClickListener {
            if(user != null) {
                formValidation()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent(this, SparePartEditActivity::class.java)
            intent.putExtra(SparePartEditActivity.EXTRA_DATA, model)
            startActivity(intent)
        }

        binding?.delete?.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun formValidation() {
        val qty = binding?.qty?.text.toString().trim()

        if (qty.isEmpty()) {
            Toast.makeText(this, "Minimum 1 qty product filled!", Toast.LENGTH_SHORT).show()
        } else if (qty.toInt() <= 0) {
            Toast.makeText(this, "Minimum 1 qty product filled!", Toast.LENGTH_SHORT).show()
        } else {

            if(listColor.size > 1) {
                showPopupColorProductPicker(qty.toLong())
            } else {
                binding?.progressBar?.visibility = View.VISIBLE

                val cartId = System.currentTimeMillis().toString()
                val totalPrice = qty.toLong().times(model?.price!!)
                val customSparePartList = ArrayList<CustomSparePartModel>()
                val data = mapOf(
                    "uid" to cartId,
                    "userId" to user?.uid,
                    "name" to model?.name,
                    "totalPrice" to totalPrice,
                    "productId" to model?.uid,
                    "customSparePartList" to customSparePartList,
                    "isAssembled" to false,
                    "category" to "spare part",
                    "type" to model?.type,
                    "color" to model?.color,
                    "image" to model?.image!![0],
                    "qty" to qty.toLong(),
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .document(cartId)
                    .set(data)
                    .addOnCompleteListener {
                        binding?.progressBar?.visibility = View.GONE
                        if (it.isSuccessful) {
                            showSuccessDialog()
                        } else {
                            showFailureDialog()
                        }
                    }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showPopupColorProductPicker(qty: Long) {
        val spinner: SearchableSpinner
        val saveBtn: Button
        val title: TextView
        val option: TextView
        val discardBtn: Button
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_color_product)

        spinner = dialog.findViewById(R.id.spinner)
        title = dialog.findViewById(R.id.editText)
        option = dialog.findViewById(R.id.option)
        saveBtn = dialog.findViewById(R.id.save)
        discardBtn = dialog.findViewById(R.id.discard)

        title.text = "Choose Spare Part Color"
        option.text = "color"


        val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, listColor)
        spinner?.adapter = adapter

        discardBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            dialog.dismiss()

            binding?.progressBar?.visibility = View.VISIBLE

            val cartId = System.currentTimeMillis().toString()
            val totalPrice = qty.times(model?.price!!)
            val customSparePartList = ArrayList<CustomSparePartModel>()
            val data = mapOf(
                "uid" to cartId,
                "userId" to user?.uid,
                "name" to model?.name,
                "productId" to model?.uid,
                "totalPrice" to totalPrice,
                "type" to model?.type,
                "customSparePartList" to customSparePartList,
                "isAssembled" to false,
                "category" to "spare part",
                "color" to spinner?.selectedItem.toString(),
                "image" to model?.image!![0],
                "qty" to qty,
            )

            FirebaseFirestore
                .getInstance()
                .collection("cart")
                .document(cartId)
                .set(data)
                .addOnCompleteListener {
                    binding?.progressBar?.visibility = View.GONE
                    if (it.isSuccessful) {
                        showSuccessDialog()
                    } else {
                        showFailureDialog()
                    }
                }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Success Add Spare Part To Cart")
            .setMessage("You can see product on cart page for transaction")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                startActivity(Intent(this, CartActivity::class.java))
            }
            .show()
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Failure Add Spare Part To Cart")
            .setMessage("Ups there problem with your internet connection, please try again later!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete Spare Part")
            .setMessage("Are you sure want to delete this Spare Part ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YES") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deleteSparePart()
            }
            .setNegativeButton("NO", null)
            .show()
    }

    private fun deleteSparePart() {
        FirebaseFirestore
            .getInstance()
            .collection("spare_parts")
            .document(model?.uid!!)
            .delete()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    AlertDialog.Builder(this)
                        .setTitle("Success Delete Spare Part")
                        .setMessage("Operation success")
                        .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                        .setPositiveButton("OK") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            onBackPressed()
                        }
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Failure Delete Spare Part")
                        .setMessage("Ups, your internet connection already trouble, pleas try again later!")
                        .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                        .setPositiveButton("OK") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            onBackPressed()
                        }
                }
            }
    }

    private fun checkRole() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user?.uid!!)
                .get()
                .addOnSuccessListener {
                    val role = "" + it.data!!["role"]
                    if(role == "admin") {
                        binding?.edit?.visibility = View.VISIBLE
                        binding?.delete?.visibility = View.VISIBLE
                    }
                }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getSparePartColor() {
        val listColor: List<String> = model?.color?.split(",")!!.map { it.trim() }
        var words = ""
        for(i in listColor.indices) {

            words = listColor[i]

            val valueTV = TextView(this)
            valueTV.text = words
            valueTV.id = i
            valueTV.setTextColor(resources.getColor(R.color.black))
            valueTV.background = resources.getDrawable(R.drawable.bg_border)
            valueTV.setPadding(20, 5, 20, 5)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginStart = 16
            valueTV.layoutParams = params
            (binding?.llColor as LinearLayout).addView(valueTV)
        }
    }

    private fun initSlider() {
        val imageList: ArrayList<SlideModel> = ArrayList() // Create image list

        for (i in model?.image!!.indices) {
            imageList.add(SlideModel(model?.image!![i], ScaleTypes.CENTER_CROP))
        }

        binding?.sliderImage?.setImageList(imageList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}