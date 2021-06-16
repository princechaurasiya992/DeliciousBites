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

class RegistrationActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etEmailAddress: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var progressBar: ProgressBar

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        setUpToolbar()

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etName = findViewById(R.id.etName)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        btnRegister.setOnClickListener {

            val name = etName.text.toString()
            val mobileNumber = etMobileNumber.text.toString()
            val emailAddress = etEmailAddress.text.toString()
            val deliveryAddress = etDeliveryAddress.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (!Validations().validateName(name, etName))
            else if (!Validations().validateEmail(emailAddress, etEmailAddress))
            else if (!Validations().validateMobileNumber(mobileNumber, etMobileNumber))
            else if (!Validations().validateDeliveryAddress(deliveryAddress, etDeliveryAddress))
            else if (!Validations().validatePassword(password, etPassword))
            else if (!Validations().validateConfirmPassword(
                    password,
                    confirmPassword,
                    etConfirmPassword
                )
            )
            else {
                sendServerRequest(name, mobileNumber, emailAddress, deliveryAddress, password)
            }

        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun savePreferences(
        userId: String,
        name: String,
        mobile: String,
        email: String,
        address: String
    ) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("user_id", userId).apply()
        sharedPreferences.edit().putString("name", name).apply()
        sharedPreferences.edit().putString("mobile", mobile).apply()
        sharedPreferences.edit().putString("email", email).apply()
        sharedPreferences.edit().putString("address", address).apply()
    }

    private fun sendServerRequest(
        name: String,
        mobileNumber: String,
        emailAddress: String,
        deliveryAddress: String,
        password: String
    ) {
        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this@RegistrationActivity)
        val url = "http://13.235.250.119/v2/register/fetch_result"
        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", password)
        jsonParams.put("address", deliveryAddress)
        jsonParams.put("email", emailAddress)

        if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
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

                            Toast.makeText(
                                this@RegistrationActivity,
                                "You are successfully registered!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(
                                this@RegistrationActivity,
                                MainActivity::class.java
                            )
                            startActivity(intent)
                            finish()

                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@RegistrationActivity,
                                "The Mobile Number or Email is already registered!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Some error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    Response.ErrorListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Volley error $it",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
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
