package com.prince.deliciousbites.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity

class AlertDialogBoxes {
    fun noInternet(activity: Activity) {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is not Found")
        dialog.setPositiveButton("Open Settings") { text, listener ->
            val settingsIntent = Intent(Settings.ACTION_SETTINGS)
            startActivity(activity as Context, settingsIntent, null)
            activity.finish()
        }
        dialog.setNegativeButton("Exit") { text, listener ->
            ActivityCompat.finishAffinity(activity)
        }
        dialog.create()
        dialog.show()
    }

    fun resetCartItems(activity: Activity) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Going back will reset cart items. Do you still want to proceed?")
        dialog.setPositiveButton("Yes") { text, listener ->
            activity.finish()
        }
        dialog.setNegativeButton("No") { text, listener ->

        }
        dialog.create()
        dialog.show()
    }

    fun exit(activity: Activity) {
        val dialog = AlertDialog.Builder(activity as Context)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Are you sure you want to exit?")
        dialog.setPositiveButton("Yes") { text, listener ->
            ActivityCompat.finishAffinity(activity)
        }
        dialog.setNegativeButton("No") { text, listener ->

        }
        dialog.create()
        dialog.show()
    }

}