package com.example.asgkalitekontrol.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentAddTotalWorkReportBinding
import com.example.asgkalitekontrol.view.activity.MainActivity
import com.example.asgkalitekontrol.view.model.PersonnelInstance
import com.example.asgkalitekontrol.view.model.ErrorReport
import com.example.asgkalitekontrol.view.model.TotalReport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddTotalWorkFragment : Fragment() , AdapterView.OnItemSelectedListener{

    lateinit var totalWorkBinding: FragmentAddTotalWorkReportBinding
    var databese = FirebaseDatabase.getInstance().reference
    lateinit var mainAct : MainActivity
    var spinnerItem : Int = 0
    var operationList : List<String> = ArrayList()
    var operatorList : List<String> = ArrayList()
    var modelList : List<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainAct = context as MainActivity
        totalWorkBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_total_work_report, container, false)

        return totalWorkBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListsFromDatabase()
        setButtons()
        setSpinner()
    }

    fun getListsFromDatabase(){
        databese.addValueEventListener(object : ValueEventListener {
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

    fun setAutoTextAdapters(){
        var operatationAdapter : ArrayAdapter<String> = ArrayAdapter(mainAct, R.layout.support_simple_spinner_dropdown_item, operationList)
        var modelAdapter : ArrayAdapter<String> = ArrayAdapter(mainAct, R.layout.support_simple_spinner_dropdown_item, modelList)
        var operatorAdapter : ArrayAdapter<String> = ArrayAdapter(mainAct, R.layout.support_simple_spinner_dropdown_item, operatorList)
        totalWorkBinding.autotextOperatationName.setAdapter(operatationAdapter)
        totalWorkBinding.autotextOperatationName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Operasyon Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return operationList.contains(text.toString())

            }

        }

        totalWorkBinding.autotextOperatorsName.setAdapter(operatorAdapter)
        totalWorkBinding.autotextOperatorsName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Operatör Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return operatorList.contains(text.toString())
            }

        }

        totalWorkBinding.autotextModelName.setAdapter(modelAdapter)
        totalWorkBinding.autotextModelName.validator = object : AutoCompleteTextView.Validator{
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

    fun setButtons(){

        totalWorkBinding.btnAddReport.setOnClickListener {

            if(totalWorkBinding.autotextModelName.text.toString() != "" &&
                totalWorkBinding.autotextOperatorsName.text.toString() != "" &&
                totalWorkBinding.autotextOperatationName.text.toString() != "" &&
                totalWorkBinding.edtChecked.text.toString() != "" &&
                totalWorkBinding.edtFinishedJobs.text.toString() != ""  &&
                    spinnerItem != 0 ){

                var report = TotalReport(
                    model = totalWorkBinding.autotextModelName.text.toString(),
                    operator = totalWorkBinding.autotextOperatorsName.text.toString(),
                    operation = totalWorkBinding.autotextOperatationName.text.toString(),
                    finished_jobs = totalWorkBinding.edtFinishedJobs.text.toString().toInt(),
                    total_jobs = totalWorkBinding.edtChecked.text.toString().toInt(),
                    uid = UUID.randomUUID().toString(),
                    date = Calendar.getInstance().time.toLocaleString(),
                    qcPersonnel = PersonnelInstance.personnelInstance?.name!!,
                    timeinMilis = Calendar.getInstance().timeInMillis,
                    whichHour = spinnerItem
                )

                databese.child("total_reports").child(report.uid).setValue(report)
                databese.child("operators").child(report.operator).child("total_reports").child(report.uid).setValue(report)
                Toast.makeText(context, "Rapor başarılı bir şekilde eklendi",Toast.LENGTH_SHORT).show()
//                reportFragmentBinding.autotextOperatorsName.setText("")
//                reportFragmentBinding.autotextOperatationName.setText("")
//                reportFragmentBinding.edtErrors.setText("")
//                reportFragmentBinding.edtChecked.setText("")
            }else{
                Toast.makeText(context, "Lütfen Tüm Alanları Doldurunuz",Toast.LENGTH_SHORT).show()
            }


        }

        totalWorkBinding.spinnerHour.onItemSelectedListener = this

    }
    fun setSpinner(){
        ArrayAdapter.createFromResource(mainAct, R.array.which_hour, android.R.layout.simple_spinner_dropdown_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            totalWorkBinding.spinnerHour.adapter = adapter
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        spinnerItem = position+1
    }

}