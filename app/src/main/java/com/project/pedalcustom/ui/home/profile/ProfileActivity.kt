package com.project.pedalcustom.ui.home.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityProfileBinding
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private var binding : ActivityProfileBinding ? = null
    private var user : FirebaseUser? = null
    private var name = ""
    private var dob = ""
    private var gender = ""
    private var phone = ""
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        user = FirebaseAuth.getInstance().currentUser
        retrieveUserData()

        binding?.nameEdit?.setOnClickListener {
            shopEditPopup("name")
        }

        binding?.dobEdit?.setOnClickListener {
            shopEditPopup("dob")
        }

        binding?.genderEdit?.setOnClickListener {
            shopEditPopup("gender")
        }

        binding?.phoneEdit?.setOnClickListener {
            shopEditPopup("phone")
        }

        binding?.discard?.setOnClickListener {
            onBackPressed()
        }

        binding?.addressEdit?.setOnClickListener {
            val options = arrayOf("Add New Address", "Edit Current Address")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose option")
            builder.setItems(options) { dialogs, which ->
                // the user clicked on colors[which]
                if (which == 0) {
                    /// add new address
                    dialogs.dismiss()
                    shopEditPopup("address add")
                } else {
                    /// edit address
                    shopEditPopup("address edit")
                }
            }
            builder.show()
        }

        binding?.save?.setOnClickListener {
            binding?.save?.isEnabled = false
            saveProfile()
        }

    }

    private fun saveProfile() {
        val data = mapOf(
            "name" to name,
            "address" to address,
            "phone" to phone,
            "dob" to dob,
            "gender" to gender,
        )

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(user?.uid!!)
            .update(data)
            .addOnCompleteListener {
                binding?.save?.isEnabled = true
                if(it.isSuccessful) {
                    Toast.makeText(this, "Success update data", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failure update data, internet connection trouble", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun shopEditPopup(option: String) {
        val etName: TextInputEditText
        val etPhone: TextInputEditText
        val etAddress: TextInputEditText
        val etDob: TextInputEditText
        val rbMale : RadioButton
        val editText : TextView
        val rbFemale : RadioButton
        val confirmBtn: Button
        val dateRangeBtn: ImageView
        val nameView : TextInputLayout
        val addressView : TextInputLayout
        val phoneView : TextInputLayout
        val dobView : ConstraintLayout
        val rg : RadioGroup
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_edit_profile)

        confirmBtn = dialog.findViewById(R.id.confirmBtn)
        editText = dialog.findViewById(R.id.editText)
        editText.text = "Edit $option"

        when (option) {
            "name" -> {
                etName = dialog.findViewById(R.id.name)
                nameView = dialog.findViewById(R.id.nameView)
                nameView.visibility = View.VISIBLE
                etName.setText(name)
                confirmBtn.setOnClickListener {
                    val nameEdit = etName.text.toString().trim()
                    if(nameEdit.isEmpty()) {
                        Toast.makeText(this, "Full name must be filled!", Toast.LENGTH_SHORT).show()
                    } else {
                        name = nameEdit
                        binding?.name?.text = name
                    }
                    dialog.dismiss()
                }
            }
            "phone" -> {
                etPhone = dialog.findViewById(R.id.phone)
                phoneView = dialog.findViewById(R.id.phoneView)
                phoneView.visibility = View.VISIBLE
                etPhone.setText(phone)
                confirmBtn.setOnClickListener {
                    val phoneEdit = etPhone.text.toString().trim()
                    if(phoneEdit.isEmpty()) {
                        Toast.makeText(this, "Phone number must be filled!", Toast.LENGTH_SHORT).show()
                    } else {
                        phone = phoneEdit
                        binding?.phone?.text = phone
                    }
                    dialog.dismiss()
                }
            }
            "address add" -> {
                etAddress = dialog.findViewById(R.id.address)
                addressView = dialog.findViewById(R.id.addressView)
                addressView.visibility = View.VISIBLE

                confirmBtn.setOnClickListener {
                    val addressEdit = etAddress.text.toString().trim()
                    if(addressEdit.isEmpty()) {
                        Toast.makeText(this, "Address must be filled!", Toast.LENGTH_SHORT).show()
                    } else {
                         address = "$address, $addressEdit"
                        setAddress(address, "delete")
                    }
                    dialog.dismiss()
                }
            }
            "address edit" -> {
                etAddress = dialog.findViewById(R.id.address)
                addressView = dialog.findViewById(R.id.addressView)
                addressView.visibility = View.VISIBLE
                etAddress.setText(address)

                confirmBtn.setOnClickListener {
                    val addressEdit = etAddress.text.toString().trim()
                    if(addressEdit.isEmpty()) {
                        Toast.makeText(this, "Address must be filled!", Toast.LENGTH_SHORT).show()
                    } else {
                        address = addressEdit
                        setAddress(address, "edit")
                    }
                    dialog.dismiss()
                }
            }
            "dob" -> {
                etDob = dialog.findViewById(R.id.dob)
                dobView = dialog.findViewById(R.id.dobView)
                dobView.visibility = View.VISIBLE
                etDob.setText(dob)
                dateRangeBtn = dialog.findViewById(R.id.dateRangeBtn)

                dateRangeBtn.setOnClickListener {
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
                            etDob.setText(dob)
                            binding?.dob?.text = dob
                        },
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                    datePicker.show()
                }

                confirmBtn.setOnClickListener {
                    val dobEdit = etDob.text.toString().trim()
                    dob = dobEdit
                    dialog.dismiss()
                }
            }
            else -> {
                rg = dialog.findViewById(R.id.radioGroup)
                rg.visibility = View.VISIBLE
                rbMale = dialog.findViewById(R.id.rbMale)
                rbFemale = dialog.findViewById(R.id.rbFemale)

                if(gender == "Male") {
                    rbMale.isChecked = true
                } else {
                    rbFemale.isChecked = true
                }

                confirmBtn.setOnClickListener {
                    gender = if(rbMale.isChecked) {
                        "Male"
                    } else {
                        "Female"
                    }
                    dialog.dismiss()
                    binding?.gender?.text = gender
                }
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun retrieveUserData() {
        val uid = user?.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid!!)
            .get()
            .addOnSuccessListener {
                name = "" + it.data!!["name"]
                dob = "" + it.data!!["dob"]
                gender = "" + it.data!!["gender"]
                val email = "" + it.data!!["email"]
                phone = "" + it.data!!["phone"]
                address = "" + it.data!!["address"]

                binding?.name?.text = name
                binding?.dob?.text = dob
                binding?.gender?.text = gender
                binding?.email?.text = email
                binding?.phone?.text = phone
                setAddress(address, "retrieve")
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setAddress(address: String, option: String) {
        val listAddress: List<String> = address.split(",").map { it.trim() }
        var words = ""

        if(option != "retrieve") {
           binding?.llAddress?.removeAllViews()
        }

        for(i in listAddress.indices) {

            val addressTv = TextView(this)
            addressTv.text = "Address"
            addressTv.setTypeface(addressTv.typeface, Typeface.BOLD)
            (binding?.llAddress as LinearLayout).addView(addressTv)

            words = listAddress[i]

            val valueTV = TextView(this)
            valueTV.text = words
            valueTV.id = i

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 16
            valueTV.layoutParams = params
            (binding?.llAddress as LinearLayout).addView(valueTV)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}