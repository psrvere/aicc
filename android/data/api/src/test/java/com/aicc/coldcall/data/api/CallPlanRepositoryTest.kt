package com.aicc.coldcall.data.api

import com.aicc.coldcall.core.model.DealStage
import com.aicc.coldcall.core.network.AiccApiService
import com.aicc.coldcall.core.network.dto.CallPlanItemDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CallPlanRepositoryTest {

    private val api = mockk<AiccApiService>()
    private lateinit var repository: CallPlanRepository

    @Before
    fun setUp() {
        repository = CallPlanRepository(api)
    }

    @Test
    fun `getTodayPlan fetches from API and maps to domain`() = runTest {
        val dto = CallPlanItemDto(
            id = "1",
            name = "Alice",
            phone = "555-0001",
            business = "Acme",
            dealStage = "Qualified",
            callCount = 3,
            reason = "Follow-up due",
        )
        coEvery { api.getTodayCallPlan() } returns listOf(dto)

        val plan = repository.getTodayPlan()
        assertEquals(1, plan.size)
        assertEquals("Alice", plan[0].name)
        assertEquals(DealStage.Qualified, plan[0].dealStage)
        assertEquals("Follow-up due", plan[0].reason)
    }

    @Test
    fun `getTodayPlan returns empty list when API returns empty`() = runTest {
        coEvery { api.getTodayCallPlan() } returns emptyList()

        val plan = repository.getTodayPlan()
        assertEquals(0, plan.size)
    }
}
