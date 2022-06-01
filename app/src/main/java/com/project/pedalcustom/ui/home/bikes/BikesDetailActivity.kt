package com.project.pedalcustom.ui.home.bikes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.Homepage
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityBikesDetailBinding
import java.text.DecimalFormat


class BikesDetailActivity : AppCompatActivity() {

    private var binding: ActivityBikesDetailBinding? = null
    private var model: BikesModel? = null
    private var uid = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBikesDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        initSlider()
        getBikeColor()
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

        binding?.addToCartBtn?.setOnClickListener {

        }

        binding?.edit?.setOnClickListener {
            val intent = Intent(this, BikesEditActivity::class.java)
            intent.putExtra(BikesEditActivity.EXTRA_DATA, model)
            startActivity(intent)
        }

        binding?.delete?.setOnClickListener {
            showDeleteDialog()
        }

    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete Bikes")
            .setMessage("Are you sure want to delete this bike")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YES") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deleteBike()
            }
            .setNegativeButton("NO", null)
            .show()
    }

    private fun deleteBike() {
        FirebaseFirestore
            .getInstance()
            .collection("bikes")
            .document(model?.uid!!)
            .delete()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    AlertDialog.Builder(this)
                        .setTitle("Success Delete Bike")
                        .setMessage("Operation success")
                        .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                        .setPositiveButton("OK") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            onBackPressed()
                        }
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Failure Delete Bike")
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
            uid = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
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
    private fun getBikeColor() {
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