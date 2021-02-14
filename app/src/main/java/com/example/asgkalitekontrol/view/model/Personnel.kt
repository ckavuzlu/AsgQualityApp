package com.example.asgkalitekontrol.view.model

object PersonnelInstance{
    var personnelInstance : Personnel? = null
}

class Personnel(
    var name : String,
    var username : String,
    var password : String,
    var accountType : String
)