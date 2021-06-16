package com.prince.deliciousbites.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.prince.deliciousbites.R
import com.prince.deliciousbites.util.AlertDialogBoxes
import com.prince.deliciousbites.util.ConnectionManager
import com.prince.deliciousbites.util.Validations
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var progressBar: ProgressBar

    lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity,
                MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)


        btnLogin.setOnClickListener {

            val mobileNumber = etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            //Validating mobile number and password
            if (!Validations().validateMobileNumber(mobileNumber, etMobileNumber))
            else if (!Validations().validatePassword(password, etPassword))
            else {
                sendServerRequest(mobileNumber, password)
            }

        }
    }
    fun onClickRegister(v: View)
    {
        val intent = Intent(this@LoginActivity,
            RegistrationActivity::class.java)
        startActivity(intent)
    }
    fun onClickForgotPassword(v: View)
    {
        val intent = Intent(this@LoginActivity,
            ForgotPasswordActivity::class.java)
        startActivity(intent)
    }
    fun savePreferences(userId: String, name: String, mobile: String, email: String, address: String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("user_id", userId).apply()
        sharedPreferences.edit().putString("name", name).apply()
        sharedPreferences.edit().putString("mobile", mobile).apply()
        sharedPreferences.edit().putString("email", email).apply()
        sharedPreferences.edit().putString("address", address).apply()
    }

    private fun sendServerRequest(mobileNumber: String, password: String) {
        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this@LoginActivity)
        val url = "http://13.235.250.119/v2/login/fetch_result"
        val jsonParams = JSONObject()

        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", password)

        if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val dataMainObject = it.getJSONObject("data")
                        val success = dataMainObject.getBoolean("success")

                        if (success) {
                            val data = dataMainObject.getJSONObject("data")

                            val resUserId = data.getString("user_id")
                            val resName = data.getString("name")
                            val resEmail = data.getString("email")
                            val resMobile = data.getString("mobile_number")
                            val resAddress = data.getString("address")

                            savePreferences(resUserId, resName, resMobile, resEmail, resAddress)

                            Toast.makeText(this@LoginActivity,"You are successfully logged in!!!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@LoginActivity,
                                MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity,"Incorrect Credentials!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity,"Some error occurred!", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@LoginActivity,"Volley error $it", Toast.LENGTH_SHORT).show()
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "6a4924a7b848d4"
                    return headers
                }
            }
            queue.add(jsonRequest)
        } else {
            progressBar.visibility = View.GONE
            AlertDialogBoxes().noInternet(this)
        }

    }

}
