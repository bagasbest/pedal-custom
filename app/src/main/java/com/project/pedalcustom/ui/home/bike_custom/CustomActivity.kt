package com.project.pedalcustom.ui.home.bike_custom

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pedalcustom.R
import com.project.pedalcustom.authentication.LoginActivity
import com.project.pedalcustom.databinding.ActivityCustomeBinding
import com.project.pedalcustom.ui.home.bike_custom.load_bike.LoadBikeActivity
import com.project.pedalcustom.ui.home.bike_custom.load_bike.LoadBikeModel
import com.project.pedalcustom.ui.home.cart.CartActivity
import com.project.pedalcustom.ui.home.sparepart.SparePartModel
import com.project.pedalcustom.ui.home.sparepart.SparePartViewModel
import com.project.pedalcustom.utils.IFirebaseLoadDone
import java.text.DecimalFormat

class CustomActivity : AppCompatActivity(), IFirebaseLoadDone {

    private var binding: ActivityCustomeBinding? = null
    private var bikeType = ""
    private lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    private var sparePartList: List<SparePartModel> = ArrayList()
    private var customSparePartList = ArrayList<CustomSparePartModel>()
    private var isAssembled = false
    private var totalPriceCustomBike = 0L
    private val formatter = DecimalFormat("#,###")
    private var model: LoadBikeModel? = null
    private var option = ""
    private var user: FirebaseUser? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        iFirebaseLoadDone = this
        user = FirebaseAuth.getInstance().currentUser
        option = intent.getStringExtra(OPTION).toString()
        initialCapacityList()

        if (option == "load") {
            model = intent.getParcelableExtra(EXTRA_DATA)
            bikeType = model?.bikeType.toString()
            isAssembled = model?.isAssembled == true
            if (isAssembled) {
                binding?.assembled?.isChecked = true
            } else {
                binding?.notAssembled?.isChecked = true
            }

            totalPriceCustomBike = model?.totalPrice!!
            binding?.totalPrice?.text = "Total:\nRp${formatter.format(model?.totalPrice)}"
        }
        initBikeType()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.saveBtn?.setOnClickListener {
            if (bikeType != "") {
                if (option == "create") {
                    saveCustomBikeToDatabase()
                } else {
                    binding?.saveBtn?.isEnabled = false
                    val data = mapOf(
                        "totalPrice" to totalPriceCustomBike,
                        "saveName" to model?.saveName,
                        "bikeType" to bikeType,
                        "customSparePartList" to customSparePartList,
                        "isAssembled" to isAssembled
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("custom_save_data")
                        .document(model?.uid!!)
                        .update(data)
                        .addOnCompleteListener {
                            binding?.saveBtn?.isEnabled = true
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Success overwrite custom bike!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failure overwrite custom bike!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                }
            } else {
                Toast.makeText(
                    this,
                    "Please choose bike type, and fill every column below!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding?.cartBtn?.setOnClickListener {
            if (user != null) {
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding?.accToCart?.setOnClickListener {
            if (user != null) {
                if (bikeType != "") {
                    addBikeCustomToCart()
                } else {
                    Toast.makeText(
                        this,
                        "Please choose bike type, and fill every column below!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding?.loadBtn?.setOnClickListener {
            if (user != null) {
                startActivity(Intent(this, LoadBikeActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding?.brakeSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.brakeQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.brakeLeversSp?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding?.brakeLeversQty?.setText("")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            })

        binding?.chainringSp?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding?.chainringQty?.setText("")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            })

        binding?.chainSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.chainQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.crancksSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.cranksQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.derailleurSp?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding?.derailleurQty?.setText("")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            })

        binding?.dropOutSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.dropOutQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.frameSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.frameQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.forkSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.forkQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.handleGripSp?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding?.handleGripQty?.setText("")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            })

        binding?.handleStemSp?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding?.handleStemQty?.setText("")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            })


        binding?.handleBarSp?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding?.handleBarQty?.setText("")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            })

        binding?.headSetSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.headSetQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.hubSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.hubQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.hydrolinesSp?.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding?.hydrolinesQty?.setText("")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            })

        binding?.pedalsSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.pedalsQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.rotorSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.rotorQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.shockSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.shockQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.saddleSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.saddleQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.seatPostSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.seatPostQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.wheelSetSp?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding?.wheelSetQty?.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        binding?.brakeQty?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val name = binding?.brakeSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }


                if (p0.toString().isEmpty()) {
                    binding?.brakePrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[0] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.brakePrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.brakeLeversSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.brakeLeversPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[1] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.brakeLeversPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.chainringSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.chainringPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[2] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.chainringPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.chainSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.chainPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[3] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.chainPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.crancksSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.cranksPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[3] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.cranksPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.derailleurSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.derailleurPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[5] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.derailleurPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.dropOutSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.dropOutPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[6] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.dropOutPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.frameSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.framePrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[7] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.framePrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.forkSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.forkPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[8] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.forkPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.handleGripSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.handleGripPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[9] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.handleGripPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.handleStemSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.handleStemPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[10] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.handleStemPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.handleBarSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.handleBarPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[11] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.handleBarPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.headSetSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.headSetPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[12] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.headSetPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.hubSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.hubPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[13] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.hubPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.hydrolinesSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.hydrolinesPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[14] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.hydrolinesPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.pedalsSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.pedalsPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[15] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.pedalsPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.rotorSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.rotorPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[16] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.rotorPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.shockSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.shockPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[17] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.shockPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.saddleSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.saddlePrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[18] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.saddlePrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.seatPostSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.seatPostPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[19] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.seatPostPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
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
                val name = binding?.wheelSetSp?.selectedItem.toString()
                var price = 0L
                var productId = ""

                for (i in sparePartList.indices) {
                    if (sparePartList[i].name == name) {
                        price = sparePartList[i].price!!
                        productId = sparePartList[i].uid!!

                        break
                    }
                }

                if (p0.toString().isEmpty()) {
                    binding?.wheelSetPrice?.setText("0")
                    val model = CustomSparePartModel(name, productId, 0, 0)
                    customSparePartList[20] = model
                    showPriceTotal()

                } else {
                    price *= p0.toString().toInt()
                    binding?.wheelSetPrice?.setText(price.toString())
                    val model = CustomSparePartModel(
                        name,
                        productId,
                        p0.toString().toInt(),
                        price
                    )
                    customSparePartList[20] = model
                    showPriceTotal()
                }


            }

        })
    }

    private fun addBikeCustomToCart() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait until finish...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val customBikeList = ArrayList<CustomSparePartModel>()

        val uid = System.currentTimeMillis().toString()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        for (i in 0..21) {
            if (customSparePartList[i].qty!! > 0) {
                customBikeList.add(customSparePartList[i])
            }
        }

        val data = mapOf(
            "uid" to uid,
            "name" to "Custom Bike",
            "userId" to userId,
            "totalPrice" to totalPriceCustomBike,
            "type" to bikeType,
            "customSparePartList" to customBikeList,
            "isAssembled" to isAssembled,
            "category" to "custom bike",
            "qty" to 1,
            "color" to "",
            "image" to "",
        )

        FirebaseFirestore
            .getInstance()
            .collection("cart")
            .document(uid)
            .set(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    progressDialog.dismiss()
                    showSuccessDialog()
                } else {
                    progressDialog.dismiss()
                    showFailureDialog()
                }
            }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Success Add Custom Bike To Cart")
            .setMessage("You can see product on cart page for transaction")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                startActivity(Intent(this, CartActivity::class.java))
            }
            .show()
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Failure Add Custom Bike To Cart")
            .setMessage("Ups there problem with your internet connection, please try again later!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun initBikeType() {
        if (option == "create") {
            bikeType = intent.getStringExtra(BIKE_TYPE).toString()
        }
        if (bikeType != "null") {

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

            if (option == "load") {
                for (i in 0..binding?.radioGroup2?.childCount!!) {
                    binding?.radioGroup2?.getChildAt(i)?.isEnabled = false
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

        if (isAssembled) {
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
            intent.putExtra(OPTION, option)
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


        if (option == "load") {
            val spinnerPosition: Int = adapter.getPosition(model?.customSparePartList?.get(0)?.name)
            binding?.brakeSp?.setSelection(spinnerPosition)

            val spinnerPosition2: Int =
                adapter2.getPosition(model?.customSparePartList?.get(1)?.name)
            binding?.brakeLeversSp?.setSelection(spinnerPosition2)

            val spinnerPosition3: Int =
                adapter3.getPosition(model?.customSparePartList?.get(2)?.name)
            binding?.chainringSp?.setSelection(spinnerPosition3)

            val spinnerPosition4: Int =
                adapter4.getPosition(model?.customSparePartList?.get(3)?.name)
            binding?.chainSp?.setSelection(spinnerPosition4)

            val spinnerPosition5: Int =
                adapter5.getPosition(model?.customSparePartList?.get(4)?.name)
            binding?.crancksSp?.setSelection(spinnerPosition5)

            val spinnerPosition6: Int =
                adapter6.getPosition(model?.customSparePartList?.get(5)?.name)
            binding?.derailleurSp?.setSelection(spinnerPosition6)

            val spinnerPosition7: Int =
                adapter7.getPosition(model?.customSparePartList?.get(6)?.name)
            binding?.dropOutSp?.setSelection(spinnerPosition7)

            val spinnerPosition8: Int =
                adapter8.getPosition(model?.customSparePartList?.get(7)?.name)
            binding?.frameSp?.setSelection(spinnerPosition8)

            val spinnerPosition9: Int =
                adapter9.getPosition(model?.customSparePartList?.get(8)?.name)
            binding?.forkSp?.setSelection(spinnerPosition9)

            val spinnerPosition10: Int =
                adapter10.getPosition(model?.customSparePartList?.get(9)?.name)
            binding?.handleGripSp?.setSelection(spinnerPosition10)

            val spinnerPosition11: Int =
                adapter11.getPosition(model?.customSparePartList?.get(10)?.name)
            binding?.handleStemSp?.setSelection(spinnerPosition11)

            val spinnerPosition12: Int =
                adapter12.getPosition(model?.customSparePartList?.get(11)?.name)
            binding?.handleBarSp?.setSelection(spinnerPosition12)

            val spinnerPosition13: Int =
                adapter13.getPosition(model?.customSparePartList?.get(12)?.name)
            binding?.headSetSp?.setSelection(spinnerPosition13)

            val spinnerPosition14: Int =
                adapter14.getPosition(model?.customSparePartList?.get(13)?.name)
            binding?.hubSp?.setSelection(spinnerPosition14)

            val spinnerPosition15: Int =
                adapter15.getPosition(model?.customSparePartList?.get(14)?.name)
            binding?.hydrolinesSp?.setSelection(spinnerPosition15)

            val spinnerPosition16: Int =
                adapter16.getPosition(model?.customSparePartList?.get(15)?.name)
            binding?.pedalsSp?.setSelection(spinnerPosition16)

            val spinnerPosition17: Int =
                adapter17.getPosition(model?.customSparePartList?.get(16)?.name)
            binding?.rotorSp?.setSelection(spinnerPosition17)

            val spinnerPosition18: Int =
                adapter18.getPosition(model?.customSparePartList?.get(17)?.name)
            binding?.shockSp?.setSelection(spinnerPosition18)

            val spinnerPosition19: Int =
                adapter19.getPosition(model?.customSparePartList?.get(18)?.name)
            binding?.saddleSp?.setSelection(spinnerPosition19)

            val spinnerPosition20: Int =
                adapter20.getPosition(model?.customSparePartList?.get(19)?.name)
            binding?.seatPostSp?.setSelection(spinnerPosition20)

            val spinnerPosition21: Int =
                adapter21.getPosition(model?.customSparePartList?.get(21)?.name)
            binding?.wheelSetSp?.setSelection(spinnerPosition21)


            Handler().postDelayed({
                /// INISIASI DATA KEDALAM KOLOM KOLOM
                binding?.brakeQty?.setText(model?.customSparePartList!![0].qty.toString())
                binding?.brakePrice?.setText(model?.customSparePartList!![0].price.toString())

                binding?.brakeLeversQty?.setText(model?.customSparePartList!![1].qty.toString())
                binding?.brakeLeversPrice?.setText(model?.customSparePartList!![1].price.toString())

                binding?.chainringQty?.setText(model?.customSparePartList!![2].qty.toString())
                binding?.chainringPrice?.setText(model?.customSparePartList!![2].price.toString())

                binding?.chainQty?.setText(model?.customSparePartList!![3].qty.toString())
                binding?.chainPrice?.setText(model?.customSparePartList!![3].price.toString())

                binding?.cranksQty?.setText(model?.customSparePartList!![4].qty.toString())
                binding?.cranksPrice?.setText(model?.customSparePartList!![4].price.toString())

                binding?.derailleurQty?.setText(model?.customSparePartList!![5].qty.toString())
                binding?.derailleurPrice?.setText(model?.customSparePartList!![5].price.toString())

                binding?.dropOutQty?.setText(model?.customSparePartList!![6].qty.toString())
                binding?.dropOutPrice?.setText(model?.customSparePartList!![6].price.toString())

                binding?.frameQty?.setText(model?.customSparePartList!![7].qty.toString())
                binding?.framePrice?.setText(model?.customSparePartList!![7].price.toString())

                binding?.forkQty?.setText(model?.customSparePartList!![8].qty.toString())
                binding?.forkPrice?.setText(model?.customSparePartList!![8].price.toString())

                binding?.handleGripQty?.setText(model?.customSparePartList!![9].qty.toString())
                binding?.handleGripPrice?.setText(model?.customSparePartList!![9].price.toString())

                binding?.handleStemQty?.setText(model?.customSparePartList!![10].qty.toString())
                binding?.handleStemPrice?.setText(model?.customSparePartList!![10].price.toString())

                binding?.handleBarQty?.setText(model?.customSparePartList!![11].qty.toString())
                binding?.handleBarPrice?.setText(model?.customSparePartList!![11].price.toString())

                binding?.headSetQty?.setText(model?.customSparePartList!![12].qty.toString())
                binding?.headSetPrice?.setText(model?.customSparePartList!![12].price.toString())

                binding?.hubQty?.setText(model?.customSparePartList!![13].qty.toString())
                binding?.hubPrice?.setText(model?.customSparePartList!![13].price.toString())

                binding?.hydrolinesQty?.setText(model?.customSparePartList!![14].qty.toString())
                binding?.hydrolinesPrice?.setText(model?.customSparePartList!![14].price.toString())

                binding?.pedalsQty?.setText(model?.customSparePartList!![15].qty.toString())
                binding?.pedalsPrice?.setText(model?.customSparePartList!![15].price.toString())

                binding?.rotorQty?.setText(model?.customSparePartList!![16].qty.toString())
                binding?.rotorPrice?.setText(model?.customSparePartList!![16].price.toString())

                binding?.shockQty?.setText(model?.customSparePartList!![17].qty.toString())
                binding?.shockPrice?.setText(model?.customSparePartList!![17].price.toString())

                binding?.saddleQty?.setText(model?.customSparePartList!![18].qty.toString())
                binding?.saddlePrice?.setText(model?.customSparePartList!![18].price.toString())

                binding?.seatPostQty?.setText(model?.customSparePartList!![19].qty.toString())
                binding?.seatPostPrice?.setText(model?.customSparePartList!![19].price.toString())

                binding?.wheelSetQty?.setText(model?.customSparePartList!![20].qty.toString())
                binding?.wheelSetPrice?.setText(model?.customSparePartList!![20].price.toString())
            }, 500)
        }
    }

    private fun getPropertyNameList(
        spList: List<SparePartModel>,
        sparePartType: String
    ): List<String> {
        val result = ArrayList<String>()
        for (sp in spList) {
            if (sp.type == sparePartType) {
                result.add(sp.name!!)
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