package com.example.asgkalitekontrol.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentLoginBinding
import com.example.asgkalitekontrol.view.viewModels.LoginViewModel

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

        initButtons()
    }

    private fun initButtons(){
        loginBinding.btnLogIn.setOnClickListener {
            loginViewModel.logIn()
            val action = LoginFragmentDirections.actionLoginFragmentToAddingFragment()
            loginBinding.btnLogIn.findNavController().navigate(action)
        }
    }

}