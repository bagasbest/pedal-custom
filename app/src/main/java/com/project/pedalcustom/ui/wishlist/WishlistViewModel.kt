package com.project.pedalcustom.ui.wishlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class WishlistViewModel : ViewModel() {

    private val wishList = MutableLiveData<ArrayList<WishlistModel>>()
    private val listData = ArrayList<WishlistModel>()
    private val TAG = WishlistViewModel::class.java.simpleName


    fun setWishList(userId : String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("wishlist")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = WishlistModel()

                        model.name = document.data["name"].toString()
                        model.productId = document.data["productId"].toString()
                        model.userId = document.data["userId"].toString()
                        model.collection = document.data["collection"].toString()
                        model.uid = document.data["uid"].toString()
                        model.description = document.data["description"].toString()
                        model.type = document.data["type"].toString()
                        model.code = document.data["code"].toString()
                        model.color = document.data["color"].toString()
                        model.sold = document.data["sold"] as Long
                        model.specification = document.data["specification"].toString()
                        model.image = document.data["image"] as ArrayList<String>
                        model.favoriteBy = document.data["favoriteBy"] as ArrayList<String>
                        model.price = document.data["price"] as Long

                        listData.add(model)
                    }
                    wishList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getWishList() : LiveData<ArrayList<WishlistModel>> {
        return wishList
    }



}