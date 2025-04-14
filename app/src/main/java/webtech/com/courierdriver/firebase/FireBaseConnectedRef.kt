package webtech.com.courierdriver.firebase

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import webtech.com.courierdriver.utilities.LogUtils


/*
Created by ​
Hannure Abdulrahim


on 7/21/2019.
 */
class  FireBaseConnectedRef {


    companion object {

        private val instance = FireBaseConnectedRef()
        internal lateinit var context: Context

        fun getInstance(ctx: Context): FireBaseConnectedRef {
            context = ctx.applicationContext
            return instance
        }
    }

    fun  fireBaseConnected (pushFrom:String ,mFirebaseInstance: FirebaseDatabase)
    {


        val connectedRef = mFirebaseInstance.getReference(".info/connected")



        connectedRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {

                    LogUtils.error(LogUtils.TAG, "FIRE_BASE : connected from " + pushFrom)
                    //Toast.makeText(context, "FIRE_BASE : connected from " + pushFrom, Toast.LENGTH_LONG).show()
                } else {

                    LogUtils.error(LogUtils.TAG, "FIRE_BASE : not connected from "+ pushFrom)
                    //Toast.makeText(context, "FIRE_BASE : not connected from "+ pushFrom, Toast.LENGTH_LONG).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {

                LogUtils.error(LogUtils.TAG, "FIRE_BASE : Listener was cancelled from "+pushFrom)
                //Toast.makeText(context, "FIRE_BASE : Listener was cancelled from "+pushFrom, Toast.LENGTH_LONG).show()
            }
        })

    }
}