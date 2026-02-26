package com.aicc.coldcall.data.api

import com.aicc.coldcall.core.database.CallLogDao
import com.aicc.coldcall.core.model.DealStage
import com.aicc.coldcall.core.model.Disposition
import com.aicc.coldcall.core.network.AiccApiService
import com.aicc.coldcall.core.network.dto.CallLogCreateDto
import com.aicc.coldcall.core.network.dto.CallLogDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CallLogRepositoryTest {

    private val api = mockk<AiccApiService>()
    private val callLogDao = mockk<CallLogDao>(relaxUnitFun = true)
    private lateinit var repository: CallLogRepository

    @Before
    fun setUp() {
        repository = CallLogRepository(api, callLogDao)
    }

    @Test
    fun `logCall posts to API and caches locally`() = runTest {
        val createDto = CallLogCreateDto(
            contactId = "1",
            durationSeconds = 120,
            disposition = "Connected",
            summary = "Good call",
            dealStage = "Qualified",
        )
        val responseDto = CallLogDto(
            id = "log-1",
            contactId = "1",
            timestamp = "2025-01-15T10:00:00Z",
            durationSeconds = 120,
            disposition = "Connected",
            summary = "Good call",
            dealStage = "Qualified",
        )
        coEvery { api.logCall(createDto) } returns responseDto

        val callLog = repository.logCall(createDto)
        assertEquals("log-1", callLog.id)
        assertEquals("1", callLog.contactId)
        assertEquals(120, callLog.durationSeconds)
        assertEquals(Disposition.Connected, callLog.disposition)
        assertEquals(DealStage.Qualified, callLog.dealStage)

        coVerify { callLogDao.insert(any()) }
    }

    @Test
    fun `logCall maps all fields correctly`() = runTest {
        val createDto = CallLogCreateDto(
            contactId = "2",
            durationSeconds = 60,
            disposition = "NoAnswer",
        )
        val responseDto = CallLogDto(
            id = "log-2",
            contactId = "2",
            timestamp = "2025-01-15T11:00:00Z",
            durationSeconds = 60,
            disposition = "NoAnswer",
            dealStage = "New",
        )
        coEvery { api.logCall(createDto) } returns responseDto

        val callLog = repository.logCall(createDto)
        assertEquals(Disposition.NoAnswer, callLog.disposition)
        assertEquals(DealStage.New, callLog.dealStage)
    }
}
