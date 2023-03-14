package com.drivequant.drivekit.ui.extension

import com.drivequant.drivekit.driverdata.community.statistics.DKScoreStatistics

internal fun DKScoreStatistics.getMedianScore() = this.percentiles[9]