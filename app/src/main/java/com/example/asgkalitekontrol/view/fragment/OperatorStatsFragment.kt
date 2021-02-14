package com.example.asgkalitekontrol.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentOperatorStatsBinding
import com.example.asgkalitekontrol.view.activity.MainActivity
import com.example.asgkalitekontrol.view.model.ErrorReport
import com.example.asgkalitekontrol.view.model.TotalReport
import com.example.asgkalitekontrol.view.viewModels.ReportViewModel
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.exp

class OperatorStatsFragment : Fragment(){

    lateinit var operatorStatsBinding: FragmentOperatorStatsBinding
    lateinit var mainAct : MainActivity
    private lateinit var database: DatabaseReference
    val reportViewModel : ReportViewModel by viewModels()
    val args : OperatorStatsFragmentArgs by navArgs()
    var errorReportList : MutableList<ErrorReport> = ArrayList()
    var totalReportList : MutableList<TotalReport> = ArrayList()
    var filteredErrorReportList : List<ErrorReport> = ArrayList()
    var filteredTotalReport : List<TotalReport> = ArrayList()
    var operator : String = ""
    var expectedJobs : Int = 0
    var finishedJobs : Int = 0
    var mistakes : Int = 0
    val calender = Calendar.getInstance()
    val year = calender.get(Calendar.YEAR)
    val month = calender.get(Calendar.MONTH)
    val day = calender.get(Calendar.DAY_OF_MONTH)
    var startTime : Long = 0
    var endTime : Long = 0
    var model : String = ""

    var modelList : List<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        operatorStatsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_operator_stats, container, false)
        return operatorStatsBinding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        mainAct = context as MainActivity

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.setOnTouchListener(View.OnTouchListener { v, event ->
//            operatorStatsBinding.txtModelName.clearFocus()
//            return@OnTouchListener true
//        })
        reportViewModel.getOperatorReports(args.operator)
        getListsFromDatabase()
        listenReports()
        listenButtons()
    }

    fun listenButtons(){

        operatorStatsBinding.startDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(mainAct, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                operatorStatsBinding.startDate.setText("Başlangıç Tarihi : $dayOfMonth/${(monthOfYear+1)}/$year")
                val myDate = "$dayOfMonth/${(monthOfYear+1)}/$year 00:00:01"
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                startTime = sdf.parse(myDate).time

            }, year, month, day)
            datePickerDialog.show()
        }

        operatorStatsBinding.endDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(mainAct, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                operatorStatsBinding.endDate.setText("Bitiş Tarihi : $dayOfMonth/${(monthOfYear+1)}/$year")
                val myDate = "$dayOfMonth/${(monthOfYear+1)}/$year 23:59:59"
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                endTime = sdf.parse(myDate).time

            }, year, month, day)
            datePickerDialog.show()
        }

        operatorStatsBinding.btnApply.setOnClickListener {
            operatorStatsBinding.btnApply.requestFocus()
            filterData(model)
        }
    }

    fun listenReports(){
        reportViewModel.operatorTotalReportList.observe(viewLifecycleOwner, Observer {
            totalReportList = it as MutableList<TotalReport>
            pushDataToView(totalReportList, errorReportList)
        })
        reportViewModel.operatorErrorReportList.observe(viewLifecycleOwner, Observer {
            errorReportList = it as MutableList<ErrorReport>
            pushDataToView(totalReportList, errorReportList)
        })

    }

    fun filterData(model : String) {
        Log.i("seasea"," filtere girdi")
        filteredErrorReportList = errorReportList
        filteredTotalReport = totalReportList
        if(model != ""){
            filteredErrorReportList = filteredErrorReportList.filter { it.model == model }
            filteredTotalReport = filteredTotalReport.filter { it.model == model }
        }

        if (operatorStatsBinding.startDate.text.toString()
                .substringAfter("Başlangıç Tarihi : ", "") != ""
        ) {
            filteredTotalReport = filteredTotalReport.filter {
                it.timeinMilis > startTime
            }
        }
        if (operatorStatsBinding.endDate.text.toString()
                .substringAfter("Bitiş Tarihi : ", "") != "          "
        ) {
            filteredTotalReport = filteredTotalReport.filter {
                it.timeinMilis < endTime
            }

        }
        if (operatorStatsBinding.startDate.text.toString()
                .substringAfter("Başlangıç Tarihi : ", "") != ""
        ) {
            filteredErrorReportList = filteredErrorReportList.filter {
                it.timeinMilis > startTime
            }
        }
        if (operatorStatsBinding.endDate.text.toString()
                .substringAfter("Bitiş Tarihi : ", "") != "          "
        ) {
            filteredErrorReportList = filteredErrorReportList.filter {
                it.timeinMilis < endTime
            }
        }
        pushDataToView(filteredTotalReport , filteredErrorReportList)
    }

    fun pushDataToView(totalList : List<TotalReport> = ArrayList() ,errorList : List<ErrorReport> = ArrayList()){
        expectedJobs = 0
        finishedJobs = 0
        mistakes = 0
            totalList.forEach {
                expectedJobs += it.total_jobs
                finishedJobs += it.finished_jobs
                Log.i("seasea","Yeni veri hesaplanıyor")
            }
            errorList.forEach {
                mistakes += it.error_jobs
            }
        operatorStatsBinding.txtOperatorName.setText(args.operator)
        operatorStatsBinding.txtExpectedJobs.setText("Beklenen İş : $expectedJobs")
        operatorStatsBinding.totalJobs.setText("Toplam İş : $finishedJobs ( %${((finishedJobs.toFloat()/expectedJobs.toFloat())*100).toInt()} )")
        operatorStatsBinding.txtMistakes.setText("Yapılan Hata : $mistakes (  %${((mistakes.toFloat()/expectedJobs.toFloat())*100).toInt()} )")
        Log.i("seasea","Yeni veri ekrana basıldı")
    }

    fun getListsFromDatabase(){
        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                modelList = (snapshot.child("models").value as HashMap<String, String>).keys.toList()
                setAutoTextAdapters()
            }

        })
    }

    fun setAutoTextAdapters(){
        var modelAdapter : ArrayAdapter<String> = ArrayAdapter(mainAct, R.layout.support_simple_spinner_dropdown_item, modelList)


        operatorStatsBinding.txtModelName.setAdapter(modelAdapter)
        operatorStatsBinding.txtModelName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Toast.makeText(mainAct, "Model Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                model = ""
                filterData(model)
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                model = text.toString()
                if(modelList.contains(model)){
                    filterData(model)
                    Log.i("seasea","filtrelendi text : ${model}")
                }
                return modelList.contains(model)
            }

        }
        operatorStatsBinding.txtModelName.setOnItemClickListener { parent, view, position, id ->
            operatorStatsBinding.txtModelName.clearFocus()
        }
        operatorStatsBinding.txtModelName.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                operatorStatsBinding.txtModelName.clearFocus()
            }
            return@setOnEditorActionListener false
        }


    }

}