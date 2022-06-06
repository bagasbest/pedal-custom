package com.project.pedalcustom.ui.home.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CartViewModel : ViewModel() {

    private val cartList = MutableLiveData<ArrayList<CartModel>>()
    private val listData = ArrayList<CartModel>()
    private val TAG = CartViewModel::class.java.simpleName


    fun setCartList(uid: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("cart")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = CartModel()

                        model.userId = document.data["userId"].toString()
                        model.uid = document.data["uid"].toString()
                        model.productId = document.data["productId"].toString()
                        model.name = document.data["name"].toString()
                        model.type = document.data["type"].toString()
                        model.category = document.data["category"].toString()
                        model.qty = document.data["qty"] as Long
                        model.color = document.data["color"].toString()
                        model.image = document.data["image"].toString()
                        model.isAssembled = document.data["isAssembled"] as Boolean
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.customSparePartList = document.toObject(CartModel::class.java).customSparePartList

                        listData.add(model)
                    }
                    cartList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getCart() : LiveData<ArrayList<CartModel>> {
        return cartList
    }



}