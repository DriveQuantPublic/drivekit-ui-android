package com.drivequant.drivekit.ui.tripdetail.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drivequant.drivekit.common.ui.component.GaugeType

import com.drivequant.drivekit.databaseutils.entity.EcoDriving
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.TripDetailViewConfig
import com.drivequant.drivekit.ui.TripsViewConfig
import com.drivequant.drivekit.ui.tripdetail.viewmodel.EcoDrivingViewModel
import kotlinx.android.synthetic.main.eco_driving_fragment.*

class EcoDrivingFragment : Fragment() {

    companion object {
        fun newInstance(ecoDriving: EcoDriving, tripDetailViewConfig: TripDetailViewConfig, tripsViewConfig: TripsViewConfig) : EcoDrivingFragment{
            val fragment = EcoDrivingFragment()
            fragment.viewModel = EcoDrivingViewModel(
                ecoDriving,
                tripDetailViewConfig
            )
            fragment.tripsViewConfig = tripsViewConfig
            return fragment
        }
    }

    private lateinit var viewModel: EcoDrivingViewModel
    private lateinit var tripsViewConfig: TripsViewConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.eco_driving_fragment, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("viewModel", viewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (savedInstanceState?.getSerializable("viewModel") as EcoDrivingViewModel?)?.let {
            viewModel = it
        }
        score_gauge.configure(viewModel.getScore(), GaugeType.ECO_DRIVING)
        accelAdvice.text = viewModel.getAccelMessage()
        mainAdvice.text = viewModel.getMaintainMessage()
        decelAdvice.text = viewModel.getDecelMessage()
        gauge_type_title.text = viewModel.getGaugeTitle()
    }
}
