package com.project.pedalcustom.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

object User {

    fun saveImageUser(uid: String?, image: String, context: Context) {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid!!)
            .update("image", image)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(context, "Success update image", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failure update image", Toast.LENGTH_SHORT).show()
                }
            }
    }

}