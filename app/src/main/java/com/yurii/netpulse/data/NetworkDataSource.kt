package com.yurii.netpulse.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.net.Inet4Address

class NetworkDataSource(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private class NetworkCallback(private val onUpdate: () -> Unit) : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            onUpdate()
        }
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            onUpdate()
        }
        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            onUpdate()
        }
        override fun onLost(network: Network) {
            onUpdate()
        }
    }

    val networkStateFlow: Flow<NetworkState> = callbackFlow {
        trySend(getCurrentNetworkState())
        val networkCallback = NetworkCallback { trySend(getCurrentNetworkState()) }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }.distinctUntilChanged()

    fun getCurrentNetworkState(): NetworkState {

        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        val isConnected: Boolean = networkCapabilities != null

        val networkHandle: Long? = activeNetwork?.networkHandle

        val transport: TransportTypes = if (networkCapabilities != null) {
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                    TransportTypes.TRANSPORT_CELLULAR
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                    TransportTypes.TRANSPORT_WIFI
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ->
                    TransportTypes.TRANSPORT_BLUETOOTH
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->
                    TransportTypes.TRANSPORT_ETHERNET
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ->
                    TransportTypes.TRANSPORT_VPN
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) ->
                    TransportTypes.TRANSPORT_WIFI_AWARE
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) ->
                    TransportTypes.TRANSPORT_LOWPAN
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_USB) ->
                    TransportTypes.TRANSPORT_USB
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_THREAD) ->
                    TransportTypes.TRANSPORT_THREAD
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_SATELLITE) ->
                    TransportTypes.TRANSPORT_SATELLITE
                else ->
                    TransportTypes.UNKNOWN
            }
        } else {
            TransportTypes.NONE
        }

        val isValidated: Boolean = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false

        val isMetered: Boolean? = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)?.not()

        val signalStrengthDbm: Int = networkCapabilities?.signalStrength ?: NetworkCapabilities.SIGNAL_STRENGTH_UNSPECIFIED

        val ipAddress: String? = run {
            val linkProperties = connectivityManager.getLinkProperties(activeNetwork)
            if (linkProperties != null) {
                val defaultRoute = linkProperties.routes.find { it.isDefaultRoute }
                if (defaultRoute != null) {
                    for (linkAddress in linkProperties.linkAddresses) {
                        val address = linkAddress.address
                        if (address is Inet4Address && !address.isLoopbackAddress && defaultRoute.matches(address)) {
                            return@run address.hostAddress
                        }
                    }
                }
                for (linkAddress in linkProperties.linkAddresses) {
                    val address = linkAddress.address
                    if (address is Inet4Address && !address.isLoopbackAddress) {
                        return@run address.hostAddress
                    }
                }
            }
            null
        }

        return NetworkState(isConnected, networkHandle, transport, isValidated, isMetered, signalStrengthDbm, ipAddress)

    }

}