package webtech.com.courierdriver.activity

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import webtech.com.courierdriver.databinding.ActivityLocationDisclosureBinding
import webtech.com.courierdriver.utilities.GPSUtility

class LocationDisclosureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationDisclosureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationDisclosureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // method call to initialize the views
        initViews()

        binding.tvNoThanks.setOnClickListener {
            finish()
        }

        binding.tvLocationTurnOn.setOnClickListener {
            /// here ask ofr permission
            if (GPSUtility.askForPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    GPSUtility.LOCATION,
                    this@LocationDisclosureActivity
                )
            ) {
                gotoLoginScreen()

            } else {
                //Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }


        }


    }


    /**
     * method to initialize the views
     */
    private fun initViews() {

    }


    private fun gotoLoginScreen() {
        val i = Intent(this@LocationDisclosureActivity, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        // close this activity
        finish()

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GPSUtility.LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Location permission granted!", Toast.LENGTH_SHORT).show()
                gotoLoginScreen()

            }
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()

    }

    public override fun onResume() {
        super.onResume()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    public override fun onPause() {

        super.onPause()
        ///these below two line will disable landscape mode ,check onResume() also
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }
}