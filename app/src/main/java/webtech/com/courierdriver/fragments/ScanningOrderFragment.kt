package webtech.com.courierdriver.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.R
import webtech.com.courierdriver.databinding.FragmentScanningOrderBinding
import webtech.com.courierdriver.databinding.FragmentWelcomeBinding
import webtech.com.courierdriver.events.ScannedOrderEvent
import java.io.IOException

class ScanningOrderFragment : Fragment() {

    //    private lateinit var binding :FragmentScanningOrderBinding
    var mActivity: Activity? = null
    private var driverUserId: String? = null
    private val REQUEST_CAMERA_PERMISSION = 201
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    private lateinit var binding: FragmentScanningOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            // driverId = arguments!!.getString(DRIVER_ID)
            driverUserId = requireArguments().getString(DRIVER_EMAIL_ID)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanning_order, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentScanningOrderBinding.bind(view)
        binding.btnAction.setOnClickListener {

            // post order ID here
            binding.txtBarcodeValue.text.let {
                EventBus.getDefault().post(ScannedOrderEvent(it.toString()))
            }

        }


    }


    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ///check on detach also removed title
        (mActivity as AppCompatActivity).supportActionBar!!.setTitle(R.string.scanning_orders)
        initialiseDetectorsAndSources()

    }

    override fun onPause() {
        super.onPause()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        /// for new version > M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Code here
            //first convert context into Activity ,since context is also Activity
            val activity = if (context is Activity) context else null
            mActivity = activity
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        /// for old  version < M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Code here
            mActivity = activity
        }
    }

    override fun onDetach() {
        super.onDetach()
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle("")

    }


    companion object {
        private val DRIVER_EMAIL_ID = "driver_email_id"

        fun newInstance(driverId: String): ScanningOrderFragment {
            val fragment = ScanningOrderFragment()
            val args = Bundle()
            args.putString(DRIVER_EMAIL_ID, driverId)
            fragment.arguments = args
            return fragment
        }
    }


    private fun initialiseDetectorsAndSources() {
        Toast.makeText(mActivity, "Barcode scanner started", Toast.LENGTH_SHORT).show()
        barcodeDetector = BarcodeDetector.Builder(mActivity)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(mActivity, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        binding.surfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            mActivity!!,
                            Manifest.permission.CAMERA
                        ) === PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource!!.start(binding.surfaceView.getHolder())
                    } else {
                        ActivityCompat.requestPermissions(
                            mActivity!!,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })
        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(
                    mActivity,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    binding.txtBarcodeValue.post(Runnable {

                        binding.btnAction?.let {
                            binding.btnAction.visibility = View.VISIBLE
                            binding.btnAction.setText("CLICK TO ADD ORDER")
                        }

                        binding.txtBarcodeValue?.let {
                            binding.txtBarcodeValue.setText(barcodes.valueAt(0).displayValue)
                        }
                        //txtBarcodeTitle.setText("# ORDER ID:")


                    })
                }
            }
        })
    }

}