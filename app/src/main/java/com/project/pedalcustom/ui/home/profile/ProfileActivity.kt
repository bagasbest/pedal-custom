package com.project.pedalcustom.ui.home.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
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
import com.rosemaryapp.amazingspinner.AmazingSpinner
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    private var user: FirebaseUser? = null
    private var name = ""
    private var dob = ""
    private var gender = ""
    private var phone = ""
    private var address = ""
    private var city = ""
    private var cityPickerAddEdit = ""
    private var cityAddressList = ArrayList<CityAddressModel>()

    override fun onResume() {
        super.onResume()
        retrieveUserData()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        user = FirebaseAuth.getInstance().currentUser

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
                    val intent = Intent(this, ProfileEditCityAddressActivity::class.java)
                    intent.putExtra(ProfileEditCityAddressActivity.EXTRA_DATA, cityAddressList)
                    startActivity(intent)
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
            "city" to city,
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
                if (it.isSuccessful) {
                    Toast.makeText(this, "Success update data", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Failure update data, internet connection trouble",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun shopEditPopup(option: String) {
        val etName: TextInputEditText
        val etPhone: TextInputEditText
        val etAddress: TextInputEditText
        val etDob: TextInputEditText
        val rbMale: RadioButton
        val editText: TextView
        val rbFemale: RadioButton
        val confirmBtn: Button
        val dateRangeBtn: ImageView
        val nameView: TextInputLayout
        val popupCityAddress: LinearLayout
        val citySp: AmazingSpinner
        val phoneView: TextInputLayout
        val dobView: ConstraintLayout
        val rg: RadioGroup
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_edit_profile)

        popupCityAddress = dialog.findViewById(R.id.popup_address)
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
                    if (nameEdit.isEmpty()) {
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
                    if (phoneEdit.isEmpty()) {
                        Toast.makeText(this, "Phone number must be filled!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        phone = phoneEdit
                        binding?.phone?.text = phone
                    }
                    dialog.dismiss()
                }
            }
            "address add" -> {
                popupCityAddress.visibility = View.VISIBLE
                etAddress = dialog.findViewById(R.id.address)
                citySp = dialog.findViewById(R.id.city)
                dropdownCity(citySp)

                confirmBtn.setOnClickListener {
                    val addressEdit = etAddress.text.toString().trim()
                    if (addressEdit.isEmpty()) {
                        Toast.makeText(this, "Address must be filled!", Toast.LENGTH_SHORT).show()
                    } else if (cityPickerAddEdit == "") {
                        Toast.makeText(this, "City must be picked", Toast.LENGTH_SHORT).show()
                    } else {
                        address = "$address, $addressEdit"
                        city = "$city, $cityPickerAddEdit"
                        setAddress(address, city)
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
                    val datePicker = DatePickerDialog(
                        this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        { _, i1, i2, i3 ->

                            val formattedDate =
                                SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
                            val selectedDate = Calendar.getInstance()
                            selectedDate.set(Calendar.YEAR, i1)
                            selectedDate.set(Calendar.MONTH, i2)
                            selectedDate.set(Calendar.DAY_OF_MONTH, i3)

                            val date = formattedDate.format(selectedDate.time)
                            dob = date
                            etDob.setText(dob)
                            binding?.dob?.text = dob
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
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

                if (gender == "Male") {
                    rbMale.isChecked = true
                } else {
                    rbFemale.isChecked = true
                }

                confirmBtn.setOnClickListener {
                    gender = if (rbMale.isChecked) {
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

    private fun dropdownCity(citySp: AmazingSpinner) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.city, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        citySp.setAdapter(adapter)
        citySp.setOnItemClickListener { _, _, _, _ ->
            cityPickerAddEdit = citySp.text.toString()
        }
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
                city = "" + it.data!!["city"]

                binding?.name?.text = name
                binding?.dob?.text = dob
                binding?.gender?.text = gender
                binding?.email?.text = email
                binding?.phone?.text = phone
                setAddress(address, city, )
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setAddress(address: String, city: String) {
        cityAddressList.clear()
        val listAddress = address.split(",").map { it.trim() }
        val listCity = city.split(",").map { it.trim() }
        var cityWord = ""
        var addressWord = ""

        binding?.llAddress?.removeAllViews()

        for (i in listAddress.indices) {

            val model = CityAddressModel()
            model.address = listAddress[i]
            model.city = listCity[i]
            cityAddressList.add(model)

            val addressTv = TextView(this)
            addressTv.text = "City and Address"
            addressTv.setTypeface(addressTv.typeface, Typeface.BOLD)

            addressWord = listAddress[i]
            cityWord = listCity[i]

            val valueCityTV = TextView(this)
            valueCityTV.text = "City: $cityWord"
            valueCityTV.id = i

            val valueAddressTV = TextView(this)
            valueAddressTV.text = "Address: $addressWord"
            valueAddressTV.id = i

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 30
            valueAddressTV.layoutParams = params

            (binding?.llAddress as LinearLayout).addView(addressTv)
            (binding?.llAddress as LinearLayout).addView(valueCityTV)
            (binding?.llAddress as LinearLayout).addView(valueAddressTV)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}