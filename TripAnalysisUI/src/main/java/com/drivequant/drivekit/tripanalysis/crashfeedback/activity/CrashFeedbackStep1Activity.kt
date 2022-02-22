package com.drivequant.drivekit.tripanalysis.crashfeedback.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.drivekit.tripanalysis.ui.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.tripanalysis.crashfeedback.viewmodel.CrashFeedbackStep1ViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.android.synthetic.main.dk_layout_activity_crash_feedback_step1.*
import java.util.*

class CrashFeedbackStep1Activity : BaseCrashFeedbackActivity() {

    private lateinit var viewModel: CrashFeedbackStep1ViewModel
    private var fromFullScreenIntent: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DriveKitUI.analyticsListener?.trackScreen(
            DKResource.convertToString(
                this,
                "dk_tag_rankings" //TODO
            ), javaClass.simpleName
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        /*fromFullScreenIntent = intent.getBooleanExtra(IS_FULL_SCREEN_INTENT_EXTRA_KEY, false)
        if (fromFullScreenIntent) {
            DriveKitTripAnalysis.setCrashFeedbackTimer(10 * 60) // 10 minutes
        }*/

        setContentView(R.layout.dk_layout_activity_crash_feedback_step1)

        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(CrashFeedbackStep1ViewModel::class.java)
        }

        /*DriveKitTripAnalysis.setCrashFeedbackListener(object : DKCrashFeedbackListener {
            override fun onProgress(currentSecond: Int, timeoutSecond: Int) {
                runOnUiThread {
                    //text_view_progress.text = "$currentSecond"
                    //text_view_total.text = "$timeoutSecond"
                }
            }

            override fun timeoutReached() {
                runOnUiThread {
                    finish()
                }
            }

        })*/



        initTitle()
        initDescription()
        initTimer()

        startTimer() // TODO mock seconds
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

    private fun startTimer() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            private var counter = 0
            override fun run() {
                runOnUiThread {
                    updateTimer(counter)
                    counter++
                }
            }
        }, 0, 1000) //Update text every second
    }

    private fun initTimer() {
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry(0f))
        entries.add(PieEntry(60f))

        val pieDataSet = PieDataSet(entries, null)
        pieDataSet.setColors(intArrayOf(R.color.dkBadMean, R.color.dkGood), this)

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)
        pieDataSet.selectionShift = 2f
        pieDataSet.sliceSpace = 1f

        timer.apply {
            setTouchEnabled(false)
            data = pieData
            isRotationEnabled = false
            description = null
            setUsePercentValues(true)
            legend.isEnabled = false
            setEntryLabelColor(R.color.dkBadMean)
            //transparentCircleRadius = 63f
            transparentCircleRadius = 1f
            setTransparentCircleAlpha(255)
            setTransparentCircleColor(Color.WHITE)
            holeRadius = 60f
            setHoleColor(ContextCompat.getColor(context, R.color.dkBadMean)) // TODO care context
            invalidate()
        }
    }

    private fun updateTimer(duration: Int) {
        val nbSecond = (duration % 60).toInt()
        //var durationText: String = Utils.convertSecondeToString(duration, true, false)
        var durationText = "test"
        when {
            nbSecond in 1..9 -> {
                durationText += ":0$nbSecond"
            }
            nbSecond >= 10 -> {
                durationText += ":$nbSecond"
            }
            nbSecond == 0 -> {
                durationText += ":00"
            }
        }
        val h = Highlight(0f, 0f, 0)

        timer.apply {
            highlightValues(arrayOf(h))
            centerText = durationText
            setCenterTextSize(25f)
            setCenterTextColor(ContextCompat.getColor(context, R.color.dkBadMean)) // TODO care context
        }
        val entries: MutableList<PieEntry> = ArrayList()
        entries.add(PieEntry(nbSecond.toFloat()))
        entries.add(PieEntry((60 - nbSecond).toFloat()))
        val pieDataSet = PieDataSet(entries, null)
        pieDataSet.setColors(intArrayOf(R.color.dkGood, R.color.dkBadMean), this)
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)
        pieDataSet.selectionShift = 0.1f
        pieDataSet.sliceSpace = 0.1f
        timer.data = pieData
        timer.invalidate()
    }

    fun onNoCrashButtonClicked(view: View) {
        //DriveKitTripAnalysis.stopCrashFeedbackTimer()
        //startActivity(Intent(this, CrashScreenConfirmationActivity::class.java))
        finish()
    }

    fun onAssistanceButtonClicked(view: View) {
        dismissKeyguard()
        launchPhoneCall() // Do not wait the webservice response to make the phone call !
        /*DriveKitTripAnalysis.stopCrashFeedbackTimer()
        DriveKitTripAnalysis.sendUserFeedback(
            feedbackType = CrashFeedbackType.CRASH_CONFIRMED,
            severity = CrashFeedbackSeverity.CRITICAL,
            listener = object : CrashUserFeedbackListener {
                override fun onFeedbackSent(status: CrashFeedbackStatus) {
                    view.context?.let {
                        Toast.makeText(it, "Critical crash feedback status: $status", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        )*/
    }
}