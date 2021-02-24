package com.example.asgkalitekontrol.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentLoginBinding
import com.example.asgkalitekontrol.view.viewModels.LoginViewModel
import kotlin.math.log

class LoginFragment : Fragment() {
    lateinit var loginBinding : FragmentLoginBinding
    val loginViewModel : LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container,false)

        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBinding.loginVM = loginViewModel
        checkUserAuth()
        listenWarnings()
        listenProgress()
    }

    fun listenProgress(){
        loginBinding.loginVM!!.progressLiveData.observe(viewLifecycleOwner, Observer {
            if(it){
                loginBinding.prgressLogin.visibility = View.VISIBLE
            }else{
                loginBinding.prgressLogin.visibility = View.GONE
            }
        })
    }
    fun checkUserAuth(){
        loginBinding.loginVM!!.personnel.observe(viewLifecycleOwner, Observer {
            if(it.accountType == "QualityControlPersonnel"){
                val action = LoginFragmentDirections.actionLoginFragmentToReportFragment()
                findNavController().navigate(action)
            }
            if(it.accountType == "Admin"){
                val action = LoginFragmentDirections.actionLoginFragmentToProfileFragment()
                findNavController().navigate(action)
            }

        })
    }

    fun listenWarnings(){
        loginBinding.loginVM!!.warningLiveData.observe(viewLifecycleOwner, Observer {
            val builder = AlertDialog.Builder(this.activity)
            builder.setTitle("Uyarı")
            builder.setMessage(it)
            builder.setPositiveButton("Tamam") { dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        })
    }

}