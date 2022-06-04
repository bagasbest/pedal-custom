package com.project.pedalcustom.ui.home.bike_custom

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivityCustomeBinding
import com.project.pedalcustom.ui.home.bike_custom.load_bike.LoadBikeActivity
import com.project.pedalcustom.ui.home.bike_custom.load_bike.LoadBikeModel
import com.project.pedalcustom.ui.home.sparepart.SparePartModel
import com.project.pedalcustom.ui.home.sparepart.SparePartViewModel
import com.project.pedalcustom.utils.IFirebaseLoadDone
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class CustomActivity : AppCompatActivity(), IFirebaseLoadDone {

    private var binding: ActivityCustomeBinding? = null
    private var bikeType = ""
    private lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    private var sparePartList: List<SparePartModel> = ArrayList()
    private var customSparePartList = ArrayList<CustomSparePartModel>()
    private var isAssembled = false
    private var totalPriceCustomBike = 0L
    private val formatter = DecimalFormat("#,###")
    private var model : LoadBikeModel ? = null
    private var option = ""

    /// spare part price
    private var brakePrice = 0L
    private var brakeLeversPrice = 0L
    private var chainRingPrice = 0L
    private var chainPrice = 0L
    private var crankPrice = 0L
    private var derailleurPrice = 0L
    private var dropOutPrice = 0L
    private var framePrice = 0L
    private var forkPrice = 0L
    private var handleGripPrice = 0L
    private var handleStemPrice = 0L
    private var handleBarPrice = 0L
    private var headSetPrice = 0L
    private var hubPrice = 0L
    private var hydrolinesPrice = 0L
    private var pedalPrice = 0L
    private var rotorPrice = 0L
    private var shockPrice = 0L
    private var saddlePrice = 0L
    private var seatPostPrice = 0L
    private var wheelSetPrice = 0L


    /// spare part id
    private var brakeId = ""
    private var brakeLeversId = ""
    private var chainRingId = ""
    private var chainId = ""
    private var crankId = ""
    private var derailleurId = ""
    private var dropOutId = ""
    private var frameId = ""
    private var forkId = ""
    private var handleGripId = ""
    private var handleStemId = ""
    private var handleBarId = ""
    private var headSetId = ""
    private var hubId = ""
    private var hydrolinesId = ""
    private var pedalId = ""
    private var rotorId = ""
    private var shockId = ""
    private var saddleId = ""
    private var seatPostId = ""
    private var wheelSetId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        iFirebaseLoadDone = this
        option = intent.getStringExtra(OPTION).toString()
        if(option == "create") {
            initialCapacityList()
        } else {
            model = intent.getParcelableExtra(EXTRA_DATA)
            bikeType = model?.bikeType.toString()
        }
        initBikeType()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            if (bikeType != "") {
                saveCustomBikeToDatabase()
            } else {
                Toast.makeText(
                    this,
                    "Please choose bike type, and fill every column below!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding?.cartBtn?.setOnClickListener {

        }

        binding?.accToCart?.setOnClickListener {

        }

        binding?.loadBtn?.setOnClickListener {
            startActivity(Intent(this, LoadBikeActivity::class.java))
        }

        binding?.brakeQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.brakePrice?.setText(price.toString())
                    val name = binding?.brakeSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, brakeId, 0, 0)
                    customSparePartList[0] = model
                    showPriceTotal()

                } else {
                    price = brakePrice * p0.toString().toInt()
                    binding?.brakePrice?.setText(price.toString())
                    val name = binding?.brakeSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, brakeId, p0.toString().toInt(), price)
                    customSparePartList[0] = model
                    showPriceTotal()
                }

            }

        })

        binding?.brakeLeversQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.brakeLeversPrice?.setText(price.toString())
                    val name = binding?.brakeLeversSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, brakeLeversId, 0, 0)
                    customSparePartList[1] = model
                    showPriceTotal()
                } else {
                    price = brakeLeversPrice * p0.toString().toInt()
                    binding?.brakeLeversPrice?.setText(price.toString())
                    val name = binding?.brakeLeversSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, brakeLeversId, p0.toString().toInt(), price)
                    customSparePartList[1] = model
                    showPriceTotal()
                }


            }

        })

        binding?.chainringQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.chainringPrice?.setText(price.toString())
                    val name = binding?.chainringSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, chainRingId, 0, 0)
                    customSparePartList[2] = model
                    showPriceTotal()
                } else {
                    price = chainPrice * p0.toString().toInt()
                    binding?.chainringPrice?.setText(price.toString())
                    val name = binding?.chainringSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, chainRingId, p0.toString().toInt(), price)
                    customSparePartList[2] = model
                    showPriceTotal()
                }

            }

        })


        binding?.chainQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.chainPrice?.setText(price.toString())
                    val name = binding?.chainSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, chainId, 0, 0)
                    customSparePartList[3] = model
                    showPriceTotal()
                } else {
                    price = chainPrice * p0.toString().toInt()
                    binding?.chainPrice?.setText(price.toString())
                    val name = binding?.chainSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, chainId, p0.toString().toInt(), price)
                    customSparePartList[3] = model
                    showPriceTotal()
                }
            }
        })


        binding?.cranksQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.cranksPrice?.setText(price.toString())
                    val name = binding?.crancksSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, crankId, 0, 0)
                    customSparePartList[4] = model
                    showPriceTotal()
                } else {
                    price = crankPrice * p0.toString().toInt()
                    binding?.cranksPrice?.setText(price.toString())
                    val name = binding?.crancksSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, crankId, p0.toString().toInt(), price)
                    customSparePartList[4] = model
                    showPriceTotal()
                }

            }

        })


        binding?.derailleurQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.derailleurPrice?.setText(price.toString())
                    val name = binding?.derailleurSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, derailleurId, 0, 0)
                    customSparePartList[5] = model
                    showPriceTotal()
                } else {
                    price = derailleurPrice * p0.toString().toInt()
                    binding?.derailleurPrice?.setText(price.toString())
                    val name = binding?.derailleurSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, derailleurId, p0.toString().toInt(), price)
                    customSparePartList[5] = model
                    showPriceTotal()
                }

            }

        })


        binding?.dropOutQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.dropOutPrice?.setText(price.toString())
                    val name = binding?.dropOutSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, dropOutId, 0, 0)
                    customSparePartList[6] = model
                    showPriceTotal()
                } else {
                    price = dropOutPrice * p0.toString().toInt()
                    binding?.dropOutPrice?.setText(price.toString())
                    val name = binding?.dropOutSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, dropOutId, p0.toString().toInt(), price)
                    customSparePartList[6] = model
                    showPriceTotal()
                }

            }

        })

        binding?.frameQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.framePrice?.setText(price.toString())
                    val name = binding?.frameSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, frameId, 0, 0)
                    customSparePartList[7] = model
                    showPriceTotal()
                } else {
                    price = framePrice * p0.toString().toInt()
                    binding?.framePrice?.setText(price.toString())
                    val name = binding?.frameSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, frameId, p0.toString().toInt(), price)
                    customSparePartList[7] = model
                    showPriceTotal()
                }

            }

        })

        binding?.forkQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.forkPrice?.setText(price.toString())
                    val name = binding?.forkSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, forkId, 0, 0)
                    customSparePartList.add(8, model)
                    customSparePartList[8] = model
                    showPriceTotal()
                } else {
                    price = forkPrice * p0.toString().toInt()
                    binding?.forkPrice?.setText(price.toString())
                    val name = binding?.forkSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, forkId, p0.toString().toInt(), price)
                    customSparePartList.add(8, model)
                    customSparePartList[8] = model
                    showPriceTotal()
                }

            }

        })

        binding?.handleGripQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.handleGripPrice?.setText(price.toString())
                    val name = binding?.handleGripSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, handleGripId, 0, 0)
                    customSparePartList[9] = model
                    showPriceTotal()
                } else {
                    price = handleGripPrice * p0.toString().toInt()
                    binding?.handleGripPrice?.setText(price.toString())
                    val name = binding?.handleGripSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, handleGripId, p0.toString().toInt(), price)
                    customSparePartList[9] = model
                    showPriceTotal()
                }

            }

        })

        binding?.handleStemQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.handleStemPrice?.setText(price.toString())
                    val name = binding?.handleStemSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, handleStemId, 0, 0)
                    customSparePartList[10] = model
                    showPriceTotal()
                } else {
                    price = handleStemPrice * p0.toString().toInt()
                    binding?.handleStemPrice?.setText(price.toString())
                    val name = binding?.handleStemSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, handleStemId, p0.toString().toInt(), price)
                    customSparePartList[10] = model
                    showPriceTotal()
                }

            }

        })

        binding?.handleBarQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.handleBarPrice?.setText(price.toString())
                    val name = binding?.handleBarSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, handleBarId, 0, 0)
                    customSparePartList[11] = model
                    showPriceTotal()
                } else {
                    price = handleBarPrice * p0.toString().toInt()
                    binding?.handleBarPrice?.setText(price.toString())
                    val name = binding?.handleBarSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, handleBarId, p0.toString().toInt(), price)
                    customSparePartList[11] = model
                    showPriceTotal()
                }

            }

        })

        binding?.headSetQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.headSetPrice?.setText(price.toString())
                    val name = binding?.headSetSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, handleStemId, 0, 0)
                    customSparePartList[12] = model
                    showPriceTotal()
                } else {
                    price = headSetPrice * p0.toString().toInt()
                    binding?.headSetPrice?.setText(price.toString())
                    val name = binding?.headSetSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, handleStemId, p0.toString().toInt(), price)
                    customSparePartList[12] = model
                    showPriceTotal()
                }

            }

        })

        binding?.hubQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.hubPrice?.setText(price.toString())
                    val name = binding?.hubSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, hubId, 0, 0)
                    customSparePartList[13] = model
                    showPriceTotal()
                } else {
                    price = hubPrice * p0.toString().toInt()
                    binding?.hubPrice?.setText(price.toString())
                    val name = binding?.hubSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, hubId, p0.toString().toInt(), price)
                    customSparePartList[13] = model
                    showPriceTotal()
                }

            }

        })

        binding?.hydrolinesQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.hydrolinesPrice?.setText(price.toString())
                    val name = binding?.hydrolinesSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, hydrolinesId, 0, 0)
                    customSparePartList[14] = model
                    showPriceTotal()
                } else {
                    price = hydrolinesPrice * p0.toString().toInt()
                    binding?.hydrolinesPrice?.setText(price.toString())
                    val name = binding?.hydrolinesSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, hydrolinesId, p0.toString().toInt(), price)
                    customSparePartList[14] = model
                    showPriceTotal()
                }

            }

        })

        binding?.pedalsQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.pedalsPrice?.setText(price.toString())
                    val name = binding?.pedalsSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, pedalId, 0, 0)
                    customSparePartList[15] = model
                    showPriceTotal()
                } else {
                    price = pedalPrice * p0.toString().toInt()
                    binding?.pedalsPrice?.setText(price.toString())
                    val name = binding?.pedalsSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, pedalId, p0.toString().toInt(), price)
                    customSparePartList[15] = model
                    showPriceTotal()
                }

            }

        })

        binding?.rotorQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.rotorPrice?.setText(price.toString())
                    val name = binding?.rotorSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, rotorId,0, 0)
                    customSparePartList[16] = model
                    showPriceTotal()
                } else {
                    price = rotorPrice * p0.toString().toInt()
                    binding?.rotorPrice?.setText(price.toString())
                    val name = binding?.rotorSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, rotorId, p0.toString().toInt(), price)
                    customSparePartList[16] = model
                    showPriceTotal()
                }

            }

        })


        binding?.shockQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.shockPrice?.setText(price.toString())
                    val name = binding?.shockSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, shockId, 0, 0)
                    customSparePartList[17] = model
                    showPriceTotal()
                } else {
                    price = shockPrice * p0.toString().toInt()
                    binding?.shockPrice?.setText(price.toString())
                    val name = binding?.shockSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, shockId, p0.toString().toInt(), price)
                    customSparePartList[17] = model
                    showPriceTotal()
                }

            }


        })


        binding?.saddleQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.saddlePrice?.setText(price.toString())
                    val name = binding?.saddleSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, saddleId, 0, 0)
                    customSparePartList[18] = model
                    showPriceTotal()
                } else {
                    price = saddlePrice * p0.toString().toInt()
                    binding?.saddlePrice?.setText(price.toString())
                    val name = binding?.saddleSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, saddleId, p0.toString().toInt(), price)
                    customSparePartList[18] = model
                    showPriceTotal()
                }

            }

        })

        binding?.seatPostQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.seatPostPrice?.setText(price.toString())
                    val name = binding?.seatPostSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, seatPostId, 0, 0)
                    customSparePartList[19] = model
                    showPriceTotal()
                } else {
                    price = seatPostPrice * p0.toString().toInt()
                    binding?.seatPostPrice?.setText(price.toString())
                    val name = binding?.seatPostSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, seatPostId, p0.toString().toInt(), price)
                    customSparePartList[19] = model
                    showPriceTotal()
                }

            }

        })


        binding?.wheelSetQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val price: Long
                if (p0.toString().isEmpty()) {
                    price = 0L
                    binding?.wheelSetPrice?.setText(price.toString())
                    val name = binding?.wheelSetSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, wheelSetId, 0, 0)
                    customSparePartList[20] = model
                    showPriceTotal()
                } else {
                    price = wheelSetPrice * p0.toString().toInt()
                    binding?.wheelSetPrice?.setText(price.toString())
                    val name = binding?.wheelSetSp?.selectedItem.toString()
                    val model = CustomSparePartModel(name, wheelSetId, p0.toString().toInt(), price)
                    customSparePartList[20] = model
                    showPriceTotal()
                }

            }

        })
    }

    private fun initBikeType() {
        if(option == "create") {
            bikeType = intent.getStringExtra(BIKE_TYPE).toString()
        }
        if(bikeType != "null") {

            when (bikeType) {
                "MTB" -> {
                    binding?.mtb?.isChecked = true
                }
                "BMX" -> {
                    binding?.bmx?.isChecked = true
                }
                "Folding" -> {
                    binding?.folding?.isChecked = true
                }
                "Racing" -> {
                    binding?.racing?.isChecked = true
                }
                "Any" -> {
                    binding?.any?.isChecked = true
                }
            }

            binding?.custom?.visibility = View.VISIBLE
            getAllSparePart()
        }

    }

    private fun initialCapacityList() {
        val model = CustomSparePartModel()
        for (i in 0..21) {
            customSparePartList.add(model)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showPriceTotal() {
        totalPriceCustomBike = 0L
        for (i in customSparePartList.indices) {
            totalPriceCustomBike += customSparePartList[i].price!!
        }

        if(isAssembled) {
            totalPriceCustomBike += 100000
        }
        binding?.totalPrice?.text = "Total:\nRp.${formatter.format(totalPriceCustomBike)}"
    }

    private fun saveCustomBikeToDatabase() {
        val etName: EditText
        val saveBtn: Button
        val discardBtn: Button
        val pb: ProgressBar
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_save_custom)

        etName = dialog.findViewById(R.id.nameEt)
        saveBtn = dialog.findViewById(R.id.save)
        pb = dialog.findViewById(R.id.progressBar)
        discardBtn = dialog.findViewById(R.id.discard)

        discardBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Save Data Name must be filled!", Toast.LENGTH_SHORT).show()
            } else {
                pb.visibility = View.VISIBLE

                val myUid = FirebaseAuth.getInstance().currentUser!!.uid
                val uid = System.currentTimeMillis().toString()

                val data = mapOf(
                    "uid" to uid,
                    "userId" to myUid,
                    "totalPrice" to totalPriceCustomBike,
                    "saveName" to name,
                    "bikeType" to bikeType,
                    "customSparePartList" to customSparePartList,
                    "isAssembled" to isAssembled
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("custom_save_data")
                    .document(uid)
                    .set(data)
                    .addOnCompleteListener {
                        pb.visibility = View.GONE
                        if (it.isSuccessful) {
                            dialog.dismiss()
                            Toast.makeText(this, "Success save custom bike!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            dialog.dismiss()
                            Toast.makeText(this, "Failure save custom bike!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }


        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun bikeType(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked


            // Check which radio button was clicked
            when (view.getId()) {
                R.id.mtb ->
                    if (checked) {
                        bikeType = "MTB"
                    }
                R.id.bmx ->
                    if (checked) {
                        bikeType = "BMX"
                    }

                R.id.folding ->
                    if (checked) {
                        bikeType = "Folding"
                    }

                R.id.racing ->
                    if (checked) {
                        bikeType = "Racing"
                    }

                R.id.any ->
                    if (checked) {
                        bikeType = "Any"
                    }
            }

            val intent = Intent(this, CustomActivity::class.java)
            intent.putExtra(BIKE_TYPE, bikeType)
            startActivity(intent)
            finish()
        }
    }

    private fun getAllSparePart() {
        val viewModel = ViewModelProvider(this)[SparePartViewModel::class.java]

        viewModel.setListSparePartByBikeType(bikeType)
        viewModel.getSparePart().observe(this) { sparePartList ->
                iFirebaseLoadDone.onFirebaseLoadSuccess(sparePartList)
        }
    }

    override fun onFirebaseLoadSuccess(sparePart: List<SparePartModel>) {
        this.sparePartList = sparePart
        /// BRAKE
        val sparePartBrake = getPropertyNameList(sparePart, "Brake")
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartBrake)
        binding?.brakeSp?.adapter = adapter

        /// BRAKE LEVERS
        val sparePartBrakeLevers = getPropertyNameList(sparePart, "Brake Levers")
        val adapter2 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartBrakeLevers)
        binding?.brakeLeversSp?.adapter = adapter2


        /// CHAIN RING
        val sparePartChainRing = getPropertyNameList(sparePart, "Chain Ring")
        val adapter3 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartChainRing)
        binding?.chainringSp?.adapter = adapter3


        /// CHAIN RING
        val sparePartChain = getPropertyNameList(sparePart, "Chain")
        val adapter4 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartChain)
        binding?.chainSp?.adapter = adapter4

        /// CRANKS
        val sparePartCranks = getPropertyNameList(sparePart, "Cranks")
        val adapter5 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartCranks)
        binding?.crancksSp?.adapter = adapter5


        /// Derailleur
        val sparePartDerailleur = getPropertyNameList(sparePart, "Derailleur")
        val adapter6 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartDerailleur)
        binding?.derailleurSp?.adapter = adapter6


        /// Drop Out
        val sparePartDropOut = getPropertyNameList(sparePart, "Drop Out")
        val adapter7 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartDropOut)
        binding?.dropOutSp?.adapter = adapter7

        /// Frame
        val sparePartFrame = getPropertyNameList(sparePart, "Frame")
        val adapter8 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartFrame)
        binding?.frameSp?.adapter = adapter8


        /// Fork
        val sparePartFork = getPropertyNameList(sparePart, "Fork")
        val adapter9 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePartFork)
        binding?.forkSp?.adapter = adapter9


        /// Handle Grip
        val spareHandleGrip = getPropertyNameList(sparePart, "Handle Grip")
        val adapter10 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareHandleGrip)
        binding?.handleGripSp?.adapter = adapter10


        /// Handle Stem
        val spareHandleStem = getPropertyNameList(sparePart, "Handle Stem")
        val adapter11 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareHandleStem)
        binding?.handleStemSp?.adapter = adapter11

        /// Handle Bar
        val spareHandleBar = getPropertyNameList(sparePart, "Handlebar")
        val adapter12 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareHandleBar)
        binding?.handleBarSp?.adapter = adapter12


        /// Headset
        val spareHeadSet = getPropertyNameList(sparePart, "Headset")
        val adapter13 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareHeadSet)
        binding?.headSetSp?.adapter = adapter13

        /// Hub
        val spareHub = getPropertyNameList(sparePart, "Hub")
        val adapter14 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareHub)
        binding?.hubSp?.adapter = adapter14


        /// Hydrolines
        val spareHydrolines = getPropertyNameList(sparePart, "Hydrolines")
        val adapter15 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareHydrolines)
        binding?.hydrolinesSp?.adapter = adapter15


        /// Pedals
        val sparePedals = getPropertyNameList(sparePart, "Pedals")
        val adapter16 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, sparePedals)
        binding?.pedalsSp?.adapter = adapter16


        /// Rotor
        val spareRotor = getPropertyNameList(sparePart, "Rotor")
        val adapter17 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareRotor)
        binding?.rotorSp?.adapter = adapter17


        /// Shock
        val spareShock = getPropertyNameList(sparePart, "Shock")
        val adapter18 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareShock)
        binding?.shockSp?.adapter = adapter18


        /// Saddle
        val spareSaddle = getPropertyNameList(sparePart, "Saddle")
        val adapter19 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareSaddle)
        binding?.saddleSp?.adapter = adapter19


        /// Shock
        val spareSeatPost = getPropertyNameList(sparePart, "Seatpost")
        val adapter20 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareSeatPost)
        binding?.seatPostSp?.adapter = adapter20


        /// Wheel Set
        val spareWheelSet = getPropertyNameList(sparePart, "Wheel Set")
        val adapter21 =
            ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, spareWheelSet)
        binding?.wheelSetSp?.adapter = adapter21

    }

    private fun getPropertyNameList(
        spList: List<SparePartModel>,
        sparePartType: String
    ): List<String> {
        val result = ArrayList<String>()
        for (sp in spList) {
            if (sp.type == sparePartType) {
                result.add(sp.name!!)

                when (sparePartType) {
                    "Brake" -> {
                        brakePrice = sp.price!!
                        brakeId = sp.uid!!
                    }
                    "Brake Levers" -> {
                        brakeLeversPrice = sp.price!!
                        brakeLeversId = sp.uid!!
                    }
                    "Chain Ring" -> {
                        chainRingPrice = sp.price!!
                        chainRingId = sp.uid!!

                    }
                    "Chain" -> {
                        chainPrice = sp.price!!
                        chainId = sp.uid!!

                    }
                    "Cranks" -> {
                        crankPrice = sp.price!!
                        crankId = sp.uid!!

                    }
                    "Derailleur" -> {
                        derailleurPrice = sp.price!!
                        derailleurId = sp.uid!!

                    }
                    "Drop Out" -> {
                        dropOutPrice = sp.price!!
                        dropOutId = sp.uid!!

                    }
                    "Frame" -> {
                        framePrice = sp.price!!
                        frameId = sp.uid!!

                    }
                    "Fork" -> {
                        forkPrice = sp.price!!
                        forkId = sp.uid!!

                    }
                    "Handle Grip" -> {
                        handleGripPrice = sp.price!!
                        handleGripId = sp.uid!!

                    }
                    "Handle Stem" -> {
                        handleStemPrice = sp.price!!
                        handleStemId = sp.uid!!

                    }
                    "Handlebar" -> {
                        handleBarPrice = sp.price!!
                        handleBarId = sp.uid!!

                    }
                    "Headset" -> {
                        headSetPrice = sp.price!!
                        headSetId = sp.uid!!

                    }
                    "Hub" -> {
                        hubPrice = sp.price!!
                        hubId = sp.uid!!

                    }
                    "Hydrolines" -> {
                        hydrolinesPrice = sp.price!!
                        hydrolinesId = sp.uid!!

                    }
                    "Pedals" -> {
                        pedalPrice = sp.price!!
                        pedalId = sp.uid!!

                    }
                    "Rotor" -> {
                        rotorPrice = sp.price!!
                        rotorId = sp.uid!!

                    }
                    "Shock" -> {
                        shockPrice = sp.price!!
                        shockId = sp.uid!!

                    }
                    "Saddle" -> {
                        saddlePrice = sp.price!!
                        saddleId = sp.uid!!

                    }
                    "Seatpost" -> {
                        seatPostPrice = sp.price!!
                        seatPostId = sp.uid!!

                    }
                    "Wheel Set" -> {
                        wheelSetPrice = sp.price!!
                        wheelSetId = sp.uid!!
                    }
                }
            }
        }
        return result
    }

    override fun onFirebaseLoadFailed(message: String) {

    }

    @SuppressLint("SetTextI18n")
    fun assembled(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.assembled ->
                    if (checked) {
                        if (!isAssembled) {
                            isAssembled = true
                            totalPriceCustomBike += 100000
                            binding?.totalPrice?.text =
                                "Total:\nRp${formatter.format(totalPriceCustomBike)}"
                        }
                    }
                R.id.notAssembled ->
                    if (checked) {
                        if (isAssembled) {
                            isAssembled = false
                            totalPriceCustomBike -= 100000
                            binding?.totalPrice?.text =
                                "Total:\nRp${formatter.format(totalPriceCustomBike)}"
                        }
                    }
            }

        }
    }

    companion object {
        const val BIKE_TYPE = "type"
        const val EXTRA_DATA = "model"
        const val OPTION = "load"
    }
}