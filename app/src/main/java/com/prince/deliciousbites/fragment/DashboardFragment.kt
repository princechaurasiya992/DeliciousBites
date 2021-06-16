package com.prince.deliciousbites.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.prince.deliciousbites.R
import com.prince.deliciousbites.adapter.DashboardRecyclerAdapter
import com.prince.deliciousbites.model.Restaurant
import com.prince.deliciousbites.util.AlertDialogBoxes
import com.prince.deliciousbites.util.ConnectionManager
import com.prince.deliciousbites.util.Sorter
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    lateinit var progressLayout: RelativeLayout

    val restaurantInfoList = arrayListOf<Restaurant>()

    var checkedItem = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        sendServerRequest()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sorter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        if (id == R.id.action_sort) {

            val builder = AlertDialog.Builder(activity as Context)
            var listener = 100

            builder.setTitle("Sort By?")
            builder.setSingleChoiceItems(
                R.array.restaurants_sort_by,
                checkedItem
            ) { dialog, which ->
                listener = which
                checkedItem = which
            }
            builder.setPositiveButton("OK") { dialog, which ->
                if (restaurantInfoList.isNotEmpty()) {
                    when (listener) {
                        0 -> {
                            Collections.sort(restaurantInfoList, Sorter.restaurantsRatingComparator)
                            restaurantInfoList.reverse()
                        }
                        1 -> {
                            Collections.sort(restaurantInfoList, Sorter.restaurantsPriceComparator)
                            restaurantInfoList.reverse()
                        }
                        2 -> {
                            Collections.sort(restaurantInfoList, Sorter.restaurantsPriceComparator)
                        }
                        100 -> Toast.makeText(
                            activity as Context,
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
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    // Here we will handle the response
                    try {
                        progressLayout.visibility = View.GONE

                        val dataMainObject = it.getJSONObject("data")
                        val success = dataMainObject.getBoolean("success")

                        if (success) {
                            val data = dataMainObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJasonObject = data.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJasonObject.getString("id"),
                                    restaurantJasonObject.getString("name"),
                                    restaurantJasonObject.getString("rating"),
                                    restaurantJasonObject.getString("cost_for_one"),
                                    restaurantJasonObject.getString("image_url")
                                )
                                restaurantInfoList.add(restaurantObject)
                                recyclerAdapter =
                                    DashboardRecyclerAdapter(
                                        activity as Context,
                                        restaurantInfoList
                                    )
                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
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
