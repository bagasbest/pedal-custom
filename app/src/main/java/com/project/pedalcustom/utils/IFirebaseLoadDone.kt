package com.project.pedalcustom.utils

import com.project.pedalcustom.ui.home.sparepart.SparePartModel

interface IFirebaseLoadDone {
    fun onFirebaseLoadSuccess(sparePart: List<SparePartModel>)
    fun onFirebaseLoadFailed(message: String)
}