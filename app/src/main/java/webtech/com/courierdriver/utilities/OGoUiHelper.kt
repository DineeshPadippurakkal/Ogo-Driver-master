package webtech.com.courierdriver.utilities

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/*
Created by ​
Hannure Abdulrahim


on 7/22/2019.
 */
class OGoUiHelper
    {


    fun isPlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return ConnectionResult.SUCCESS == status
    }



}
