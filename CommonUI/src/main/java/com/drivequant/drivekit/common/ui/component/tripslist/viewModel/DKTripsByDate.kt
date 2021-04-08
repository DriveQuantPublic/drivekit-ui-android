package com.drivequant.drivekit.common.ui.component.tripslist.viewModel

import com.drivequant.drivekit.common.ui.component.tripslist.DKTripListItem
import java.util.*

data class DKTripsByDate(
    val date: Date,
    val trips: List<DKTripListItem>
)