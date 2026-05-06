package com.example.cursova

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.runner.RunWith
import com.example.cursova.domain.BikePart
import com.example.cursova.domain.PartType
import com.example.cursova.ui.ConfiguratorViewModel

@RunWith(AndroidJUnit4::class)
class ConfiguratorViewModelTest {

    private lateinit var viewModel: ConfiguratorViewModel

    @Before
    fun setUp() {
        // Отримуємо справжній контекст додатка для AndroidViewModel та Room
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = ConfiguratorViewModel(application)
    }

    @Test
    fun selecting_parts_updates_total_price_correctly() {
        val testFrame = BikePart(id = "1", name = "Megatower", type = PartType.FRAME, price = 3600.0, imageUrl = "")
        val testFork = BikePart(id = "2", name = "ZEB Ultimate", type = PartType.FORK, price = 1100.0, imageUrl = "")

        viewModel.selectPart(testFrame)
        viewModel.selectPart(testFork)

        val expectedPrice = 4700.0
        val actualPrice = viewModel.currentBuild.value.values.filterNotNull().sumOf { it.price }
        assertEquals(expectedPrice, actualPrice, 0.0)
    }

    @Test
    fun clearSelection_removes_part_from_current_build() {
        val testBrakes = BikePart(id = "3", name = "SRAM Code", type = PartType.BRAKES, price = 250.0, imageUrl = "")
        viewModel.selectPart(testBrakes)

        viewModel.clearSelection(PartType.BRAKES)

        val currentBrake = viewModel.currentBuild.value[PartType.BRAKES]
        assertNull(currentBrake)
    }
}