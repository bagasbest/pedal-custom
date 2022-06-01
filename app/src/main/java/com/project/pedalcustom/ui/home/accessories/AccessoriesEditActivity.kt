package com.project.pedalcustom.ui.home.accessories

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.pedalcustom.Homepage
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityAccessoriesEditBinding
import com.project.pedalcustom.ui.home.bikes.BikesEditImageAdapter

class AccessoriesEditActivity : AppCompatActivity() {

    private var binding : ActivityAccessoriesEditBinding? = null
    private var model : AccessoriesModel? = null
    private var imageList = ArrayList<String>()
    private var image: String? = null
    private val REQUEST_IMAGE_GALLERY = 1001
    private var imageAdapter : BikesEditImageAdapter? =null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessoriesEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        imageList.addAll(model?.image!!)
        initRecyclerViewImage()

        binding?.imageUploaded?.text = "Image Uploaded : ${model?.image?.size}"
        binding?.name?.setText(model?.name)
        binding?.code?.setText(model?.code)
        binding?.type?.setText(model?.type)
        binding?.color?.setText(model?.code)
        binding?.description?.setText(model?.description)
        binding?.specification?.setText(model?.specification)
        binding?.price?.setText(model?.price.toString())

        Glide.with(this)
            .load(R.drawable.bike_accesories)
            .into(binding!!.imageView2)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.addImage?.setOnClickListener {
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
            Toast.makeText(this, "Accessories name must be filled!", Toast.LENGTH_SHORT).show()
        } else if (code.isEmpty()) {
            Toast.makeText(this, "Accessories code must be filled!", Toast.LENGTH_SHORT).show()
        } else if (type.isEmpty()) {
            Toast.makeText(this, "Accessories type must be filled!", Toast.LENGTH_SHORT).show()
        } else if (color.isEmpty()) {
            Toast.makeText(this, "Accessories color must be filled!", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Accessories description must be filled!", Toast.LENGTH_SHORT).show()
        } else if (spec.isEmpty()) {
            Toast.makeText(this, "Accessories specification must be filled!", Toast.LENGTH_SHORT).show()
        } else if (price.isEmpty()) {
            Toast.makeText(this, "Accessories price must be filled!", Toast.LENGTH_SHORT).show()
        } else if (imageList.size == 0) {
            Toast.makeText(this, "Accessories image must be added!", Toast.LENGTH_SHORT).show()
        } else {

            binding?.progressBar?.visibility = View.VISIBLE

            val data = mapOf(
                "name" to name,
                "code" to code,
                "type" to type,
                "color" to color,
                "description" to description,
                "specification" to spec,
                "price" to price.toLong(),
                "image" to imageList,
            )

            FirebaseFirestore
                .getInstance()
                .collection("accessories")
                .document(model?.uid!!)
                .update(data)
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
            .setTitle("Failure Edit Accessories")
            .setMessage("Ups, your internet connection already trouble, pleas try again later!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Accessories Edit Successfully")
            .setMessage("Accessories will be renew in store")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val intent = Intent(this, Homepage::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
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
        val imageFileName = "Accessories/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        image = uri.toString()

                        imageList.add(image!!)
                        initRecyclerViewImage()
                        binding?.imageUploaded?.text = "New Image uploaded = ${imageList.size}"
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


    private fun initRecyclerViewImage() {
        binding?.rvImage?.layoutManager =
            LinearLayoutManager(this)
        imageAdapter = BikesEditImageAdapter(imageList)
        binding?.rvImage?.adapter = imageAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA ="data"
    }
}