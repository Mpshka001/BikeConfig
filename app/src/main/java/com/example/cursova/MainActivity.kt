package com.example.cursova

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cursova.ui.BrandSelectionScreen
import com.example.cursova.ui.ConfiguratorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // прозора шторка
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "start") {

                // экран 1: выбор бренда
                composable("start") {
                    BrandSelectionScreen(
                        onBrandSelected = { brandName ->
                            // Передаем выбранный бренд как аргумент
                            navController.navigate("configurator/$brandName")
                        }
                    )
                }

                // экран 2: конфигуратор (принимает аргумент бренда)
                composable(
                    route = "configurator/{brand}",
                    arguments = listOf(navArgument("brand") { type = NavType.StringType })
                ) { backStackEntry ->
                    val brand = backStackEntry.arguments?.getString("brand") ?: "Specialized"
                    
                    ConfiguratorScreen(
                        selectedBrand = brand,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onReturnToMenu = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
