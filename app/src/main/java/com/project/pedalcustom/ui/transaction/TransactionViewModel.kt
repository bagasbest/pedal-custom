package com.project.pedalcustom.ui.transaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class TransactionViewModel : ViewModel() {

    private val transactionList = MutableLiveData<ArrayList<TransactionModel>>()
    private val listData = ArrayList<TransactionModel>()
    private val TAG = TransactionViewModel::class.java.simpleName


    fun setTransactionListByUid(userId: String, status: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("transaction")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = TransactionModel()

                        model.userId = document.data["userId"].toString()
                        model.userName = document.data["userName"].toString()
                        model.userPhone = document.data["userPhone"].toString()
                        model.userAddress = document.data["userAddress"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.date = document.data["date"].toString()
                        model.status = document.data["status"].toString()
                        model.uid = document.data["uid"].toString()
                        model.productId = document.data["productId"].toString()
                        model.productName = document.data["productName"].toString()
                        model.type = document.data["type"].toString()
                        model.category = document.data["category"].toString()
                        model.qty = document.data["qty"] as Long
                        model.color = document.data["color"].toString()
                        model.image = document.data["image"].toString()
                        model.rating = document.data["rating"] as Double
                        model.isAssembled = document.data["isAssembled"] as Boolean
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.customSparePartList = document.toObject(TransactionModel::class.java).customSparePartList

                        listData.add(model)
                    }
                    transactionList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setTransactionListAll(status: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("transaction")
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = TransactionModel()

                        model.userId = document.data["userId"].toString()
                        model.userName = document.data["userName"].toString()
                        model.userPhone = document.data["userPhone"].toString()
                        model.userAddress = document.data["userAddress"].toString()
                        model.paymentProof = document.data["paymentProof"].toString()
                        model.date = document.data["date"].toString()
                        model.status = document.data["status"].toString()
                        model.uid = document.data["uid"].toString()
                        model.productId = document.data["productId"].toString()
                        model.productName = document.data["productName"].toString()
                        model.type = document.data["type"].toString()
                        model.category = document.data["category"].toString()
                        model.qty = document.data["qty"] as Long
                        model.rating = document.data["rating"] as Double
                        model.color = document.data["color"].toString()
                        model.image = document.data["image"].toString()
                        model.isAssembled = document.data["isAssembled"] as Boolean
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.customSparePartList = document.toObject(TransactionModel::class.java).customSparePartList

                        listData.add(model)
                    }
                    transactionList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getTransaction() : LiveData<ArrayList<TransactionModel>> {
        return transactionList
    }




}