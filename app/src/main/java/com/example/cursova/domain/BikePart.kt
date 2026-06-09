package com.example.cursova.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PartType {
    FRAME,
    REAR_SHOCK,
    FORK,
    WHEELS,
    BOTTOM_BRACKET,
    CRANKS,
    CASSETTE,
    CHAIN,
    DRIVETRAIN,
    BRAKES,
    COCKPIT,
    DROPPER,
    SADDLE
}

// сутність таблиці бази данних
@Entity
data class BikePart(
    // первинни ключ (uuid)
    @PrimaryKey val id: String,
    val name: String,
    val type: PartType,
    val price: Double,
    val imageUrl: String,

    // поля сумісності dub/hollowtech
    val standard: String? = null,

    //  звук втулки
    val audioUrl: String? = null
)