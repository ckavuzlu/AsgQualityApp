package com.example.asgkalitekontrol.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentAddReportBinding
import com.example.asgkalitekontrol.view.activity.MainActivity
import com.example.asgkalitekontrol.view.model.PersonnelInstance
import com.example.asgkalitekontrol.view.model.ErrorReport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddReportFragment : Fragment(){

    var databese = FirebaseDatabase.getInstance().reference
    lateinit var reportFragmentBinding : FragmentAddReportBinding
    lateinit var mainAct : MainActivity
    var operationList : List<String> = ArrayList()
    var operatorList : List<String> = ArrayList()
    var modelList : List<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainAct = context as MainActivity
        reportFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_report,container,false)
        mainAct.mainActivityBinding.bottomNavBarPersonnel.visibility = View.VISIBLE
        return reportFragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListsFromDatabase()

        setButtons()

    }


    fun getListsFromDatabase(){
        databese.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                operatorList = (snapshot.child("operators").value as HashMap<String, String>).keys.toList()
                operationList = (snapshot.child("operatations").value as HashMap<String, String>).keys.toList()
                modelList = (snapshot.child("models").value as HashMap<String, String>).keys.toList()
                setAutoTextAdapters()
            }

        })
    }

    fun setButtons(){

        reportFragmentBinding.btnAddErrorReport.setOnClickListener {

            if(reportFragmentBinding.autotextModelName.text.toString() != "" &&
                reportFragmentBinding.autotextOperatorsName.text.toString() != "" &&
                reportFragmentBinding.autotextOperatationName.text.toString() != "" &&
                reportFragmentBinding.edtChecked.text.toString() != "" &&
                reportFragmentBinding.edtErrors.text.toString() != ""){

                var report = ErrorReport(
                    model = reportFragmentBinding.autotextModelName.text.toString(),
                    operator = reportFragmentBinding.autotextOperatorsName.text.toString(),
                    operation = reportFragmentBinding.autotextOperatationName.text.toString(),
                    error_jobs = reportFragmentBinding.edtErrors.text.toString().toInt(),
                    total_jobs = reportFragmentBinding.edtChecked.text.toString().toInt(),
                    uid = UUID.randomUUID().toString(),
                    date = Calendar.getInstance().time.toLocaleString(),
                    qcPersonnel = PersonnelInstance.personnelInstance?.name!!,
                    timeinMilis = Calendar.getInstance().timeInMillis
                )

                databese.child("error_reports").child(report.uid).setValue(report)
                databese.child("operators").child(report.operator).child("error_reports").child(report.uid).setValue(report)
                Toast.makeText(context, "Rapor başarılı bir şekilde eklendi",Toast.LENGTH_SHORT).show()
//                reportFragmentBinding.autotextOperatorsName.setText("")
//                reportFragmentBinding.autotextOperatationName.setText("")
//                reportFragmentBinding.edtErrors.setText("")
//                reportFragmentBinding.edtChecked.setText("")
            }else{
                Toast.makeText(context, "Lütfen Tüm Alanları Doldurunuz",Toast.LENGTH_SHORT).show()
            }


        }
    }

    fun setAutoTextAdapters(){
        var operatationAdapter : ArrayAdapter<String> = ArrayAdapter(mainAct, R.layout.support_simple_spinner_dropdown_item, operationList)
        var modelAdapter : ArrayAdapter<String> = ArrayAdapter(mainAct, R.layout.support_simple_spinner_dropdown_item, modelList)
        var operatorAdapter : ArrayAdapter<String> = ArrayAdapter(mainAct, R.layout.support_simple_spinner_dropdown_item, operatorList)
        reportFragmentBinding.autotextOperatationName.setAdapter(operatationAdapter)
        reportFragmentBinding.autotextOperatationName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Operasyon Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return operationList.contains(text.toString())
            }

        }

        reportFragmentBinding.autotextOperatorsName.setAdapter(operatorAdapter)
        reportFragmentBinding.autotextOperatorsName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Operatör Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return operatorList.contains(text.toString())
            }

        }

        reportFragmentBinding.autotextModelName.setAdapter(modelAdapter)
        reportFragmentBinding.autotextModelName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Model Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return modelList.contains(text.toString())
            }

        }


    }


}