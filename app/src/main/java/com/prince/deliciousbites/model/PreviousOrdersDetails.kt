package com.prince.deliciousbites.model

import org.json.JSONArray

data class PreviousOrdersDetails(
    val foodItem: JSONArray,
    val orderId: String,
    val orderDate: String,
    val resName: String
)