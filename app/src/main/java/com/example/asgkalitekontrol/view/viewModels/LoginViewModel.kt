package com.example.asgkalitekontrol.view.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asgkalitekontrol.view.model.Personnel
import com.example.asgkalitekontrol.view.model.PersonnelInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginViewModel : ViewModel(){

    var databese = FirebaseDatabase.getInstance().reference
    var userName: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    var personnel: MutableLiveData<Personnel> = MutableLiveData()
    var warningLiveData: MutableLiveData<String> = MutableLiveData()
    var progressLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun logIn(){
        progressLiveData.value = true
        val listener = databese.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                warningLiveData.value = "Bir Hata oluştu, daha sonra tekrar deneyiniz."
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("users").children.forEach {
                    if(it.key == userName.value){
                        Log.i("seasea","${it.value}")
                        if((it.value as HashMap<*, *>).get(key = "password") == password.value){
                            var persObj = Personnel(
                                name = (it.value as HashMap<*, *>).get(key = "name").toString(),
                                username = (it.value as HashMap<*, *>).get(key = "username").toString(),
                                password = (it.value as HashMap<*, *>).get(key = "password").toString(),
                                accountType = (it.value as HashMap<*, *>).get(key = "accountType").toString())
                            personnel.value = persObj
                            PersonnelInstance.personnelInstance = persObj
                            progressLiveData.value = false
                        }
                }
                }
                if(personnel.value == null){
                    warningLiveData.value = "Giriş Bilgileri Hatalı Lütfen Tekrar Deneyiniz"
                    progressLiveData.value = false
                }
            }
        })
    }
}