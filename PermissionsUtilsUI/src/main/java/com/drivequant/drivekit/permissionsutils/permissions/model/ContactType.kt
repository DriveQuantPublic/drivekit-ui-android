package com.drivequant.drivekit.permissionsutils.permissions.model

import android.net.Uri
import com.drivequant.drivekit.common.ui.listener.ContentMail

/**
 * Created by Mohamed on 2020-04-16.
 */
// Copyright (c) 2020 DriveQuant. All rights reserved.

sealed class ContactType {
    object NONE : ContactType()
    data class WEB(val url: Uri) : ContactType()
    data class EMAIL(val contentMail: ContentMail) : ContactType()
}