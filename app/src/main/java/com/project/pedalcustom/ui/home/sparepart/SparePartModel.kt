package com.project.pedalcustom.ui.home.sparepart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SparePartModel(

    var uid : String? = null,
    var name : String? = null,
    var code : String? = null,
    var type : String? = null,
    var color : String? = null,
    var description : String? = null,
    var specification : String? = null,
    var price : Long? = 0L,
    var sold : Long? = 0L,
    var image : ArrayList<String>? = null,
    var favoriteBy : ArrayList<String> ? = null,

    ) : Parcelable