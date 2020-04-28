package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.os.Bundle
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

open class BasePermissionActivity : RequestPermissionActivity() {

    companion object {
        const val PERMISSION_VIEWS_LIST_EXTRA = "permission-views-list-extra"
    }

    private var nextPermissionViews = ArrayList<PermissionView>()

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionViewsListExtra = intent.getSerializableExtra(PERMISSION_VIEWS_LIST_EXTRA)
        permissionViewsListExtra?.let {
            nextPermissionViews =
                intent.getSerializableExtra(PERMISSION_VIEWS_LIST_EXTRA) as ArrayList<PermissionView>
        }
    }

    protected fun next() {
        nextPermissionViews.remove(nextPermissionViews.first())
        if (nextPermissionViews.isEmpty()) {
            PermissionsUtilsUI.permissionViewListener?.onFinish()
        } else {
            nextPermissionViews.first().launchActivity(this, nextPermissionViews)
        }
    }
}
