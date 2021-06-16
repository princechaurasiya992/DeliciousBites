package com.prince.deliciousbites.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.prince.deliciousbites.R
import com.prince.deliciousbites.activity.RestaurantDetailsActivity
import com.prince.deliciousbites.database.RestaurantDatabase
import com.prince.deliciousbites.database.RestaurantEntity
import com.prince.deliciousbites.model.Restaurant
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantPrice.text = "Rs. ${restaurant.restaurantPrice}/person"
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).into(holder.imgRestaurantImage)

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId.toInt(),
            restaurant.restaurantName,
            restaurant.restaurantPrice,
            restaurant.restaurantRating,
            restaurant.restaurantImage
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imgFavouriteIcon.setImageResource(R.drawable.ic_favourite)
        } else {
            holder.imgFavouriteIcon.setImageResource(R.drawable.ic_non_favourite)
        }

        holder.imgFavouriteIcon.setOnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${restaurant.restaurantName} added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.imgFavouriteIcon.setImageResource(R.drawable.ic_favourite)
                } else {
                    Toast.makeText(context, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${restaurant.restaurantName} removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.imgFavouriteIcon.setImageResource(R.drawable.ic_non_favourite)
                } else {
                    Toast.makeText(context, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.llContent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurantId)
            intent.putExtra("res_name", restaurant.restaurantName)
            context.startActivity(intent)
        }

    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val imgFavouriteIcon: ImageView = view.findViewById(R.id.imgFavouriteIcon)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {

                1 -> {
                    //Check DB if the book is favourite or not
                    val restaurant: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.restaurant_id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    //Save the book into DB as favourite
                    db.restaurantDao().inserRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    //Remove the favourite book
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}