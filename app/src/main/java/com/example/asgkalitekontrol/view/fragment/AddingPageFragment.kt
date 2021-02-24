package com.example.asgkalitekontrol.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentAddingPageBinding
import com.example.asgkalitekontrol.view.activity.MainActivity
import com.example.asgkalitekontrol.view.model.Personnel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddingPageFragment : Fragment(){

    private lateinit var addingPageBinding : FragmentAddingPageBinding
    lateinit var mainAct : MainActivity
    private lateinit var database: DatabaseReference
    var operatorSalary : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addingPageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_adding_page,container,false)
        mainAct = context as MainActivity
        mainAct.mainActivityBinding.bottomNavBar.visibility = View.VISIBLE
        return addingPageBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun initButtons(){

        addingPageBinding.btnAddModel.setOnClickListener {
            var modelName = addingPageBinding.edtAddModelName.text.toString()

            database.child("models").child(modelName).child("name").setValue(modelName)
            database.child("models").child(modelName).child("isDone").setValue(false)
            Toast.makeText(context, "$modelName başarılı bir şekilde eklendi",Toast.LENGTH_SHORT).show()
            addingPageBinding.edtAddModelName.setText("")
        }
        //Add Operator
        addingPageBinding.btnAddOperator.setOnClickListener {
            var operatorName = addingPageBinding.edtAddOpperator.text.toString()

            if(addingPageBinding.edtAddOpperatorSalary.text.toString() == ""){
                operatorSalary = 0
            }else {
                operatorSalary  = addingPageBinding.edtAddOpperatorSalary.text.toString().toInt()
            }
            database.child("operators").child(operatorName).child("name").setValue(operatorName)
            database.child("operators").child(operatorName).child("salary").setValue(operatorSalary)
            Toast.makeText(context, "$operatorName başarılı bir şekilde eklendi",Toast.LENGTH_SHORT).show()
            addingPageBinding.edtAddOpperator.setText("")
        }
        addingPageBinding.btnAddOperatation.setOnClickListener {
            var operatationName = addingPageBinding.edtAddOperatation.text.toString()

            database.child("operatations").child(operatationName).child("name").setValue(operatationName)
            Toast.makeText(context, "$operatationName başarılı bir şekilde eklendi",Toast.LENGTH_SHORT).show()
            addingPageBinding.edtAddOperatation.setText("")
            addingPageBinding.edtAddOpperatorSalary.setText("")
        }
        addingPageBinding.btnAddPersonel.setOnClickListener {
            var personnel = Personnel(name = addingPageBinding.edtAddPersonnelName.text.toString(),
                username = addingPageBinding.edtAddPersonelUserName.text.toString(),
                password = addingPageBinding.edtAddPersonelPassword.text.toString(),
                accountType = "QualityControlPersonnel")
            if(personnel.name == "" ){
              addingPageBinding.edtAddPersonnelName.error = "Lütfen Tüm alanları doldurunuz"
            }else if(personnel.username == ""){
                addingPageBinding.edtAddPersonelUserName.error = "Lütfen Tüm alanları doldurunuz"
            }else if(personnel.password == "" ){
                addingPageBinding.edtAddPersonelPassword.error = "Lütfen Tüm alanları doldurunuz"
            }else if(personnel.password != addingPageBinding.edtAddPersonelPasswordCheck.text.toString()){
                addingPageBinding.edtAddPersonelPassword.error = "Şifreleri Tekrar Kontrol Ediniz"
                addingPageBinding.edtAddPersonelPasswordCheck.error = "Şifreleri Tekrar Kontrol Ediniz"
            }else{
                database.child("personnel").child(personnel.name).setValue(personnel)
                database.child("users").child(personnel.username).setValue(personnel)
                Toast.makeText(context, "Kalite kontrol personeli başarılı bir şekilde eklendi",Toast.LENGTH_SHORT).show()
                addingPageBinding.edtAddPersonnelName.setText("")
                addingPageBinding.edtAddPersonelUserName.setText("")
                addingPageBinding.edtAddPersonelPassword.setText("")
                addingPageBinding.edtAddPersonelPasswordCheck.setText("")

            }
        }

    }
}