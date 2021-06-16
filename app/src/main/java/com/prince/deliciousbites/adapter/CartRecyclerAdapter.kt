package com.prince.deliciousbites.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prince.deliciousbites.R
import com.prince.deliciousbites.model.RestaurantMenu

class CartRecyclerAdapter(val context: Context, val foodList: ArrayList<RestaurantMenu>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val food = foodList[position]
        holder.txtCartFoodSN.text = "${position + 1}."
        holder.txtCartFoodName.text = food.foodName
        holder.txtCartFoodPrice.append(food.foodPrice)
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCartFoodSN: TextView = view.findViewById(R.id.txtCartFoodSN)
        val txtCartFoodName: TextView = view.findViewById(R.id.txtCartFoodName)
        val txtCartFoodPrice: TextView = view.findViewById(R.id.txtCartFoodPrice)
    }

}