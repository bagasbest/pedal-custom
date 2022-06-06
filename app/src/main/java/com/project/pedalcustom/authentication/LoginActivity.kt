package com.project.pedalcustom.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.Homepage
import com.project.pedalcustom.HomepageAdmin
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private var binding : ActivityLoginBinding ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initView()

        binding?.signInBtn?.setOnClickListener {
           formValidation()
        }

    }

    private fun initView() {
        Glide.with(this)
            .load(R.drawable.splash_grey)
            .into(binding!!.splashGrey)
    }

    private fun formValidation() {
        val email = binding?.email?.text.toString().trim()
        val password = binding?.password?.text.toString().trim()

        if(email.isEmpty()) {
            Toast.makeText(this, "Email must be filled!", Toast.LENGTH_SHORT).show()
        } else if(!email.contains("@") || !email.contains(".")) {
            Toast.makeText(this, "Email format wrong!", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password must be filled!", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6) {
            Toast.makeText(this, "Password must be 6 character or more!", Toast.LENGTH_SHORT).show()
        } else {

            binding?.progressBar?.visibility = View.VISIBLE

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful) {

                        val uid = FirebaseAuth.getInstance().currentUser!!.uid
                        FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { task ->
                                val role = "" + task.data!!["role"]
                                val prefs = getSharedPreferences(
                                    "role", Context.MODE_PRIVATE
                                )
                                prefs.edit().putString("role", role).apply()

                                if(role == "user") {
                                    binding?.progressBar?.visibility = View.GONE
                                    startActivity(Intent(this, Homepage::class.java))
                                } else {
                                    binding?.progressBar?.visibility = View.GONE
                                    startActivity(Intent(this, HomepageAdmin::class.java))
                                }
                            }
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        showFailureDialog()
                    }
                }
        }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Failure Login")
            .setMessage("Ups, there are maybe trouble: 1. Check email & password, 2. check your internet connection!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}