package com.drivequant.drivekit.tripanalysis.crashfeedback.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.drivekit.tripanalysis.ui.R
import com.drivekit.tripanalysis.ui.databinding.DkLayoutActivityCrashFeedbackStep1Binding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.pixelToSp
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKDataFormatter
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.common.ui.utils.DurationUnit
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

class CrashFeedbackStep1Activity : BaseCrashFeedbackActivity() {

    private lateinit var viewModel: CrashFeedbackStep1ViewModel
    private lateinit var binding: DkLayoutActivityCrashFeedbackStep1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DkLayoutActivityCrashFeedbackStep1Binding.inflate(layoutInflater)

        DriveKitUI.analyticsListener?.trackScreen(getString(R.string.dk_tag_trip_analysis_crash_feedback_step1), javaClass.simpleName)

        DriveKitTripAnalysis.setCrashFeedbackTimer(60) // 1 minute

        setContentView(binding.root)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[CrashFeedbackStep1ViewModel::class.java]
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
        viewModel.getTitleResId().let { titleResId ->
            binding.textViewTitle.apply {
                setText(titleResId)
                pixelToSp(context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_text_xbigger))
                setTypeface(DriveKitUI.primaryFont(context), Typeface.NORMAL)
            }
        }
    }

    private fun initDescription() {
        viewModel.getDescriptionResId().let { descriptionResId ->
            binding.textViewDescription.apply {
                setText(descriptionResId)
                pixelToSp(context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_text_normal))
                setLineSpacing(25f, 1f)
                setTypeface(DriveKitUI.primaryFont(context), Typeface.NORMAL)
            }
        }
    }

    private fun initTimer() {
        binding.timer.apply {
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
        val data = DKDataFormatter.formatDuration(this, (total - duration).toDouble(), maxUnit = DurationUnit.SECOND)
        val spannable = DKSpannable()
        data.forEach {
            when (it) {
                is FormatType.VALUE -> spannable.append(
                    it.value,
                    this.resSpans {
                        color(DKColors.fontColorOnPrimaryColor)
                        typeface(Typeface.BOLD)
                        size(com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium)
                    })
                else -> {
                    // do nothing
                }
            }
        }

        binding.timer.apply {
            highlightValues(arrayOf(Highlight(0f, 0f, 0)))
            centerText = spannable.toSpannable().toString()
            val textSize = context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_text_medium)
            setCenterTextColor(DKColors.mainFontColor)
            setCenterTextTypeface(DriveKitUI.primaryFont(context))
            setCenterTextSize(textSize)
        }
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry((60-duration).toFloat()))
        entries.add(PieEntry(duration.toFloat()))
        val pieDataSet = PieDataSet(entries, null)
        pieDataSet.setColors(intArrayOf(R.color.dkCrashFeedbackAssistance, R.color.dkCrashFeedbackAssistance_10), this)
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)
        pieDataSet.selectionShift = 0f
        pieDataSet.sliceSpace = 0f
        binding.timer.data = pieData
        binding.timer.invalidate()
    }

    fun onNoCrashButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        DriveKitTripAnalysis.stopCrashFeedbackTimer()
        startActivity(Intent(this, CrashFeedbackStep2Activity::class.java))
        finish()
    }

    fun onAssistanceButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
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
