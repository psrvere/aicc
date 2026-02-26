package com.aicc.coldcall.feature.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aicc.coldcall.core.network.BaseUrlProvider
import com.aicc.coldcall.core.network.TokenProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object SettingsProviderModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsBindingsModule {

    @Binds
    @Singleton
    abstract fun bindTokenProvider(store: ServerConfigStore): TokenProvider

    @Binds
    @Singleton
    abstract fun bindBaseUrlProvider(store: ServerConfigStore): BaseUrlProvider
}
