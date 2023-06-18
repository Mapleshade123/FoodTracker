package com.example.myapplication.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.FoodItem
import com.example.myapplication.data.MealItem
import com.example.myapplication.ui.components.BottomNavigation
import com.example.myapplication.viewmodels.FoodViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTrackingScreen(navController: NavController, viewModel: FoodViewModel) {
    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = { AddMealButton(navController = navController) },
        bottomBar = { BottomNavigation(navController = navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item { TodaysMeals(viewModel) }
            item { WeeklyStats(viewModel) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Meal Tracking") }
    )
}

@Composable
fun AddMealButton(navController: NavController) {
    FloatingActionButton(onClick = { navController.navigate(Screen.MealLog.route) }) {
        Icon(Icons.Default.Add, contentDescription = "Add Meal")
    }
}

@Composable
fun TodaysMeals(viewModel: FoodViewModel) {
    val meals = viewModel.getMealsByDate(LocalDate.now().atStartOfDay(), LocalDateTime.now())
        .collectAsState(
            initial = listOf()
        )

    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Today's meals",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )
            meals.value.forEach { meal ->
                val food =
                    viewModel.getFoodItem(meal.foodId).collectAsState(initial = FoodItem("", 0))
                Text(
                    text = " ${food.value.name}, Quantity: ${meal.quantity}",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun WeeklyStats(viewModel: FoodViewModel) {
    val statsArr = mutableListOf(
        LocalDate.now() to viewModel.getCaloriesByDate(
            LocalDate.now().atStartOfDay(),
            LocalDate.now().atStartOfDay().plusDays(1)
        ).collectAsState(initial = 0)
    )

    for (i in 1..6) {
        statsArr.add(LocalDate.now().minusDays(i.toLong()) to viewModel.getCaloriesByDate(
            LocalDate.now().minusDays(i.toLong()).atStartOfDay(),
            LocalDate.now().minusDays(i.toLong() - 1).atStartOfDay()
        ).collectAsState(initial = 0))
    }

    statsArr.reverse()

    val stats = statsArr.toMap()

    val max = max(2000, stats.maxOf { it.value.value }.toLong() )
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Statistics",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )
            BarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                data = stats,
                max = max
            )  //Added size modifiers
        }
    }
}

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: Map<LocalDate, State<Int>>,
    max: Long,
    gridLineCount: Int = 5 // Number of grid lines
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val barWidth =
                (size.width - 40.dp.toPx()) / data.size - 16 // Subtract a margin for spacing between bars and to fit grid labels
            var xOffset = 40.dp.toPx() // Leave some space on the left for grid labels
            val gridLabelPaint = android.graphics.Paint().apply {
                textSize = 20f
                color = android.graphics.Color.BLACK
            }

            // Draw the grid lines and labels
            for (i in 0 until gridLineCount) {
                val lineHeight = size.height * (i.toFloat() / (gridLineCount - 1))
                drawLine(
                    color = Color.LightGray,
                    start = Offset(xOffset, lineHeight),
                    end = Offset(size.width, lineHeight)
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "${((gridLineCount - 1 - i) * (max / (gridLineCount - 1))).toInt()}",
                    0f,
                    lineHeight,
                    gridLabelPaint
                )
            }

            // Draw the bars
            data.forEach { (day, value) ->
                val barHeight = (value.value.toFloat() / max) * size.height
                drawRoundRect(
                    color = Color.Blue,
                    topLeft = Offset(
                        xOffset + 8,
                        size.height - barHeight
                    ), // Add a half of the margin to the left of each bar
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
                drawContext.canvas.nativeCanvas.drawText(
                    day.format(DateTimeFormatter.ofPattern("dd MMM")),
                    xOffset + 8,
                    size.height + 32, // Offset the text below the canvas
                    gridLabelPaint
                )
                xOffset += barWidth + 16 // Move to the right for the next bar
            }
        }
    }
}
