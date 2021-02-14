package com.example.asgkalitekontrol.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.FragmentProfileBinding
import com.example.asgkalitekontrol.view.activity.MainActivity

class ProfileFragment : Fragment(){

    lateinit var profileFragmentBinding: FragmentProfileBinding
    lateinit var mainAct : MainActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainAct = context as MainActivity
        profileFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container , false)
        return profileFragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenButtons()

    }

    fun listenButtons(){
        profileFragmentBinding.btnSignOut.setOnClickListener {

            val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
            findNavController().navigate(action)
            mainAct.mainActivityBinding.bottomNavBar.visibility = View.GONE
            mainAct.mainActivityBinding.bottomNavBarPersonnel.visibility = View.GONE



        }
    }


}