package com.drivequant.drivekit.common.ui.component.lasttripscards.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.databinding.DkFragmentLastTripsSeeMoreBinding
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.navigation.DriveKitNavigationController

internal class DKLastTripsSeeMoreFragment : Fragment() {

    private var hasMoreTrips: Boolean = false
    private var _binding: DkFragmentLastTripsSeeMoreBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
    ): View {
        _binding = DkFragmentLastTripsSeeMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            binding.textViewSeeMore.apply {
                visibility = View.VISIBLE
                normalText()
                text = if (this@DKLastTripsSeeMoreFragment.hasMoreTrips) {
                    getString(R.string.dk_common_see_more_trips)
                } else {
                    getString(R.string.dk_common_no_trips_yet)
                }
            }
            binding.root.setOnClickListener {
                DriveKitNavigationController.driverDataUIEntryPoint?.startTripListActivity(context)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
