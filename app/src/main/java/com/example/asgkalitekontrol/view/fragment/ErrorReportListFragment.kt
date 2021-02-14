package com.example.asgkalitekontrol.view.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentErrorReportListBinding
import com.example.asgkalitekontrol.view.activity.MainActivity
import com.example.asgkalitekontrol.view.adapter.ErrorReportListAdapter
import com.example.asgkalitekontrol.view.model.ErrorReport
import com.example.asgkalitekontrol.view.viewModels.ReportViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_login.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ErrorReportListFragment : Fragment() {
    lateinit var reportListBinding: FragmentErrorReportListBinding
    lateinit var mainAct: MainActivity
    var errorReportList : MutableList<ErrorReport> = ArrayList()
    var filteredErrorReportList : List<ErrorReport> = ArrayList()
    lateinit var errorReportListAdapter : ErrorReportListAdapter
    val reportViewModel : ReportViewModel by viewModels()
    var databese = FirebaseDatabase.getInstance().reference

    var operationList : List<String> = ArrayList()
    var operatorList : List<String> = ArrayList()
    var modelList : List<String> = ArrayList()

    val calender = Calendar.getInstance()
    val year = calender.get(Calendar.YEAR)
    val month = calender.get(Calendar.MONTH)
    val day = calender.get(Calendar.DAY_OF_MONTH)
    var startTime : Long = 0
    var endTime : Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainAct = context as MainActivity

        reportListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_error_report_list, container,
            false)
        return reportListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportViewModel.getErrorReports()
        listenReports()
        listenButtons()
        getListsFromDatabase()
        setSwipeListener()
    }

    fun setCardClickListener() {

        errorReportListAdapter.setOnItemClickListener {
            val action = ErrorReportListFragmentDirections.actionReportListFragmentToOperatorStatsFragment(it)
            findNavController().navigate(action)
        }

    }

    fun listenReports(){
        reportViewModel.errorReportList.observe(viewLifecycleOwner, Observer {
            errorReportList = it as MutableList<ErrorReport>
            reloadRecyclerView(it)

        })

    }

    fun reloadRecyclerView(errorList : List<ErrorReport>){
        errorReportList.sortBy { it.timeinMilis }
        errorReportListAdapter = ErrorReportListAdapter(errorList)
        reportListBinding.recyclerReportList.layoutManager = LinearLayoutManager(requireContext())
        reportListBinding.recyclerReportList.setHasFixedSize(true)
        reportListBinding.recyclerReportList.adapter = errorReportListAdapter
        setCardClickListener()

    }

    fun listenButtons(){

        reportListBinding.btnFilter.setOnClickListener {
            reportListBinding.btnFilter.visibility = View.GONE
            reportListBinding.txtReports.visibility = View.GONE
            reportListBinding.filterConstraint.visibility = View.VISIBLE

            val param = reportListBinding.recyclerReportList.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,0,0,0)
            reportListBinding.recyclerReportList.layoutParams = param


        }

        reportListBinding.btnApply.setOnClickListener {
            reportListBinding.btnFilter.visibility = View.VISIBLE
            reportListBinding.filterConstraint.visibility = View.GONE
            reportListBinding.txtReports.visibility = View.VISIBLE
            val param = reportListBinding.recyclerReportList.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,140,0,0)
            reportListBinding.recyclerReportList.layoutParams = param

                filteredErrorReportList = errorReportList

            if(reportListBinding.autotextModelName.text.toString() != ""){
                filteredErrorReportList = filteredErrorReportList.filter {
                    it.model == reportListBinding.autotextModelName.text.toString()
                }
            }
            if(reportListBinding.autotextOperatorsName.text.toString() != ""){
                filteredErrorReportList = filteredErrorReportList.filter {
                    it.operator == reportListBinding.autotextOperatorsName.text.toString()
                }
            }
            if(reportListBinding.autotextOperatationName.text.toString() != ""){
                filteredErrorReportList = filteredErrorReportList.filter {
                    it.operation == reportListBinding.autotextOperatationName.text.toString()
                }
            }
            if(reportListBinding.startDate.text.toString().substringAfter("Başlangıç Tarihi : ","") != ""){
                filteredErrorReportList = filteredErrorReportList.filter {
                    it.timeinMilis > startTime
                }
            }
            if(reportListBinding.endDate.text.toString().substringAfter("Bitiş Tarihi : ","") != "          "){
                filteredErrorReportList = filteredErrorReportList.filter {
                    it.timeinMilis < endTime
                }
            }
            errorReportListAdapter = ErrorReportListAdapter(filteredErrorReportList)
            reportListBinding.recyclerReportList.layoutManager = LinearLayoutManager(requireContext())
            reportListBinding.recyclerReportList.setHasFixedSize(true)
            reportListBinding.recyclerReportList.adapter = errorReportListAdapter
                }

        reportListBinding.startDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(mainAct, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                reportListBinding.startDate.setText("Başlangıç Tarihi : $dayOfMonth/${(monthOfYear+1)}/$year")
                val myDate = "$dayOfMonth/${(monthOfYear+1)}/$year 00:00:01"
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                startTime = sdf.parse(myDate).time

            }, year, month, day)
            datePickerDialog.show()
        }

        reportListBinding.endDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(mainAct, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                reportListBinding.endDate.setText("Bitiş Tarihi : $dayOfMonth/${(monthOfYear+1)}/$year")
                Log.i("seasea","Bitiş Tarihi : $dayOfMonth/${(monthOfYear+1)}/$year")
                val myDate = "$dayOfMonth/${(monthOfYear+1)}/$year 23:59:59"
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                endTime = sdf.parse(myDate).time

            }, year, month, day)
            datePickerDialog.show()
        }

        }

    fun setSwipeListener(){

        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val builder = AlertDialog.Builder(mainAct)
                    builder.setTitle("Uyarı")
                    builder.setMessage("Raporu Siliyorsunuz ! Silerseniz Rapor kalıcı olarak yok olacak.")
                    builder.setPositiveButton("Sil") { dialog, which ->
                        dialog.dismiss()
                        reportViewModel.deleteReport(errorReportList[viewHolder.adapterPosition].uid, "error")
                        errorReportListAdapter.adaptersErrorReportList.clear()
                        errorReportListAdapter.notifyDataSetChanged()
                    }
                    builder.setNegativeButton("İptal"){dialog, which ->
                        errorReportListAdapter.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }).attachToRecyclerView(reportListBinding.recyclerReportList)

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
        reportListBinding.autotextOperatationName.setAdapter(operatationAdapter)
        reportListBinding.autotextOperatationName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Operasyon Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return operationList.contains(text.toString())
            }

        }

        reportListBinding.autotextOperatorsName.setAdapter(operatorAdapter)
        reportListBinding.autotextOperatorsName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Operatör Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return operatorList.contains(text.toString())
            }

        }

        reportListBinding.autotextModelName.setAdapter(modelAdapter)
        reportListBinding.autotextModelName.validator = object : AutoCompleteTextView.Validator{
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