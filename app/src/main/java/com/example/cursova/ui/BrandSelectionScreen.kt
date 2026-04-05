package com.example.cursova.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BrandSelectionScreen(
    onBrandSelected: (String) -> Unit // коллбек навигации с передачей бренда
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // центрируем контент
    ) {
        // заголовок экрана
        Text(
            text = "ОБЕРІТЬ БРЕНД",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Бренд 1: Specialized
        BrandItem(
            logoName = "logo_specialized",
            brandName = "SPECIALIZED",
            onClick = { onBrandSelected("Specialized") }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Бренд 2: Santa Cruz
        BrandItem(
            logoName = "logo_santacruz", // Если логотипа нет, будет просто текст
            brandName = "SANTA CRUZ",
            onClick = { onBrandSelected("Santa Cruz") }
        )
    }
}

@Composable
fun BrandItem(logoName: String, brandName: String, onClick: () -> Unit) {
    val context = LocalContext.current
    // динамически ищем ресурс по имени строки
    val resId = context.resources.getIdentifier(logoName, "drawable", context.packageName)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() }, // кликабельна вся область
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = brandName,
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
        } else {
            // фоллбек, если картинки нет (для отладки или если нет лого)
            Text(brandName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}
