package com.project.pedalcustom.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.authentication.LoginActivity
import com.project.pedalcustom.authentication.RegisterActivity
import com.project.pedalcustom.databinding.FragmentHomeBinding
import com.project.pedalcustom.ui.home.accessories.AccessoriesActivity
import com.project.pedalcustom.ui.home.bikes.BikesActivity
import com.project.pedalcustom.ui.home.profile.ProfileActivity
import com.project.pedalcustom.ui.home.sparepart.SparePartActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

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

            val uid = user.uid
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener {
                    val name = "" + it.data!!["name"]
                    val image = "" + it.data!!["image"]

                    binding.username.text = "Welcome,\n$name"
                    if(image != "") {
                        Glide.with(this)
                            .load(image)
                            .into(binding.imageView)
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

        }

        binding.item3.setOnClickListener {
            startActivity(Intent(activity, SparePartActivity::class.java))
        }

        binding.item4.setOnClickListener {
            startActivity(Intent(activity, AccessoriesActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}