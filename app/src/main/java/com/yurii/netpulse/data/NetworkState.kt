package com.yurii.netpulse.data

data class NetworkState(
    val isConnected: Boolean,
    val networkHandle: Long?,
    val transport: TransportTypes,
    val isValidated: Boolean,
    val isMetered: Boolean?,
    val signalStrengthDbm: Int,
    val ipAddress: String?
)

enum class TransportTypes {
    TRANSPORT_CELLULAR,
    TRANSPORT_WIFI,
    TRANSPORT_BLUETOOTH,
    TRANSPORT_ETHERNET,
    TRANSPORT_VPN,
    TRANSPORT_WIFI_AWARE,
    TRANSPORT_LOWPAN,
    TRANSPORT_TEST,
    TRANSPORT_USB,
    TRANSPORT_THREAD,
    TRANSPORT_SATELLITE,
    UNKNOWN,
    NONE
}