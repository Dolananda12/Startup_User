package com.example.startup_user
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.startup_user.Network.AuthenticationInterface
import com.example.startup_user.Network.RetrofitInstance
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainActivityViewModel : ViewModel() {
    var monthnumber=""
    var burn_rate = 0
    var cust_growth = 0
    var cust_rent_rate = 0
    var name = ""
    var monthly_active_users = 0
    var month_revenue = 0
    var teamsize=0
    var netIncome = 0
    var email = ""
    var contact_SPOC = 0
    var address = ""
    var month_reg = 0
    var token =""
    var company_name = ""
    var year_reg = 0
    var domain = "Agriculture"
    var subdomain = ""
    var business_structure = ""
    var panCard = ""
    var no_employees = 0
    var moa_base64 = ""
    var aoa_base64 = ""
    var financial_reportBase64 = ""
    var funding_status = ""
    var Intellectual_Property = false
    fun sendKYC(context: Context) {
        viewModelScope.launch {
            val finalKYC = FinalKYC(
                SignUPData(
                    company_name,
                    email,
                    articles_of_association = aoa_base64,
                    business_structure,
                    domain,
                    contact_SPOC,
                    funding_status,
                   token,
                    intellectual_property = Intellectual_Property.toString(),
                    latest_financial_report = financial_reportBase64,
                    moa_base64,
                    number_of_employees = no_employees,
                    address,
                    month_reg.toString(),
                    year_reg.toString(),
                    subdomain
                )
            )
            val authentication_interface =
                RetrofitInstance.create().create(AuthenticationInterface::class.java)
            try {
                println(finalKYC)
                val response = authentication_interface.sendCredentials(data = finalKYC)
                val responseBody = response.errorBody()?.string() ?: response.body()?.toString()
                println("Raw response: $responseBody")
            } catch (e: Exception) {
                println("Error occurred: ${e.message}")
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun sendReport(context: Context){
        viewModelScope.launch {
            val authentication_interface =
                RetrofitInstance.create().create(AuthenticationInterface::class.java)
            try {
                val response = authentication_interface.sendMonthReport(
                    Month(token,monthnumber,"2024",burn_rate,month_revenue,cust_rent_rate,monthly_active_users,no_employees,netIncome)
                )
                if(response.isSuccessful){
                    Toast.makeText(context,"Hi:${response.body()}", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(context,response.message().toString(), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                println("Error occurred: ${e.message}")
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    var data_set : MutableList<Month> = ArrayList()
    fun viewReports(context: Context):LiveData<Boolean>{
        val s = MutableLiveData(false)
        viewModelScope.launch {
            val authentication_interface =
                RetrofitInstance.create().create(AuthenticationInterface::class.java)
            try{
                val response= authentication_interface.recieveReports(Report12(token))
                if(response.isSuccessful){
                    println(response.body().toString())
                    data_set= Json.decodeFromString(response.body().toString())
                    Toast.makeText(context, "data retrieved", Toast.LENGTH_SHORT).show()
                    s.value=true
                }else{
                    Toast.makeText(context, "response failed", Toast.LENGTH_SHORT).show()
                    s.value=false
                }
            }catch (e : Exception){
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                s.value=false
                e.printStackTrace()
            }
        }
    return s
    }
    fun Authentiaction(context: Context,username:String,password:String): LiveData<Boolean>{
        val s = MutableLiveData<Boolean>(false)
        viewModelScope.launch {
            val authentication_interface =
                RetrofitInstance.create().create(AuthenticationInterface::class.java)
            try {
                val response = authentication_interface.verifyCredentials(
                    AuthObject(username,password)
                )
                if(response.isSuccessful){
                    s.value=true
                    name=response.body().toString()
                    Toast.makeText(context,"Hi:${response.body()}", Toast.LENGTH_SHORT).show()
                }else{
                    s.value=false
                }
                Toast.makeText(context,response.message().toString(), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                s.value=false
                println("Error occurred: ${e.message}")
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    return s
    }
}