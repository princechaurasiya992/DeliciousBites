package com.prince.deliciousbites.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.prince.deliciousbites.R

class ProfileFragment : Fragment() {
    lateinit var txtUserName: TextView
    lateinit var txtUserMobile: TextView
    lateinit var txtUserEmail: TextView
    lateinit var txtDeliveryAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtUserName = view.findViewById(R.id.txtUserName)
        txtUserMobile = view.findViewById(R.id.txtUserMobile)
        txtUserEmail = view.findViewById(R.id.txtUserEmail)
        txtDeliveryAddress = view.findViewById(R.id.txtDeliveryAddress)

        txtUserName.text = "${arguments?.getString("userName")}"
        txtUserMobile.text = "+91-${arguments?.getString("userMobile")}"
        txtUserEmail.text = "${arguments?.getString("userEmail")}"
        txtDeliveryAddress.text = "${arguments?.getString("userDeliveryAddress")}"

        return view
    }

}
