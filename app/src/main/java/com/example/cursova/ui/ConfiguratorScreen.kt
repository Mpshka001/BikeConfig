package com.example.cursova.ui

import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cursova.domain.BikePart
import com.example.cursova.domain.PartType

// модель для координат деталей на визуализаторе
data class PartConfig(
    val x: Float,
    val y: Float,
    val size: Float,
    val rotation: Float = 0f
)

// константы цветов для ui
private val SpecializedRed = Color(0xFFD0021B)
private val SuccessGreen = Color(0xFF4CAF50)
private val PriceRed = Color(0xFFD32F2F)

@Composable
fun ConfiguratorScreen(
    selectedBrand: String = "Specialized",
    vm: ConfiguratorViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onReturnToMenu: () -> Unit
) {
    val allParts = vm.parts.collectAsState().value
    val selectedCategory = vm.selectedCategory.collectAsState().value
    val currentBuild = vm.currentBuild.collectAsState().value

    var showSummary by remember { mutableStateOf(false) }
    val selectedSizesMap = remember { mutableStateMapOf<PartType, String>() }

    val orderedCategories = remember {
        listOf(
            PartType.FRAME,
            PartType.REAR_SHOCK,
            PartType.FORK,
            PartType.WHEELS,
            PartType.BOTTOM_BRACKET,
            PartType.DRIVETRAIN,
            PartType.CASSETTE,
            PartType.CRANKS,
            PartType.CHAIN,
            PartType.COCKPIT,
            PartType.BRAKES,
            PartType.DROPPER,
            PartType.SADDLE
        )
    }

    val currentFork = currentBuild[PartType.FORK]
    val isOrangeFork = currentFork?.name?.contains("Orange", ignoreCase = true) == true
    val isZeb = currentFork?.name?.contains("Zeb", ignoreCase = true) == true

    val currentFrame = currentBuild[PartType.FRAME]
    val isSantaCruzFrame = currentFrame?.name?.contains("Santa Cruz", ignoreCase = true) == true
    val isNukeproofFrame = currentFrame?.name?.contains("Nukeproof", ignoreCase = true) == true
    val isNukeproofMega = currentFrame?.name?.contains("Mega", ignoreCase = true) == true
    val isNukeproofGiga = currentFrame?.name?.contains("Giga", ignoreCase = true) == true
    val isTransitionFrame = currentFrame?.name?.contains("Transition", ignoreCase = true) == true

    val fixedConfigs = remember(isOrangeFork, isZeb, isSantaCruzFrame, isNukeproofFrame, isNukeproofMega, isNukeproofGiga, isTransitionFrame) {
        mapOf(
            PartType.FRAME to if (isNukeproofMega) {
                PartConfig(-6f, -80f, 216f, 0f)
            } else if (isNukeproofGiga) {
                PartConfig(-12f, -78f, 218f, 0f)
            } else if (isTransitionFrame) {
                PartConfig(-13f, -81f, 182f, 1f)
            } else {
                PartConfig(-6f, -80f, 219f, 0f)
            },
            PartType.REAR_SHOCK to if (isSantaCruzFrame) {
                PartConfig(-4f, -53f, 46f, 325f)
            } else if (isNukeproofMega) {
                PartConfig(6f, -82f, 40f, 331f)
            } else if (isNukeproofGiga) {
                PartConfig(2f, -59f, 41f, 328f)
            } else if (isTransitionFrame) {
                PartConfig(-1f, -58f, 41f, 91f)
            } else {
                PartConfig(1f, -85f, 46f, 136f)
            },
            PartType.FORK to when {
                isZeb -> PartConfig(91f, -87f, 161f, 0f)
                isOrangeFork -> PartConfig(95f, -86f, 193f, 0f)
                else -> PartConfig(92f, -85f, 180f, 0f)
            },
            PartType.BOTTOM_BRACKET to PartConfig(-7f, -23f, 40f, 0f),
            PartType.CRANKS to PartConfig(-7f, -23f, 59f, 0f),
            PartType.CASSETTE to PartConfig(-101f, -31f, 50f, 0f),
            PartType.CHAIN to PartConfig(-49f, -20f, 156f, 0f),
            PartType.DRIVETRAIN to PartConfig(-96f, -13f, 44f, 0f),
            PartType.COCKPIT to PartConfig(76f, -127f, 63f, 0f),
            PartType.BRAKES to PartConfig(114f, -23f, 28f, -115f),
            PartType.DROPPER to if (isTransitionFrame) {
                PartConfig(-32f, -111f, 70f, -18f)
            } else {
                PartConfig(-39f, -115f, 73f, -20f)
            },
            PartType.SADDLE to if (isTransitionFrame) {
                PartConfig(-41f, -145f, 44f, -3f)
            } else {
                PartConfig(-50f, -150f, 44f, -3f)
            }
        )
    }

    if (showSummary) {
        SummaryScreen(
            currentBuild = currentBuild,
            configs = fixedConfigs,
            selectedSizes = selectedSizesMap,
            onBack = { showSummary = false },
            onOrderClick = onReturnToMenu
        )
    } else {
        ConfiguratorContent(
            selectedBrand = selectedBrand,
            vm = vm,
            allParts = allParts,
            selectedCategory = selectedCategory,
            currentBuild = currentBuild,
            orderedCategories = orderedCategories,
            fixedConfigs = fixedConfigs,
            selectedSizesMap = selectedSizesMap,
            onBackClick = onBackClick,
            onFinish = { showSummary = true }
        )
    }
}

@Composable
fun ConfiguratorContent(
    selectedBrand: String,
    vm: ConfiguratorViewModel,
    allParts: List<BikePart>,
    selectedCategory: PartType,
    currentBuild: Map<PartType, BikePart?>,
    orderedCategories: List<PartType>,
    fixedConfigs: Map<PartType, PartConfig>,
    selectedSizesMap: MutableMap<PartType, String>,
    onBackClick: () -> Unit,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val selectedPartInCurrentCategory = currentBuild[selectedCategory]

    // Состояние для плеера звука втулки
    var currentMediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            currentMediaPlayer?.release()
        }
    }

    // Фильтруем детали. Если это выбор рамы - показываем только рамы выбранного бренда.
    val partsToShow = remember(allParts, selectedCategory, selectedBrand) {
        allParts.filter { part ->
            if (part.type != selectedCategory) return@filter false
            if (part.name.contains("(Orange)", ignoreCase = true)) return@filter false

            // Фильтр для отображения только дефолтного черного мегатауэра в списке (остальные цвета доступны при выборе)
            if (part.name.contains("Megatower", ignoreCase = true) && !part.name.contains("Black", ignoreCase = true)) return@filter false
            // Фильтр для ZEB
            if (part.name.contains("ZEB", ignoreCase = true) && part.name.contains("Slab Grey", ignoreCase = true)) return@filter false
            if (part.name.contains("ZEB", ignoreCase = true) && part.name.contains("Red", ignoreCase = true)) return@filter false

            // Логика фильтрации рам по выбранному бренду
            if (selectedCategory == PartType.FRAME) {
                when (selectedBrand) {
                    "Santa Cruz" -> part.name.contains("Santa Cruz", ignoreCase = true)
                    "Nukeproof" -> part.name.contains("Nukeproof", ignoreCase = true)
                    "Transition" -> part.name.contains("Transition", ignoreCase = true)
                    else -> part.name.contains("S-Works", ignoreCase = true)
                }
            } else {
                true
            }
        }
    }

    val (displayedSizes, displayedColorText) = remember(selectedCategory, selectedBrand, currentBuild) {
        when (selectedCategory) {
            PartType.FRAME -> if (selectedBrand in listOf("Santa Cruz", "Nukeproof", "Transition")) {
                Pair(listOf("S", "M", "L", "XL", "XXL"), null)
            } else {
                Pair(listOf("S1", "S2", "S3", "S4", "S5", "S6"), "Satin")
            }
            PartType.FORK -> Pair(listOf("29\""), null)
            PartType.REAR_SHOCK -> {
                val frameName = currentBuild[PartType.FRAME]?.name ?: ""
                when {
                    frameName.contains("Hightower", ignoreCase = true) -> Pair(listOf("210x55"), null)
                    frameName.contains("Megatower", ignoreCase = true) -> Pair(listOf("230x60"), null)
                    frameName.contains("Nukeproof", ignoreCase = true) -> Pair(listOf("230x65"), null)
                    frameName.contains("Transition", ignoreCase = true) -> Pair(listOf("205x62.5"), null)
                    else -> Pair(listOf("210x52.5"), null)
                }
            }
            PartType.WHEELS -> Pair(listOf("29\""), null)
            PartType.CRANKS -> Pair(listOf("165 mm", "170 mm", "175 mm"), null)
            PartType.DROPPER -> Pair(listOf("150 mm", "175 mm", "200 mm"), null)
            PartType.SADDLE -> Pair(listOf("S", "M", "L"), null)
            else -> Pair(emptyList(), null)
        }
    }

    var selectedSize by remember(selectedCategory) {
        mutableStateOf(selectedSizesMap[selectedCategory] ?: displayedSizes.firstOrNull() ?: "")
    }

    LaunchedEffect(selectedCategory, selectedSize) {
        if (selectedSize.isNotEmpty()) {
            selectedSizesMap[selectedCategory] = selectedSize
        }
        if (selectedCategory == PartType.FRAME && selectedSizesMap[selectedCategory] == null) {
            selectedSize = displayedSizes.firstOrNull() ?: ""
            if (selectedSize.isNotEmpty()) {
                selectedSizesMap[selectedCategory] = selectedSize
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Box(
            modifier = Modifier.weight(0.45f).fillMaxWidth().background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxSize().offset(x = (-20).dp), contentAlignment = Alignment.Center) {
                BikeVisualizer(build = currentBuild, configs = fixedConfigs)
            }
        }

        Text(
            text = getCategoryTitle(selectedCategory),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )

        LazyColumn(modifier = Modifier.weight(0.2f).fillMaxWidth().padding(horizontal = 8.dp)) {
            items(partsToShow) { part ->
                val isSelected = selectedPartInCurrentCategory?.id == part.id ||
                        (part.name.contains("Fox 38") && selectedPartInCurrentCategory?.name?.contains("Fox 38") == true) ||
                        (part.name.contains("Megatower") && selectedPartInCurrentCategory?.name?.contains("Megatower") == true) ||
                        (part.name.contains("ZEB") && selectedPartInCurrentCategory?.name?.contains("ZEB") == true)
                val isBrandSelection = selectedCategory == PartType.BOTTOM_BRACKET

                PartItemCard(
                    part = part,
                    isSelected = isSelected,
                    isLarge = isBrandSelection,
                    showPrice = !isBrandSelection,
                    onClick = { vm.selectPart(part) }
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().weight(0.35f),
            shadowElevation = 0.dp,
            color = Color.Transparent,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    if (selectedPartInCurrentCategory != null) {
                        val part = selectedPartInCurrentCategory

                        // Чистая логика для имен (для заголовка детального вида)
                        val displayName = when {
                            part.name.contains("Megatower", ignoreCase = true) -> "Santa Cruz Megatower"
                            part.name.contains("ZEB", ignoreCase = true) -> "RockShox ZEB Ultimate"
                            part.name.contains("Fox 38", ignoreCase = true) -> "Fox 38 Factory Grip2"
                            else -> part.name.replace(Regex("\\s*\\(.*?\\)"), "").trim()
                        }

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // ЗВУК ВТУЛКИ (динамік)
                            if (part.audioUrl != null) {
                                IconButton(onClick = {
                                    val resId = context.resources.getIdentifier(part.audioUrl, "raw", context.packageName)
                                    if (resId != 0) {
                                        currentMediaPlayer?.release()
                                        val mp = MediaPlayer.create(context, resId)
                                        mp?.setOnCompletionListener {
                                            it.release()
                                            if (currentMediaPlayer == it) currentMediaPlayer = null
                                        }
                                        mp?.start()
                                        currentMediaPlayer = mp
                                    }
                                }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play hub sound", tint = Color.Black)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            if (selectedCategory != PartType.BOTTOM_BRACKET) {
                                Text("$${part.price}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PriceRed)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFEEEEEE))

                        if (selectedCategory == PartType.FORK && part.name.contains("Fox", ignoreCase = true)) {
                            Text("Колір:", fontSize = 14.sp, color = Color.Gray); Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ColorSelectionCircle(Color.Black, !part.name.contains("Orange", true)) { allParts.find { it.type == PartType.FORK && !it.name.contains("Orange") && it.name.contains("Fox") }?.let { vm.selectPart(it) } }
                                ColorSelectionCircle(Color(0xFFFF6600), part.name.contains("Orange", true)) { allParts.find { it.type == PartType.FORK && it.name.contains("Orange") }?.let { vm.selectPart(it) } }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        } else if (selectedCategory == PartType.FORK && part.name.contains("ZEB", ignoreCase = true)) {
                            Text("Колір:", fontSize = 14.sp, color = Color.Gray); Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ColorSelectionCircle(Color.Black, part.name.contains("Black", true)) { allParts.find { it.type == PartType.FORK && it.name.contains("ZEB", true) && it.name.contains("Black", true) }?.let { vm.selectPart(it) } }
                                ColorSelectionCircle(Color(0xFF546E7A), part.name.contains("Slab Grey", true)) { allParts.find { it.type == PartType.FORK && it.name.contains("ZEB", true) && it.name.contains("Slab", true) }?.let { vm.selectPart(it) } }
                                ColorSelectionCircle(Color.Red, part.name.contains("Red", true)) { allParts.find { it.type == PartType.FORK && it.name.contains("ZEB", true) && it.name.contains("Red", true) }?.let { vm.selectPart(it) } }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        } else if (selectedCategory == PartType.FRAME && part.name.contains("Megatower", ignoreCase = true)) {
                            Text("Колір:", fontSize = 14.sp, color = Color.Gray); Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ColorSelectionCircle(Color.Black, part.name.contains("Black", true)) { allParts.find { it.type == PartType.FRAME && it.name.contains("Megatower Black", true) }?.let { vm.selectPart(it) } }
                                ColorSelectionCircle(Color.Blue, part.name.contains("Blue", true)) { allParts.find { it.type == PartType.FRAME && it.name.contains("Megatower Blue", true) }?.let { vm.selectPart(it) } }
                                ColorSelectionCircle(Color.Gray, part.name.contains("Grey", true) && !part.name.contains("Light", true) && !part.name.contains("Slab", true)) { allParts.find { it.type == PartType.FRAME && it.name.contains("Megatower Grey", true) && !it.name.contains("Light", true) && !it.name.contains("Slab", true) }?.let { vm.selectPart(it) } }
                                ColorSelectionCircle(Color.LightGray, part.name.contains("Light Grey", true)) { allParts.find { it.type == PartType.FRAME && it.name.contains("Megatower Light Grey", true) }?.let { vm.selectPart(it) } }
                                ColorSelectionCircle(Color.Red, part.name.contains("Red", true)) { allParts.find { it.type == PartType.FRAME && it.name.contains("Megatower Red", true) }?.let { vm.selectPart(it) } }
                                ColorSelectionCircle(Color(0xFF546E7A), part.name.contains("Slab Grey", true)) { allParts.find { it.type == PartType.FRAME && it.name.contains("Megatower Slab Grey", true) }?.let { vm.selectPart(it) } }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        } else if (displayedColorText != null) {
                            Text(buildAnnotatedString { append("Колір: "); withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) { append(displayedColorText) } }, fontSize = 14.sp, color = Color.Gray); Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (displayedSizes.isNotEmpty()) {
                            Text("Розмір:", fontSize = 14.sp, color = Color.Gray); Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                displayedSizes.forEach { size ->
                                    val isSelected = size == selectedSize
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .height(44.dp)
                                            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) Color.Black else Color.LightGray, RoundedCornerShape(4.dp))
                                            .background(if (isSelected) Color.White else Color(0xFFF5F5F5))
                                            .clickable {
                                                selectedSize = size
                                                selectedSizesMap[selectedCategory] = size
                                            }
                                            .padding(horizontal = 12.dp)
                                    ) {
                                        Text(size, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Оберіть деталь вище", color = Color.Gray) }
                    }
                }

                if (selectedPartInCurrentCategory != null || selectedCategory == orderedCategories.first()) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                            val currentIndex = orderedCategories.indexOf(selectedCategory)
                            val isFirstStep = currentIndex == 0
                            val isLastStep = selectedCategory == orderedCategories.last()

                            OutlinedButton(
                                onClick = {
                                    if (isFirstStep) {
                                        onBackClick()
                                    } else {
                                        vm.clearSelection(selectedCategory)
                                        val prevIndex = (currentIndex - 1 + orderedCategories.size) % orderedCategories.size
                                        vm.selectCategory(orderedCategories[prevIndex])
                                    }
                                },
                                modifier = Modifier.height(56.dp).weight(0.3f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }

                            Button(
                                onClick = {
                                    if (isLastStep) {
                                        onFinish()
                                    } else {
                                        val nextIndex = (currentIndex + 1) % orderedCategories.size
                                        vm.selectCategory(orderedCategories[nextIndex])
                                    }
                                },
                                modifier = Modifier.height(56.dp).weight(1f),
                                enabled = selectedPartInCurrentCategory != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLastStep) SuccessGreen else SpecializedRed,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isLastStep) "ЗАВЕРШИТИ" else "ДОДАТИ ДО ЗБІРКИ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(if (isLastStep) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryTitle(type: PartType): String {
    return when (type) {
        PartType.FRAME -> "Оберіть раму"
        PartType.FORK -> "Оберіть вилку"
        PartType.REAR_SHOCK -> "Оберіть амортизатор"
        PartType.WHEELS -> "Оберіть колеса"
        PartType.BOTTOM_BRACKET -> "Оберіть бренд трансмісії"
        PartType.DRIVETRAIN -> "Оберіть перемикач"
        PartType.CASSETTE -> "Оберіть касету"
        PartType.CRANKS -> "Оберіть шатуни"
        PartType.CHAIN -> "Оберіть ланцюг"
        PartType.BRAKES -> "Оберіть гальма"
        PartType.COCKPIT -> "Оберіть кокпіт"
        PartType.DROPPER -> "Оберіть дроппер"
        PartType.SADDLE -> "Оберіть сідло"
    }
}

@Composable
fun ColorSelectionCircle(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(32.dp).clip(CircleShape).background(color).border(if (isSelected) 3.dp else 1.dp, if (isSelected) Color(0xFFEEEEEE) else Color.Transparent, CircleShape).clickable { onClick() }
    ) {
        if (isSelected) { Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = if (color == Color.White) Color.Black else Color.White, modifier = Modifier.size(16.dp).align(Alignment.Center)) }
    }
}

@Composable
fun PartItemCard(
    part: BikePart,
    isSelected: Boolean,
    isLarge: Boolean = false,
    showPrice: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp),
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else null,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        val padding = if (isLarge) 24.dp else 12.dp
        val imageSize = if (isLarge) 80.dp else 40.dp
        val bgSize = if (isLarge) 100.dp else 56.dp

        Row(modifier = Modifier.padding(padding), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = Color(0xFFF5F5F5), modifier = Modifier.size(bgSize)) {
                Box(contentAlignment = Alignment.Center) { PartImage(part.imageUrl, Modifier.size(imageSize)) }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                // Чистим имя для отображения в списке карточек, как и для детального вида
                val cleanedItemName = when {
                    part.name.contains("Megatower", ignoreCase = true) -> "Santa Cruz Megatower"
                    part.name.contains("ZEB", ignoreCase = true) -> "RockShox ZEB Ultimate"
                    part.name.contains("Fox 38", ignoreCase = true) -> "Fox 38 Factory Grip2"
                    else -> part.name.replace(Regex("\\s*\\(.*?\\)"), "").trim()
                }
                Text(cleanedItemName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                if (showPrice) {
                    Text("$${part.price}", color = Color.Gray, fontSize = 14.sp)
                }
            }
            if (isSelected) Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color(0xFF388E3C), modifier = Modifier.size(24.dp))
        }
    }
}

// визуализатор байка, рендерит слои изображений друг на друга
@Composable
fun BikeVisualizer(modifier: Modifier = Modifier, build: Map<PartType, BikePart?>, configs: Map<PartType, PartConfig>) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        build[PartType.WHEELS]?.let {
            PartImage(it.imageUrl, Modifier.size(151.dp).offset((-106).dp, (-30).dp))
            PartImage(it.imageUrl, Modifier.size(151.dp).offset(125.dp, (-21).dp))
        }

        RenderPart(PartType.CASSETTE, build, configs)
        RenderPart(PartType.DROPPER, build, configs)
        RenderPart(PartType.REAR_SHOCK, build, configs)
        RenderPart(PartType.FORK, build, configs)
        RenderPart(PartType.FRAME, build, configs)
        RenderPart(PartType.SADDLE, build, configs)
        RenderPart(PartType.CHAIN, build, configs)
        RenderPart(PartType.CRANKS, build, configs)
        RenderPart(PartType.DRIVETRAIN, build, configs)
        RenderPart(PartType.BRAKES, build, configs)
        RenderPart(PartType.COCKPIT, build, configs)
    }
}

@Composable
fun RenderPart(type: PartType, build: Map<PartType, BikePart?>, configs: Map<PartType, PartConfig>) {
    build[type]?.let { part ->
        val config = configs[type] ?: PartConfig(0f, 0f, 100f)
        PartImage(name = part.imageUrl, modifier = Modifier.offset(x = config.x.dp, y = config.y.dp).rotate(config.rotation).size(config.size.dp))
    }
}

@Composable
fun PartImage(name: String, modifier: Modifier) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
    if (resId != 0) { Image(painter = painterResource(id = resId), contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit) }
}