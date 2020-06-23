package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.smallText

/**
 * Created by gabrielmorin on 05/04/2017.
 */

class EditableText(context: Context) : LinearLayout(context) {

    interface OnTextChangedListener {
        fun onTextChanged(editableText: EditableText, text: String?)
    }

    private var editTextSettingsEditable = true
    private var editTextSettingsGreyedOut = false
    private var inputType = InputType.TYPE_CLASS_TEXT
    private var textInputLayout: TextInputLayout? = null
    private var textViewLabel: TextView? = null
    private var textViewDescription: TextView? = null
    private var editText: EditText? = null
    private var mKeepUnderline = false
    private var onTextChangedListener: OnTextChangedListener? = null

    init {
        val view = View.inflate(context, R.layout.dk_layout_edit_text_settings, null)
        textInputLayout = view.findViewById(R.id.text_view)
        textViewLabel = view.findViewById(R.id.text_view_label)
        textViewLabel?.setTextColor(DriveKitUI.colors.complementaryFontColor())

        textViewDescription = view.findViewById(R.id.text_view_description)
        textViewDescription?.smallText(DriveKitUI.colors.complementaryFontColor())

        editText = view.findViewById(R.id.edit_text)
        editText?.setTextColor(DriveKitUI.colors.mainFontColor())
        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                onTextChangedListener?.onTextChanged(this@EditableText, editable.toString())
            }
        })
        addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun getTextInputLayout(): TextInputLayout? {
        return textInputLayout
    }

    fun getIsEditable() : Boolean {
        return editTextSettingsEditable
    }

    val text: String
        get() = editText!!.text.toString()

    fun setOnTextChangedListener(onTextChangedListener: OnTextChangedListener?) {
        this.onTextChangedListener = onTextChangedListener
    }

    fun setInputType(inputType: Int) {
        this.inputType = inputType
        editText?.inputType = inputType
    }

    fun setHint(hint: String?) {
        editText?.hint = hint
    }

    fun setLabel(label: String?) {
        textViewLabel?.text = label
    }

    fun setDescription(description: String?){
        description?.let {
            textViewDescription?.text = description
            textViewDescription?.visibility = View.VISIBLE
        }?:run {
            textViewDescription?.visibility = View.GONE
        }
    }

    fun setSettingsTitle(settingsTitle: String?) {
        if (settingsTitle != null) textInputLayout?.hint = settingsTitle
    }

    fun setSettingsText(settingsText: String?) {
        if (settingsText != null) editText?.setText(settingsText)
    }

    fun setIsEditable(isEditable: Boolean) {
        editTextSettingsEditable = isEditable
        editText!!.isEnabled = isEditable
        editText!!.isClickable = isEditable
        editText!!.isLongClickable = isEditable
        editText!!.isFocusable = isEditable
        editText!!.isFocusableInTouchMode = isEditable
        if (!isEditable && !mKeepUnderline) {
            editText!!.background = null
        }
    }

    fun setKeepUnderline(keepUnderline: Boolean) {
        mKeepUnderline = keepUnderline
    }

    fun setIsGreyedOut(isGreyedOut: Boolean) {
        editTextSettingsGreyedOut = isGreyedOut
        editText!!.alpha = (if (isGreyedOut) 0.5f else 1.0f)
    }
}