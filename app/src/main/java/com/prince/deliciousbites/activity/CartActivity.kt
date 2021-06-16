package com.prince.deliciousbites.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.prince.deliciousbites.R
import com.prince.deliciousbites.adapter.CartRecyclerAdapter
import com.prince.deliciousbites.database.FoodDatabase
import com.prince.deliciousbites.database.FoodEntity
import com.prince.deliciousbites.model.RestaurantMenu
import com.prince.deliciousbites.util.AlertDialogBoxes
import com.prince.deliciousbites.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {
    lateinit var recyclerCart: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var rlCart: RelativeLayout
    lateinit var rlOrderPlaced: RelativeLayout
    lateinit var btnPlaceOrder: Button
    lateinit var btnOrderPlaced: Button
    lateinit var txtCartItems: TextView

    var resId: String? = "100"
    var resName: String? = "Restaurant Name"
    var dbFoodList = listOf<FoodEntity>()
    var orderList = ArrayList<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        rlCart = findViewById(R.id.rlCart)
        rlOrderPlaced = findViewById(R.id.rlOrderPlaced)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        btnOrderPlaced = findViewById(R.id.btnOrderPlaced)
        txtCartItems = findViewById(R.id.txtCartItems)
        rlCart.visibility = View.VISIBLE
        rlOrderPlaced.visibility = View.GONE

        recyclerCart = findViewById(R.id.recyclerCart)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        layoutManager = LinearLayoutManager(this)

        dbFoodList = RetrieveFoods(this).execute().get()

        //Extracting Food list from database which is in JSON form

        for (orderEntity in dbFoodList) {
            orderList.addAll(
                Gson().fromJson(
                    orderEntity.foodItems,
                    Array<RestaurantMenu>::class.java
                ).asList()
            )
        }

        recyclerAdapter = CartRecyclerAdapter(this, orderList)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager

        setUpToolbar()

        if (intent != null) {
            resId = intent.getStringExtra("res_id")
            resName = intent.getStringExtra("res_name")
        } else {
            finish()
            Toast.makeText(this@CartActivity, "Some unexpected error occurred!", Toast.LENGTH_SHORT)
                .show()
        }
        if (resId == "100" || resName == "Restaurant Name") {
            finish()
            Toast.makeText(this@CartActivity, "Some unexpected error occurred!", Toast.LENGTH_SHORT)
                .show()
        }

        txtCartItems.append(resName)
        btnPlaceOrder.text = "Place Order (Total: Rs. ${totalCost()})"

        btnPlaceOrder.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            rlCart.visibility = View.GONE
            rlOrderPlaced.visibility = View.GONE
            sendServerRequest()
        }

        btnOrderPlaced.setOnClickListener {
            val intent = Intent(
                this@CartActivity,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class RetrieveFoods(val context: Context) : AsyncTask<Void, Void, List<FoodEntity>>() {

        override fun doInBackground(vararg params: Void?): List<FoodEntity> {
            val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods-db").build()

            return db.foodDao().getAllFoods()
        }

    }

    private fun sendServerRequest() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        val jsonParams = JSONObject()

        val sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("user_id", null)

        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", resId)
        jsonParams.put("total_cost", totalCost().toString())

        val jSONArray = JSONArray()
        for (i in 0 until orderList.size) {
            val jSONObject = JSONObject()
            jSONObject.put("food_item_id", orderList[i].foodId)
            jSONArray.put(i, jSONObject)
        }
        jsonParams.put("food", jSONArray)

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val dataMainObject = it.getJSONObject("data")
                        val success = dataMainObject.getBoolean("success")

                        if (success) {
                            DeleteAllFoods(this).execute()
                            progressLayout.visibility = View.GONE
                            rlCart.visibility = View.GONE
                            rlOrderPlaced.visibility = View.VISIBLE

                        } else {
                            progressBar.visibility = View.GONE
                            rlCart.visibility = View.VISIBLE
                            Toast.makeText(this, "Some error occurred!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        progressBar.visibility = View.GONE
                        rlCart.visibility = View.VISIBLE
                        Toast.makeText(this, "Some error occurred!", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    progressBar.visibility = View.GONE
                    rlCart.visibility = View.VISIBLE
                    Toast.makeText(this, "Volley error $it", Toast.LENGTH_SHORT).show()
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
            rlCart.visibility = View.VISIBLE
            AlertDialogBoxes().noInternet(this)
        }
    }

    //Calculating total cost of the foods
    private fun totalCost(): Int {
        var sum = 0
        for (i in 0 until orderList.size) {
            val cost = orderList[i].foodPrice.toInt()
            sum += cost
        }
        return sum
    }

    class DeleteAllFoods(val context: Context) : AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            //Remove all the foods from Cart
            db.foodDao().deleteAllFood()
            db.close()
            return true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        DeleteAllFoods(this).execute()
        super.onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (rlCart.visibility == View.VISIBLE) {
            DeleteAllFoods(this).execute()
            super.onBackPressed()
        } else if (rlOrderPlaced.visibility == View.VISIBLE) {
            val intent = Intent(
                this@CartActivity,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }

    }

    override fun onStop() {
        DeleteAllFoods(this).execute()
        super.onStop()
    }

}
