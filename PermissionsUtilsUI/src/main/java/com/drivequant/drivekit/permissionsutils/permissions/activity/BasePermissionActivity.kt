package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

open class BasePermissionActivity : RequestPermissionActivity() {

    companion object {
        const val PERMISSION_VIEWS_LIST_EXTRA = "permission-views-list-extra"
    }

    private var nextPermissionViews = ArrayList<PermissionView>()

    protected fun setToolbar(titleResId: String) {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        this.title = DKResource.convertToString(this, titleResId)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(this, "dk_tag_permissions_onboarding"), javaClass.simpleName)

        val permissionViewsListExtra = intent.getSerializableExtra(PERMISSION_VIEWS_LIST_EXTRA)
        permissionViewsListExtra?.let {
            nextPermissionViews =
                intent.getSerializableExtra(PERMISSION_VIEWS_LIST_EXTRA) as ArrayList<PermissionView>
        }
    }

    protected fun forward(){
        finish()
        next()
    }

    protected fun next() {
        if (nextPermissionViews.isNotEmpty()){
            nextPermissionViews.remove(nextPermissionViews.first())
        }
        if (nextPermissionViews.isEmpty()) {
            PermissionsUtilsUI.permissionViewListener?.onFinish()
        } else {
            nextPermissionViews.first().launchActivity(this, nextPermissionViews)
        }
    }
}
