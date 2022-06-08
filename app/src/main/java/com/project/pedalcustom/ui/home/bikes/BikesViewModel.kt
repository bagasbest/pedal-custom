package com.project.pedalcustom.ui.home.bikes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BikesViewModel : ViewModel() {

    private val bikesList = MutableLiveData<ArrayList<BikesModel>>()
    private val listData = ArrayList<BikesModel>()
    private val TAG = BikesViewModel::class.java.simpleName


    fun setListBikes() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("bikes")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = BikesModel()

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
                    bikesList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListBikesByQuery(query: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("bikes")
                .whereGreaterThanOrEqualTo("nameTemp", query)
                .whereLessThanOrEqualTo("nameTemp", query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = BikesModel()

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
                    bikesList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getBikes() : LiveData<ArrayList<BikesModel>> {
        return bikesList
    }



}