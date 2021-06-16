package com.prince.deliciousbites.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prince.deliciousbites.R
import com.prince.deliciousbites.model.PreviousOrdersDetails
import com.prince.deliciousbites.model.RestaurantMenu
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderHistoryRecyclerAdapter(
    val context: Context,
    val orderHistoryList: ArrayList<PreviousOrdersDetails>
) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val obj: Any = orderHistoryList[position]
        val orderDetails: PreviousOrdersDetails = obj as PreviousOrdersDetails
        holder.txtResHistoryResName.text = orderDetails.resName
        holder.txtDate.text = formatDate(orderDetails.orderDate)
        setUpRecycler(holder.recyclerResHistoryItems, orderDetails)
    }

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResHistoryResName: TextView = view.findViewById(R.id.txtResHistoryResName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerResHistoryItems: RecyclerView = view.findViewById(R.id.recyclerResHistoryItems)
    }

    private fun formatDate(str: String): String {
        val date = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH).parse(str)
        return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date as Date)
    }

    private fun setUpRecycler(recyclerView: RecyclerView, orderDetails: PreviousOrdersDetails) {
        val arrayList = ArrayList<RestaurantMenu>()
        val length: Int = orderDetails.foodItem.length()
        for (i in 0 until length) {
            val jSONObject: JSONObject = orderDetails.foodItem.getJSONObject(i)
            val string = jSONObject.getString("food_item_id")
            val string2 = jSONObject.getString("name")
            val string3 = jSONObject.getString("cost")
            arrayList.add(RestaurantMenu(string, string2, string3))
        }
        val cartItemAdapter = CartRecyclerAdapter(context, arrayList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = cartItemAdapter
    }

}