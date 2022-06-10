package com.project.pedalcustom.ui.home.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.Homepage
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityProfileEditCityAddressBinding


class ProfileEditCityAddressActivity : AppCompatActivity() {

    private var binding : ActivityProfileEditCityAddressBinding ? = null
    private var adapter : CityProfileAdapter? = null
    private var cityAddressList = ArrayList<CityAddressModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditCityAddressBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initRecyclerView()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            formValidation()
        }
    }

    private fun formValidation() {
        if(cityAddressList.size == 0)  {
            Toast.makeText(this, "Sorry, minimum 1 address added!", Toast.LENGTH_SHORT).show()
        } else {

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val city = ArrayList<String>()
            val address = ArrayList<String>()

            for(i in cityAddressList.indices) {
                city.add(cityAddressList[i].city!!)
                address.add(cityAddressList[i].address!!)
            }

            val finalCity = city.joinToString(", ")
            val finalAddress = address.joinToString(", ")

            val data = mapOf(
                "city" to finalCity,
                "address" to finalAddress,
            )

            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .update(data)
                .addOnCompleteListener {
                  if(it.isSuccessful) {
                      Toast.makeText(this, "Successfully update city and address!", Toast.LENGTH_SHORT).show()
                      val intent = Intent(this, Homepage::class.java)
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                      startActivity(intent)
                      finish()
                  }
                }
        }
    }

    private fun initRecyclerView() {
        val cityList = arrayListOf("Jakarta", "Bogor", "Depok", "Tangerang", "Bekasi", "Out of JABODETABEK")
        cityAddressList = intent.getParcelableArrayListExtra(EXTRA_DATA)!!
        binding?.rvEditCityAddress?.layoutManager = LinearLayoutManager(this)
        adapter = CityProfileAdapter(cityAddressList, cityList)
        binding?.rvEditCityAddress?.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}