package com.aicc.coldcall.data.api

import com.aicc.coldcall.core.database.CallLogDao
import com.aicc.coldcall.core.database.ContactDao
import com.aicc.coldcall.core.network.AiccApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideContactRepository(
        api: AiccApiService,
        contactDao: ContactDao,
    ): ContactRepository = ContactRepository(api, contactDao)

    @Provides
    @Singleton
    fun provideCallPlanRepository(api: AiccApiService): CallPlanRepository =
        CallPlanRepository(api)

    @Provides
    @Singleton
    fun provideCallLogRepository(
        api: AiccApiService,
        callLogDao: CallLogDao,
    ): CallLogRepository = CallLogRepository(api, callLogDao)

    @Provides
    @Singleton
    fun provideDashboardRepository(api: AiccApiService): DashboardRepository =
        DashboardRepository(api)
}
