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
import com.example.cursova.ui.BrandSelectionScreen
import com.example.cursova.ui.ConfiguratorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // включаем режим "от края до края"
        // это убирает фиолетовую полосу и делает статус-бар прозрачным
        // SystemBarStyle.light означает, что иконки (часы) будут темными (для светлого фона)
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
                        onBrandSelected = {
                            navController.navigate("configurator")
                        }
                    )
                }

                // экран 2: конфигуратор
                composable("configurator") {
                    ConfiguratorScreen(
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