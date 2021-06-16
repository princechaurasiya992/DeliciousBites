package com.prince.deliciousbites.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.prince.deliciousbites.R
import com.prince.deliciousbites.adapter.RestaurantDetailsRecyclerAdapter
import com.prince.deliciousbites.database.FoodDatabase
import com.prince.deliciousbites.database.FoodEntity
import com.prince.deliciousbites.model.RestaurantMenu
import com.prince.deliciousbites.util.AlertDialogBoxes
import com.prince.deliciousbites.util.ConnectionManager
import com.prince.deliciousbites.util.Sorter
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class RestaurantDetailsActivity : AppCompatActivity() {
    lateinit var recyclerRestaurantDetails: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantDetailsRecyclerAdapter
    lateinit var progressBar: ProgressBar
    lateinit var btnProceedToCart: Button

    var orderList = arrayListOf<RestaurantMenu>()
    val foodInfoList = arrayListOf<RestaurantMenu>()
    var restaurantId: String? = "100"
    var resName: String? = "Restaurant Name"
    var checkedItem = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        recyclerRestaurantDetails = findViewById(R.id.recyclerRestaurantDetails)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        btnProceedToCart.visibility = View.GONE

        layoutManager = LinearLayoutManager(this)

        if (intent != null) {
            restaurantId = intent.getStringExtra("restaurant_id")
            resName = intent.getStringExtra("res_name")
        } else {
            finish()
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (restaurantId == "100" || resName == "Restaurant Name") {
            finish()
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        setUpToolbar(resName)

        btnProceedToCart.setOnClickListener {
            proceedToCart()
        }

        sendServerRequest()
    }

    override fun onBackPressed() {
        if (orderList.isNotEmpty()) {
            AlertDialogBoxes().resetCartItems(this)
        } else {
            super.onBackPressed()
        }
    }

    private fun setUpToolbar(resName: String?) {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = resName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun proceedToCart() {

        val gson = Gson()
        val foodItems = gson.toJson(orderList)

        val async = ItemsOfCart(this, restaurantId.toString(), foodItems).execute()
        val result = async.get()
        if (result) {
            val intent = Intent(
                this@RestaurantDetailsActivity,
                CartActivity::class.java
            )
            intent.putExtra("res_id", restaurantId)
            intent.putExtra("res_name", resName)
            startActivity(intent)
        } else {
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some unexpected error",
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }

    class ItemsOfCart(context: Context, val restaurantId: String, val foodItems: String) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            //Save the food into DB
            db.foodDao().insertFood(FoodEntity(restaurantId, foodItems))
            db.close()
            return true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_sorter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.action_sort) {

            val builder = AlertDialog.Builder(this)
            var listener = 100

            builder.setTitle("Sort By?")
            builder.setSingleChoiceItems(R.array.foods_sort_by, checkedItem) { dialog, which ->
                listener = which
                checkedItem = which
            }
            builder.setPositiveButton("OK") { dialog, which ->

                if (foodInfoList.isNotEmpty()) {
                    when (listener) {
                        0 -> {
                            Collections.sort(foodInfoList, Sorter.foodsPriceComparator)
                            foodInfoList.reverse()
                        }
                        1 -> {
                            Collections.sort(foodInfoList, Sorter.foodsPriceComparator)
                        }
                        100 -> Toast.makeText(
                            this,
                            "No change is made",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
            builder.setNegativeButton("CANCEL") { dialog, which ->
                dialog.cancel()
            }
            builder.create()
            builder.show()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sendServerRequest() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"
        if (ConnectionManager().checkConnectivity(this)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    // Here we will handle the response
                    try {

                        val dataMainObject = it.getJSONObject("data")
                        val success = dataMainObject.getBoolean("success")

                        if (success) {
                            val data = dataMainObject.getJSONArray("data")
                            progressBar.visibility = View.GONE

                            for (i in 0 until data.length()) {
                                val foodJasonObject = data.getJSONObject(i)
                                val foodObject = RestaurantMenu(
                                    foodJasonObject.getString("id"),
                                    foodJasonObject.getString("name"),
                                    foodJasonObject.getString("cost_for_one")
                                )

                                foodInfoList.add(foodObject)
                                recyclerAdapter = RestaurantDetailsRecyclerAdapter(
                                    this,
                                    foodInfoList,
                                    object : RestaurantDetailsRecyclerAdapter.OnItemClickListener {
                                        override fun onAddItemClick(restaurantMenu: RestaurantMenu) {
                                            orderList.add(restaurantMenu)
                                            if (orderList.isNotEmpty()) {
                                                btnProceedToCart.visibility = View.VISIBLE
                                            }
                                        }

                                        override fun onRemoveItemClick(restaurantMenu: RestaurantMenu) {
                                            orderList.remove(restaurantMenu)
                                            if (orderList.isEmpty()) {
                                                btnProceedToCart.visibility = View.GONE
                                            }
                                        }
                                    })

                                recyclerRestaurantDetails.adapter = recyclerAdapter
                                recyclerRestaurantDetails.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(
                                this@RestaurantDetailsActivity,
                                "Some error occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RestaurantDetailsActivity,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    // Here we will handle the errors
                    Toast.makeText(
                        this@RestaurantDetailsActivity,
                        "Volley error occurred!!!",
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
            queue.add(jsonObjectRequest)
        } else {
            AlertDialogBoxes().noInternet(this)
        }
    }

}
