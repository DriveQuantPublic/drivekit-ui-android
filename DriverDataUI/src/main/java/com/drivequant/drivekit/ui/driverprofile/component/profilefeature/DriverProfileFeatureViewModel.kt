package com.drivequant.drivekit.ui.driverprofile.component.profilefeature

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

internal data class DriverProfileFeatureViewModel(
    @StringRes val titleId: Int,
    val description: DriverProfileFeatureDescription,
    @DrawableRes val iconId: Int
) {
    fun getDescription(context: Context): String = description.getDescription(context)
}

internal sealed class DriverProfileFeatureDescription {
    data class SimpleDescription(@StringRes val descriptionId: Int) :
        DriverProfileFeatureDescription()

    class ParameterizedDescription(@StringRes val descriptionId: Int, vararg param: String) :
        DriverProfileFeatureDescription() {
        val params: Array<out String> = param
    }

    class PluralDescription(
        @PluralsRes val descriptionId: Int,
        val pluralArg: Int,
        vararg param: String
    ) : DriverProfileFeatureDescription() {
        val params: Array<out String> = param
    }

    fun getDescription(context: Context): String = when (this) {
        is SimpleDescription -> context.getString(this.descriptionId)
        is ParameterizedDescription -> context.getString(this.descriptionId, *this.params)
        is PluralDescription -> context.resources.getQuantityString(this.descriptionId, this.pluralArg, *this.params)
    }
}
