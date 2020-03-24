package com.drivequant.drivekit.vehicle.ui.beacon.fragment.children

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.beaconutils.BeaconBatteryReaderListener
import com.drivequant.beaconutils.BeaconBatteryReaderScanner
import com.drivequant.beaconutils.BeaconData
import com.drivequant.beaconutils.compatibility.BeaconScannerBatteryReaderPreLollipop
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconViewModel
import com.drivequant.drivekit.vehicle.ui.extension.computeSubtitle
import kotlinx.android.synthetic.main.fragment_beacon_child_scanner_info.*

class BeaconScannerInfoFragment : Fragment(), BeaconBatteryReaderListener {
    companion object {
        fun newInstance(viewModel: BeaconViewModel, isValid: Boolean): BeaconScannerInfoFragment {
            val fragment = BeaconScannerInfoFragment()
            fragment.viewModel = viewModel
            fragment.isValid = isValid
            return fragment
        }
    }

    private lateinit var viewModel: BeaconViewModel
    private var isValid: Boolean = false
    private var batteryLevel = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_beacon_child_scanner_info, container, false).setDKStyle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //startBatteryReaderScanner()

        view_border.setBackgroundColor(DKColors().mainFontColor())

        val mainFontColor = DKColors().mainFontColor()
        val primaryColor = DKColors().primaryColor()
        val neutralColor = DKColors().neutralColor()

        text_view_connected_vehicle_name.normalText(mainFontColor)
        text_view_connected_vehicle_name.typeface = Typeface.DEFAULT_BOLD
        text_view_connected_vehicle_name.text = viewModel.vehicle?.let {
            it.computeSubtitle(requireContext(), listOf()) // TODO listOf
        }?:run {
            DKResource.convertToString(requireContext(), "dk_beacon_vehicle_unknown")
        }

        button_beacon_info.setOnClickListener {
            viewModel.launchDetailFragment()
        }

        view_separator.setBackgroundColor(neutralColor)

        text_view_major_title.normalText(primaryColor)
        text_view_major_title.text = DKResource.convertToString(requireContext(), "dk_beacon_major")

        text_view_major_value.normalText(mainFontColor)
        text_view_major_value.typeface = Typeface.DEFAULT_BOLD
        text_view_major_value.text = viewModel.seenBeacon?.major.toString()

        text_view_minor_title.normalText(primaryColor)
        text_view_minor_title.text = DKResource.convertToString(requireContext(), "dk_beacon_minor")

        text_view_minor_value.normalText(mainFontColor)
        text_view_minor_value.typeface = Typeface.DEFAULT_BOLD
        text_view_minor_value.text = viewModel.seenBeacon?.minor.toString()

        viewModel.seenBeacon?.let {
            text_view_distance.text = buildBeaconCharacteristics(String.format("%.0f", it.accuracy), "dk_common_unit_meter")
            DKResource.convertToDrawable(requireContext(), "dk_beacon_distance")?.let { drawable ->
                text_view_distance.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
            }

            text_view_battery.text = buildBeaconCharacteristics(batteryLevel.toString(), "%")
            computeBatteryDrawable(50)?.let { drawable ->
                text_view_battery.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
            }

            text_view_signal_intensity.text = buildBeaconCharacteristics(it.rssi.toString(), "dBm") // TODO common UI string ?
            DKResource.convertToDrawable(requireContext(), "dk_beacon_signal_intensity")?.let { drawable ->
                text_view_signal_intensity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopBatteryReaderScanner()
    }

    private fun startBatteryReaderScanner(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BeaconBatteryReaderScanner.registerListener(this, requireContext())
        } else {
            BeaconScannerBatteryReaderPreLollipop.registerBeaconListener(requireContext(), this)
        }
    }

    private fun stopBatteryReaderScanner(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BeaconBatteryReaderScanner.unregisterListener(this, requireContext())
        } else {
            BeaconScannerBatteryReaderPreLollipop.unregisterBeaconListener()
        }
    }

    override fun getBeacon(): BeaconData {
        return viewModel.seenBeacon?.let {
             BeaconData(it.proximityUuid, it.major, it.minor)
        }?: run {
            BeaconData("")
        }
    }

    override fun onBatteryLevelRead(batteryLevel: Int) {
        this.batteryLevel = batteryLevel
        text_view_battery.text = buildBeaconCharacteristics(batteryLevel.toString(), "%")
    }

    private fun computeBatteryDrawable(batteryLevel: Int): Drawable? {
        val idResDrawableBattery = when {
            batteryLevel >= 75 -> "dk_beacon_battery_100"
            batteryLevel >= 50 -> "dk_beacon_battery_75"
            batteryLevel >= 25 -> "dk_beacon_battery_50"
            else -> "dk_beacon_battery_25"
        }
        return DKResource.convertToDrawable(requireContext(), idResDrawableBattery)
    }

    private fun buildBeaconCharacteristics(value: String, unitIdentifier: String) : Spannable {
        val mainFontColor = DKColors().mainFontColor()
        val primaryColor = DKColors().primaryColor()
        val unit = DKResource.convertToString(requireContext(), unitIdentifier)

        return DKSpannable()
            .append(value, requireContext().resSpans {
                color(primaryColor)
                typeface(Typeface.BOLD)
                size(R.dimen.dk_text_medium)
            })
            .append(" ")
            .append(unit, requireContext().resSpans {
            color(mainFontColor)
            typeface(Typeface.NORMAL)
            size(R.dimen.dk_text_small)
        }).toSpannable()
    }
}