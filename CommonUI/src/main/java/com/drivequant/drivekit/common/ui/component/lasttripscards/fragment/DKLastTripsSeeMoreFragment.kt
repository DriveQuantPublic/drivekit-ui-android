package com.drivequant.drivekit.common.ui.component.lasttripscards.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController
import kotlinx.android.synthetic.main.dk_fragment_last_trips.root
import kotlinx.android.synthetic.main.dk_fragment_last_trips_see_more.text_view_see_more

internal class DKLastTripsSeeMoreFragment : Fragment() {

    private var hasMoreTrips: Boolean = false

    companion object {
        fun newInstance(
            hasMoreTrips: Boolean
        ): DKLastTripsSeeMoreFragment {
            val fragment = DKLastTripsSeeMoreFragment()
            fragment.hasMoreTrips = hasMoreTrips
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_last_trips_see_more, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            text_view_see_more.apply {
                visibility = View.VISIBLE
                normalText(DriveKitUI.colors.complementaryFontColor())
                text = if (this@DKLastTripsSeeMoreFragment.hasMoreTrips) {
                    getString(R.string.dk_common_see_more_trips)
                } else {
                    getString(R.string.dk_common_no_trips_yet)
                }
            }
            root.setOnClickListener {
                DriveKitNavigationController.driverDataUIEntryPoint?.startTripListActivity(context)
            }
        }
    }
}
