package com.victoryvalery.tfsproject.domain.services

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityObserver {

    fun observe(): Flow<NetworkConnectivityStatus>

    enum class NetworkConnectivityStatus {
        Available, Unavailable, Init
    }
}
