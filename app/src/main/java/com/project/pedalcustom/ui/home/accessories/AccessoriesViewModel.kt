package com.project.pedalcustom.ui.home.accessories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AccessoriesViewModel : ViewModel() {

    private val accessoriesList = MutableLiveData<ArrayList<AccessoriesModel>>()
    private val listData = ArrayList<AccessoriesModel>()
    private val TAG = AccessoriesViewModel::class.java.simpleName


    fun setListAccessories() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("accessories")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = AccessoriesModel()

                        model.name = document.data["name"].toString()
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
                    accessoriesList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getAccessories() : LiveData<ArrayList<AccessoriesModel>> {
        return accessoriesList
    }


}