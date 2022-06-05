package com.project.pedalcustom.ui.home.cart

import android.os.Parcelable
import com.project.pedalcustom.ui.home.bike_custom.CustomSparePartModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartModel(
    var uid: String? = null,
    var name: String? = null,
    var userId: String? = null,
    var totalPrice: Long? = 0L,
    var type: String? = null,
    var customSparePartList: ArrayList<CustomSparePartModel>? = null,
    var isAssembled: Boolean? = false,
    var category: String? = null,
    var qty: Long? = 0L,
    var color: String? = null,
    var image: String? = null,
) : Parcelable