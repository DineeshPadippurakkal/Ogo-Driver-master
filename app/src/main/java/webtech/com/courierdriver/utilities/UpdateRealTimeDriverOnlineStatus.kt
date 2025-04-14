package webtech.com.courierdriver.utilities

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import webtech.com.courierdriver.constants.OGoConstant

object UpdateRealTimeDriverOnlineStatus {
    fun updateDriverOnlineStatus(context: Context, preferenceHelper: String?) {
        // Example of how you might use preferenceHelper values
        val preferenceHelper = PreferenceHelper(context)
        preferenceHelper.driverOnlineStatus = preferenceHelper.toString()

        // If updating real-time database like Firebase
        val databaseRef = FirebaseDatabase.getInstance().reference
//        val driverId = preferenceHelper.driverId // Assuming there's a driver ID stored in preferences
//
//        if (driverId != null) {
//            databaseRef.child("drivers").child(driverId).child("onlineStatus")
//                .setValue(driverStatus)
//                .addOnSuccessListener {
//                    Log.d("UpdateDriverStatus", "Successfully updated driver status in database.")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("UpdateDriverStatus", "Failed to update driver status", e)
//                }
//        }
    }
}