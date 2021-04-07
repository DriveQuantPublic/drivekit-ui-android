package com.drivequant.drivekit.common.ui.component.tripslist

import java.util.*

data class DKTripsByDate(
    val date: Date,
    val trips: List<DKTripListItem>)