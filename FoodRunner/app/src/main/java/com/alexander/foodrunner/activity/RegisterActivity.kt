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

class RegisterActivity : AppCompatActivity()
{
    lateinit var etName : EditText
    lateinit var etEmail : EditText
    lateinit var etMobile : EditText
    lateinit var etAddress : EditText
    lateinit var etPassword: EditText
    lateinit var etConfirm : EditText
    lateinit var btnRegister : android.widget.Button
    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    lateinit var fileData : SharedPreferences
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        title = "Register Yourself"

        fileData = getSharedPreferences(getString(R.string.userdata),android.content.Context.MODE_PRIVATE)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirm = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.registerToolbar)

        setupToolbar()


        btnRegister.setOnClickListener{

            val name = etName.text.toString().trimEnd()
            val email = etEmail.text.toString().trimEnd()
            val mobile = etMobile.text.toString().trimEnd()
            val address = etAddress.text.toString().trimEnd()
            val password = etPassword.text.toString().trimEnd()
            val confirm = etConfirm.text.toString().trimEnd()

            val correctName : Boolean
            val correctPassword : Boolean
            val confirmed : Boolean
            val correctEmail : Boolean
            val allfilled : Boolean

            correctName = name.length >= 3
            correctPassword = password.length >= 4
            confirmed = password == confirm
            correctEmail = (email.contains("@") && email.contains(".com"))
            allfilled = (email.isNotBlank() && address.isNotBlank() && mobile.isNotBlank())


            if(correctName && correctEmail && correctPassword && confirmed && allfilled)
            {
                val requestQueue = Volley.newRequestQueue(this@RegisterActivity)

                val url = "http://13.235.250.119/v2/register/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("name",name)
                jsonParams.put("mobile_number",mobile)
                jsonParams.put("password",password)
                jsonParams.put("address",address)
                jsonParams.put("email",email)

                val online = ConnectionManager().checkConnectivity(this@RegisterActivity)

                if(online) {
                    val jsonObjReq = object : JsonObjectRequest(Method.POST, url, jsonParams,

                            Response.Listener {
                                try {
                                    val maindata = it.getJSONObject("data")
                                    val success = maindata.getBoolean("success")

                                    if (success == true)
                                    {
                                        val data = maindata.getJSONObject("data")

                                        user = User(
                                                data.getString("user_id"),
                                                data.getString("name"),
                                                data.getString("email"),
                                                data.getString("mobile_number"),
                                                data.getString("address")
                                        )

                                        val intent = Intent( this@RegisterActivity,
                                                LoginActivity::class.java)
                                        startActivity(intent)
                                        saveRegistration (user,password)
                                        this.finish()

                                        Toast.makeText(this@RegisterActivity,
                                                "Registered Successfully",
                                                Toast.LENGTH_SHORT).show()
                                    }
                                    else
                                    {
                                        val dialog = AlertDialog.Builder(this@RegisterActivity)
                                        dialog.setTitle("Registration Failed")
                                        dialog.setMessage("Account already exists")
                                        dialog.setPositiveButton("Okay") { text, Listener -> }
                                        dialog.create()
                                        dialog.show()
                                    }


                                } catch (e: JSONException) {
                                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                                    dialog.setTitle("Failed")
                                    dialog.setMessage("Some Error has occurred in JSON request!!!")
                                    dialog.setPositiveButton("Okay") { text, Listener -> }
                                    dialog.create()
                                    dialog.show()
                                }
                            },
                            Response.ErrorListener {
                                val dialog = AlertDialog.Builder(this@RegisterActivity)
                                dialog.setTitle("Failed")
                                dialog.setMessage("Some Error has occurred in Volley!!!")
                                dialog.setPositiveButton("Okay") { text, Listener -> }
                                dialog.create()
                                dialog.show()
                            }
                    ){
                        override fun getHeaders(): MutableMap<String, String>
                        {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "fb3de6b5669b46"
                            return headers
                        }
                    }

                    requestQueue.add(jsonObjReq)

                }
                else
                {
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Failed")
                    dialog.setMessage("Internet Connection is not available")
                    dialog.setPositiveButton("Open Settings") {text, Listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this@RegisterActivity?.finish()
                    }
                    dialog.setNegativeButton("Close App") {text, Listener ->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }

            else if(!correctName)
                Toast.makeText(this@RegisterActivity,"Name should contain atleast 3 characters",Toast.LENGTH_SHORT).show()
            else if( !correctEmail )
                Toast.makeText(this@RegisterActivity,"Incorrect Email",Toast.LENGTH_SHORT).show()
            else if(!correctPassword)
                Toast.makeText(this@RegisterActivity,"Password should contain atleast 4 characters",Toast.LENGTH_SHORT).show()
            else if(!confirmed)
                Toast.makeText(this@RegisterActivity,"Incorrect Confirm Password",Toast.LENGTH_SHORT).show()
            else if(!allfilled)
                Toast.makeText(this@RegisterActivity,"All fields need to be filled",Toast.LENGTH_SHORT).show()

        }


    }

    fun saveRegistration (user:User, password:String)
    {
        fileData.edit().putString("Id",user.id).apply()
        fileData.edit().putString("Name",user.name).apply()
        fileData.edit().putString("Email",user.email).apply()
        fileData.edit().putString("Mobile",user.mobile).apply()
        fileData.edit().putString("Address",user.address).apply()
        fileData.edit().putString("Password",password).apply()
    }

    fun setupToolbar ()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title= "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if( item.itemId == android.R.id.home)
            this.finish()

        return super.onOptionsItemSelected(item)
    }

}



















