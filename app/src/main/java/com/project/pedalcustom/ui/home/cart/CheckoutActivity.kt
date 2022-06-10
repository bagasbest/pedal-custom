package com.project.pedalcustom.ui.home.cart

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.pedalcustom.Homepage
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityCheckoutBinding
import com.project.pedalcustom.ui.home.bike_custom.CustomSparePartModel
import com.project.pedalcustom.ui.home.profile.ProfileActivity
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckoutActivity : AppCompatActivity() {

    private var binding: ActivityCheckoutBinding? = null
    private var uid = FirebaseAuth.getInstance().currentUser!!.uid
    private var addressList = ArrayList<String>()
    private var cityList = ArrayList<String>()
    private var cityAddressList = ArrayList<String>()
    private var adapter: CheckoutAdapter? = null
    private lateinit var cartList : ArrayList<CartModel>
    private var paymentProof: String? = null
    private var cityMatcher = ""
    private val REQUEST_IMAGE_GALLERY = 1001
    private var totalPrice = 0L
    private var userName = ""
    private var userPhone = ""
    private var userAddress = ""
    private var sendingFee = 0L

    override fun onResume() {
        super.onResume()
        getAddress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initRecyclerView()


        binding?.checkoutBtn?.setOnClickListener {
            formValidation()
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.chooseOtherAddress?.setOnClickListener {
            showDropdown()
        }

        binding?.addNewAddress?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_IMAGE_GALLERY)
        }

        binding?.paymentProof?.setOnClickListener {
            if(paymentProof != null) {
                ImagePicker.with(this)
                    .galleryOnly()
                    .compress(1024)
                    .start(REQUEST_IMAGE_GALLERY)
            }
        }
    }

    private fun formValidation() {
        if(paymentProof == null) {
            Toast.makeText(this, "Ups, you must transfer money to Pedal Custom ATM!", Toast.LENGTH_SHORT).show()
        } else {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Please wait until finish...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val c = Calendar.getInstance()
            val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formattedDate = df.format(c.time)
            val address = binding?.address?.text.toString()

            for(i in cartList.indices) {
                val transactionId = System.currentTimeMillis().toString()

                val data = mapOf(
                    "uid" to transactionId,
                    "userName" to userName,
                    "userId" to uid,
                    "userPhone" to userPhone,
                    "userAddress" to address,
                    "totalPrice" to cartList[i].totalPrice,
                    "type" to cartList[i].type,
                    "customSparePartList" to cartList[i].customSparePartList,
                    "isAssembled" to cartList[i].isAssembled,
                    "category" to cartList[i].category,
                    "qty" to cartList[i].qty,
                    "color" to cartList[i].color,
                    "image" to cartList[i].image,
                    "date" to formattedDate,
                    "productName" to cartList[i].name,
                    "productId" to cartList[i].productId,
                    "status" to "On Process",
                    "paymentProof" to paymentProof,
                    "rating" to 0.0,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("transaction")
                    .document(transactionId)
                    .set(data)
                    .addOnCompleteListener {
                        FirebaseFirestore
                            .getInstance()
                            .collection("cart")
                            .document(cartList[i].uid!!)
                            .delete()

                        if(it.isSuccessful && i == cartList.size-1) {
                            progressDialog.dismiss()
                            showSuccessDialog()
                        }
                    }
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Successfully Create Transaction")
            .setMessage("Please check transaction progress on Transaction Menu!")
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

    @SuppressLint("SetTextI18n")
    private fun getTotalPrice() {

        totalPrice = 0L
        for(i in cartList.indices) {
            totalPrice += cartList[i].totalPrice!!
        }
        val formatter = DecimalFormat("#,###")
        binding?.totalPrice?.text = "Rp${formatter.format(totalPrice+sendingFee)}"
    }

    private fun initRecyclerView() {
        cartList = intent.getParcelableArrayListExtra(EXTRA_DATA)!!
        binding?.rvCart?.layoutManager = LinearLayoutManager(this)
        adapter = CheckoutAdapter(cartList)
        binding?.rvCart?.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun showDropdown() {
        val spinner: SearchableSpinner
        val saveBtn: Button
        val discardBtn: Button
        val title: TextView
        val option: TextView
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_color_product)

        spinner = dialog.findViewById(R.id.spinner)
        title = dialog.findViewById(R.id.editText)
        option = dialog.findViewById(R.id.option)
        saveBtn = dialog.findViewById(R.id.save)
        discardBtn = dialog.findViewById(R.id.discard)

        title.text = "Choose Address"
        option.text = "Address"


        val adapter =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, cityAddressList)
        spinner?.adapter = adapter

        discardBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            dialog.dismiss()
            cityMatcher = spinner?.selectedItem?.toString().toString()
            userAddress = cityMatcher
            binding?.address?.text = cityMatcher

            sendingFee = if(cityMatcher.contains("Out of JABODETABEK")) {
                100000
            } else {
                50000
            }
            getTotalPrice()

        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun getAddress() {
        addressList.clear()
        cityList.clear()
        cityAddressList.clear()

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { it ->
                val city = "" + it.data!!["city"]
                val address = "" + it.data!!["address"]
                userName = "" + it.data!!["name"]
                userPhone = "" + it.data!!["phone"]


                addressList = address.split(",").map { data -> data.trim() } as ArrayList<String>
                cityList = city.split(",").map { data -> data.trim() } as ArrayList<String>

                for(i in addressList.indices) {
                    cityAddressList.add("${cityList[i]}, ${addressList[i]}")
                }

                if(userAddress == "") {
                    userAddress = "${cityList[0]}, ${addressList[0]}"
                    binding?.address?.text = userAddress
                    cityMatcher = cityList[0]
                }


                sendingFee = if(!cityMatcher.contains("Out of JABODETABEK")) {
                    50000
                } else {
                    100000
                }
                getTotalPrice()

                Log.e("tad", userAddress)
                Log.e("tad", cityMatcher)
            }
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
        val imageFileName = "payment_proof/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        paymentProof = uri.toString()
                        Glide.with(this)
                            .load(paymentProof)
                            .into(binding!!.paymentProof)

                        binding?.imageHint?.visibility = View.GONE
                        binding?.imageHintTxt?.visibility = View.GONE
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

    companion object {
        const val EXTRA_DATA = "data"
    }
}