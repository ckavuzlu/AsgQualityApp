package com.example.asgkalitekontrol.view.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asgkalitekontrol.view.model.ErrorReport
import com.example.asgkalitekontrol.view.model.TotalReport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReportViewModel() : ViewModel(){
    var databese = FirebaseDatabase.getInstance().reference
    var errorReportList : MutableLiveData<List<ErrorReport>> = MutableLiveData()
    var totalReportList : MutableLiveData<List<TotalReport>> = MutableLiveData()
    var operatorErrorReportList : MutableLiveData<List<ErrorReport>> = MutableLiveData()
    var operatorTotalReportList : MutableLiveData<List<TotalReport>> = MutableLiveData()
    var tempList : MutableList<ErrorReport> = ArrayList()
    var tempTotalReport : MutableList<TotalReport> = ArrayList()

    fun getErrorReports(){
        databese.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("error_reports").children.forEach{
                   var report = ErrorReport(model = (it.value as HashMap<*,*>).get(key = "model").toString(),
                       error_jobs = (it.value as HashMap<*,*>).get(key = "error_jobs").toString().toInt(),
                       total_jobs = (it.value as HashMap<*,*>).get(key = "total_jobs").toString().toInt(),
                       operation = (it.value as HashMap<*,*>).get(key = "operation").toString(),
                       operator = (it.value as HashMap<*,*>).get(key = "operator").toString(),
                       uid = (it.value as HashMap<*,*>).get(key = "uid").toString(),
                        date = (it.value as HashMap<*,*>).get(key = "date").toString(),
                        qcPersonnel = (it.value as HashMap<*,*>).get(key = "qcPersonnel").toString(),
                        timeinMilis = (it.value as HashMap<*,*>).get(key = "timeinMilis").toString().toLong())
                    tempList.add(report)
                }
                    errorReportList.value = tempList
            }

        })
        tempList.clear()
    }

    fun getTotalReports(){
        databese.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("total_reports").children.forEach{
                    var report = TotalReport(model = (it.value as HashMap<*,*>).get(key = "model").toString(),
                        finished_jobs = (it.value as HashMap<*,*>).get(key = "finished_jobs").toString().toInt(),
                        total_jobs = (it.value as HashMap<*,*>).get(key = "total_jobs").toString().toInt(),
                        operation = (it.value as HashMap<*,*>).get(key = "operation").toString(),
                        operator = (it.value as HashMap<*,*>).get(key = "operator").toString(),
                        uid = (it.value as HashMap<*,*>).get(key = "uid").toString(),
                        date = (it.value as HashMap<*,*>).get(key = "date").toString(),
                        qcPersonnel = (it.value as HashMap<*,*>).get(key = "qcPersonnel").toString(),
                        timeinMilis = (it.value as HashMap<*,*>).get(key = "timeinMilis").toString().toLong(),
                        whichHour = (it.value as HashMap<*,*>).get(key = "whichHour").toString().toInt())
                    tempTotalReport.add(report)
                }
                totalReportList.value = tempTotalReport
            }

        })
        tempTotalReport.clear()
    }

    fun getOperatorReports(operator : String){
        Log.i("seasea",operator)
        databese.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("operators").child(operator).child("error_reports").children.forEach {
                    var report = ErrorReport(model = (it.value as HashMap<*,*>).get(key = "model").toString(),
                        error_jobs = (it.value as HashMap<*,*>).get(key = "error_jobs").toString().toInt(),
                        total_jobs = (it.value as HashMap<*,*>).get(key = "total_jobs").toString().toInt(),
                        operation = (it.value as HashMap<*,*>).get(key = "operation").toString(),
                        operator = (it.value as HashMap<*,*>).get(key = "operator").toString(),
                        uid = (it.value as HashMap<*,*>).get(key = "uid").toString(),
                        date = (it.value as HashMap<*,*>).get(key = "date").toString(),
                        qcPersonnel = (it.value as HashMap<*,*>).get(key = "qcPersonnel").toString(),
                        timeinMilis = (it.value as HashMap<*,*>).get(key = "timeinMilis").toString().toLong())
                    tempList.add(report)
                }
                snapshot.child("operators").child(operator).child("total_reports").children.forEach{
                    var totalReport = TotalReport(model = (it.value as HashMap<*,*>).get(key = "model").toString(),
                        finished_jobs = (it.value as HashMap<*,*>).get(key = "finished_jobs").toString().toInt(),
                        total_jobs = (it.value as HashMap<*,*>).get(key = "total_jobs").toString().toInt(),
                        operation = (it.value as HashMap<*,*>).get(key = "operation").toString(),
                        operator = (it.value as HashMap<*,*>).get(key = "operator").toString(),
                        uid = (it.value as HashMap<*,*>).get(key = "uid").toString(),
                        date = (it.value as HashMap<*,*>).get(key = "date").toString(),
                        qcPersonnel = (it.value as HashMap<*,*>).get(key = "qcPersonnel").toString(),
                        timeinMilis = (it.value as HashMap<*,*>).get(key = "timeinMilis").toString().toLong(),
                        whichHour = (it.value as HashMap<*,*>).get(key = "whichHour").toString().toInt())
                    tempTotalReport.add(totalReport)
                }
                operatorErrorReportList.value = tempList
                operatorTotalReportList.value = tempTotalReport
            }

        })
    }

    fun deleteReport(id : String, reportType : String){
        if(reportType == "error"){
            databese.child("error_reports").child(id).removeValue()
        }
        if(reportType == "total"){
            databese.child("total_reports").child(id).removeValue()
        }
    }
}