package com.drivequant.drivekit.ui.synthesiscards.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.*
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.viewmodel.DKSynthesisCardViewModel
import kotlinx.android.synthetic.main.dk_fragment_synthesis_card_item.*

class DKSynthesisCardFragment(
    private val synthesisCard: DKSynthesisCard
) : Fragment() {

    private lateinit var viewModel: DKSynthesisCardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dk_fragment_synthesis_card_item, container, false)

    // TODO restore state

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(
                this,
                DKSynthesisCardViewModel.DKSynthesisCardViewModelFactory(synthesisCard)
            )
                .get(DKSynthesisCardViewModel::class.java)
        }
        updateContent()

    }

    private fun updateContent(){
        // TITLE
        title.text = viewModel.getTitle(requireContext())
        title.headLine2(DriveKitUI.colors.complementaryFontColor())

        // EXPLANATION CONTENT
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
                    .positiveButton()
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
                val descriptionTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_description)

                titleTextView?.text = DKResource.convertToString(requireContext(), "app_name")
                descriptionTextView?.text = explanation

                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        } ?: run {
            explanation_content.visibility = View.GONE
        }

        // GAUGE
        score_gauge.configure(
            viewModel.getScore(),
            synthesisCard.getGaugeConfiguration(),
            Typeface.BOLD
        )

        // TOP CARDINFO
        val icon = synthesisCard.getTopSynthesisCardInfo(requireContext()).getIcon(requireContext())
        val text = synthesisCard.getTopSynthesisCardInfo(requireContext()).getText(requireContext())
        if (icon != null && text.isNotEmpty()) {
            top_card_info.init(icon, text)
        }

        // MIDDLE CARDINFO
        val middleIcon = synthesisCard.getMiddleSynthesisCardInfo(requireContext()).getIcon(
            requireContext()
        )
        val middleText = synthesisCard.getMiddleSynthesisCardInfo(requireContext()).getText(
            requireContext()
        )
        if (middleIcon != null && middleText.isNotEmpty()) {
            middle_card_info.init(middleIcon, middleText)
        }

        // BOTTOM CARDINFO
        val bottomIcon = synthesisCard.getBottomSynthesisCardInfo(requireContext()).getIcon(
            requireContext()
        )
        val bottomText = synthesisCard.getBottomSynthesisCardInfo(requireContext()).getText(
            requireContext()
        )
        if (bottomIcon != null && bottomText.isNotEmpty()) {
            bottom_card_info.init(bottomIcon, bottomText)
        }

        // BOTTOM TEXT
        val bottomTextValue = viewModel.getBottomText(requireContext())
        if (bottomTextValue != null) {
            bottomTextValue.setSpan(AbsoluteSizeSpan(18, true), 0, bottomTextValue.length, 0)
            bottom_text.text = bottomTextValue
        }
    }
}