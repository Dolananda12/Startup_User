package com.example.startup_user

data class Month(
    var deviceToken : String,
    var month: String,
    var year: String,
    val burnRate: Int,
    val monthlyRevenue :Int,
    val newCustomer: Int,
    val monthlyActiveUsers: Int,
    val teamSize: Int,
    val netIncome : Int
)