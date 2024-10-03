package com.example.startup_user

import kotlinx.serialization.Serializable
@Serializable
data class SignUPData(
    val company_name: String,
    val email : String,
    val articles_of_association: String,
    val business_structure: String,
    val company_domain: String,
    val contact_representative: Number,
    val current_funding_status: String,
    val deviceToken: String,
    val intellectual_property: String,
    val latest_financial_report: String,
    val memorandum_of_association: String,
    val number_of_employees: Number,
    val registered_address: String,
    val registration_month: String,
    val registration_year: String,
    val sub_domain: String
)

