package com.aicc.coldcall.data.api

import com.aicc.coldcall.core.database.CallLogDao
import com.aicc.coldcall.core.database.toEntity
import com.aicc.coldcall.core.model.CallLog
import com.aicc.coldcall.core.network.AiccApiService
import com.aicc.coldcall.core.network.dto.CallLogCreateDto
import com.aicc.coldcall.core.network.dto.toDomain
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallLogRepository @Inject constructor(
    private val api: AiccApiService,
    private val callLogDao: CallLogDao,
) {
    suspend fun logCall(dto: CallLogCreateDto): CallLog {
        val callLog = api.logCall(dto).toDomain()
        try {
            callLogDao.insert(callLog.toEntity())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cache call log locally", e)
        }
        return callLog
    }

    companion object {
        private const val TAG = "CallLogRepository"
    }
}
