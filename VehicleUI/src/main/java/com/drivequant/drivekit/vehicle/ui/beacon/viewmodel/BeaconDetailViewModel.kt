package com.drivequant.drivekit.vehicle.ui.beacon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.vehicle.DriveKitVehicle
import com.drivequant.drivekit.vehicle.ui.R

class BeaconDetailViewModel(
    val vehicleId: String,
    val vehicleName: String,
    val batteryLevel: Int,
    val seenBeacon: BeaconInfo
) : ViewModel() {

    var data: MutableList<BeaconDetailField> = mutableListOf()

    fun buildListData(context: Context){
        DriveKitVehicle.vehiclesQuery().whereEqualTo("vehicleId", vehicleId).queryOne().executeOne()?.let { vehicle ->
            vehicle.beacon?.let { beacon ->
                if (beacon.proximityUuid.equals(seenBeacon.proximityUuid, true)
                    && beacon.major == seenBeacon.major && beacon.minor == seenBeacon.minor)
                {
                    data.add(
                        BeaconDetailField(
                            getTitle(context, "dk_beacon_vehicule_linked"),
                            DKSpannable().append(vehicleName).toSpannable()
                        )
                    )
                }
                data.add(
                    BeaconDetailField(
                        getTitle(context, "dk_beacon_uuid"),
                        DKSpannable().append(beacon.proximityUuid.substring(0, 8) + "…").toSpannable()
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, "dk_beacon_major"),
                        DKSpannable().append(seenBeacon.major.toString()).toSpannable()
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, "dk_beacon_minor"),
                        DKSpannable().append(seenBeacon.minor.toString()).toSpannable()
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, "dk_beacon_battery"),
                        buildValue(context, batteryLevel.toString(), "%")
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, "dk_beacon_distance"),
                        buildValue(context, seenBeacon.accuracy.toString(), "dk_common_unit_meter")
                    )
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, "dk_beacon_rssi"),
                        buildValue(context, seenBeacon.rssi.toString(), "dBm"))
                )
                data.add(
                    BeaconDetailField(
                        getTitle(context, "dk_beacon_tx"),
                        buildValue(context, seenBeacon.txPower.toString(), "dBm"))
                )
            }
        }
    }

    private fun getTitle(context: Context, identifier: String): String {
        return DKResource.convertToString(context, identifier)
    }

    private fun buildValue(context: Context, value: String, unitIdentifier: String) : Spannable {
        val mainFontColor = DriveKitUI.colors.mainFontColor()
        val primaryColor = DriveKitUI.colors.primaryColor()
        val unit = DKResource.convertToString(context, unitIdentifier)

        return DKSpannable()
            .append(value, context.resSpans {
                color(primaryColor)
                typeface(Typeface.BOLD)
                size(R.dimen.dk_text_medium)
            })
            .append(" ")
            .append(unit, context.resSpans {
                color(mainFontColor)
                typeface(Typeface.NORMAL)
                size(R.dimen.dk_text_small)
            }).toSpannable()
    }

    @Suppress("UNCHECKED_CAST")
    class BeaconDetailViewModelFactory(
        private val vehicleId: String,
        private val vehicleName: String,
        private val batteryLevel: Int,
        private val seenBeacon: BeaconInfo
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BeaconDetailViewModel(vehicleId, vehicleName, batteryLevel, seenBeacon) as T
        }
    }
}

data class BeaconDetailField(
    val title: String,
    val value: Spannable)