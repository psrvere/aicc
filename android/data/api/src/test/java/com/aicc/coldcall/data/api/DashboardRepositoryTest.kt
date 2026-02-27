package com.aicc.coldcall.data.api

import com.aicc.coldcall.core.model.DealStage
import com.aicc.coldcall.core.network.AiccApiService
import com.aicc.coldcall.core.network.dto.DashboardStatsDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DashboardRepositoryTest {

    private val api = mockk<AiccApiService>()
    private lateinit var repository: DashboardRepository

    @Before
    fun setUp() {
        repository = DashboardRepository(api)
    }

    @Test
    fun `getStats fetches from API and maps to domain`() = runTest {
        val dto = DashboardStatsDto(
            callsToday = 10,
            connectedToday = 5,
            conversionRate = 0.25,
            streak = 3,
            pipeline = mapOf("Qualified" to 4, "Proposal" to 2),
        )
        coEvery { api.getDashboardStats() } returns dto

        val stats = repository.getStats()
        assertEquals(10, stats.callsToday)
        assertEquals(5, stats.connectedToday)
        assertEquals(0.25, stats.conversionRate, 0.001)
        assertEquals(3, stats.streak)
        assertEquals(4, stats.pipeline[DealStage.Qualified])
        assertEquals(2, stats.pipeline[DealStage.Proposal])
    }

    @Test
    fun `getStats handles empty pipeline`() = runTest {
        val dto = DashboardStatsDto(
            callsToday = 0,
            connectedToday = 0,
            conversionRate = 0.0,
            streak = 0,
            pipeline = emptyMap(),
        )
        coEvery { api.getDashboardStats() } returns dto

        val stats = repository.getStats()
        assertEquals(0, stats.callsToday)
        assertEquals(0, stats.pipeline.size)
    }
}
