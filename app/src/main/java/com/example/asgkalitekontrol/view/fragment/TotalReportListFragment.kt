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
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentTotalReportListBinding
import com.example.asgkalitekontrol.view.activity.MainActivity
import com.example.asgkalitekontrol.view.adapter.ErrorReportListAdapter
import com.example.asgkalitekontrol.view.adapter.TotalReportListAdapter
import com.example.asgkalitekontrol.view.model.ErrorReport
import com.example.asgkalitekontrol.view.model.TotalReport
import com.example.asgkalitekontrol.view.viewModels.ReportViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TotalReportListFragment : Fragment(){

    lateinit var totalReportListBinding : FragmentTotalReportListBinding
    lateinit var mainAct: MainActivity

    var totalReportList : MutableList<TotalReport> = ArrayList()
    var filteredTotalReportList : List<TotalReport> = ArrayList()
    lateinit var totalReportListAdapter : TotalReportListAdapter
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
        totalReportListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_total_report_list,
            container, false)
        return  totalReportListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reportViewModel.getTotalReports()
        listenReports()
        listenButtons()
        getListsFromDatabase()
        setSwipeListener()
    }

    fun setCardClickListener() {

        totalReportListAdapter.setOnItemClickListener {
            val action = TotalReportListFragmentDirections.actionTotalReportListFragmentToOperatorStatsFragment(it)
            findNavController().navigate(action)
        }

    }

    fun listenReports(){
        reportViewModel.totalReportList.observe(viewLifecycleOwner , androidx.lifecycle.Observer {
                totalReportList = it as MutableList<TotalReport>
            totalReportList.sortBy { it.timeinMilis}
                totalReportListAdapter = TotalReportListAdapter(it)
                totalReportListBinding.recyclerReportList.layoutManager = LinearLayoutManager(requireContext())
                totalReportListBinding.recyclerReportList.setHasFixedSize(true)
                totalReportListBinding.recyclerReportList.adapter = totalReportListAdapter
            setCardClickListener()

        })
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
        totalReportListBinding.autotextOperatationName.setAdapter(operatationAdapter)
        totalReportListBinding.autotextOperatationName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Operasyon Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return operationList.contains(text.toString())
            }

        }

        totalReportListBinding.autotextOperatorsName.setAdapter(operatorAdapter)
        totalReportListBinding.autotextOperatorsName.validator = object : AutoCompleteTextView.Validator{
            override fun fixText(invalidText: CharSequence?): CharSequence {
                Log.i("seasea","invalid $invalidText")
                Toast.makeText(mainAct, "Operatör Bulunamadı Lütfen Listeden Seçin", Toast.LENGTH_SHORT).show()
                return ""
            }
            override fun isValid(text: CharSequence?): Boolean {
                return operatorList.contains(text.toString())
            }

        }

        totalReportListBinding.autotextModelName.setAdapter(modelAdapter)
        totalReportListBinding.autotextModelName.validator = object : AutoCompleteTextView.Validator{
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

    fun listenButtons(){

        totalReportListBinding.btnFilter.setOnClickListener {
            totalReportListBinding.btnFilter.visibility = View.GONE
            totalReportListBinding.txtReports.visibility = View.GONE
            totalReportListBinding.filterConstraint.visibility = View.VISIBLE

            val param = totalReportListBinding.recyclerReportList.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,0,0,0)
            totalReportListBinding.recyclerReportList.layoutParams = param


        }

        totalReportListBinding.btnApply.setOnClickListener {
            totalReportListBinding.btnFilter.visibility = View.VISIBLE
            totalReportListBinding.filterConstraint.visibility = View.GONE
            totalReportListBinding.txtReports.visibility = View.VISIBLE
            val param = totalReportListBinding.recyclerReportList.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,140,0,0)
            totalReportListBinding.recyclerReportList.layoutParams = param

            filteredTotalReportList = totalReportList

            if(totalReportListBinding.autotextModelName.text.toString() != ""){
                filteredTotalReportList = filteredTotalReportList.filter {
                    it.model == totalReportListBinding.autotextModelName.text.toString()
                }
            }
            if(totalReportListBinding.autotextOperatorsName.text.toString() != ""){
                filteredTotalReportList = filteredTotalReportList.filter {
                    it.operator == totalReportListBinding.autotextOperatorsName.text.toString()
                }
            }
            if(totalReportListBinding.autotextOperatationName.text.toString() != ""){
                filteredTotalReportList = filteredTotalReportList.filter {
                    it.operation == totalReportListBinding.autotextOperatationName.text.toString()
                }
            }
            if(totalReportListBinding.startDate.text.toString().substringAfter("Başlangıç Tarihi : ","") != ""){
                filteredTotalReportList = filteredTotalReportList.filter {
                    it.timeinMilis > startTime
                }
            }
            if(totalReportListBinding.endDate.text.toString().substringAfter("Bitiş Tarihi : ","") != "          "){
                filteredTotalReportList = filteredTotalReportList.filter {
                    it.timeinMilis < endTime
                }
            }
            totalReportListAdapter = TotalReportListAdapter(filteredTotalReportList)
            totalReportListBinding.recyclerReportList.layoutManager = LinearLayoutManager(requireContext())
            totalReportListBinding.recyclerReportList.setHasFixedSize(true)
            totalReportListBinding.recyclerReportList.adapter = totalReportListAdapter
        }

        totalReportListBinding.startDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(mainAct, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                totalReportListBinding.startDate.setText("Başlangıç Tarihi : $dayOfMonth/${(monthOfYear+1)}/$year")
                val myDate = "$dayOfMonth/${(monthOfYear+1)}/$year 00:00:01"
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                startTime = sdf.parse(myDate).time

            }, year, month, day)
            datePickerDialog.show()
        }

        totalReportListBinding.endDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(mainAct, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                totalReportListBinding.endDate.setText("Bitiş Tarihi : $dayOfMonth/${(monthOfYear+1)}/$year")
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
                        reportViewModel.deleteReport(totalReportList[viewHolder.adapterPosition].uid, "total")
                        totalReportListAdapter.adaptersTotalReportList.clear()
                        totalReportListAdapter.notifyDataSetChanged()
                    }
                    builder.setNegativeButton("İptal"){dialog, which ->
                        totalReportListAdapter.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }).attachToRecyclerView(totalReportListBinding.recyclerReportList)

    }

}