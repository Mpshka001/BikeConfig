package com.example.cursova.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedBuildsScreen(
    viewModel: ConfiguratorViewModel,
    onBuildSelected: () -> Unit // Щоб повернутися назад після вибору
) {
    // збираємо дані з Flow
    val builds by viewModel.savedBuilds.collectAsState(initial = emptyList())

    if (builds.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Немає збережених збірок")
        }
    } else {
        LazyColumn {
            items(builds) { build ->
                SavedBuildItem(build) {
                    viewModel.loadBuild(build) // Завантажуємо в пам'ять
                    onBuildSelected() // Переходимо на екран конфігуратора
                }
            }
        }
    }
}

@Composable
fun SavedBuildItem(build: com.example.cursova.domain.SavedBuild, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = build.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ціна: $${build.totalPrice}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Дата: ${dateFormat.format(Date(build.date))}", style = MaterialTheme.typography.bodySmall)
        }
    }
}