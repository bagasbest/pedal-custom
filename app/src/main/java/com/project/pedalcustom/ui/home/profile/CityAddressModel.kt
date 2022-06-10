package com.project.pedalcustom.ui.home.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityAddressModel(
    var city : String? = null,
    var address : String? = null,
) : Parcelable