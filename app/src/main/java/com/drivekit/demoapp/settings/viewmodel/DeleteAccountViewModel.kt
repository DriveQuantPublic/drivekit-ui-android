package com.drivekit.demoapp.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.DriveKitListenerManager
import com.drivequant.drivekit.core.driver.UpdateUserIdStatus
import com.drivequant.drivekit.core.driver.deletion.DeleteAccountStatus
import com.drivequant.drivekit.core.networking.DriveKitListener
import com.drivequant.drivekit.core.networking.RequestError

internal class DeleteAccountViewModel : ViewModel(), DriveKitListener {

    init {
        DriveKitListenerManager.addListener(this)
    }

    val accountDeletionError = MutableLiveData<Int>()
    val accountDeletionForbidden = MutableLiveData<Int>()
    val accountDeleted = MutableLiveData<Any>()

    fun deleteAccount(instantDeletion: Boolean = false) {
        DriveKit.deleteAccount(instantDeletion)
    }

    override fun onAccountDeleted(status: DeleteAccountStatus) {
        when (status) {
            DeleteAccountStatus.SUCCESS -> { accountDeleted.postValue(Any()) }
            DeleteAccountStatus.FAILED_TO_DELETE -> accountDeletionError.postValue(R.string.dk_common_error_message)
            DeleteAccountStatus.FORBIDDEN -> accountDeletionForbidden.postValue(R.string.account_deletion_error_forbidden)
        }
    }

    override fun onAuthenticationError(errorType: RequestError) {
        // do nothing
    }

    override fun onConnected() {
        // do nothing
    }

    override fun onDisconnected() {
        // do nothing
    }

    override fun userIdUpdateStatus(status: UpdateUserIdStatus, userId: String?) {
        // do nothing
    }

    override fun onCleared() {
        super.onCleared()
        DriveKitListenerManager.removeListener(this)
    }
}