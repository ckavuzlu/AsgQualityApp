package com.example.asgkalitekontrol.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.asgkalitekontrol.databinding.CardReportBinding
import com.example.asgkalitekontrol.view.model.ErrorReport


class ErrorReportListAdapter(var errorReportList : List<ErrorReport> = ArrayList()) : RecyclerView.Adapter<ErrorReportListAdapter.ReportListViewHolder>(){

    private lateinit var reportItemBinding: CardReportBinding
    var adaptersErrorReportList : MutableList<ErrorReport> = errorReportList as MutableList<ErrorReport>
    private var onItemClickListener: ((operator : String) -> Unit)? = null
    inner class ReportListViewHolder(private val reportItemBinding : CardReportBinding) : RecyclerView.ViewHolder(reportItemBinding.root){
        fun bind(errorReport : ErrorReport, onItemClickListener: ((operator : String) -> Unit)?){

            reportItemBinding.txtOperatorName.text = errorReport.operator
            reportItemBinding.txtOperation.text = errorReport.operation
            reportItemBinding.txtCheckedJobs.text = "Kontrol Edilen İş : ${errorReport.total_jobs}"
            reportItemBinding.txtErrorJobs.text = "Hatalı İş : ${errorReport.error_jobs}"
            reportItemBinding.txtQcPersonnelName?.text = errorReport.qcPersonnel
            reportItemBinding.txtDate?.text = errorReport.date
            if((errorReport.error_jobs).toFloat()/(errorReport.total_jobs).toFloat() >= 0.1){
                reportItemBinding.reportCardConstraint.setBackgroundColor(Color.parseColor("#ff0d00"))
            }else{
                reportItemBinding.reportCardConstraint.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            itemView.setOnClickListener { onItemClickListener?.invoke(errorReport.operator) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListViewHolder {
        reportItemBinding = CardReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReportListViewHolder(reportItemBinding)
    }

    override fun getItemCount(): Int {
      return adaptersErrorReportList.size
    }

    override fun onBindViewHolder(holder: ReportListViewHolder, position: Int) {
        val infoPoolItem = adaptersErrorReportList[position]
        infoPoolItem.let {
            holder.bind(it , onItemClickListener)
        }
    }

    fun setOnItemClickListener(onItemClickListener: (operator : String) -> Unit){
        this.onItemClickListener = onItemClickListener
    }
}