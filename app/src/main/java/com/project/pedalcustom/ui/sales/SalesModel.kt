package com.project.pedalcustom.ui.sales

data class SalesModel(
    var category : String? = null,
    var productName : String? = null,
    var date : Long? = 0L,
    var month : Long? = 0L,
    var year : Long? = 0L,
    var timeInMillis : Long? = 0L,
)