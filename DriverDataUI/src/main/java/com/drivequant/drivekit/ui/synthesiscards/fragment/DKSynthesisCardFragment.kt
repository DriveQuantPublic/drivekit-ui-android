package com.drivequant.drivekit.ui.synthesiscards.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.extension.tintDrawable
import com.drivequant.drivekit.common.ui.graphical.DKColors
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.databinding.DkFragmentSynthesisCardItemBinding
import com.drivequant.drivekit.ui.synthesiscards.DKSynthesisCard
import com.drivequant.drivekit.ui.synthesiscards.viewmodel.DKSynthesisCardViewModel

class DKSynthesisCardFragment : Fragment() {

    private lateinit var synthesisCard: DKSynthesisCard
    private lateinit var viewModel: DKSynthesisCardViewModel
    private var _binding: DkFragmentSynthesisCardItemBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView

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
        _binding = DkFragmentSynthesisCardItemBinding.inflate(inflater, container, false)
        FontUtils.overrideFonts(context, binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this::synthesisCard.isInitialized) {
            if (!this::viewModel.isInitialized) {
                viewModel = ViewModelProvider(
                    this,
                    DKSynthesisCardViewModel.DKSynthesisCardViewModelFactory(synthesisCard)
                )[DKSynthesisCardViewModel::class.java]
            }
            updateContent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateContent() {
        binding.title.text = viewModel.getTitle(requireContext())
        binding.title.headLine2()

        viewModel.getExplanationContent(requireContext())?.let { explanation ->
            binding.explanationContent.visibility = View.VISIBLE

            ContextCompat.getDrawable(requireContext(), com.drivequant.drivekit.common.ui.R.drawable.dk_common_info)?.let {
                it.tintDrawable(DKColors.secondaryColor)
                binding.explanationContent.setImageDrawable(it)
            }

            binding.explanationContent.setOnClickListener {
                val alertDialog = DKAlertDialog.LayoutBuilder()
                    .init(requireContext())
                    .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
                    .positiveButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_close))
                    .show()

                val titleTextView = alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
                val descriptionTextView =
                    alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)

                titleTextView?.setText(R.string.app_name)
                descriptionTextView?.text = explanation

                titleTextView?.headLine1()
                descriptionTextView?.normalText()
            }
        } ?: run {
            binding.explanationContent.visibility = View.GONE
        }

        binding.scoreGauge.configure(
            viewModel.getScore(),
            synthesisCard.getGaugeConfiguration(),
            Typeface.NORMAL,
            synthesisCard.getGaugeConfiguration().getTitle(requireContext())
        )

        val icon = viewModel.getTopSynthesisCardIcon(requireContext())
        val text = viewModel.getTopSynthesisCardInfo(requireContext())
        if (icon != null && text.isNotEmpty()) {
            binding.topCardInfo.apply {
                visibility = View.VISIBLE
                init(icon, text)
            }
        }

        val middleIcon = viewModel.getMiddleSynthesisCardIcon(requireContext())
        val middleText = viewModel.getMiddleSynthesisCardInfo(requireContext())
        if (middleIcon != null && middleText.isNotEmpty()) {
            binding.middleCardInfo.apply {
                visibility = View.VISIBLE
                init(middleIcon, middleText)
            }
        }

        val bottomIcon = viewModel.getBottomSynthesisCardIcon(requireContext())
        val bottomText = viewModel.getBottomSynthesisCardInfo(requireContext())
        if (bottomIcon != null && bottomText.isNotEmpty()) {
            binding.bottomCardInfo.apply {
                visibility = View.VISIBLE
                init(bottomIcon, bottomText)
            }
        }

        val bottomTextValue = viewModel.getBottomText(requireContext())
        if (bottomTextValue != null) {
            binding.bottomText.text = bottomTextValue
            binding.bottomText.visibility = View.VISIBLE
        } else {
            binding.bottomText.visibility = View.GONE
        }

        if (viewModel.shouldHideCardInfoContainer(requireContext())) {
            val scoreParams = binding.scoreGauge.layoutParams as ConstraintLayout.LayoutParams
            scoreParams.endToEnd = binding.container.id
            scoreParams.bottomToBottom = binding.container.id
            binding.cardInfoContainer.visibility = View.GONE
        }
    }
}
