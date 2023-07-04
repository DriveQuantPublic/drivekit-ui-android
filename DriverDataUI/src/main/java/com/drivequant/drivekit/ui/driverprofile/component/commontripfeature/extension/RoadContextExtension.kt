package com.drivequant.drivekit.ui.driverprofile.component.commontripfeature.extension

import com.drivequant.drivekit.databaseutils.entity.RoadContext
import com.drivequant.drivekit.ui.R

internal fun RoadContext.getTitle() = when (this) {
    RoadContext.CITY -> R.string.dk_driverdata_usual_trip_card_context_city
    RoadContext.EXPRESSWAYS -> R.string.dk_driverdata_usual_trip_card_context_expressways
    RoadContext.HEAVY_URBAN_TRAFFIC -> R.string.dk_driverdata_usual_trip_card_context_heavy_urban
    RoadContext.SUBURBAN -> R.string.dk_driverdata_usual_trip_card_context_suburban
    RoadContext.TRAFFIC_JAM -> R.string.dk_driverdata_usual_trip_card_context_traffic_jam
}
