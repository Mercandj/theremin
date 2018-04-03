package com.mercandalli.android.apps.theremin.application

import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Static methods related to the applications.
 */
object AppUtils {

    fun launchApp(context: Context, packageName: String, activityInfoName: String) {
        val componentName = ComponentName(packageName, activityInfoName)
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.component = componentName
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        context.startActivity(intent)
    }

} // Non-instantiable.
