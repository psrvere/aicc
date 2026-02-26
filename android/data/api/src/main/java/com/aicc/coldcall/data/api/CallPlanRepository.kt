package com.aicc.coldcall.data.api

import com.aicc.coldcall.core.model.CallPlanItem
import com.aicc.coldcall.core.network.AiccApiService
import com.aicc.coldcall.core.network.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallPlanRepository @Inject constructor(
    private val api: AiccApiService,
) {
    suspend fun getTodayPlan(): List<CallPlanItem> =
        api.getTodayCallPlan().map { it.toDomain() }
}
