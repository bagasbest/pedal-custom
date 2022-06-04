package com.project.pedalcustom.ui.home.bike_custom.load_bike

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LoadBikeViewModel : ViewModel() {

    private val loadBikeList = MutableLiveData<ArrayList<LoadBikeModel>>()
    private val listData = ArrayList<LoadBikeModel>()
    private val TAG = LoadBikeViewModel::class.java.simpleName


    fun setListSaveData() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("custom_save_data")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = LoadBikeModel()

                        model.userId = document.data["userId"].toString()
                        model.uid = document.data["uid"].toString()
                        model.saveName = document.data["saveName"].toString()
                        model.bikeType = document.data["bikeType"].toString()
                        model.isAssembled = document.data["isAssembled"] as Boolean
                        model.totalPrice = document.data["totalPrice"] as Long
                        model.customSparePartList = document.toObject(LoadBikeModel::class.java).customSparePartList

                        listData.add(model)
                    }
                    loadBikeList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getSaveData() : LiveData<ArrayList<LoadBikeModel>> {
        return loadBikeList
    }


}