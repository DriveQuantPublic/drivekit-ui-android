package com.drivequant.drivekit.common.ui.listener

interface ContentMail {
    fun getRecipients(): List<String>
    fun getBccRecipients(): List<String>
    fun getSubject(): String
    fun getMailBody(): String
    fun overrideMailBodyContent(): Boolean
}