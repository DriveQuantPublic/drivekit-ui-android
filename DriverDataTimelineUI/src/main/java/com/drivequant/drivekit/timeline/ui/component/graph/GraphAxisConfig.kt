package com.drivequant.drivekit.timeline.ui.component.graph

internal data class GraphAxisConfig(
    val min: Double,
    val max: Double,
    val labels: LabelType,
)

internal sealed class LabelType {
    data class RAW_VALUES(val labelCount: Int) : LabelType()
    data class CUSTOM_LABELS(val labels: List<String>) : LabelType()

    val count = when (this) {
        is CUSTOM_LABELS -> labels.size
        is RAW_VALUES -> labelCount
    }

    val titles = when (this) {
        is CUSTOM_LABELS -> labels
        is RAW_VALUES -> null
    }
}
