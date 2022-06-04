package com.project.pedalcustom.ui.home.bike_custom.load_bike

import android.os.Parcelable
import com.project.pedalcustom.ui.home.bike_custom.CustomSparePartModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoadBikeModel(
    var uid : String? = null,
    var userId : String? = null,
    var totalPrice : Long? = 0L,
    var saveName : String? = null,
    var bikeType : String? = null,
    var customSparePartList : ArrayList<CustomSparePartModel>? = null,
    var isAssembled : Boolean? = false,
) : Parcelable
