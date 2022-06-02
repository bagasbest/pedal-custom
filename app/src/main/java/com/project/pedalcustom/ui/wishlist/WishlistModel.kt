package com.project.pedalcustom.ui.wishlist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WishlistModel (
    var uid : String? = null,
    var userId : String? = null,
    var productId : String? = null,
    var collection : String? = null,
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