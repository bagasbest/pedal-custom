package com.project.pedalcustom.ui.sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SalesViewModel : ViewModel() {

    private val salesList = MutableLiveData<ArrayList<SalesModel>>()
    private val listData = ArrayList<SalesModel>()
    private val TAG = SalesViewModel::class.java.simpleName


    fun setSalesMonthly(lastMonth: Long, currentMonth: Long) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("log_transaction")
                .whereGreaterThanOrEqualTo("month", lastMonth)
                .whereLessThanOrEqualTo("month", currentMonth)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = SalesModel()


                        model.date = document.data["date"] as Long
                        model.year = document.data["year"] as Long
                        model.month = document.data["month"] as Long
                        model.timeInMillis = document.data["timeInMillis"] as Long
                        model.productName = document.data["productName"].toString()
                        model.category = document.data["category"].toString()

                        listData.add(model)
                    }
                    salesList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setSalesDate(currentMonday: Long, currentDate: Long) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("log_transaction")
                .whereGreaterThanOrEqualTo("timeInMillis", currentMonday)
                .whereLessThanOrEqualTo("timeInMillis", currentDate)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = SalesModel()


                        model.date = document.data["date"] as Long
                        model.year = document.data["year"] as Long
                        model.month = document.data["month"] as Long
                        model.timeInMillis = document.data["timeInMillis"] as Long
                        model.productName = document.data["productName"].toString()
                        model.category = document.data["category"].toString()

                        listData.add(model)
                    }
                    salesList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setSalesYearly(currentYear: Long) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("log_transaction")
                .whereEqualTo("year", currentYear)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = SalesModel()


                        model.date = document.data["date"] as Long
                        model.year = document.data["year"] as Long
                        model.month = document.data["month"] as Long
                        model.timeInMillis = document.data["timeInMillis"] as Long
                        model.productName = document.data["productName"].toString()
                        model.category = document.data["category"].toString()

                        listData.add(model)
                    }
                    salesList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getSales() : LiveData<ArrayList<SalesModel>> {
        return salesList
    }
}