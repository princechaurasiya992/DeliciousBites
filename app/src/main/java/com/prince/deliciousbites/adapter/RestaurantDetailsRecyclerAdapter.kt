package com.prince.deliciousbites.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prince.deliciousbites.R
import com.prince.deliciousbites.model.RestaurantMenu

class RestaurantDetailsRecyclerAdapter(
    val context: Context,
    val foodList: ArrayList<RestaurantMenu>,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RestaurantDetailsRecyclerAdapter.RestaurantDetailsViewHolder>() {

    private val list = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurant_details_single_row, parent, false)
        return RestaurantDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: RestaurantDetailsViewHolder, position: Int) {
        val food = foodList[position]
        val restaurantMenu: RestaurantMenu = food
        holder.txtFoodName.text = food.foodName
        holder.txtFoodPrice.text = "Rs. ${food.foodPrice}"
        holder.txtFoodSerialNo.text = "${position + 1}."

        if (food.foodId !in list) {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
        } else {
            holder.btnAdd.visibility = View.GONE
            holder.btnRemove.visibility = View.VISIBLE
        }

        holder.btnAdd.setOnClickListener {

            holder.btnAdd.visibility = View.GONE
            holder.btnRemove.visibility = View.VISIBLE

            listener.onAddItemClick(restaurantMenu)
            list.add(food.foodId)

        }
        holder.btnRemove.setOnClickListener {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE

            listener.onRemoveItemClick(restaurantMenu)
            list.remove(food.foodId)
        }

    }

    class RestaurantDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val txtFoodSerialNo: TextView = view.findViewById(R.id.txtFoodSerialNo)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnRemove: Button = view.findViewById(R.id.btnRemove)
    }

    interface OnItemClickListener {
        fun onAddItemClick(restaurantMenu: RestaurantMenu)
        fun onRemoveItemClick(restaurantMenu: RestaurantMenu)
    }

}