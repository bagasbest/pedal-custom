package com.project.pedalcustom.ui.transaction

import android.os.Parcelable
import com.project.pedalcustom.ui.home.bike_custom.CustomSparePartModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionModel(
    var uid : String? = null,
    var userName : String? = null,
    var userId : String? = null,
    var userPhone : String? = null,
    var userAddress : String? = null,
    var totalPrice : Long? = 0L,
    var type : String? = null,
    var customSparePartList : ArrayList<CustomSparePartModel>? = null,
    var isAssembled : Boolean? = false,
    var category : String? = null,
    var qty : Long? = 0L,
    var color : String? = null,
    var image : String? = null,
    var date : String? = null,
    var productName : String? = null,
    var productId : String? = null,
    var status : String? = null,
    var paymentProof : String? = null,
    var rating : Double? = 0.0,
) : Parcelable