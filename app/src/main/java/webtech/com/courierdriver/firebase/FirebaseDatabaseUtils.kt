package webtech.com.courierdriver.firebase

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import webtech.com.courierdriver.BuildConfig
import webtech.com.courierdriver.constants.ApiConstant

/*
Created by ​
Hannure Abdulrahim


on 7/22/2019.
 */
object  FirebaseDatabaseUtils
{
      var  mFirebaseInstance: FirebaseDatabase? = null
      var   mFirebaseDatabase: DatabaseReference? = null

     fun firebaseDatabaseRefrence(context: Context, className: String): DatabaseReference?
    {


        //dont sysn here
        //FirebaseDatabase.getInstance().getReference().keepSynced(true)

        if(ApiConstant.REAL_TIME_FIRBASE_CONNECT == ApiConstant.REAL_TIME_FIREBASE_DB_DEFAULT_ONE )
        {
            //default db from JSON (To connect to your default fire base real time databases)
            mFirebaseInstance = FirebaseDatabase.getInstance()
        }else{

            mFirebaseInstance = FirebaseDatabase.getInstance("https://courierdriver-6b3c1-7e91d.firebaseio.com/")

        }

        mFirebaseInstance!!.getReference().keepSynced(true)

        FireBaseConnectedRef.getInstance(context).fireBaseConnected(className,mFirebaseInstance!!)


        // get reference to 'Drivers' node
        // mFirebaseDatabase = mFirebaseInstance!!.getReference("drivers")
        mFirebaseDatabase = mFirebaseInstance!!.getReference(BuildConfig.FIREBASE_SECRATE_NODE_FINAL)
        if(mFirebaseDatabase != null)
            return mFirebaseDatabase
        else
            return null


    }
}
