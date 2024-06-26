package com.drivequant.drivekit.common.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.utils.FontUtils
import com.google.android.material.imageview.ShapeableImageView

class FilterAdapter(
    context: Context,
    resource: Int,
    private var filterItemList: List<FilterItem>
) : ArrayAdapter<FilterItem>(context, resource, filterItemList) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    private fun getCustomView(position: Int, parent: ViewGroup): View {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.dk_filter_spinner_item, parent, false)

        val imageViewItem = view.findViewById<ShapeableImageView>(R.id.image_view_item)
        val textViewItem = view.findViewById<TextView>(R.id.text_view_display_name_item)
        val item = filterItemList[position]
        textViewItem.text = item.getTitle(context)
        imageViewItem.setImageDrawable(item.getImage(context))
        FontUtils.overrideFonts(context, view)
        return view
    }

    fun setItems(items: List<FilterItem>){
        filterItemList = items
    }
}

interface FilterItem {
    fun getItemId(): Any?
    fun getImage(context: Context): Drawable?
    fun getTitle(context: Context): String
}