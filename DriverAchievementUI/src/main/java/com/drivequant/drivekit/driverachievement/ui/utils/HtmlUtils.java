package com.drivequant.drivekit.driverachievement.ui.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.drivequant.drivekit.driverachievement.ui.R;

/**
 * Created by Mohamed on 11/02/2020.
 */

public class HtmlUtils {
    public static String getTextHighlight(String text, Context context) {
        return "<big><b><span style=\"color:" + ContextCompat.getColor(context, R.color.dk_primary_color) + "\">" + text + "</span></b></big>";
    }
}
