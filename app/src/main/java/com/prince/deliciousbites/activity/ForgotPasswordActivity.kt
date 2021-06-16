package com.prince.deliciousbites.activity

import android.content.Intent
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

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etEmailAddress: EditText
    lateinit var progressBar: ProgressBar
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        progressBar = findViewById(R.id.progressBar)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {

            val mobileNumber = etMobileNumber.text.toString()
            val email = etEmailAddress.text.toString()

            if (!Validations().validateMobileNumber(mobileNumber, etMobileNumber))
            else if (!Validations().validateEmail(email, etEmailAddress))
            else {
                sendServerRequest(mobileNumber, email)
            }

        }
    }

    private fun sendServerRequest(mobileNumber: String, email: String) {
        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val jsonParams = JSONObject()

        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("email", email)

        if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val dataMainObject = it.getJSONObject("data")
                        val success = dataMainObject.getBoolean("success")

                        if (success) {
                            val firstTry = dataMainObject.getBoolean("first_try")

                            if (firstTry) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "OTP is sent to your Email Address!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@ForgotPasswordActivity,
                                    ResetPasswordActivity::class.java
                                )
                                intent.putExtra("mobile_number", mobileNumber)
                                startActivity(intent)
                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "New OTP will be provided for next 24 hours. Kindly, use the same OTP!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@ForgotPasswordActivity,
                                    ResetPasswordActivity::class.java
                                )
                                intent.putExtra("mobile_number", mobileNumber)
                                startActivity(intent)
                            }

                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Something went wrong!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Some error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ForgotPasswordActivity,
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
