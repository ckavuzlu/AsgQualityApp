package com.example.asgkalitekontrol.view.model

data class TotalReport(
    var model : String ,
    var operator : String ,
    var operation : String ,
    var total_jobs : Int ,
    var finished_jobs : Int ,
    var uid : String ,
    var date : String ,
    var qcPersonnel : String,
    var timeinMilis : Long,
    var whichHour : Int
)