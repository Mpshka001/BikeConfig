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
    onBrandSelected: () -> Unit // коллбек навигации
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

        // единственный доступный бренд
        BrandItem(
            logoName = "logo_specialized",
            brandName = "SPECIALIZED",
            onClick = onBrandSelected
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
            // фоллбек, если картинки нет (для отладки)
            Text(brandName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}