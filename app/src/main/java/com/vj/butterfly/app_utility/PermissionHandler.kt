package com.vj.butterfly.app_utility

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler {
    //val SMS_PERMISSION = 112

    val permissionSendSMS = Manifest.permission.SEND_SMS

    fun requestSMSPermission(activity: Activity){
            ActivityCompat.requestPermissions(activity, arrayOf(permissionSendSMS),
                EnumConstants.SMS_PERMISSION.ordinal
            )
    }

    fun permissionGranted(permission: String, activityContext: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
                activityContext, permission) == PackageManager.PERMISSION_GRANTED
    }
}