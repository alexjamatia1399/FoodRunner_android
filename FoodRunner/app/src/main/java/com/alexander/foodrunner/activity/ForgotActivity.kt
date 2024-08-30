package com.alexander.foodrunner.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.alexander.foodrunner.R
import com.alexander.foodrunner.model.User
import com.alexander.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ForgotActivity : AppCompatActivity()
{
    lateinit var etMobile : EditText
    lateinit var etEmail : EditText
    lateinit var btnNext : android.widget.Button
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)


        etMobile = findViewById(R.id.etMobileForgot)
        etEmail = findViewById(R.id.etEmailForgot)
        btnNext = findViewById(R.id.btnNext)
        toolbar = findViewById(R.id.forgotToolbar)

        setupToolbar()



        btnNext.setOnClickListener{

            val mobile = etMobile.text.toString().trimEnd()
            val email = etEmail.text.toString().trimEnd()


            val requestQueue = Volley.newRequestQueue(this@ForgotActivity)

            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",mobile)
            jsonParams.put("email",email)

            val jsonObjReq = object : JsonObjectRequest( Request.Method.POST, url, jsonParams,
                    Response.Listener{
                        try {
                            val maindata = it.getJSONObject("data")
                            val success = maindata.getBoolean("success")
                            val first = maindata.getBoolean("first_try")

                            if (success == true)
                            {
                                val intent = Intent(this@ForgotActivity,
                                        ResetPasswordActivity::class.java)
                                intent.putExtra("MobileNumber",mobile)
                                startActivity(intent)
                                this.finish()
                            }
                            else {
                                Toast.makeText(this@ForgotActivity,
                                        "Incorrect Email or Mobile Number",
                                        Toast.LENGTH_SHORT).show()
                            }


                        } catch (e : JSONException) {
                            val dialog = AlertDialog.Builder(this@ForgotActivity)
                            dialog.setTitle("Failed")
                            dialog.setMessage("Some Error has occurred in JSON request!!!")
                            dialog.setPositiveButton("Okay") {text, Listener -> }
                            dialog.create()
                            dialog.show()
                        }
                    },
                    Response.ErrorListener{
                        val dialog = AlertDialog.Builder(this@ForgotActivity)
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

            val online = ConnectionManager().checkConnectivity(this@ForgotActivity)

            if( online )
            {
                requestQueue.add (jsonObjReq)
            }
            else
            {
                val dialog = AlertDialog.Builder(this@ForgotActivity)
                dialog.setTitle("Failed")
                dialog.setMessage("Internet Connection is not available")
                dialog.setPositiveButton("Open Settings") {text, Listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@ForgotActivity?.finish()
                }
                dialog.setNegativeButton("Close App") {text, Listener ->
                    ActivityCompat.finishAffinity(this@ForgotActivity)
                }
                dialog.create()
                dialog.show()
            }

        }

    }

    fun setupToolbar ()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title= "Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        if(id==android.R.id.home)
            super.onBackPressed()

        return super.onOptionsItemSelected(item)
    }
}





















