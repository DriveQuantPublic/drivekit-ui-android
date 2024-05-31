package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.common.ui.extension.format
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconRetrievedInfo

class BeaconDetailViewModel(
    val vehicleId: String,
    val vehicleName: String,
    val beaconRetrievedInfo: DKBeaconRetrievedInfo,
    val seenBeacon: BeaconInfo
) : ViewModel() {

    var data: MutableList<BeaconDetailField> = mutableListOf()

    fun buildListData(context: Context) {
        DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()?.let { vehicle ->
            vehicle.beacon?.let { beacon ->
                if (beacon.proximityUuid.equals(seenBeacon.proximityUuid, true)
                    && beacon.major == seenBeacon.major && beacon.minor == seenBeacon.minor)
                {
                    data.add(
                        BeaconDetailField(
                            getTitle(context, R.string.dk_beacon_vehicule_linked),
                            DKSpannable().append(vehicleName).toSpannable()
                        )
                    )
                }
                data.add(
                    BeaconDetailField(
                        getTitle(context, R.string.dk_beacon_uuid),
                        DKSpannable().append(beacon.proximityUuid.substring(0, 8) + "…").toSpannable()
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, R.string.dk_beacon_major),
                        DKSpannable().append(seenBeacon.major.toString()).toSpannable()
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, R.string.dk_beacon_minor),
                        DKSpannable().append(seenBeacon.minor.toString()).toSpannable()
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, R.string.dk_beacon_battery),
                        buildValue(context, beaconRetrievedInfo.batteryLevel.toString(), "%")
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, R.string.dk_beacon_distance),
                        buildValue(context, beaconRetrievedInfo.estimatedDistance.format(1), context.getString(com.drivequant.drivekit.common.ui.R.string.dk_common_unit_meter))
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, R.string.dk_beacon_rssi),
                        buildValue(context, beaconRetrievedInfo.rssi.toString(), "dBm"))
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, R.string.dk_beacon_tx),
                        buildValue(context, beaconRetrievedInfo.txPower.toString(), "dBm"))
                )
            }
        }
    }

    private fun getTitle(context: Context, @StringRes identifier: Int): String {
        return context.getString(identifier)
    }

    private fun buildValue(context: Context, value: String, unit: String) : Spannable {
        val mainFontColor = DKColors.mainFontColor
        val primaryColor = DKColors.primaryColor

        return DKSpannable()
            .append(value, context.resSpans {
                color(primaryColor)
                typeface(Typeface.BOLD)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium)
            })
            .append(" ")
            .append(unit, context.resSpans {
                color(mainFontColor)
                typeface(Typeface.NORMAL)
                size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_small)
            }).toSpannable()
    }

    @Suppress("UNCHECKED_CAST")
    class BeaconDetailViewModelFactory(
        private val vehicleId: String,
        private val vehicleName: String,
        private val beaconRetrievedInfo: DKBeaconRetrievedInfo,
        private val seenBeacon: BeaconInfo
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BeaconDetailViewModel(vehicleId, vehicleName, beaconRetrievedInfo,  seenBeacon) as T
        }
    }
}

data class BeaconDetailField(
    val title: String,
    val value: Spannable)
