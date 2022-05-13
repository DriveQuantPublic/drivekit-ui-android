package com.drivequant.drivekit.ui.synthesiscards.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.viewmodel.DKSynthesisCardViewModel
import kotlinx.android.synthetic.main.dk_fragment_synthesis_card_item.*

class DKSynthesisCardFragment : Fragment() {

    private lateinit var synthesisCard: DKSynthesisCard
    private lateinit var viewModel: DKSynthesisCardViewModel

    companion object {
        fun newInstance(synthesisCard : DKSynthesisCard) : DKSynthesisCardFragment {
            val fragment = DKSynthesisCardFragment()
            fragment.synthesisCard = synthesisCard
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dk_fragment_synthesis_card_item, container, false)
        FontUtils.overrideFonts(context, view)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this::synthesisCard.isInitialized) {
            if (!this::viewModel.isInitialized) {
                viewModel = ViewModelProviders.of(
                    this,
                    DKSynthesisCardViewModel.DKSynthesisCardViewModelFactory(synthesisCard)
                ).get(DKSynthesisCardViewModel::class.java)
            }
            updateContent()
        }
    }

    private fun updateContent() {
        title.text = viewModel.getTitle(requireContext())
        title.headLine2()

        viewModel.getExplanationContent(requireContext())?.let { explanation ->
            explanation_content.visibility = View.VISIBLE

            ContextCompat.getDrawable(requireContext(), R.drawable.dk_common_info)?.let {
                DrawableCompat.setTint(it, DriveKitUI.colors.secondaryColor())
                explanation_content.setImageDrawable(it)
            }

            explanation_content.setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(requireContext())
                    .layout(R.layout.template_alert_dialog_layout)
                    .positiveButton(DKResource.convertToString(requireContext(), "dk_common_close"))
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(R.id.text_view_alert_description)

                titleTextView?.text = DKResource.convertToString(requireContext(), "app_name")
                descriptionTextView?.text = explanation

                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        } ?: run {
            explanation_content.visibility = View.GONE
        }

        score_gauge.configure(
            viewModel.getScore(),
            synthesisCard.getGaugeConfiguration(),
            Typeface.NORMAL,
            synthesisCard.getGaugeConfiguration().getTitle(requireContext())
        )

        val icon = viewModel.getTopSynthesisCardIcon(requireContext())
        val text = viewModel.getTopSynthesisCardInfo(requireContext())
        if (icon != null && text.isNotEmpty()) {
            top_card_info.apply {
                visibility = View.VISIBLE
                init(icon, text)
            }
        }

        val middleIcon = viewModel.getMiddleSynthesisCardIcon(requireContext())
        val middleText = viewModel.getMiddleSynthesisCardInfo(requireContext())
        if (middleIcon != null && middleText.isNotEmpty()) {
            middle_card_info.apply {
                visibility = View.VISIBLE
                init(middleIcon, middleText)
            }
        }

        val bottomIcon = viewModel.getBottomSynthesisCardIcon(requireContext())
        val bottomText = viewModel.getBottomSynthesisCardInfo(requireContext())
        if (bottomIcon != null && bottomText.isNotEmpty()) {
            bottom_card_info.apply {
                visibility = View.VISIBLE
                init(bottomIcon, bottomText)
            }
        }

        val bottomTextValue = viewModel.getBottomText(requireContext())
        if (bottomTextValue != null) {
            bottom_text.text = bottomTextValue
            bottom_text.visibility = View.VISIBLE
        } else {
            bottom_text.visibility = View.GONE
        }

        if (viewModel.shouldHideCardInfoContainer(requireContext())) {
            val scoreParams = score_gauge.layoutParams as ConstraintLayout.LayoutParams
            scoreParams.endToEnd = container.id
            scoreParams.bottomToBottom = container.id
            card_info_container.visibility = View.GONE
        }
    }
}