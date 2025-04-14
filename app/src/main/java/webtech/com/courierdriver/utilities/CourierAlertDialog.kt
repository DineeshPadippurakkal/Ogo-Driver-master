package webtech.com.courierdriver.utilities

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView

import webtech.com.courierdriver.R


/**
 * Created by Abdulrahim on 06/Sept/2018.
 */

class CourierAlertDialog(context: Context, private val title: String, private val message: String, private val oklabel: String, private val cancelLabel: String?) : Dialog(context) {

    private var mListener: OnActionListener? = null
    private var btnOk: Button? = null
    private var btnCancel: Button? = null
    private var tvTitle: TextView? = null
    private var tvMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (window != null)
            window!!.attributes.windowAnimations = R.style.AlertMessageDlgTheme
        setContentView(R.layout.makan_alert_dialog)

        btnOk = findViewById<View>(R.id.ok) as Button
        btnCancel = findViewById<View>(R.id.cancel) as Button
        tvTitle = findViewById<View>(R.id.title) as TextView
        tvMessage = findViewById<View>(R.id.message) as TextView


        btnOk!!.text = oklabel
        tvMessage!!.text = message
        tvTitle!!.text = title
        if (cancelLabel != null) {
            btnCancel!!.text = cancelLabel
            btnCancel!!.visibility = View.VISIBLE
        } else {
            btnCancel!!.visibility = View.GONE
        }

        btnOk!!.setOnClickListener {
            if (mListener != null) {
                mListener!!.onOK()
            }
        }
        btnCancel!!.setOnClickListener {
            if (mListener != null) {
                mListener!!.onCancel()
            }
        }
    }


    interface OnActionListener {
        fun onOK()
        fun onCancel()
    }

    fun setOnActionListener(listener: OnActionListener) {
        this.mListener = listener
    }
}
