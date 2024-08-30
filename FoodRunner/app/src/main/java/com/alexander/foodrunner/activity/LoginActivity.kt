package com.alexander.foodrunner.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.alexander.foodrunner.R
import android.content.Context
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.alexander.foodrunner.adapter.HomeRecyclerAdapter
import com.alexander.foodrunner.model.Restaurant
import com.alexander.foodrunner.model.User
import com.alexander.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity()
{
    lateinit var etMobile : EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin : android.widget.Button
    lateinit var txtForgot : TextView
    lateinit var txtSignup : TextView
    lateinit var currentUser : SharedPreferences
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        currentUser = getSharedPreferences(getString(R.string.logindata),Context.MODE_PRIVATE)

        val isLoggedIn = currentUser.getBoolean("LoggedIn",false)
        if(isLoggedIn)
        {
            val intentLogin = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intentLogin)
            this.finish()
        }

        setContentView(R.layout.activity_login)

        etMobile = findViewById(R.id.etMobileLogin)
        etPassword = findViewById(R.id.etPasswordLogin)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgot = findViewById(R.id.txtForgot)
        txtSignup = findViewById(R.id.txtSignup)



        btnLogin.setOnClickListener{

            val mobile = etMobile.text.toString().trimEnd()
            val password = etPassword.text.toString().trimEnd()

            val requestQueue = Volley.newRequestQueue(this@LoginActivity)

            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",mobile)
            jsonParams.put("password",password)

            val jsonObjReq = object : JsonObjectRequest( Method.POST, url, jsonParams,
                    Response.Listener{
                        try {
                            val maindata = it.getJSONObject("data")
                            val success = maindata.getBoolean("success")

                            if (success == true)
                            {
                                val data = maindata.getJSONObject("data")

                                user = User (
                                        data.getString("user_id"),
                                        data.getString("name"),
                                        data.getString("email"),
                                        data.getString("mobile_number"),
                                        data.getString("address")
                                )

                                val intentMain = Intent(this@LoginActivity,
                                        MainActivity::class.java)

                                startActivity(intentMain)

                                loggedIn()

                                saveLogin( user, password )

                                Toast.makeText(this@LoginActivity,"Logged In Successfully",
                                        Toast.LENGTH_SHORT).show()

                                this.finish()
                            }
                            else {
                                Toast.makeText(this@LoginActivity,
                                        "Incorrect Password or Mobile Number",
                                        Toast.LENGTH_SHORT).show()
                            }


                        } catch (e : JSONException) {
                            val dialog = AlertDialog.Builder(this@LoginActivity)
                            dialog.setTitle("Failed")
                            dialog.setMessage("Some Error has occurred in JSON request!!!")
                            dialog.setPositiveButton("Okay") {text, Listener -> }
                            dialog.create()
                            dialog.show()
                        }
                    },
                    Response.ErrorListener{
                        val dialog = AlertDialog.Builder(this@LoginActivity)
                        dialog.setTitle("Failed")
                        dialog.setMessage("Some Error has occurred in Volley!!!")
                        dialog.setPositiveButton("Okay") {text, Listener -> }
                        dialog.create()
                        dialog.show()
                    }
            ) {
                override fun getHeaders(): MutableMap<String, String>
                {
                    val headers = HashMap <String, String> ()
                    headers ["Content-type"] = "application/json"
                    headers ["token"] = "fb3de6b5669b46"
                    return headers
                }
            }

            val online = ConnectionManager().checkConnectivity(this@LoginActivity)

            if( online )
            {
                requestQueue.add (jsonObjReq)
            }
            else
            {
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Failed")
                dialog.setMessage("Internet Connection is not available")
                dialog.setPositiveButton("Open Settings") {text, Listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@LoginActivity?.finish()
                }
                dialog.setNegativeButton("Close App") {text, Listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }

        }

        txtForgot.setOnClickListener{

            val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
            startActivity(intent)
        }

        txtSignup.setOnClickListener{

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    fun loggedIn()
    {
        currentUser.edit().putBoolean("LoggedIn",true).apply()
    }

    fun saveLogin (user:User, password:String)
    {
        currentUser.edit().putString("Id",user.id).apply()
        currentUser.edit().putString("Name",user.name).apply()
        currentUser.edit().putString("Email",user.email).apply()
        currentUser.edit().putString("Mobile",user.mobile).apply()
        currentUser.edit().putString("Address",user.address).apply()
        currentUser.edit().putString("Password",password).apply()
    }

}


















