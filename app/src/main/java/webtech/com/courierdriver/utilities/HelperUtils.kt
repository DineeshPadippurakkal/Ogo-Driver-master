package webtech.com.courierdriver.utilities

import android.app.Activity
import android.content.Context
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat


import java.util.*

/*
Created by
Hannure Abdulrahim


on 10/20/2020.
 */


////this is wriiten for helper methods
object HelperUtils  {


    private var mediaPlayer: MediaPlayer? = null
    private var currentMediaPlayerRes: Int = 0

    fun playNotificationSound(resId: Int, releaseAfter: Boolean, context: Context) {
     /*   if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer = null
            currentMediaPlayerRes = 0
        }*/

        if (currentMediaPlayerRes != resId || mediaPlayer == null) {

            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying)
                    mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                mediaPlayer!!.release()
            }else{
                mediaPlayer = MediaPlayer.create(context, resId)
            }
            currentMediaPlayerRes = resId


            if (releaseAfter) {
                mediaPlayer!!.setOnCompletionListener {

                    ///if you want to stop after completion
                    mediaPlayer!!.release()
                    mediaPlayer = null


                    // context as Activity
                    //if(!context.isFinishing()  && !context.isDestroyed())

                    //if you want to start again after completion
                    //mediaPlayer!!.start()

                }

            }

        }

        mediaPlayer!!.start()
    }


}