package com.drivequant.drivekit.permissionsutils.commons.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.permissionsutils.R

/**
 * Created by Mohamed on 2020-04-16.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

class DiagnosisItemView : LinearLayout {

    private var textViewDiagnosisTitle: TextView? = null
    private var textViewDiagnosisSubTitle: TextView? = null
    private var imageViewDiagnosis: ImageView? = null

    private var diagnosisTitle: String? = null
    private var diagnosisSubtitle: String? = null
    private var diagnosisTextOK: String? = null
    private var diagnosisTextKO: String? = null
    private var diagnosisLink: String? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.diagnosis_item_view, null)
        textViewDiagnosisTitle = view.findViewById(R.id.text_view_diagnosis_title)
        textViewDiagnosisSubTitle = view.findViewById(R.id.text_view_diagnosis_subtitle)
        imageViewDiagnosis = view.findViewById(R.id.image_view_diagnosis)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DiagnosisItemView, 0, 0)
            try {
                diagnosisTitle = a.getString(R.styleable.DiagnosisItemView_diagnosisTitle)
                diagnosisSubtitle = a.getString(R.styleable.DiagnosisItemView_diagnosisSubtitle)
                diagnosisTextOK = a.getString(R.styleable.DiagnosisItemView_diagnosisTextOK)
                diagnosisTextKO = a.getString(R.styleable.DiagnosisItemView_diagnosisTextKO)
                diagnosisLink = a.getString(R.styleable.DiagnosisItemView_diagnosisLink)

                textViewDiagnosisTitle?.let {
                    it.text = diagnosisTitle
                    it.normalText()
                }

                textViewDiagnosisSubTitle?.let {
                    it.text = diagnosisSubtitle
                    it.normalText(DriveKitUI.colors.secondaryColor())
                }

            } finally {
                a.recycle()
            }
        }

        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun getDiagnosisTitle(): String = diagnosisTitle!!

    fun getDiagnosticTextOK(): String = diagnosisTextOK!!

    fun getDiagnosticTextKO(): String = diagnosisTextKO!!

    fun getDiagnosisLink(): String = diagnosisLink!!

    fun setDiagnosisDrawable(hasProblem: Boolean) {
        val drawableItem = if (hasProblem) {
            ContextCompat.getDrawable(context, R.drawable.ic_high_priority_generic)
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_checked_generic)
        }

        val color = if (hasProblem) {
            DriveKitUI.colors.criticalColor()
        } else {
            ContextCompat.getColor(context, R.color.colorPrimaryGreen)
        }

        val wrapped = DrawableCompat.wrap(drawableItem!!)
        DrawableCompat.setTint(wrapped, color)
        imageViewDiagnosis?.setImageDrawable(wrapped)

    }
}
