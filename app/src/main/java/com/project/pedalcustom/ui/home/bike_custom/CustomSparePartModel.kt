package com.project.pedalcustom.ui.home.bike_custom

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomSparePartModel(
    var name : String? = null,
    var productId : String? = null,
    var qty : Int? = 0,
    var price : Long? = 0L,
) : Parcelable