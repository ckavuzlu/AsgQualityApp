package com.example.asgkalitekontrol.view.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginViewModel : ViewModel(){

    var databese = FirebaseDatabase.getInstance().reference
    var userName: MutableLiveData<String?> = MutableLiveData()
    var password: MutableLiveData<String?> = MutableLiveData()
    var warningLiveData: MutableLiveData<String> = MutableLiveData()

    fun logIn(){
        userName.value
        password.value
        val listener = databese.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                warningLiveData.value = "Bir Hata oluştu, daha sonra tekrar deneyiniz."
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("users").children.forEach {
                    if(it.key == userName.value){
                        if(it.child("${it.key}").child("password").value == password.value){
                            Log.i("seasea","Uğraşma Başardın")
                        }
                }
                }
            }
        })
    }
}