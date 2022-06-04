package com.project.pedalcustom.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.pedalcustom.R
import com.project.pedalcustom.authentication.LoginActivity
import com.project.pedalcustom.authentication.RegisterActivity
import com.project.pedalcustom.databinding.FragmentHomeBinding
import com.project.pedalcustom.ui.home.accessories.AccessoriesActivity
import com.project.pedalcustom.ui.home.bike_custom.CustomActivity
import com.project.pedalcustom.ui.home.bikes.BikesActivity
import com.project.pedalcustom.ui.home.profile.ProfileActivity
import com.project.pedalcustom.ui.home.sparepart.SparePartActivity
import com.project.pedalcustom.utils.User

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var user : FirebaseUser ? = null
    private var image: String? = null
    private val REQUEST_IMAGE_GALLERY = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        user = FirebaseAuth.getInstance().currentUser

        initView()
        checkIsLoginOrNot()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun checkIsLoginOrNot() {
        if (user != null) {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("Please wait until finish...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val uid = user?.uid
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid!!)
                .get()
                .addOnSuccessListener {
                    val name = "" + it.data!!["name"]
                    val image = "" + it.data!!["image"]

                    binding.username.text = "Welcome,\n$name"
                    if(image != "") {
                        Glide.with(this)
                            .load(image)
                            .into(binding.image)
                    }
                    progressDialog.dismiss()
                }

            binding.signInBtn.text = "Edit Profil"
            binding.signUpBtn.text = "Logout"
        }
    }

    private fun initView() {
        Glide.with(requireContext())
            .load(R.drawable.bike)
            .into(binding.bikeImage)

        Glide.with(requireContext())
            .load(R.drawable.bike_accesories)
            .into(binding.accImage)

        Glide.with(requireContext())
            .load(R.drawable.bike_part)
            .into(binding.sparePartImage)

        Glide.with(requireContext())
            .load(R.drawable.bike)
            .into(binding.customImage)

        Glide.with(requireContext())
            .load(R.drawable.splash_grey)
            .into(binding.splashGrey)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInBtn.setOnClickListener {
            if(user != null) {
                startActivity(Intent(activity, ProfileActivity::class.java))
            } else {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }

        binding.signUpBtn.setOnClickListener {
            if(user != null) {
                FirebaseAuth.getInstance().signOut()
                binding.signInBtn.text = "Sign In"
                binding.signUpBtn.text = "Sign Up"
                Glide.with(requireContext())
                    .load(R.drawable.ic_baseline_account_circle_24)
                    .into(binding.image)
            } else {
                startActivity(Intent(activity, RegisterActivity::class.java))
            }
        }

        binding.item1.setOnClickListener {
            startActivity(Intent(activity, BikesActivity::class.java))
        }

        binding.item2.setOnClickListener {
            startActivity(Intent(activity, CustomActivity::class.java))
        }

        binding.item3.setOnClickListener {
            startActivity(Intent(activity, SparePartActivity::class.java))
        }

        binding.item4.setOnClickListener {
            startActivity(Intent(activity, AccessoriesActivity::class.java))
        }

        binding.image.setOnClickListener {
            if(user != null) {
                ImagePicker.with(this)
                    .galleryOnly()
                    .compress(1024)
                    .start(REQUEST_IMAGE_GALLERY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                uploadArticleDp(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    @SuppressLint("SetTextI18n")
    private fun uploadArticleDp(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        val mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage("Please wait until process finish...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        val imageFileName = "user/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        image = uri.toString()
                        Glide.with(this)
                            .load(image)
                            .into(binding.image)

                       User.saveImageUser(user?.uid, image!!, requireContext())

                    }
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            activity,
                            "Gagal mengunggah gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            }
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    activity,
                    "Gagal mengunggah gambar",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}