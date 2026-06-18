package com.example.cursova.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursova.data.AppDatabase
import com.example.cursova.domain.BikePart
import com.example.cursova.domain.PartType
import com.example.cursova.domain.SavedBuild
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfiguratorViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application, viewModelScope).bikePartDao()

    private val _allParts = dao.getAllParts()

    val savedBuilds = dao.getAllSavedBuilds().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val dependentParts = setOf(
        PartType.CRANKS,
        PartType.CASSETTE,
        PartType.CHAIN,
        PartType.DRIVETRAIN
    )

    private val _selectedCategory = MutableStateFlow(PartType.FRAME)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _currentBuild = MutableStateFlow<Map<PartType, BikePart?>>(emptyMap())
    val currentBuild = _currentBuild.asStateFlow()

    // Стейт для режима изменить только цвет или размер
    private val _isColorOnlyMode = MutableStateFlow(false)
    val isColorOnlyMode = _isColorOnlyMode.asStateFlow()

    val parts = combine(_allParts, _currentBuild, _selectedCategory) { all: List<BikePart>, build: Map<PartType, BikePart?>, category: PartType ->
        filterParts(all, build, category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPart(part: BikePart) {
        val newBuild = _currentBuild.value.toMutableMap()
        newBuild[part.type] = part
        _currentBuild.value = newBuild
    }

    fun clearSelection(type: PartType) {
        val newBuild = _currentBuild.value.toMutableMap()
        newBuild[type] = null
        _currentBuild.value = newBuild
    }

    fun selectCategory(category: PartType) {
        _selectedCategory.value = category
    }

    // Вкл/выкл режима редактирования цвета
    fun setColorOnlyMode(enabled: Boolean) {
        _isColorOnlyMode.value = enabled
    }

    fun loadBuild(savedBuild: SavedBuild) {
        viewModelScope.launch {
            val ids = savedBuild.partsIds.split(",").filter { it.isNotBlank() }
            val parts = dao.getPartsByIds(ids)
            val newBuild = parts.associateBy { it.type }
            _currentBuild.value = newBuild
        }
    }

    fun saveCurrentBuild(name: String) {
        viewModelScope.launch {
            val parts = _currentBuild.value.values.filterNotNull()
            if (parts.isEmpty()) return@launch

            val ids = parts.joinToString(",") { it.id }
            val totalPrice = parts.sumOf { it.price }

            val newBuild = SavedBuild(
                name = name,
                partsIds = ids,
                totalPrice = totalPrice
            )
            dao.insertSavedBuild(newBuild)
        }
    }

    private fun filterParts(
        all: List<BikePart>,
        build: Map<PartType, BikePart?>,
        category: PartType
    ): List<BikePart> {
        val categoryParts = all.filter { it.type == category }

        if (category !in dependentParts) {
            return categoryParts
        }

        val installedBB = build[PartType.BOTTOM_BRACKET]

        if (installedBB != null && installedBB.standard != null) {
            return categoryParts.filter { part ->
                part.standard == null || part.standard == installedBB.standard
            }
        }

        return categoryParts
    }
}