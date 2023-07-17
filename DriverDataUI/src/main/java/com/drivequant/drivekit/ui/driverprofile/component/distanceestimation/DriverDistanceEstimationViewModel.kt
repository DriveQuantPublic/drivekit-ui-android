package com.drivequant.drivekit.ui.driverprofile.component.distanceestimation

import androidx.annotation.StringRes
import com.drivequant.drivekit.common.ui.extension.format
import kotlin.math.min

internal data class DriverDistanceEstimationViewModel(
    @StringRes val titleId: Int,
    @StringRes val distanceCaptionId: Int,
    private val distance: Double?,
    private val distanceEstimation: Int?
) {
    val hasValue: Boolean
        get() = this.distance != null && this.distanceEstimation != null

    fun getDistanceString(): String =
        if (this.hasValue) this.distance?.format(0) ?: "0" else ""

    fun getDistanceEstimationString(): String =
        if (this.hasValue) this.distanceEstimation?.toDouble()?.format(0) ?: "0" else ""

    fun getDistancePercentage(): Float =
        if (this.distanceEstimation != null && this.distanceEstimation != 0) {
            min(1f, (this.distance ?: 0.0).toFloat() / this.distanceEstimation.toFloat())
        } else {
            0.8f
        }

    fun getDistanceEstimationPercentage(): Float =
        if (this.distance != null && this.distanceEstimation != null && this.distance != 0.0) {
            min(1f, this.distanceEstimation / this.distance.toFloat())
        } else {
            1f
        }
}
