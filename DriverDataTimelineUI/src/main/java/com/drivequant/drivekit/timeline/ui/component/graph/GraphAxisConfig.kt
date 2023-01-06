package com.drivequant.drivekit.timeline.ui.component.graph

internal data class GraphAxisConfig(
    val min: Double,
    val max: Double,
    val labels: LabelType,
)

internal sealed class LabelType {
    data class RawValues(val labelCount: Int) : LabelType()
    data class CustomLabels(val labels: List<String>) : LabelType()

    fun getCount() = when (this) {
        is CustomLabels -> labels.size
        is RawValues -> labelCount
    }

    fun getTitles() = when (this) {
        is CustomLabels -> labels
        is RawValues -> null
    }
}
