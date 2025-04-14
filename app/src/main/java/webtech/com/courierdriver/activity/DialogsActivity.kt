package webtech.com.courierdriver.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import webtech.com.courierdriver.R

class DialogsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialogs)

        val msgTitle = intent.getStringExtra("title")
        val msgBody = intent.getStringExtra("body")
        if (msgTitle != null && msgBody != null) {
            showDialog(msgTitle, msgBody)
        }

    }

    private fun showDialog(msgTitle: String, msgBody: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(msgTitle)
        alertDialogBuilder.setMessage(msgBody)

        alertDialogBuilder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            finish()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}