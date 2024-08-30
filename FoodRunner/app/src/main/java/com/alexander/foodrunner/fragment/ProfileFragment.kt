package com.alexander.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alexander.foodrunner.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var img : ImageView
    lateinit var txtName : TextView
    lateinit var txtEmail : TextView
    lateinit var txtMobile : TextView
    lateinit var txtAddress : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        val profileView = inflater.inflate(R.layout.fragment_profile, container, false)

        img = profileView.findViewById(R.id.imgAc)
        txtName = profileView.findViewById(R.id.txtAcName)
        txtEmail = profileView.findViewById(R.id.txtAcEmail)
        txtMobile = profileView.findViewById(R.id.txtAcMobile)
        txtAddress = profileView.findViewById(R.id.txtAcAddress)

        val logindata : SharedPreferences? =
            activity?.getSharedPreferences(getString(R.string.logindata), Context.MODE_PRIVATE)

        txtName.text = logindata?.getString("Name","ALEXANDER")
        txtEmail.text = logindata?.getString("Email","alexanderjamatia@gmail.com")
        txtMobile.text = logindata?.getString("Mobile","1234567890")
        txtAddress.text = logindata?.getString("Address","ADDRESS")

        return profileView
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}