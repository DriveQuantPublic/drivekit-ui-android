package com.drivequant.drivekit.permissionsutils.permissions.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.setActivityTitle
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.extension.getSerializableExtraCompat
import com.drivequant.drivekit.permissionsutils.PermissionsUtilsUI
import com.drivequant.drivekit.permissionsutils.R
import com.drivequant.drivekit.permissionsutils.permissions.model.PermissionView

open class BasePermissionActivity : RequestPermissionActivity() {

    private lateinit var titleResId: String

    companion object {
        const val PERMISSION_VIEWS_LIST_EXTRA = "permission-views-list-extra"
    }

    private var nextPermissionViews = ArrayList<PermissionView>()

    protected fun setToolbar(titleResId: String) {
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        this.titleResId = titleResId
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DriveKitUI.analyticsListener?.trackScreen(DKResource.convertToString(this, "dk_tag_permissions_onboarding"), javaClass.simpleName)

        val permissionViewsListExtra = intent.getSerializableExtraCompat(PERMISSION_VIEWS_LIST_EXTRA, ArrayList::class.java)
        permissionViewsListExtra?.let {
            nextPermissionViews =
                intent.getSerializableExtraCompat(PERMISSION_VIEWS_LIST_EXTRA, ArrayList::class.java) as ArrayList<PermissionView>
        }
    }

    protected fun forward() {
        finish()
        if (nextPermissionViews.isNotEmpty()) {
            nextPermissionViews.remove(nextPermissionViews.first())
        }
        if (nextPermissionViews.isEmpty()) {
            PermissionsUtilsUI.permissionViewListener?.onFinish()
        } else {
            nextPermissionViews.first().launchActivity(this, nextPermissionViews)
        }
    }

    protected fun skip(permissionView: PermissionView) {
        permissionView.ignore()
        forward()
    }

    override fun onResume() {
        super.onResume()
        if (this::titleResId.isInitialized) {
            setActivityTitle(DKResource.convertToString(this, titleResId))
        }
    }
}
