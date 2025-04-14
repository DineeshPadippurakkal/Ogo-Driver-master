package webtech.com.courierdriver.utilities

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseDatabaseUtils {

    // Method to get Firebase Database reference based on the class name
    fun firebaseDatabaseRefrence(context: Context, className: String): DatabaseReference {
        // Getting the Firebase Database instance
        val database = FirebaseDatabase.getInstance()

        // You can store data in a specific reference based on the class name
        return database.getReference(className) // Using the className as part of the path
    }
}