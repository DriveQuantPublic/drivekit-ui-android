package com.drivequant.drivekit.tripanalysis.crashfeedback.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.FormatType
import com.drivequant.drivekit.tripanalysis.DriveKitTripAnalysis
import com.drivequant.drivekit.tripanalysis.crashfeedback.view.RoundedSlicesPieChartRenderer
import com.drivequant.drivekit.tripanalysis.crashfeedback.viewmodel.CrashFeedbackStep1ViewModel
import com.drivequant.drivekit.tripanalysis.model.crashdetection.CrashFeedbackStatus
import com.drivequant.drivekit.tripanalysis.model.crashdetection.CrashUserFeedbackListener
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackSeverity
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.CrashFeedbackType
import com.drivequant.drivekit.tripanalysis.service.crashdetection.feedback.DKCrashFeedbackListener
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.android.synthetic.main.dk_layout_activity_crash_feedback_step1.*
import java.util.*


class CrashFeedbackStep1Activity : BaseCrashFeedbackActivity() {

    private val IS_FULL_SCREEN_INTENT_EXTRA_KEY = "fullScreenIntentExtra"

    private lateinit var viewModel: CrashFeedbackStep1ViewModel
    private var fromFullScreenIntent: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                this,
                "dk_tag_trip_analysis_crash_feedback_step1"
            ), javaClass.simpleName
        )

        fromFullScreenIntent = intent.getBooleanExtra(IS_FULL_SCREEN_INTENT_EXTRA_KEY, false)
        if (fromFullScreenIntent) {
            DriveKitTripAnalysis.setCrashFeedbackTimer(10 * 60) // 10 minutes
        }

        setContentView(R.layout.dk_layout_activity_crash_feedback_step1)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(CrashFeedbackStep1ViewModel::class.java)
        }

        DriveKitTripAnalysis.setCrashFeedbackListener(object : DKCrashFeedbackListener {
            override fun onProgress(currentSecond: Int, timeoutSecond: Int) {
                runOnUiThread {
                    updateTimer(currentSecond, timeoutSecond)
                }
            }

            override fun timeoutReached() {
                runOnUiThread {
                    finish()
                }
            }
        })

        initTitle()
        initDescription()
        initTimer()
    }

    private fun initTitle() {
        viewModel.getTitleResId().let {
            text_view_title.text = this.getText(it)
            text_view_title.highlightMedium()
        }
    }

    private fun initDescription() {
        viewModel.getDescriptionResId().let {
            text_view_description.text = this.getText(it)
            text_view_description.normalText(DriveKitUI.colors.complementaryFontColor())
        }
    }

    private fun initTimer() {
        timer.apply {
            setTouchEnabled(false)
            isRotationEnabled = false
            description = null
            legend.isEnabled = false
            holeRadius = 90f
            isDrawHoleEnabled = true
            transparentCircleRadius = 61f
            renderer = RoundedSlicesPieChartRenderer(
                this,
                this.animator,
                this.viewPortHandler
            )
            setNoDataText("")
            invalidate()
        }
    }

    private fun updateTimer(duration: Int, total: Int) {
        val nbSecond = (duration % 60)
        val data = DKDataFormatter.formatExactDuration(this, (total - nbSecond) * 1000L)
        val spannable = DKSpannable()
        data.forEach {
            when (it) {
                is FormatType.VALUE -> spannable.append(
                    it.value,
                    this.resSpans {
                        color(DriveKitUI.colors.fontColorOnPrimaryColor())
                        typeface(Typeface.BOLD)
                        size(R.dimen.dk_text_xbig)
                    })
                is FormatType.UNIT -> spannable.append(
                    it.value,
                    this.resSpans {
                        color(DriveKitUI.colors.fontColorOnPrimaryColor())
                        size(R.dimen.dk_text_normal)
                    })
                is FormatType.SEPARATOR -> spannable.append(it.value)
            }
        }

        timer.apply {
            highlightValues(arrayOf(Highlight(0f, 0f, 0)))
            centerText = spannable.toSpannable().toString()
            val textSize = context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_text_small)
            setCenterTextColor(DriveKitUI.colors.mainFontColor())
            setCenterTextTypeface(DriveKitUI.primaryFont(context))
            setCenterTextSize(textSize)
        }
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry((60-nbSecond).toFloat()))
        entries.add(PieEntry(nbSecond.toFloat()))
        val pieDataSet = PieDataSet(entries, null)
        pieDataSet.setColors(intArrayOf(R.color.dkCrashFeedbackAssistance, R.color.dkCrashFeedbackAssistance_10), this)
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)
        pieDataSet.selectionShift = 0f
        pieDataSet.sliceSpace = 0f
        timer.data = pieData
        timer.invalidate()
    }

    fun onNoCrashButtonClicked(view: View) {
        DriveKitTripAnalysis.stopCrashFeedbackTimer()
        startActivity(Intent(this, CrashFeedbackStep2Activity::class.java))
        finish()
    }

    fun onAssistanceButtonClicked(view: View) {
        dismissKeyguard()
        launchPhoneCall() // Do not wait the webservice response to make the phone call !
        DriveKitTripAnalysis.stopCrashFeedbackTimer()
        DriveKitTripAnalysis.sendUserFeedback(
            feedbackType = CrashFeedbackType.CRASH_CONFIRMED,
            severity = CrashFeedbackSeverity.CRITICAL,
            listener = object : CrashUserFeedbackListener {
                override fun onFeedbackSent(status: CrashFeedbackStatus) {
                    finish()
                }
            }
        )
    }
}