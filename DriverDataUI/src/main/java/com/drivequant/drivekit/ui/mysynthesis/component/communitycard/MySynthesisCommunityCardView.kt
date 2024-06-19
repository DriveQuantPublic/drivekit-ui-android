package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.drivequant.drivekit.common.ui.extension.headLine2WithColor
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.smallText
import com.drivequant.drivekit.common.ui.utils.DKDrawableUtils
import com.drivequant.drivekit.common.ui.utils.convertDpToPx
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.mysynthesis.MySynthesisConstant

internal class MySynthesisCommunityCardView : LinearLayout {

    private lateinit var viewModel: MySynthesisCommunityCardViewModel

    private lateinit var communityCardView: LinearLayout
    private lateinit var title: TextView
    private lateinit var gaugeView: MySynthesisGaugeView

    private lateinit var communityInfo: ConstraintLayout
    private lateinit var communityInfoIcon: View
    private lateinit var communityInfoTitle: TextView
    private lateinit var communityInfoTrips: TextView
    private lateinit var communityInfoDistance: TextView
    private lateinit var communityInfoDriversCount: TextView

    private lateinit var driverInfo: ConstraintLayout
    private lateinit var driverInfoIcon: View
    private lateinit var driverInfoTitle: TextView
    private lateinit var driverInfoTrips: TextView
    private lateinit var driverInfoDistance: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context, attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val synthesisColor = context.getColor(R.color.dkMySynthesisColor)

        this.communityCardView = View.inflate(context, R.layout.dk_my_synthesis_community_card_view, null) as LinearLayout
        this.title = communityCardView.findViewById(R.id.community_card_title)
        this.gaugeView = communityCardView.findViewById(R.id.communityGaugeView)

        this.communityInfo = this.communityCardView.findViewById(R.id.community_infos)
        this.communityInfoIcon = this.communityCardView.findViewById(R.id.icon)
        this.communityInfoIcon.background = DKDrawableUtils.circleDrawable(
            MySynthesisConstant.indicatorSize, borderColor = synthesisColor,
            borderWidth = MySynthesisConstant.INDICATOR_BORDER_WIDTH.convertDpToPx().toFloat()
        )
        this.communityInfoTitle = this.communityInfo.findViewById(R.id.title)
        this.communityInfoTitle.apply {
            text = context.getString(R.string.dk_driverdata_mysynthesis_my_community)
            smallText()
        }
        this.communityInfoTrips = this.communityInfo.findViewById(R.id.trips_count_title)
        this.communityInfoDistance = this.communityInfo.findViewById(R.id.distance_count_title)
        this.communityInfoDriversCount = this.communityInfo.findViewById(R.id.drivers_count_title)

        this.driverInfo = this.communityCardView.findViewById(R.id.driver_infos)
        this.driverInfoIcon = this.driverInfo.findViewById(R.id.icon)
        this.driverInfoIcon.background = DKDrawableUtils.circleDrawable(MySynthesisConstant.indicatorSize, insideColor = synthesisColor)

        this.driverInfoTitle = this.driverInfo.findViewById(R.id.title)
        this.driverInfoTitle.apply {
            text = context.getString(R.string.dk_driverdata_mysynthesis_me)
            smallText()
        }
        this.driverInfoTrips = this.driverInfo.findViewById(R.id.trips_count_title)
        this.driverInfoDistance = this.driverInfo.findViewById(R.id.distance_count_title)

        this.communityCardView.setDKStyle()
        addView(
            this.communityCardView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun configure(viewModel: MySynthesisCommunityCardViewModel) {
        this.viewModel = viewModel
        this.gaugeView.configure(viewModel.gaugeViewModel)
        viewModel.onViewModelUpdated = this::update
        update()
    }

    private fun update() {
        configureTitle()
        configureCommunityInfo()
        configureDriverInfo()
    }

    private fun configureTitle() {
        title.apply {
            text = viewModel.getTitleText(context)
            headLine2WithColor(viewModel.getTitleColor())
        }
    }

    private fun configureCommunityInfo() {
        this.communityInfoTrips.text = viewModel.getCommunityTripsCountText(context)
        this.communityInfoDistance.text = viewModel.getCommunityDistanceKmText(context)
        this.communityInfoDriversCount.text = viewModel.getCommunityActiveDriversText(context)
    }

    private fun configureDriverInfo() {
        this.driverInfoTrips.text = viewModel.getDriverTripsCountText(context)
        this.driverInfoDistance.text = viewModel.getDriverDistanceKmText(context)
    }
}
