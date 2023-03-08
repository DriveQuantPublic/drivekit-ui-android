package com.drivequant.drivekit.ui.mysynthesis.component.communitycard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.mysynthesis.component.scorelegend.MySynthesisScoreLegendDialog

internal class MySynthesisCommunityCardView : LinearLayout {

    private lateinit var viewModel: MySynthesisCommunityCardViewModel

    private lateinit var communityCardView: LinearLayout
    private lateinit var title: TextView

    private lateinit var communityInfo: ConstraintLayout
    private lateinit var communityInfoTitle: TextView
    private lateinit var communityInfoTrips: TextView
    private lateinit var communityInfoDistance: TextView
    private lateinit var communityInfoDriversCount: TextView

    private lateinit var driverInfo: ConstraintLayout
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
        this.communityCardView = View.inflate(context, R.layout.dk_my_synthesis_community_card_view, null) as LinearLayout
        this.title = communityCardView.findViewById(R.id.community_card_title)

        this.communityInfo = this.communityCardView.findViewById(R.id.community_infos)
        this.communityInfoTitle = this.communityInfo.findViewById(R.id.title)
        this.communityInfoTitle.apply {
            text = context.getString(R.string.dk_driverdata_mysynthesis_my_community)
            smallText(DriveKitUI.colors.mainFontColor())
        }
        this.communityInfoTrips = this.communityInfo.findViewById(R.id.trips_count_title)
        this.communityInfoDistance = this.communityInfo.findViewById(R.id.distance_count_title)
        this.communityInfoDriversCount = this.communityInfo.findViewById(R.id.drivers_count_title)

        this.driverInfo = this.communityCardView.findViewById(R.id.driver_infos)
        this.driverInfoTitle = this.driverInfo.findViewById(R.id.title)
        this.driverInfoTitle.apply {
            text = context.getString(R.string.dk_driverdata_mysynthesis_me)
            smallText(DriveKitUI.colors.mainFontColor())
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
        viewModel.onViewModelUpdated = this::update
        update()
    }

    private fun update() {
        configureTitle()
        configureCommunityInfo()
        configureDriverInfo()

        MySynthesisScoreLegendDialog().show(context, viewModel.selectedScoreType)
    }

    private fun configureTitle() {
        title.apply {
            text = viewModel.getTitleText(context)
            headLine2(viewModel.getTitleColor())
        }
    }

    private fun configureCommunityInfo() {
        this.communityInfoTrips.apply {
            text = viewModel.getCommunityTripsText(context)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        this.communityInfoDistance.apply {
            text = viewModel.getCommunityDistanceText(context)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        this.communityInfoDriversCount.apply {
            text = viewModel.getCommunityActiveDriversText(context)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
    }

    private fun configureDriverInfo() {
        this.driverInfoTrips.apply {
            text = viewModel.getDriverTripsText(context)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        this.driverInfoDistance.apply {
            text = viewModel.getDriverDistanceText(context)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
    }
}