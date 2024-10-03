package com.example.startup_user

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.SideEffect
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.startup_user.ui.theme.Startup_UserTheme
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.io.InputStream
import java.time.LocalDate
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.dashboard.data.DataSource
import com.example.dashboard.model.Affirm
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainActivityViewModel
    var date = LocalDate.now()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory = ViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[MainActivityViewModel::class.java]
        setContent {
            val navController = rememberNavController()
            GetToken()
            Startup_UserTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White
                ) { innerPadding ->
                    Navigation(navHostController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
            handleDeepLink(intent, navController)
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {
                }
            )
            SideEffect {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private fun handleDeepLink(intent: Intent, navController: NavHostController) {
        intent.data?.let { uri ->
            when (uri.path) {
                "/report" -> navController.navigate("report")
                "/home"   -> navController.navigate("home")
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MonthlyReport(){
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black, // Border color when focused
            unfocusedBorderColor = Color.Gray, // Border color when not focused
            focusedLabelColor = Color.Black, // Label color when focused
            unfocusedLabelColor = Color.Gray, // Label color when not focused
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
        val cardColor = CardColors(Color.Blue,CardDefaults.cardColors().contentColor,CardDefaults.cardColors().disabledContainerColor,CardDefaults.cardColors().disabledContentColor)
        var burn_rate by remember {
            mutableStateOf(0)
        }
        var new_cust_growth by remember {
            mutableStateOf(0)
        }
        var monthly_active_users by remember {
            mutableStateOf(0)
        }
        var month_revenue by remember {
            mutableStateOf(0)
        }
        var netIncome by remember {
            mutableStateOf(0)
        }
        var month_number by remember {
            mutableStateOf("")
        }
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
             Text("Filing Monthly Report", color = Color.Blue, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp))
            OutlinedTextField(
                value = month_number.toString(),
                onValueChange = {
                    month_number=it
                    viewModel.monthnumber=month_number
                },
                label = { Text(text = "Enter Month(Name)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = textFieldColors
            )
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), colors = cardColor){
                 OutlinedTextField(
                     value = month_revenue.toString(),
                     onValueChange = {
                         month_revenue=Integer.parseInt(it.toString())
                         viewModel.month_revenue=month_revenue
                     },
                     label = { Text(text = "Revenue for this month") },
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(8.dp),
                     colors = textFieldColors,
                     keyboardOptions = KeyboardOptions.Default.copy(
                         keyboardType = KeyboardType.Number
                     )
                 )
             }
            OutlinedTextField(
                value = burn_rate.toString(),
                onValueChange = {
                    burn_rate=Integer.parseInt(it.toString())
                    viewModel.burn_rate=burn_rate
                },
                label = { Text(text = "Burn rate:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // Set keyboard type to Number
                )
            )
            OutlinedTextField(
                value = new_cust_growth.toString(),
                onValueChange = {
                    new_cust_growth=Integer.parseInt(it.toString())
                    viewModel.cust_growth=new_cust_growth
                },
                label = { Text(text = "Customer growth rate:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // Set keyboard type to Number
                )
            )
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), colors = cardColor) {
                OutlinedTextField(
                    value = monthly_active_users.toString(),
                    onValueChange = {
                        monthly_active_users = Integer.parseInt(it.toString())
                        viewModel.monthly_active_users = monthly_active_users
                    },
                    label = { Text(text = "Monthly Active Users") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                ))
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), colors = cardColor) {
                OutlinedTextField(
                    value = netIncome.toString(),
                    onValueChange = {
                        netIncome = Integer.parseInt(it.toString())
                        viewModel.netIncome = netIncome
                    },
                    label = { Text(text = "Net Income") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number // Set keyboard type to Number
                    )
                )
            }
            Button(onClick = {
                  viewModel.sendReport(applicationContext)
            }) {
                Text(text = "Submit", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Image(painter = painterResource(id = R.drawable.order_svgrepo_com), contentDescription = "pic")
       }
    }
    @Composable
    fun Navigation(navHostController: NavHostController,modifier: Modifier){
        NavHost(
            navController = navHostController,
            modifier=modifier,
            startDestination = "view_reports", // Your start destination rout
        ){
            composable("view_reports"){viewReports()}
            composable("report") { MonthlyReport() }
            composable("home"){OpeningPage(navHostController = navHostController)}
            composable("register"){Index_0(navHostController = navHostController)}
            composable("first"){Index_1(navHostController = navHostController)}
            composable("second"){Index_2(navHostController = navHostController)}
            composable("third"){Index_3(navHostController = navHostController)}
            composable("fourth"){Index_4(navHostController = navHostController)}
            composable("login"){ loginPage(navHostController = navHostController) }
            composable("loggedIN"){Home(navHostController=navHostController)}
        }
    }
    @Composable
    fun viewReports(){
        var s by remember {
            mutableStateOf(false)
        }
        viewModel.viewReports(applicationContext).observe(this@MainActivity, Observer {
            if(it){
                s=true
                println("reports came:"+viewModel.data_set)
            }
        })
        if(s) {
            Column(modifier = Modifier.fillMaxSize()) {

            }
        }
    }
    @Composable
    fun displayReport(month: Month){
      /*Column(modifier = Modifier
          .fillMaxWidth()
          .padding(5.dp)){
          Text(text = month.netIncome.toString(), color = Color.Black, fontSize = 15.sp)
      }*/
    }
    @Composable
    fun Home(navHostController: NavHostController) {
        Column {
            Text(text = "Insightix!",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue,
                modifier = Modifier
                    .padding(start = 50.dp,top = 60.dp, end = 0.dp, bottom = 0.dp)
            )
            Text(text = "Hello ${viewModel.name}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 40.dp,top = 30.dp, end = 0.dp, bottom = 0.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(DataSource.topics) { Affirm ->
                    TopicCard(Affirm,{
                        navHostController.navigate("view_report")
                    })
                }
            }
            Image(painter = painterResource(id = R.drawable.monitor_startup_svgrepo_com),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(240.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom){
                Text(text = "Empowering Startups, Solving Challenges.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(start = 20.dp,top = 20.dp, end = 0.dp, bottom = 0.dp)
                )
            }
        }
    }

    @Composable
    fun TopicCard(affirm: Affirm,onClick: () -> Unit) {
        Card(modifier = Modifier.clickable {
            onClick()
        }){
            Column{
                Box {
                    Image(
                        painter = painterResource(id = affirm.imageResourceId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .aspectRatio(1f)
                            .padding(start = 20.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Text(
                    text = stringResource(id = affirm.stringResourceId),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 50.dp,),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun loginPage(navHostController: NavHostController) {
        var username by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Blue, // Border color when focused
            unfocusedBorderColor = Color.Gray, // Border color when not focused
            focusedLabelColor = Color.Blue, // Label color when focused
            unfocusedLabelColor = Color.Gray, // Label color when not focused
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    )
            ) {
                Text(
                    text = "Welcome to Insightix!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    label = { Text(text = "Enter UserName") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = { Text(text = "Enter Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = textFieldColors
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Button(
                        onClick = {
                            viewModel.Authentiaction(applicationContext,username,password)
                                .observe(this@MainActivity, Observer {
                                    if(it){
                                        navHostController.navigate("loggedIN")
                                    }
                                })
                        },
                        colors = ButtonColors(
                            Color.Blue,
                            ButtonDefaults.buttonColors().contentColor,
                            ButtonDefaults.buttonColors().disabledContainerColor,
                            ButtonDefaults.buttonColors().disabledContentColor
                        )
                    ) {
                        Text(text = "Next", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
    @Composable
    fun GetToken(){
        LaunchedEffect(key1 = true) {
            try {
                val token=FirebaseMessaging.getInstance().token.await()
                println("token is $token")
                viewModel.token= token.toString()
            }catch (e : Exception){
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "$e", Toast.LENGTH_SHORT).show()
            }
        }

    }
    @Composable
    fun OpeningPage(navHostController: NavHostController) { //SignUp page,Login page
        var index by remember {
            mutableStateOf(true)
        }
        Column(modifier = Modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(painterResource(id = R.drawable.start_img), contentDescription = "starting_image", modifier = Modifier.padding(5.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Get connected,organised and insightful at Insightix!",
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    fontSize = 30.sp
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp), horizontalArrangement = Arrangement.Center){
                Text(
                    text = "One stop solution to all problems faced by startups!!",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        index=true
                        navHostController.navigate("login")
                    }, colors = ButtonColors(
                        containerColor =
                        if (index) Color.Blue
                        else Color.White,
                        contentColor = ButtonDefaults.buttonColors().contentColor,
                        ButtonDefaults.buttonColors().disabledContainerColor,
                        ButtonDefaults.buttonColors().disabledContentColor
                    )
                ) {
                    Text(text = "Login", fontSize = 18.sp,
                        color = if (index) Color.White
                        else Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        index=false
                        navHostController.navigate("register")
                    }, colors = ButtonColors(
                        containerColor =
                        if (!index) Color.Blue
                        else Color.White,
                        contentColor = ButtonDefaults.buttonColors().contentColor,
                        ButtonDefaults.buttonColors().disabledContainerColor,
                        ButtonDefaults.buttonColors().disabledContentColor
                    )
                ) {
                    Text(text = "Register", fontSize = 18.sp, color =  if (!index) Color.White
                    else Color.Black)
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Index_0(navHostController: NavHostController) {
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Blue, // Border color when focused
            unfocusedBorderColor = Color.Gray, // Border color when not focused
            focusedLabelColor = Color.Blue, // Label color when focused
            unfocusedLabelColor = Color.Gray, // Label color when not focused
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .border(2.dp, Color.Blue, shape = RoundedCornerShape(16.dp)), // Rounded corners for border
                shape = RoundedCornerShape(16.dp), // Rounded corners for the card
                colors = CardDefaults.cardColors(
                    containerColor = Color.White, // Card background
                    contentColor = Color.Black // Default text color
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp), // Added padding for spacing inside the card
                    verticalArrangement = Arrangement.spacedBy(10.dp) // Space between items
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Welcome to Insightix!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Color.Blue
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "You must go through the KYC process to help us understand more about your business.",
                            fontSize = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center // Aligns the text in the center
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                navHostController.navigate("first")
                            },colors = ButtonColors(
                                Color.Blue,
                                ButtonDefaults.buttonColors().contentColor,
                                ButtonDefaults.buttonColors().disabledContainerColor,
                                ButtonDefaults.buttonColors().disabledContentColor)
                        ) {
                            Text(text = "Next", fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowCalender(closeSelection: UseCaseState.() -> Unit,onClick:()->Unit){
        val localDate= LocalDate.of(2025,LocalDate.now().monthValue,LocalDate.now().dayOfMonth)
        CalendarDialog(
            state = rememberUseCaseState(visible = true, onCloseRequest = {
                closeSelection() }),
            config = CalendarConfig(
                yearSelection = true,
                monthSelection = true,
                style = CalendarStyle.MONTH,
                disabledDates = listOf(localDate)
            ),
            selection = CalendarSelection.Date { newDate ->
                date=newDate
                onClick()
            }
        )
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Index_1(navHostController: NavHostController){
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Blue, // Border color when focused
            unfocusedBorderColor = Color.Gray, // Border color when not focused
            focusedLabelColor = Color.Blue, // Label color when focused
            unfocusedLabelColor = Color.Gray, // Label color when not focused
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
        var company_name by remember {
            mutableStateOf(viewModel.company_name)
        }
        var contact by remember {
            mutableStateOf(viewModel.contact_SPOC)
        }
        var month_est by remember {
            mutableStateOf(viewModel.month_reg)
        }
        var year_est by remember {
            mutableStateOf(viewModel.year_reg)
        }
        var address by remember {
            mutableStateOf(viewModel.address)
        }
        var selectCalander by remember {
            mutableStateOf(false)
        }
        var email by remember {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Add padding to the entire column
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Space between components
        ) {
            // Header text
            Text(
                text = "Contact Details",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )
            //Company Name
            OutlinedTextField(
                value = company_name,
                onValueChange = {
                    company_name=it
                    viewModel.company_name=company_name
                },
                label = { Text(text = "Company Name") },
                isError = if(contact.toString().length==10) true else false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = textFieldColors
            )
            //email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email=it
                    viewModel.email=email
                },
                label = { Text(text = "Email") },
                isError = if(email.length==0) true else false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = textFieldColors
            )
            // Contact field
            OutlinedTextField(
                value = contact.toString(),
                onValueChange = {
                    contact=Integer.parseInt(it)
                    viewModel.contact_SPOC=contact
                },
                label = { Text(text = "SPOC for Company Contact") },
                isError = if(contact.toString().length==10) true else false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // Set keyboard type to Number
                ),
                colors = textFieldColors
            )
            // Address field
            OutlinedTextField(
                value = address,
                onValueChange = {
                    viewModel.address=it
                    address = it
                                }, // Changed from 'contact' to 'address'
                label = { Text(text = "Registration Address") },
                isError = address.isEmpty(), // Show error if field is empty
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth() // Make the field take full width
                    .padding(8.dp) // Add padding around the field
                , colors = textFieldColors
            )
            Button(
                onClick = { selectCalander = true },
                modifier = Modifier.padding(8.dp) // Add padding around the button
            ) {
                Text(text = "Select Calendar") // Add label to the button
            }
            if (selectCalander) {
                ShowCalender(
                    closeSelection = { selectCalander = false },
                    onClick = {
                        month_est = date.monthValue
                        year_est = date.year
                        viewModel.month_reg=month_est
                        viewModel.year_reg=year_est
                    }
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                Button(
                    onClick = {
                        navHostController.navigate("second")
                    },colors = ButtonColors(
                        Color.Blue,
                        ButtonDefaults.buttonColors().contentColor,
                        ButtonDefaults.buttonColors().disabledContainerColor,
                        ButtonDefaults.buttonColors().disabledContentColor)
                ) {
                    Text(text = "Next", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Index_2(navHostController: NavHostController) {
        var domain by remember { mutableStateOf(viewModel.domain) }
        var expanded by remember { mutableStateOf(false) }
        var subdomain by remember { mutableStateOf(viewModel.subdomain) } // Optional field
        var businessStructure by remember { mutableStateOf(viewModel.subdomain) }
        var companyPan by remember { mutableStateOf(viewModel.panCard) }
        val domainOptions = listOf("IT", "Agriculture", "Clothing", "Finance", "Healthcare")
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Blue, // Border color when focused
            unfocusedBorderColor = Color.Gray, // Border color when not focused
            focusedLabelColor = Color.Blue, // Label color when focused
            unfocusedLabelColor = Color.Gray, // Label color when not focused
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Add padding to the entire column
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Space between components
        ) {
            Text(
                text = "Company Details",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = domain,
                    onValueChange = { /* No change needed here, selection happens below */ },
                    label = { Text("Select Domain") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    readOnly = true, // Makes it read-only so user selects from the dropdown
                    trailingIcon = { // Icon to indicate dropdown
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    isError = domain.isEmpty() // Show error if domain is not selected
                    , colors = textFieldColors
                )
                val scrollState_1= rememberScrollState()
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    scrollState=scrollState_1
                ) {
                    domainOptions.forEach { selectedOption ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(text = selectedOption) },
                            onClick = {
                                domain = selectedOption
                                viewModel.domain=domain
                                expanded = false
                            }
                        )
                    }
                }
            }
            // Subdomain field (optional)
            OutlinedTextField(
                value = subdomain,
                onValueChange = {
                    subdomain = it
                    viewModel.subdomain=subdomain
                },
                label = { Text(text = "Enter Subdomain (Optional)") },
                isError = false, // No error for optional field
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = textFieldColors
            )
            OutlinedTextField(
                value = businessStructure,
                onValueChange = {
                    businessStructure = it
                    viewModel.business_structure=businessStructure
                                },
                label = { Text(text = "Enter Business Structure") },
                isError = businessStructure.isEmpty(), // Show error if field is empty
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = textFieldColors
            )
            OutlinedTextField(
                value = companyPan,
                onValueChange = { companyPan = it
                                viewModel.panCard=companyPan
                                },
                label = { Text(text = "Enter Company PAN") },
                isError = companyPan.isEmpty(), // Show error if field is empty
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                , colors = textFieldColors
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { navHostController.navigate("third") }, // Navigates to the next screen
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Next", fontSize = 16.sp)
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Index_3(navHostController: NavHostController) {
        var numberOfEmployees by remember { mutableStateOf(viewModel.no_employees) }
        var moaBase64 by remember { mutableStateOf(viewModel.moa_base64) }
        var aoaBase64 by remember { mutableStateOf(viewModel.aoa_base64) }

        // Launcher to handle file selection
        val moaLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            moaBase64 = uri?.let { convertToBase64(it,applicationContext) } ?: ""
        }
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Blue, // Border color when focused
            unfocusedBorderColor = Color.Gray, // Border color when not focused
            focusedLabelColor = Color.Blue, // Label color when focused
            unfocusedLabelColor = Color.Gray, // Label color when not focused
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
        val aoaLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            aoaBase64 = uri?.let { convertToBase64(it,applicationContext) } ?: ""
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Internal Details",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )
            OutlinedTextField(
                value = numberOfEmployees.toString(),
                onValueChange = {
                    numberOfEmployees=Integer.parseInt(it)
                    viewModel.no_employees=numberOfEmployees
                },
                label = { Text(text = "Number of Employees") },
                isError = if(numberOfEmployees==0) true else false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // Set keyboard type to Number
                ),
                colors = textFieldColors
            )

            // MOA Upload Button
            Button(
                onClick = { moaLauncher.launch("application/pdf") }, // Accept only PDFs
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Upload MOA (Memorandum of Association)")
            }

            // Show MOA upload status
            if (moaBase64.isNotEmpty()) {
                viewModel.moa_base64=moaBase64
                Text("MOA Uploaded Successfully", color = Color.Green)
            }

            // AOA Upload Button
            Button(
                onClick = { aoaLauncher.launch("application/pdf") }, // Accept only PDFs
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Upload AOA (Article of Association)")
            }

            // Show AOA upload status
            if (aoaBase64.isNotEmpty()) {
                viewModel.aoa_base64=aoaBase64
                Text("AOA Uploaded Successfully", color = Color.Green)
            }

            // Next Button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { navHostController.navigate("fourth") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Next", fontSize = 16.sp)
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Index_4(navHostController: NavHostController) {
        // Variables to store user inputs
        var fundingStatus by remember { mutableStateOf("") }
        var hasIntellectualProperty by remember { mutableStateOf(false) }
        var financialReportUri: Uri? by remember { mutableStateOf(null) }
        var financialReportBase64 by remember { mutableStateOf("") }
        val context = LocalContext.current
        // File picker for latest financial report
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Blue, // Border color when focused
            unfocusedBorderColor = Color.Gray, // Border color when not focused
            focusedLabelColor = Color.Blue, // Label color when focused
            unfocusedLabelColor = Color.Gray, // Label color when not focused
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                uri?.let {
                    financialReportUri = it
                    financialReportBase64 = convertToBase64(it, context)
                    Toast.makeText(context, "Financial Report Uploaded", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Company Metrics",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )

            // Current Funding Status input
            OutlinedTextField(
                value = fundingStatus,
                onValueChange = {
                    fundingStatus = it
                    viewModel.funding_status=fundingStatus
                    },
                label = { Text(text = "Enter Current Funding Status") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = textFieldColors
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Own Intellectual Property?")
                Switch(
                    checked = hasIntellectualProperty,
                    onCheckedChange = { hasIntellectualProperty = it
                    viewModel.Intellectual_Property=hasIntellectualProperty
                                      },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                )
            }
            Button(
                onClick = {
                    launcher.launch("application/pdf") // Allowing only PDF files for simplicity
                },modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White)
            ){
                Text(text = "Upload Latest Financial Report", color = Color.Green)
            }
           if (financialReportUri != null) {
                Text(
                    text = "File Uploaded: ${financialReportUri!!.lastPathSegment}",
                    modifier = Modifier.padding(8.dp),
                    color = Color.Gray
                )
                viewModel.financial_reportBase64=financialReportBase64
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                Button(
                    onClick = {
                        viewModel.sendKYC(applicationContext)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Next", fontSize = 16.sp)
                }
            }
        }
    }
    fun convertToBase64(uri: Uri, context: Context): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return ""
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
    }
}