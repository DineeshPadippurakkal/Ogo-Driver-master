package webtech.com.courierdriver.customview

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import webtech.com.courierdriver.R
import webtech.com.courierdriver.utilities.LogUtils

/*
Created by ​
Hannure Abdulrahim


on 4/21/2019.
 */

object UpdateMeeDialog {


      var am: ActivityManager?=null
      var rootName: TextView? = null
     var context: Context?=null
     var dialog: Dialog?=null
     var key1: String? = null
     var schoolId: String? = null


    fun showDialogAddRoute(activity: Activity, packageName: String,oldVersionCode: String,remoteVersionCode: String, newFeaturesMessage: String) {
        context = activity
        dialog = Dialog(context!!)

        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
        dialog!!.setContentView(R.layout.dialog_update)


        am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val cancelDialogue = dialog!!.findViewById<View>(R.id.buttonUpdate) as Button

        val newFeaturesTV = dialog!!.findViewById<View>(R.id.textMessageNewFeatures) as TextView
        val versionCodeTV = dialog!!.findViewById<View>(R.id.textMessageDesc) as TextView


        versionCodeTV.setText("Version ( "+oldVersionCode+" ) "+context!!.getString(R.string.youAreNotUpdatedMessage3))
        newFeaturesTV.setText(newFeaturesMessage)


        LogUtils.error(LogUtils.TAG + "package name : ", packageName)

        cancelDialogue.setOnClickListener {


//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName&hl=en")
//            context!!.startActivity(intent)

            openAppInPlayStore(activity)

        }


        if(dialog!!.isShowing)
        {
            dialog!!.dismiss()
            dialog!!.show()
        }else
        {
            dialog!!.show()

        }

    }


    fun openAppInPlayStore(activity: Activity) {

        ///dismiss dialogue here

        if(dialog!!.isShowing) {
            dialog!!.dismiss()
        }

        context = activity
        val uri = Uri.parse("market://details?id=" + context!!.packageName)
        val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri)

        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        flags = if (Build.VERSION.SDK_INT >= 21) {
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            flags or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        goToMarketIntent.addFlags(flags)

        try {
            startActivity(context!!, goToMarketIntent, null)
        } catch (e: Throwable) {

            val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context!!.packageName))

            intent.addFlags(flags)
            startActivity(context!!, intent, null)
        }
    }




}