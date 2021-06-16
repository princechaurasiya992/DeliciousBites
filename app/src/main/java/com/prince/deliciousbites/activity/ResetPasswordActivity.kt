package com.prince.deliciousbites.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
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

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmNewPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var progressBar: ProgressBar
    var mobileNumber: String? = "Whatever"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressBar = findViewById(R.id.progressBar)
        if (intent != null) {
            mobileNumber = intent.getStringExtra("mobile_number")
        }

        btnSubmit.setOnClickListener {
            val mobile = mobileNumber.toString()
            val otp = etOTP.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmNewPassword = etConfirmNewPassword.text.toString()

            if (!Validations().validateOTP(otp, etOTP))
            else if (!Validations().validatePassword(newPassword, etNewPassword))
            else if (!Validations().validateConfirmPassword(newPassword, confirmNewPassword, etConfirmNewPassword))
            else {
                sendServerRequest(mobile, newPassword, otp)
            }
        }
    }

    private fun sendServerRequest(mobile: String, newPassword: String, otp: String) {
        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
        val jsonParams = JSONObject()

        jsonParams.put("mobile_number", mobile)
        jsonParams.put("password", newPassword)
        jsonParams.put("otp", otp)

        if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {
            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val dataMainObject = it.getJSONObject("data")
                        val success = dataMainObject.getBoolean("success")

                        if (success) {
                            val successMessage = dataMainObject.getString("successMessage")

                            Toast.makeText(
                                this@ResetPasswordActivity,
                                successMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                this@ResetPasswordActivity,
                                LoginActivity::class.java
                            )
                            startActivity(intent)
                            finish()

                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                "Something went wrong!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Some error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ResetPasswordActivity,
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
