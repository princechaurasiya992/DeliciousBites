package com.prince.deliciousbites.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {
    @Insert
    fun insertFood(foodEntity: FoodEntity)

    @Delete
    fun deleteFood(foodEntity: FoodEntity)

    @Query("DELETE FROM foods")
    fun deleteAllFood()

    @Query("SELECT * FROM foods")
    fun getAllFoods(): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE res_id = :resId")
    fun getFoodById(resId: String): FoodEntity
}