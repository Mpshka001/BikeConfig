package com.example.cursova.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_builds")
data class SavedBuild(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val partsIds: String,
    val totalPrice: Double,
    val date: Long = System.currentTimeMillis()
)