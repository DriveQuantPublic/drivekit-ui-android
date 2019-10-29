package com.drivequant.drivekit.ui.tripdetail.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.databaseutils.entity.Safety
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.commons.views.GaugeType
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SafetyViewModel
import com.drivequant.drivekit.ui.tripdetail.viewmodel.SafetyViewModelFactory
import kotlinx.android.synthetic.main.eco_driving_fragment.score_gauge
import kotlinx.android.synthetic.main.safety_fragment.*
import kotlinx.android.synthetic.main.safety_fragment.gauge_type_title

class SafetyFragment : Fragment() {

    companion object {
        fun newInstance(safety : Safety, tripDetailViewConfig: TripDetailViewConfig, tripsViewConfig: TripsViewConfig) : SafetyFragment {
            val fragment = SafetyFragment()
            fragment.safety = safety
            fragment.tripDetailViewConfig = tripDetailViewConfig
            fragment.tripsViewConfig = tripsViewConfig
            return fragment
        }
    }

    private lateinit var safety: Safety
    private lateinit var tripDetailViewConfig: TripDetailViewConfig
    private lateinit var tripsViewConfig: TripsViewConfig
    private lateinit var viewModel : SafetyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.safety_fragment, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("config", tripsViewConfig)
        outState.putSerializable("detailConfig", tripDetailViewConfig)
        outState.putSerializable("safety", safety)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("config") as TripsViewConfig?)?.let{
            tripsViewConfig = it
        }
        (savedInstanceState?.getSerializable("detailConfig") as TripDetailViewConfig?)?.let{
            tripDetailViewConfig = it
        }
        (savedInstanceState?.getSerializable("safety") as Safety?)?.let{
            safety = it
        }
        viewModel = ViewModelProviders.of(this,
            SafetyViewModelFactory(safety)
        ).get(
            SafetyViewModel::class.java)
        gauge_type_title.text = tripDetailViewConfig.safetyGaugeTitle
        accel_description.text = tripDetailViewConfig.accelerationText
        brake_description.text = tripDetailViewConfig.decelText
        adherence_description.text = tripDetailViewConfig.adherenceText
        accel_number_event.setTextColor(tripsViewConfig.primaryColor)
        brake_number_event.setTextColor(tripsViewConfig.primaryColor)
        adherence_number_event.setTextColor(tripsViewConfig.primaryColor)

        score_gauge.configure(viewModel.getScore(), GaugeType.SAFETY, tripsViewConfig.primaryFont)
        accel_number_event.text = viewModel.getAccelNumberEvent().toString()
        brake_number_event.text = viewModel.getBrakeNumberEvent().toString()
        adherence_number_event.text = viewModel.getAdherenceNumberEvent().toString()

    }

}
