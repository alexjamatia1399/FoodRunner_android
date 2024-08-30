package com.alexander.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.alexander.foodrunner.R
import com.alexander.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity()
{
    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    lateinit var etOtp : EditText
    lateinit var etPassword : EditText
    lateinit var etConfirm : EditText
    lateinit var btnReset : android.widget.Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        toolbar = findViewById(R.id.resetToolbar)
        etOtp = findViewById(R.id.etOTP)
        etPassword = findViewById(R.id.etNewPassword)
        etConfirm = findViewById(R.id.etConfirmNewPassword)
        btnReset = findViewById(R.id.btnReset)

        setupToolbar()

        var mobile : String? = null
        if(intent != null)
            mobile = intent.getStringExtra("MobileNumber")



        btnReset.setOnClickListener {

            val otp = etOtp.text.toString().trimEnd()
            val password = etPassword.text.toString().trimEnd()
            val confirm = etConfirm.text.toString().trimEnd()

            if( password == confirm )
            {
                val requestQueue = Volley.newRequestQueue(this@ResetPasswordActivity)

                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number",mobile)
                jsonParams.put("password",password)
                jsonParams.put("otp",otp)

                val jsonObjReq = object : JsonObjectRequest( Request.Method.POST, url, jsonParams,
                        Response.Listener{
                            try {
                                val maindata = it.getJSONObject("data")
                                val success = maindata.getBoolean("success")
                                val successMessage = maindata.getString("successMessage")

                                if (success == true)
                                {
                                    val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                                    dialog.setTitle("Success")
                                    dialog.setMessage(successMessage)
                                    dialog.setPositiveButton("Okay") {text, Listener ->
                                        val intent = Intent(this@ResetPasswordActivity,
                                                LoginActivity::class.java)
                                        startActivity(intent)
                                        this.finishAffinity()
                                    }
                                    dialog.create()
                                    dialog.show()
                                }
                                else {
                                    Toast.makeText(this@ResetPasswordActivity,
                                            "Incorrect Email or Mobile Number",
                                            Toast.LENGTH_SHORT).show()
                                }


                            } catch (e : JSONException) {
                                val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                                dialog.setTitle("Failed")
                                dialog.setMessage("Some Error has occurred in JSON request!!!")
                                dialog.setPositiveButton("Okay") {text, Listener -> }
                                dialog.create()
                                dialog.show()
                            }
                        },
                        Response.ErrorListener{
                            val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
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


                val online = ConnectionManager().checkConnectivity(this@ResetPasswordActivity)
                if(online)
                {
                    requestQueue.add (jsonObjReq)
                }
                else
                {
                    val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                    dialog.setTitle("Failed")
                    dialog.setMessage("Internet Connection is not available")
                    dialog.setPositiveButton("Open Settings") {text, Listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this@ResetPasswordActivity?.finish()
                    }
                    dialog.setNegativeButton("Close App") {text, Listener ->
                        ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            else
                Toast.makeText(this@ResetPasswordActivity, "Incorrect Password Confirmation",
                        Toast.LENGTH_SHORT).show()

        }


    }

    fun setupToolbar ()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title= "Reset Password"
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