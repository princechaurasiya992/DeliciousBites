package com.prince.deliciousbites.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey val res_id: String,
    @ColumnInfo(name = "food_name") val foodItems: String
)