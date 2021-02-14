
package com.example.asgkalitekontrol.view.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.asgkalitekontrol.databinding.CardReportBinding
import com.example.asgkalitekontrol.view.model.ErrorReport
import com.example.asgkalitekontrol.view.model.TotalReport


class TotalReportListAdapter(var totalReportList : List<TotalReport> = ArrayList()) : RecyclerView.Adapter<TotalReportListAdapter.TotalReportListViewHolder>(){

    private lateinit var reportItemBinding: CardReportBinding

    private var onItemClickListener: ((operator : String) -> Unit)? = null

    var adaptersTotalReportList : MutableList<TotalReport> = totalReportList as MutableList<TotalReport>

    inner class TotalReportListViewHolder(private val reportItemBinding : CardReportBinding) : RecyclerView.ViewHolder(reportItemBinding.root){
        fun bind(totalReport : TotalReport){
            var date = totalReport.date.substring(0,11)
            reportItemBinding.txtOperatorName.text = totalReport.operator
            reportItemBinding.txtOperation.text = totalReport.operation
            reportItemBinding.txtCheckedJobs.text = "Yapılması Gereken İş : ${totalReport.total_jobs}"
            reportItemBinding.txtErrorJobs.text = "Yapılan İş : ${totalReport.finished_jobs}"
            reportItemBinding.txtQcPersonnelName?.text = totalReport.qcPersonnel
            reportItemBinding.txtDate?.text = "${date} ${totalReport.whichHour}. Saat "
            if((totalReport.finished_jobs).toFloat() < (totalReport.total_jobs).toFloat() ){
                reportItemBinding.reportCardConstraint.setBackgroundColor(Color.parseColor("#ff0d00"))
            }else if((totalReport.finished_jobs).toFloat() / (totalReport.total_jobs).toFloat() >= 1.1) {
                reportItemBinding.reportCardConstraint.setBackgroundColor(Color.parseColor("#00DF25"))
            }else{
                reportItemBinding.reportCardConstraint.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            itemView.setOnClickListener { onItemClickListener?.invoke(totalReport.operator) }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalReportListViewHolder {
        reportItemBinding = CardReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TotalReportListViewHolder(reportItemBinding)
    }

    override fun getItemCount(): Int {
        return adaptersTotalReportList.size
    }

    override fun onBindViewHolder(holder: TotalReportListViewHolder, position: Int) {
        val infoPoolItem = adaptersTotalReportList[position]
        infoPoolItem.let {
            holder.bind(it)
        }
    }

    fun setOnItemClickListener(onItemClickListener: (operator : String) -> Unit){
        this.onItemClickListener = onItemClickListener
    }

}