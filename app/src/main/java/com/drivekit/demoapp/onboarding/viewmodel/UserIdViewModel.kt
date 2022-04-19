package com.drivekit.demoapp.onboarding.viewmodel

import android.content.Context
import com.drivekit.demoapp.config.DriveKitListenerController
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.resSpans
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.common.ui.utils.DKSpannable
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.networking.DriveKitListener
import com.drivequant.drivekit.core.networking.RequestError

class UserIdViewModel : DriveKitListener {

    private var listener: UserIdDriveKitListener? = null

    fun getTitle(context: Context) = DKSpannable().append(
        DKResource.convertToString(
            context,
            "authentication_title"
        ), context.resSpans {
            color(DriveKitUI.colors.mainFontColor())
        }).append(" ").append("ⓘ", context.resSpans {
        color(DriveKitUI.colors.secondaryColor())
    }).toSpannable()

    fun getDescription(context: Context) =
        DKResource.convertToString(context, "authentication_description")

    fun sendUserId(userId: String, listener: UserIdDriveKitListener) {
        DriveKitListenerController.registerListener(this)
        DriveKit.setUserId(userId)
        this.listener = listener
    }

    override fun onConnected() {
        DriveKitListenerController.unregisterListener(this)
        this.listener?.onSetUserId(true, null)
        this.listener = null
    }

    override fun onAuthenticationError(errorType: RequestError) {
        DriveKitListenerController.unregisterListener(this)
        this.listener?.onSetUserId(false, errorType)
        this.listener = null
    }

    override fun onDisconnected() {
        DriveKitListenerController.unregisterListener(this)
        this.listener?.onSetUserId(false, null)
        this.listener = null
    }
}

interface UserIdDriveKitListener {
    fun onSetUserId(status: Boolean, requestError: RequestError?)
}

fun RequestError.getErrorMessage(context: Context) = when (this) {
    RequestError.NO_NETWORK -> "network_ko_error"
    RequestError.UNAUTHENTICATED -> "authentication_error"
    RequestError.FORBIDDEN -> "forbidden_error"
    RequestError.SERVER_ERROR -> "server_error"
    RequestError.CLIENT_ERROR -> "client_error"
    RequestError.UNKNOWN_ERROR -> "unknown_error"
}.let {
    DKResource.convertToString(context, it)
}
