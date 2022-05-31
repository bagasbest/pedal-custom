package com.project.pedalcustom.ui.home.bikes

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.pedalcustom.Homepage
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityBikesAddBinding

class BikesAddActivity : AppCompatActivity() {

    private var binding : ActivityBikesAddBinding? = null
    private var image: String? = null
    private val REQUEST_IMAGE_GALLERY = 1001
    private var imageList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBikesAddBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Glide.with(this)
            .load(R.drawable.bike)
            .into(binding!!.imageView2)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_IMAGE_GALLERY)
        }

        binding?.uploadBtn?.setOnClickListener {
            formValidation()
        }
    }

    private fun formValidation() {
        val name = binding?.name?.text.toString().trim()
        val code = binding?.code?.text.toString().trim()
        val type = binding?.type?.text.toString().trim()
        val color = binding?.color?.text.toString().trim()
        val description = binding?.description?.text.toString().trim()
        val spec = binding?.specification?.text.toString().trim()
        val price = binding?.price?.text.toString().trim()

        if(name.isEmpty()) {
            Toast.makeText(this, "Bike name must be filled!", Toast.LENGTH_SHORT).show()
        } else if (code.isEmpty()) {
            Toast.makeText(this, "Bike code must be filled!", Toast.LENGTH_SHORT).show()
        } else if (type.isEmpty()) {
            Toast.makeText(this, "Bike type must be filled!", Toast.LENGTH_SHORT).show()
        } else if (color.isEmpty()) {
            Toast.makeText(this, "Bike color must be filled!", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Bike description must be filled!", Toast.LENGTH_SHORT).show()
        } else if (spec.isEmpty()) {
            Toast.makeText(this, "Bike specification must be filled!", Toast.LENGTH_SHORT).show()
        } else if (price.isEmpty()) {
            Toast.makeText(this, "Bike price must be filled!", Toast.LENGTH_SHORT).show()
        } else if (imageList.size == 0) {
            Toast.makeText(this, "Bike image must be added!", Toast.LENGTH_SHORT).show()
        } else {

            binding?.progressBar?.visibility = View.VISIBLE
            val uid = System.currentTimeMillis().toString()
            val myList: ArrayList<String> = ArrayList()

            val data = mapOf(
                "uid" to uid,
                "name" to name,
                "code" to code,
                "type" to type,
                "color" to color,
                "description" to description,
                "specification" to spec,
                "price" to price.toLong(),
                "image" to imageList,
                "sold" to 0,
                "favoriteBy" to myList,
            )

            FirebaseFirestore
                .getInstance()
                .collection("bikes")
                .document(uid)
                .set(data)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        binding?.progressBar?.visibility = View.GONE
                        showSuccessDialog()
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        showFailureDialog()
                    }
                }

        }

    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Failure Add Bikes")
            .setMessage("Ups, your internet connection already trouble, pleas try again later!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Bikes Add Successfully")
            .setMessage("Bikes will be added in store")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                uploadArticleDp(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    @SuppressLint("SetTextI18n")
    private fun uploadArticleDp(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Please wait until process finish...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        val imageFileName = "bike/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        image = uri.toString()
                        Glide.with(this)
                            .load(image)
                            .into(binding!!.image)


                        imageList.add(image!!)
                        binding?.imageUploaded?.text = "Image uploaded = ${imageList.size}"
                    }
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Gagal mengunggah gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            }
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Gagal mengunggah gambar",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}