package com.drivequant.drivekit.ui.synthesiscards.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drivequant.drivekit.ui.R
import com.drivequant.drivekit.ui.synthesiscards.adapter.DKSynthesisCardFragmentPagerAdapter
import kotlinx.android.synthetic.main.dk_fragment_synthesis_card_viewpager.*

class DKSynthesisCardViewPagerFragment(
    private val cards: List<DKSynthesisCardFragment>
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dk_fragment_synthesis_card_viewpager, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_pager.adapter = DKSynthesisCardFragmentPagerAdapter(
            childFragmentManager,
            cards
        )
    }
}