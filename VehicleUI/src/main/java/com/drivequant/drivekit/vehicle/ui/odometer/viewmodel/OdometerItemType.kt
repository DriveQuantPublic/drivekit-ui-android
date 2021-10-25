package com.drivequant.drivekit.vehicle.ui.odometer.viewmodel

import android.content.Context

enum class OdometerItemType {
    ODOMETER, ANALYZED, ESTIMATED;

    /*
    fun getTitle(context: Context) = when (this) {
        ODOMETER -> "Vehicle mileage"
        ANALYZED -> "Distance analyzed by the application"
        ESTIMATED -> "Estimated annual distance"
    }

    fun getDescription(context: Context, viewModel: OdometerDetailViewModel) = when (this) {
        ODOMETER -> viewModel.getLastUpdateDate(context)
        ANALYZED -> viewModel.getAnalyzedDistanceDescription(context)
        ESTIMATED -> viewModel.getEstimatedDistanceDescription(context)
    }

    fun getDistance(context: Context, viewModel: OdometerDetailViewModel) = when (this) {
        ODOMETER -> viewModel.getMileageDistance(context)
        ANALYZED -> viewModel.getAnalyzedDistance(context)
        ESTIMATED -> viewModel.getEstimatedAnnualDistance(context)
    }

     */
}