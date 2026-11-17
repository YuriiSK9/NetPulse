package com.yurii.netpulse.ui

import android.net.NetworkCapabilities
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.yurii.netpulse.R
import com.yurii.netpulse.data.NetworkState
import com.yurii.netpulse.data.TransportTypes

@Composable
fun NetPulseScreen(networkState: NetworkState, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val appIcon = remember {
        context.packageManager.getApplicationIcon(context.packageName).toBitmap(256, 256).asImageBitmap()
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).fillMaxWidth().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Image(
                bitmap = appIcon,
                contentDescription = stringResource(R.string.netpulse_icon_content_description),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        }
        if (networkState.isConnected) {
            if (networkState.transport == TransportTypes.TRANSPORT_WIFI) {
                when {
                    !networkState.isValidated || networkState.signalStrengthDbm < -88 ->
                        NetworkImage(R.drawable.ic_wifi_signal_0, stringResource(R.string.ic_wifi_signal_0_content_description))
                    networkState.signalStrengthDbm < -77 ->
                        NetworkImage(R.drawable.ic_wifi_signal_1, stringResource(R.string.ic_wifi_signal_1_content_description))
                    networkState.signalStrengthDbm < -66 ->
                        NetworkImage(R.drawable.ic_wifi_signal_2, stringResource(R.string.ic_wifi_signal_2_content_description))
                    networkState.signalStrengthDbm < -55 ->
                        NetworkImage(R.drawable.ic_wifi_signal_3, stringResource(R.string.ic_wifi_signal_3_content_description))
                    else ->
                        NetworkImage(R.drawable.ic_wifi_signal_4, stringResource(R.string.ic_wifi_signal_4_content_description))
                }
            } else if (networkState.isValidated) {
                NetworkImage(R.drawable.ic_connected_true, stringResource(R.string.ic_connected_true_content_description))
            } else {
                NetworkImage(R.drawable.ic_connected_false, stringResource(R.string.ic_validated_false_content_description))
            }
            NetworkInfoText(stringResource(R.string.device_connected))
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onBackground),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                SelectionContainer {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        NetworkInfoText(stringResource(R.string.network_handle_label), networkState.networkHandle?.toString() ?: stringResource(R.string.network_handle_none))
                        NetworkInfoText(stringResource(R.string.transport_label), when (networkState.transport) {
                            TransportTypes.TRANSPORT_WIFI -> stringResource(R.string.transport_wifi)
                            TransportTypes.TRANSPORT_CELLULAR -> stringResource(R.string.transport_cellular)
                            TransportTypes.TRANSPORT_BLUETOOTH -> stringResource(R.string.transport_bluetooth)
                            TransportTypes.TRANSPORT_ETHERNET -> stringResource(R.string.transport_ethernet)
                            TransportTypes.TRANSPORT_VPN -> stringResource(R.string.transport_vpn)
                            TransportTypes.TRANSPORT_WIFI_AWARE -> stringResource(R.string.transport_wifi_aware)
                            TransportTypes.TRANSPORT_LOWPAN -> stringResource(R.string.transport_lowpan)
                            TransportTypes.TRANSPORT_TEST -> stringResource(R.string.transport_test)
                            TransportTypes.TRANSPORT_USB -> stringResource(R.string.transport_usb)
                            TransportTypes.TRANSPORT_THREAD -> stringResource(R.string.transport_thread)
                            TransportTypes.TRANSPORT_SATELLITE -> stringResource(R.string.transport_satellite)
                            TransportTypes.UNKNOWN -> stringResource(R.string.transport_unknown)
                            TransportTypes.NONE -> stringResource(R.string.transport_none)
                        })
                        NetworkInfoText(stringResource(R.string.metered_label), networkState.isMetered.toString().replaceFirstChar { it.uppercase() })
                        NetworkInfoText(stringResource(R.string.validated_label), networkState.isValidated.toString().replaceFirstChar { it.uppercase() })
                        if (networkState.signalStrengthDbm != NetworkCapabilities.SIGNAL_STRENGTH_UNSPECIFIED) {
                            NetworkInfoText(stringResource(R.string.signal_strength_label), stringResource(R.string.signal_strength_dbm, networkState.signalStrengthDbm))
                        }
                        NetworkInfoText(stringResource(R.string.ip_address_label), networkState.ipAddress ?: stringResource(R.string.ip_address_none))
                    }
                }
            }
        } else {
            NetworkImage(R.drawable.ic_connected_false, stringResource(R.string.ic_connected_false_content_description))
            NetworkInfoText(stringResource(R.string.device_not_connected))
        }
    }

}

@Composable
private fun NetworkInfoText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
    )
}
@Composable
private fun NetworkInfoText(boldText: String, normalText: String) {
    Row {
        Text(
            text = boldText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = normalText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
@Composable
private fun NetworkImage(@DrawableRes resourceId: Int, contentDescription: String) {
    BoxWithConstraints (
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = contentDescription,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.width(minOf(maxWidth, 512.dp))
        )
    }
}
