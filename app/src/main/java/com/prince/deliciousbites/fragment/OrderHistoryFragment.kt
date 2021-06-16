package com.prince.deliciousbites.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.prince.deliciousbites.R
import com.prince.deliciousbites.adapter.OrderHistoryRecyclerAdapter
import com.prince.deliciousbites.model.PreviousOrdersDetails
import com.prince.deliciousbites.util.AlertDialogBoxes
import com.prince.deliciousbites.util.ConnectionManager
import java.util.*

class OrderHistoryFragment : Fragment() {

    lateinit var llHasOrders: LinearLayout
    lateinit var orderHistoryRecylerAdapter: OrderHistoryRecyclerAdapter
    var orderHistoryList = ArrayList<PreviousOrdersDetails>()
    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var rlNoOrders: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    var userId: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        llHasOrders = view.findViewById(R.id.llHasOrders)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        progressLayout = view.findViewById(R.id.progressLayout)
        rlNoOrders = view.findViewById(R.id.rlNoOrders)

        layoutManager = LinearLayoutManager(activity as Context)

        progressLayout.visibility = View.VISIBLE
        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        userId = sharedPreferences.getString("user_id", null)
        sendServerRequest(userId)

        return view
    }

    private fun sendServerRequest(str: String?) {

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$str"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    // Here we will handle the response
                    progressLayout.visibility = View.GONE
                    try {
                        val jSONObject2 = it.getJSONObject("data")
                        val success = jSONObject2.getBoolean("success")
                        if (success) {
                            val jSONArray = jSONObject2.getJSONArray("data")
                            if (jSONArray.length() == 0) {
                                llHasOrders.visibility = View.GONE
                                rlNoOrders.visibility = View.VISIBLE
                            }
                            val length = jSONArray.length()
                            for (i in 0 until length) {
                                val jSONObject3 = jSONArray.getJSONObject(i)
                                val jSONArray2 = jSONObject3.getJSONArray("food_items")
                                val i2 = jSONObject3.getString("order_id")
                                val string = jSONObject3.getString("restaurant_name")

                                val string2 = jSONObject3.getString("order_placed_at")

                                orderHistoryList.add(
                                    PreviousOrdersDetails(
                                        jSONArray2,
                                        i2,
                                        string2,
                                        string
                                    )
                                )
                                if (orderHistoryList.isEmpty()) {
                                    llHasOrders.visibility = View.GONE
                                    rlNoOrders.visibility = View.VISIBLE
                                } else {
                                    llHasOrders.visibility = View.VISIBLE
                                    rlNoOrders.visibility = View.GONE

                                    if (activity != null) {

                                        orderHistoryRecylerAdapter = OrderHistoryRecyclerAdapter(
                                            activity as Context,
                                            orderHistoryList
                                        )
                                        recyclerOrderHistory.adapter = orderHistoryRecylerAdapter
                                        recyclerOrderHistory.layoutManager = layoutManager
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    // Here we will handle the errors
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

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
            AlertDialogBoxes().noInternet(activity as Activity)
        }

    }

}
