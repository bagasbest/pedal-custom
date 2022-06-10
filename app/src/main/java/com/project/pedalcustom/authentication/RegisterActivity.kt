package com.project.pedalcustom.authentication

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.himanshurawat.hasher.HashType
import com.himanshurawat.hasher.Hasher
import com.project.pedalcustom.Homepage
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var binding : ActivityRegisterBinding ? = null
    private var gender = ""
    private var dob = ""
    private var city = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initView()
        dropdownCity()

        binding?.dateRangeBtn?.setOnClickListener {
            showCalendar()
        }

        binding?.signUpBtn?.setOnClickListener {
            formValidation()
        }

    }

    private fun dropdownCity() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.city, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.city?.setAdapter(adapter)
        binding?.city?.setOnItemClickListener { _, _, _, _ ->
            city = binding?.city!!.text.toString()
        }
    }

    private fun showCalendar() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            { _, i1, i2, i3 ->

                val formattedDate = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, i1)
                selectedDate.set(Calendar.MONTH, i2)
                selectedDate.set(Calendar.DAY_OF_MONTH, i3)

                val date = formattedDate.format(selectedDate.time)
                dob = date
                binding?.dob?.setText(dob)

            },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    private fun formValidation() {
        val email = binding?.email?.text.toString().trim()
        val name = binding?.name?.text.toString().trim()
        val address = binding?.address?.text.toString().trim()
        val phone = binding?.phone?.text.toString().trim()
        val password = binding?.password?.text.toString().trim()

        if(email.isEmpty()) {
            Toast.makeText(this, "Email must be filled!", Toast.LENGTH_SHORT).show()
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email must contain '@' and following '.com'", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password must be filled!", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6) {
            Toast.makeText(this, "Password must be 6 character or more!", Toast.LENGTH_SHORT).show()
        }  else if (name.isEmpty()) {
            Toast.makeText(this, "Name must be filled!", Toast.LENGTH_SHORT).show()
        } else if (city == "") {
            Toast.makeText(this, "City must be filled!", Toast.LENGTH_SHORT).show()
        } else if (address.isEmpty()) {
            Toast.makeText(this, "Address must be filled!", Toast.LENGTH_SHORT).show()
        } else if (phone.isEmpty()) {
            Toast.makeText(this, "Phone number must be filled!", Toast.LENGTH_SHORT).show()
        } else if (gender == "") {
            Toast.makeText(this, "Gender must be choose!", Toast.LENGTH_SHORT).show()
        }  else if (dob == "") {
            Toast.makeText(this, "Date of birth must be filled!", Toast.LENGTH_SHORT).show()
        } else {

            binding?.progressBar?.visibility = View.VISIBLE

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        /// save user data to database
                        saveUserDataToDatabase(email, password, name, city, address, phone)
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        try {
                            throw it.exception!!
                        } catch (e: FirebaseAuthUserCollisionException) {
                            showFailureDialog("This email already registered in our database, please choose another email!")
                        } catch (e: java.lang.Exception) {
                            Log.e("TAG", e.message!!)
                        }
                    }
                }
        }
    }

    private fun saveUserDataToDatabase(email: String, password: String, name: String, city: String, address: String, phone: String) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val data = mapOf(
            "uid" to uid,
            "email" to email,
            "password" to Hasher.hash(password, HashType.SHA_1),
            "name" to name,
            "city" to city,
            "address" to address,
            "phone" to phone,
            "dob" to dob,
            "gender" to gender,
            "image" to "",
            "role" to "user"
        )

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .set(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    binding?.progressBar?.visibility = View.GONE
                    showSuccessDialog()
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    showFailureDialog("Ups, your internet connection already trouble, pleas try again later!")
                }
            }
    }

    private fun initView() {
        Glide.with(this)
            .load(R.drawable.splash_grey)
            .into(binding!!.splashGrey)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.rbMale ->
                    if (checked) {
                        gender = "Male"
                    }
                R.id.rbFemale ->
                    if (checked) {
                        gender = "Female"
                    }
            }
        }
    }

    /// munculkan dialog ketika gagal registrasi
    private fun showFailureDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Failure Registration")
            .setMessage(message)
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /// munculkan dialog ketika sukses registrasi
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Registration Successfully")
            .setMessage("Please login with your email and password")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                startActivity(Intent(this, Homepage::class.java))
            }
            .show()
    }
}