package com.example.asgkalitekontrol.view.model

data class ErrorReport(
    var model : String ,
    var operator : String ,
    var operation : String ,
    var total_jobs : Int ,
    var error_jobs : Int ,
    var uid : String ,
    var date : String ,
    var qcPersonnel : String,
    var timeinMilis : Long
)