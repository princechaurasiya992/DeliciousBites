package com.prince.deliciousbites.util

import com.prince.deliciousbites.model.Restaurant
import com.prince.deliciousbites.model.RestaurantMenu

class Sorter {
    companion object {
        var restaurantsRatingComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
            if (restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true) == 0) {
                //Sort according to name if rating is same
                restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
            } else {
                restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true)
            }
        }
        var restaurantsPriceComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
            if (restaurant1.restaurantPrice.compareTo(restaurant2.restaurantPrice, true) == 0) {
                //Sort according to name if rating is same
                restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
            } else {
                restaurant1.restaurantPrice.toInt() - restaurant2.restaurantPrice.toInt()
            }
        }
        var foodsPriceComparator = Comparator<RestaurantMenu> { food1, food2 ->
            if (food1.foodPrice.compareTo(food2.foodPrice, true) == 0) {
                //Sort according to name if rating is same
                food1.foodName.compareTo(food2.foodName, true)
            } else {
                food1.foodPrice.toInt() - food2.foodPrice.toInt()
            }
        }
    }
}