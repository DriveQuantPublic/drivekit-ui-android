package com.drivequant.drivekit.vehicle.ui.beacon.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.drivequant.beaconutils.BeaconInfo
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.vehicle.ui.DriveKitVehicleUI
import com.drivequant.drivekit.vehicle.ui.R
import com.drivequant.drivekit.vehicle.ui.beacon.adapter.BeaconDetailAdapter
import com.drivequant.drivekit.vehicle.ui.beacon.viewmodel.BeaconDetailViewModel
import com.drivequant.drivekit.vehicle.ui.utils.DKBeaconRetrievedInfo
import kotlinx.android.synthetic.main.fragment_beacon_detail.*
import java.lang.StringBuilder

class BeaconDetailFragment : Fragment() {
    companion object {
        fun newInstance(viewModel: BeaconDetailViewModel) : BeaconDetailFragment {
            val fragment = BeaconDetailFragment()
            fragment.viewModel = viewModel
            return fragment
        }
    }

    private lateinit var viewModel: BeaconDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        DriveKitVehicleUI.beaconDiagnosticMail?.let {
            setHasOptionsMenu(true)
        }
        return inflater.inflate(R.layout.fragment_beacon_detail, container, false).setDKStyle()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::viewModel.isInitialized) {
            outState.apply {
                putString("vehicleName", viewModel.vehicleName)
                putString("vehicleId", viewModel.vehicleId)
                putInt("batteryLevel", viewModel.beaconRetrievedInfo.batteryLevel)
                putDouble("estimatedDistance", viewModel.beaconRetrievedInfo.estimatedDistance)
                putInt("rssi", viewModel.beaconRetrievedInfo.rssi)
                putInt("txPower", viewModel.beaconRetrievedInfo.txPower)
                putSerializable("seenBeacon", viewModel.seenBeacon)
            }
        }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            val vehicleName = it.getString("vehicleName")
            val vehicleId = it.getString("vehicleId")
            val batteryLevel = it.getInt("batteryLevel")
            val estimatedDistance = it.getDouble("estimatedDistance")
            val rssi = it.getInt("rssi")
            val txPower = it.getInt("txPower")
            val seenBeacon = it.getSerializable("seenBeacon") as BeaconInfo ?
            if (vehicleId != null && vehicleName != null && seenBeacon != null) {
                viewModel = ViewModelProviders.of(
                    this,
                    BeaconDetailViewModel.BeaconDetailViewModelFactory(
                        vehicleId,
                        vehicleName,
                        DKBeaconRetrievedInfo(batteryLevel, estimatedDistance, rssi, txPower),
                        seenBeacon)
                ).get(BeaconDetailViewModel::class.java)
            }
        }

        viewModel.buildListData(requireContext())
        val layoutManager =
            LinearLayoutManager(requireContext())
        recycler_view_container.layoutManager = layoutManager
        recycler_view_container.adapter = BeaconDetailAdapter(requireContext(), viewModel)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
            val menuInflater = activity?.menuInflater
            menuInflater?.inflate(R.menu.beacon_detail_menu_bar, menu)

            val item = menu.findItem(R.id.action_mail)
            item?.let {
                val wrapped = DrawableCompat.wrap(it.icon)
                DrawableCompat.setTint(wrapped, Color.WHITE)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_mail){
            sendMail()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendMail(){
        val recipients = DriveKitVehicleUI.beaconDiagnosticMail?.getRecipients()?.toTypedArray()
        val bccRecipients = DriveKitVehicleUI.beaconDiagnosticMail?.getBccRecipients()?.toTypedArray()
        val subject = DriveKitVehicleUI.beaconDiagnosticMail?.getSubject()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.putExtra(Intent.EXTRA_BCC, bccRecipients)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, buildBody())
        startActivity(intent)
    }

    private fun buildBody(): String{
        val body = DriveKitVehicleUI.beaconDiagnosticMail?.getMailBody()
        val sb = StringBuilder()
        sb.append(body)
        sb.append("\n__________\n")
        for (line in viewModel.data){
            sb.append("${line.title} : ${line.value} \n")
        }
        return sb.toString()
    }
}