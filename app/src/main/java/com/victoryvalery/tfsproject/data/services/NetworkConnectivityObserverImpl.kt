package com.victoryvalery.tfsproject.data.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.victoryvalery.tfsproject.domain.services.NetworkConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class NetworkConnectivityObserverImpl @Inject constructor(
    appContext: Context
) : NetworkConnectivityObserver {

    private val connectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<NetworkConnectivityObserver.NetworkConnectivityStatus> {

        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(NetworkConnectivityObserver.NetworkConnectivityStatus.Available) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(NetworkConnectivityObserver.NetworkConnectivityStatus.Unavailable) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(NetworkConnectivityObserver.NetworkConnectivityStatus.Unavailable) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(NetworkConnectivityObserver.NetworkConnectivityStatus.Unavailable) }
                }

            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}

