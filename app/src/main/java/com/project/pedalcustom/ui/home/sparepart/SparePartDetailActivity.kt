package com.project.pedalcustom.ui.home.sparepart

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.project.pedalcustom.R
import com.project.pedalcustom.databinding.ActivitySparePartDetailBinding
import java.text.DecimalFormat

class SparePartDetailActivity : AppCompatActivity() {
    private var binding : ActivitySparePartDetailBinding? = null
    private var model: SparePartModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySparePartDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Glide.with(this)
            .load(R.drawable.bike_part)
            .into(binding!!.imageView2)

        model = intent.getParcelableExtra(EXTRA_DATA)
        initSlider()
        getSparePartColor()
        val formatter = DecimalFormat("#,###")

        binding?.price?.text = "Rp." + formatter.format(model?.price)
        binding?.code?.text = "#${model?.code}"
        binding?.name?.text = model?.name
        binding?.description?.text = model?.description
        binding?.specification?.text = model?.specification
        binding?.type?.text = "Type: ${model?.type}"
        binding?.sold?.text = "Sold: ${model?.sold}"




        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.addToCartBtn?.setOnClickListener {

        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getSparePartColor() {
        val listColor: List<String> = model?.color?.split(",")!!.map { it.trim() }
        var words = ""
        for(i in listColor.indices) {

            words = listColor[i]

            val valueTV = TextView(this)
            valueTV.text = words
            valueTV.id = i
            valueTV.setTextColor(resources.getColor(R.color.black))
            valueTV.background = resources.getDrawable(R.drawable.bg_border)
            valueTV.setPadding(20, 5, 20, 5)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginStart = 16
            valueTV.layoutParams = params
            (binding?.llColor as LinearLayout).addView(valueTV)
        }
    }

    private fun initSlider() {
        val imageList: ArrayList<SlideModel> = ArrayList() // Create image list

        for (i in model?.image!!.indices) {
            imageList.add(SlideModel(model?.image!![i], ScaleTypes.CENTER_CROP))
        }

        binding?.sliderImage?.setImageList(imageList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}