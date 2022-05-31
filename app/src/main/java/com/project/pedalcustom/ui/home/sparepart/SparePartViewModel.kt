package com.project.pedalcustom.ui.home.sparepart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
class SparePartViewModel : ViewModel() {

    private val sparePartList = MutableLiveData<ArrayList<SparePartModel>>()
    private val listData = ArrayList<SparePartModel>()
    private val TAG = SparePartViewModel::class.java.simpleName


    fun setListSparePart() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("spare_parts")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = SparePartModel()

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
                    sparePartList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getSparePart() : LiveData<ArrayList<SparePartModel>> {
        return sparePartList
    }


}