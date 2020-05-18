package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

open class BasePermissionActivity : RequestPermissionActivity() {

    companion object {
        const val PERMISSION_VIEWS_LIST_EXTRA = "permission-views-list-extra"
    }

    private var nextPermissionViews = ArrayList<PermissionView>()

    protected fun setToolbar() {
       val titleResId = when (this) {
           is ActivityRecognitionPermissionActivity ->  "dk_perm_utils_permissions_phone_settings_activity_title"
           is BackgroundTaskPermissionActivity -> "dk_perm_utils_permissions_phone_settings_background_title"
           is LocationPermissionActivity ->   "dk_perm_utils_permissions_location_title"
           else -> throw IllegalArgumentException("Instance must extends BaseActivity")
        }

        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        this.title = DKResource.convertToString(this, titleResId)
    }

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
