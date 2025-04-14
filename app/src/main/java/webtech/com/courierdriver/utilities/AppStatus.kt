package webtech.com.courierdriver.utilities

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager

/**
 * Created by Abdulrahim Hannure on 4/17/2017.
 */
object AppStatus {
    private val instance: AppStatus = AppStatus



    var context: Context? = null
    fun getInstance(ctx: Context): AppStatus {
        context = ctx.applicationContext
        return instance
    }

    ////////////////////////S
    //This method is written to get to to know when application is in background
    fun isApplicationBroughtToBackground(activity: Activity): Boolean {
        val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return if (activityManager != null) {


            val tasks = activityManager.getRunningTasks(1)

            // Check the top Activity against the list of Activities contained in the Application's package.
            if (!tasks.isEmpty()) {
                val topActivity = tasks[0].topActivity
                try {
                    val pi = activity.packageManager.getPackageInfo(activity.packageName, PackageManager.GET_ACTIVITIES)
                    for (activityInfo in pi.activities!!) {

                        if (topActivity!!.className == activityInfo.name) {
                            return false
                        }
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    return false // Never happens.
                }
            }
            true
        } else {
            ///// activityManager is null
            false
        }
    }

    //////////////////////E
    object AppVisibilityHelper {
        fun isForeground(context: Context, packageName: String): Boolean {


            //final String packageName = "com.acb.android";
            val manager = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            val runningTaskInfo = manager.getRunningTasks(1)
            val componentInfo = runningTaskInfo[0].topActivity!!
            return componentInfo.packageName == packageName
        }
    }



    /// check where service is running in Foreground
    fun isServiceRunningInForeground(context:Context, serviceClass:Class<*>):Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName() == service.service.getClassName())
            {
                if (service.foreground)
                {
                    return true
                }
            }
        }
        return false
    }


}