package com.example.cursova.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cursova.domain.BikePart
import com.example.cursova.domain.PartType
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SummaryScreen(
    currentBuild: Map<PartType, BikePart?>,
    configs: Map<PartType, PartConfig>,
    selectedSizes: Map<PartType, String>,
    onBack: () -> Unit,
    onOrderClick: () -> Unit
) {
    val context = LocalContext.current

    // считаем общую сумму сборки
    val totalPrice = currentBuild.values.sumOf { it?.price ?: 0.0 }
    val formattedPrice = NumberFormat.getCurrencyInstance(Locale.US).format(totalPrice)

    // функция-интент: открывает браузер с поиском детали в google shopping
    fun searchInStore(query: String) {
        val url = "https://www.google.com/search?tbm=shop&q=${Uri.encode(query)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        // заголовок с поздравлением
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ВІТАЄМО!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF4CAF50) // зеленый "успех"
            )
            Text(
                text = "Твій байк готовий до збірки",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        // карточка с визуализацией готового байка
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    brush = Brush.verticalGradient(listOf(Color(0xFFF5F5F5), Color.White)),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // тень под колесами
            Box(
                modifier = Modifier
                    .offset(y = 80.dp)
                    .size(width = 250.dp, height = 25.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.1f))
            )
            // сам рендер байка
            Box(modifier = Modifier.graphicsLayer(scaleX = 1.0f, scaleY = 1.0f).offset(x = (-10).dp)) {
                BikeVisualizer(build = currentBuild, configs = configs)
            }
        }

        // блок с итоговой ценой
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Загальна вартість:", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(
                text = formattedPrice,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFD32F2F) // красный ценник
            )
        }

        Text(
            text = "Комплектація:",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "(натисніть на деталь, щоб знайти ціну)",
            modifier = Modifier.padding(horizontal = 24.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )

        // список деталей с возможностью клика
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            currentBuild.forEach { (type, part) ->
                if (part != null) {
                    val sizeInfo = selectedSizes[type]?.let { "Розмір: $it" }

                    // убираем лишние слова из названия для чистоты
                    val displayName = part.name
                        .replace(" (Orange)", "")
                        .replace(" (Black)", "")
                        .replace(" Grip2", "")
                        .replace(" Factory", "")

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { searchInStore(displayName) } // клик запускает поиск
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // название категории (с большой буквы)
                            Text(
                                text = type.name.lowercase().replace("_", " ")
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )

                            // название детали
                            Text(
                                text = displayName,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(2f),
                                maxLines = 1
                            )

                            // цена детали
                            Text(
                                text = "$${part.price.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        if (sizeInfo != null) {
                            Text(
                                text = sizeInfo,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    Divider(color = Color(0xFFEEEEEE))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // кнопка возврата в меню
        Button(
            onClick = onOrderClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Головне меню", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // кнопка редактирования
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            Text("Редагувати збірку", color = Color.Gray)
        }
    }
}