package com.prince.deliciousbites.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.prince.deliciousbites.R
import com.prince.deliciousbites.fragment.*
import com.prince.deliciousbites.util.AlertDialogBoxes

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var headerView: View
    lateinit var headerUserName: TextView
    lateinit var headerMobileNumber: TextView
    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val userName = sharedPreferences.getString("name", "No Name")
        val userMobile = sharedPreferences.getString("mobile", "No Mobile Number")
        val userEmail = sharedPreferences.getString("email", "No Email Address")
        val userAddress = sharedPreferences.getString("address", "No Delivery Address")

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        headerView = navigationView.getHeaderView(0)
        headerUserName = headerView.findViewById(R.id.headerUserName)
        headerMobileNumber = headerView.findViewById(R.id.headerMobileNumber)

        headerUserName.text = "$userName"
        headerMobileNumber.text = "+91-$userMobile"

        setUpToolbar()
        openDashboard()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.dashboard -> {
                    openDashboard()
                    //Delay is provided just for smooth transition of the drawer
                    Handler().postDelayed({
                        drawerLayout.closeDrawers()
                    }, 50)
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouritesFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    Handler().postDelayed({
                        drawerLayout.closeDrawers()
                    }, 50)
                }
                R.id.profile -> {
                    val fragment = ProfileFragment()
                    val bundle = Bundle()

                    bundle.putString("userName", userName)
                    bundle.putString("userMobile", userMobile)
                    bundle.putString("userEmail", userEmail)
                    bundle.putString("userDeliveryAddress", userAddress)

                    fragment.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            fragment
                        )
                        .commit()
                    supportActionBar?.title = "Profile Details"
                    Handler().postDelayed({
                        drawerLayout.closeDrawers()
                    }, 50)
                }
                R.id.orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderHistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Previous Orders"
                    Handler().postDelayed({
                        drawerLayout.closeDrawers()
                    }, 50)
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FAQsFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    Handler().postDelayed({
                        drawerLayout.closeDrawers()
                    }, 50)
                }
                R.id.logOut -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setPositiveButton("Yes") { text, listener ->
                        val intent = Intent(
                            this,
                            LoginActivity::class.java
                        )
                        sharedPreferences.edit().clear().apply()
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No") { text, listener ->
                        openDashboard()
                    }
                    dialog.create()
                    dialog.show()

                    Handler().postDelayed({
                        drawerLayout.closeDrawers()
                    }, 50)
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openDashboard() {
        val fragment = DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when (frag) {
            !is DashboardFragment -> openDashboard()
            else -> {
                //Exit from the app
                AlertDialogBoxes().exit(this)
            }
        }
    }
}
