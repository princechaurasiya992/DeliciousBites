package com.prince.deliciousbites.util

import android.widget.EditText

class Validations {
    fun validateName(name: String, etName: EditText): Boolean {
        if (name.isEmpty()) {
            etName.error = "Enter Your Name!"
            return false
        }
        else if (name.length < 3) {
            etName.error = "Name must be of greater than three characters!"
            return false
        }
        else {
            return true
        }
    }
    fun validateMobileNumber(mobileNumber: String, etMobileNumber: EditText): Boolean {
        if (mobileNumber.isEmpty()) {
            etMobileNumber.error = "Enter Your Mobile Number!"
            return false
        }
        else if (mobileNumber.length != 10) {
            etMobileNumber.error = "Invalid Mobile Number!"
            return false
        }
        else {
            return true
        }
    }
    fun validateEmail(emailAddress: String, etEmailAddress: EditText): Boolean {
        if (emailAddress.isEmpty()) {
            etEmailAddress.error = "Enter your Email Address!"
            return false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            etEmailAddress.error = "Invalid Email Address!"
            return false
        }
        else {
            return true
        }
    }
    fun validateDeliveryAddress(deliveryAddress: String, etDeliveryAddress: EditText): Boolean {
        if (deliveryAddress.isEmpty()) {
            etDeliveryAddress.error = "Enter Your Delivery Address!"
            return false
        } else {
            return true
        }
    }
    fun validatePassword(password: String, etPassword: EditText): Boolean {
        if (password.isEmpty()) {
            etPassword.error = "Enter Password!"
            return false
        } else if (password.length < 4) {
            etPassword.error = "Invalid Password!"
            return false
        } else {
            return true
        }

    }
    fun validateConfirmPassword(password: String, confirmPassword: String, etConfirmPassword: EditText): Boolean {
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Enter Confirm Password!"
            return false
        } else if (confirmPassword != password) {
            etConfirmPassword.error = "Password Mismatched!"
            return false
        } else {
            return true
        }
    }
    fun validateOTP(otp: String, etOTP: EditText): Boolean {
        if (otp.isEmpty()) {
            etOTP.error = "Enter OTP!"
            return false
        } else if (otp.length != 4) {
            etOTP.error = "Invalid OTP!"
            return false
        } else {
            return true
        }
    }
}