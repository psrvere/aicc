package com.aicc.coldcall.data.api

import com.aicc.coldcall.core.model.DashboardStats
import com.aicc.coldcall.core.network.AiccApiService
import com.aicc.coldcall.core.network.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val api: AiccApiService,
) {
    suspend fun getStats(): DashboardStats =
        api.getDashboardStats().toDomain()
}
