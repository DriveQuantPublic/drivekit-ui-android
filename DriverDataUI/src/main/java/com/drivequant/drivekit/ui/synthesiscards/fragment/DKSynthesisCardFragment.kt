package com.drivequant.drivekit.ui.synthesiscards.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.viewmodel.DKSynthesisCardViewModel
import kotlinx.android.synthetic.main.dk_fragment_synthesis_card_item.*

class DKSynthesisCardFragment(val synthesisCard: DKSynthesisCard) : Fragment() {

    private lateinit var viewModel: DKSynthesisCardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dk_fragment_synthesis_card_item, container, false)
    }

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
                // TODO AlertDialog
                Toast.makeText(requireContext(), explanation, Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            explanation_content.visibility = View.GONE
        }

        // GAUGE
        // TODO add title for gauge, change value with "progress"
        score_gauge.configure(9.9, synthesisCard.getGaugeConfiguration())

        // TOP CARDINFO
        val icon = synthesisCard.getTopSynthesisCardInfo(requireContext()).getIcon(requireContext())
        val text = synthesisCard.getTopSynthesisCardInfo(requireContext()).getText(requireContext())
        if (icon != null && text.isNotEmpty()) {
            top_card_info.init(icon, text)
        }

        // MIDDLE CARDINFO
        val middleIcon = synthesisCard.getMiddleSynthesisCardInfo(requireContext())
            .getIcon(requireContext())
        val middleText = synthesisCard.getMiddleSynthesisCardInfo(requireContext())
            .getText(requireContext())
        if (middleIcon != null && middleText.isNotEmpty()) {
            middle_card_info.init(middleIcon, middleText)
        }

        // BOTTOM CARDINFO
        val bottomIcon = synthesisCard.getBottomSynthesisCardInfo(requireContext())
            .getIcon(requireContext())
        val bottomText = synthesisCard.getBottomSynthesisCardInfo(requireContext())
            .getText(requireContext())
        if (bottomIcon != null && bottomText.isNotEmpty()) {
            bottom_card_info.init(bottomIcon, bottomText)
        }
    }
}