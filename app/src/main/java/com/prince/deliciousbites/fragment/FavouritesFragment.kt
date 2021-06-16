package com.prince.deliciousbites.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.prince.deliciousbites.R
import com.prince.deliciousbites.adapter.DashboardRecyclerAdapter
import com.prince.deliciousbites.database.RestaurantDatabase
import com.prince.deliciousbites.database.RestaurantEntity
import com.prince.deliciousbites.model.Restaurant

class FavouritesFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var rlFavourites: RelativeLayout
    lateinit var rlNoFavourites: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: DashboardRecyclerAdapter

    var arrayRestaurantList = ArrayList<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourites)
        progressLayout = view.findViewById(R.id.progressLayout)
        rlFavourites = view.findViewById(R.id.rlFavourites)
        rlNoFavourites = view.findViewById(R.id.rlNoFavorites)
        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        manageRecycler()

        return view
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {

        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db")
                .build()

            return db.restaurantDao().getAllRestaurants()
        }

    }

    private fun manageRecycler() {
        val dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()
        for (restaurantEntity in dbRestaurantList) {
            val restaurants = Restaurant(
                restaurantEntity.restaurant_id.toString(),
                restaurantEntity.restaurantName,
                restaurantEntity.restaurantRating,
                restaurantEntity.restaurantPrice,
                restaurantEntity.restaurantImage
            )
            arrayRestaurantList.add(restaurants)
        }

        if (activity != null) {
            recyclerAdapter = DashboardRecyclerAdapter(activity as Context, arrayRestaurantList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }

        if (dbRestaurantList.isEmpty()) {
            progressLayout.visibility = View.GONE
            rlFavourites.visibility = View.GONE
            rlNoFavourites.visibility = View.VISIBLE
        } else {
            progressLayout.visibility = View.GONE
            rlFavourites.visibility = View.VISIBLE
            rlNoFavourites.visibility = View.GONE
        }
    }
}
