package com.drivequant.drivekit.common.ui.component.ranking.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DKRankingViewModel
import com.drivequant.drivekit.common.ui.component.ranking.viewmodel.DriverProgression
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.setDKStyle
import com.drivequant.drivekit.common.ui.extension.tint
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog


class DriverProgressionView : LinearLayout {

    private lateinit var globalRankTextView: TextView
    private lateinit var driverProgressionImageView: ImageView
    private lateinit var driverProgressionContainer: View
    private lateinit var infoPopupConditionImageView: ImageView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.dk_driver_progression_view, null).setDKStyle()
        addView(view, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))

        this.globalRankTextView = view.findViewById(R.id.text_view_global_rank)
        this.driverProgressionImageView = view.findViewById(R.id.image_view_driver_progression)
        this.driverProgressionContainer = view.findViewById(R.id.driver_progression_container)
        this.infoPopupConditionImageView = view.findViewById(R.id.image_view_info_popup_condition)

        setStyle()
    }

    fun setDriverProgression(rankingViewModel: DKRankingViewModel) {
        val progressionIconId =
            when (rankingViewModel.getProgression()) {
                DriverProgression.GOING_DOWN -> R.drawable.dk_common_arrow_down
                DriverProgression.GOING_UP -> R.drawable.dk_common_arrow_up
                else -> null
            }
        this.globalRankTextView.text = rankingViewModel.getDriverGlobalRank(context)
        progressionIconId?.let {
            this.driverProgressionImageView.setImageResource(it)
            this.driverProgressionImageView.visibility = View.VISIBLE
        }?:run {
            this.driverProgressionImageView.visibility = View.GONE
        }
        this.driverProgressionContainer.setBackgroundColor(rankingViewModel.getBackgroundColor())
        setConditionInfoButton(rankingViewModel)
    }

    private fun setConditionInfoButton(rankingViewModel: DKRankingViewModel) {
        ContextCompat.getDrawable(context, R.drawable.dk_common_info)?.let {
            it.tint(context, R.color.secondaryColor)
            this.infoPopupConditionImageView.apply {
                visibility = if (rankingViewModel.getConditionVisibility()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                setImageDrawable(it)
                setOnClickListener {
                    val alertDialog = DKAlertDialog.LayoutBuilder()
                        .init(context)
                        .layout(R.layout.template_alert_dialog_layout)
                        .positiveButton(context.getString(R.string.dk_common_ok)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

                    val titleTextView =
                        alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                    val descriptionTextView =
                        alertDialog.findViewById<TextView>(R.id.text_view_alert_description)
                    titleTextView?.text = rankingViewModel.getConditionTitle(context)
                    descriptionTextView?.text = rankingViewModel.getConditionDescription(context)
                    titleTextView?.headLine1()
                    descriptionTextView?.normalText()
                }
            }
        }
    }

    private fun setStyle() {
        this.globalRankTextView.headLine2()
    }
}
