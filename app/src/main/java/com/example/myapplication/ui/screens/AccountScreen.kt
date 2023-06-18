package com.example.myapplication.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.FoodItem
import com.example.myapplication.data.MealItem
import com.example.myapplication.ui.components.BottomNavigation
import com.example.myapplication.viewmodels.FoodViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: FoodViewModel,
    firebaseApp: FirebaseApp
) {
    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance(firebaseApp)
    var user by remember {
        mutableStateOf(auth.currentUser)
    }

    LaunchedEffect(Unit) {
        auth.addAuthStateListener {
            user = it.currentUser
        }
    }

    var email by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var loginState by remember { mutableStateOf("") }

    val mealItems = viewModel.mealItemsLiveData.collectAsState(initial = listOf())
    val foodItems = viewModel.foodItemsLiveData.collectAsState(initial = listOf())

    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (user != null) {
                    Text(text = "Logged in as ${user!!.email}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        saveDatabase(user!!.uid, foodItems.value, mealItems.value, firebaseApp)
                    }) {
                        Text("Save to server")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        loadDatabase(user!!.uid, viewModel, firebaseApp)
                    }) {
                        Text("Load from server")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { auth.signOut() }) {
                        Text("Sign out")
                    }
                } else {
                    OutlinedTextField(

                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(loginState)
                    Button(onClick = {
                        auth.signInWithEmailAndPassword(email.text, password.text)
                            .addOnSuccessListener { authResult ->
                                Toast.makeText(
                                    context,
                                    "${authResult.user.toString()} has been logged in successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                authResult.user?.let {
                                    loginState = "${it.email} has been logged in successfully"
                                }
                            }.addOnFailureListener { ex ->
                                ex.message?.let {
                                    loginState = it
                                }
                            }

                    }) {
                        Text("Sign In")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        auth.createUserWithEmailAndPassword(email.text, password.text)
                            .addOnSuccessListener { authResult ->
                                Toast.makeText(
                                    context,
                                    "${authResult.user.toString()} has been registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                authResult.user?.let {
                                    loginState = "${it.email} has been registered successfully"
                                }
                            }.addOnFailureListener { ex ->
                                ex.message?.let {
                                    loginState = it
                                }
                            }

                    }) {
                        Text("Register")
                    }
                }
            }
        }
    )
}

fun loadDatabase(user: String, viewModel: FoodViewModel, firebaseApp: FirebaseApp) {
    val fdb = Firebase.database("https://log-food-default-rtdb.europe-west1.firebasedatabase.app")
    fdb.reference.child(user).child("fooditem").get().addOnSuccessListener { snapshot ->
        val foodItems = mutableListOf<FoodItem>()
        for (child in snapshot.children) {
            child.getValue<FoodItem>()?.let { foodItems.add(it) }
        }
        viewModel.deleteAllFood()
        viewModel.insertAllFood(*foodItems.toTypedArray())
        Toast.makeText(firebaseApp.applicationContext, "Food loaded successfully", Toast.LENGTH_SHORT)
            .show()
    }
    fdb.reference.child(user).child("mealitem").get().addOnSuccessListener { snapshot ->
        val mealItems = mutableListOf<MealItem>()
        for (child in snapshot.children) {
            child.getValue<MealItem>()?.let { mealItems.add(it) }
        }
        viewModel.deleteAllMeals()
        viewModel.insertAllMeals(*mealItems.toTypedArray())
        Toast.makeText(firebaseApp.applicationContext, "Meals loaded successfully", Toast.LENGTH_SHORT)
            .show()
    }
}


fun saveDatabase(
    user: String,
    foodItems: List<FoodItem>,
    mealItems: List<MealItem>,
    firebaseApp: FirebaseApp
) {
    val fdb = Firebase.database("https://log-food-default-rtdb.europe-west1.firebasedatabase.app")
    fdb.reference.child(user).child("fooditem").removeValue().addOnSuccessListener {
        fdb.reference.child(user).child("fooditem").updateChildren(foodItems.map {
            Random.nextInt().toString() to it
        }.toMap()).addOnSuccessListener {
            Toast.makeText(firebaseApp.applicationContext, "Food saved successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fdb.reference.child(user).child("mealitem").removeValue().addOnSuccessListener {
        fdb.reference.child(user).child("mealitem").updateChildren(mealItems.map {
            Random.nextInt().toString() to it
        }.toMap()).addOnSuccessListener {
            Toast.makeText(firebaseApp.applicationContext, "Meals saved successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }
}