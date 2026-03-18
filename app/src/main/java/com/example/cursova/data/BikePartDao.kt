package com.example.cursova.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cursova.domain.BikePart
import com.example.cursova.domain.SavedBuild
import kotlinx.coroutines.flow.Flow

@Dao
interface BikePartDao {

    // --- РОБОТА З ДЕТАЛЯМИ (КАТАЛОГ) ---

    // Отримати всі деталі (Flow для автооновлення UI)
    @Query("SELECT * FROM BikePart")
    fun getAllParts(): Flow<List<BikePart>>

    // Вставка однієї деталі (твоя стара функція)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(part: BikePart)

    // Вставка СПИСКУ деталей (потрібно для AppDatabase при першому старті)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllParts(parts: List<BikePart>)

    // Перевірка кількості деталей (щоб знати, чи треба заповнювати базу)
    @Query("SELECT COUNT(*) FROM BikePart")
    suspend fun getPartsCount(): Int

    // Очистка таблиці (залишив про всяк випадок)
    @Query("DELETE FROM BikePart")
    suspend fun deleteAll()

    // --- РОБОТА ЗІ ЗБЕРЕЖЕНИМИ ЗБІРКАМИ (ГАРАЖ) ---

    // 1. Зберегти нову збірку
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedBuild(build: SavedBuild)

    // 2. Отримати історію збірок (нові зверху)
    @Query("SELECT * FROM saved_builds ORDER BY date DESC")
    fun getAllSavedBuilds(): Flow<List<SavedBuild>>

    // 3. Відновити збірку: дістає деталі за списком ID
    @Query("SELECT * FROM BikePart WHERE id IN (:ids)")
    suspend fun getPartsByIds(ids: List<String>): List<BikePart>
}