package com.drivequant.drivekit.common.ui.component.triplist.viewModel

import com.drivequant.drivekit.common.ui.component.triplist.DKTripListItem
import java.util.*

data class DKTripsByDate(
    val date: Date,
    val trips: List<DKTripListItem>
)