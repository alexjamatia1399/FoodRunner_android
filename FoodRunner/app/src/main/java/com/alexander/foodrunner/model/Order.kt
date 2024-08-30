package com.alexander.foodrunner.model

data class Order (
        val orderId: String,
        val resName: String,
        val totalCost: String,
        val orderTime: String,
        val foodList: ArrayList<Food>
)