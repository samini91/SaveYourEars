package com.example.liftingpump.liftingpump

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.util.Log
import kotlin.jvm.javaClass
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_MEDIA_PLAY
import android.support.v4.content.ContextCompat.startActivity
import android.provider.AlarmClock
import android.support.v4.media.session.MediaButtonReceiver


/**
 * Created by Mugen on 9/1/2017.
 */
class OnEventReceiver : BroadcastReceiver() {



    override fun onReceive(context: Context?, intent: Intent?) {


      //  if(intent?.action?.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED) != null && intent?.action?.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED) == true) {
            Log.i("MyStateChange", "MyStateChange")


           // context?.packageManager?.queryBroadcastReceivers(intent,0)
            
            //val mediaPlayer  = MediaPlayer()

            val mAudioManager = context?.getSystemService(AUDIO_SERVICE) as AudioManager

            Log.i("Media Player is playing",mAudioManager.isMusicActive.toString())
        Log.i("Is order broadcast",isOrderedBroadcast.toString())

            val event = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY)
            mAudioManager.dispatchMediaKeyEvent(event)


        /*    val timerIntent = Intent(AlarmClock.ACTION_SET_TIMER)
                    .putExtra(AlarmClock.EXTRA_MESSAGE, "You can do it")
                    .putExtra(AlarmClock.EXTRA_LENGTH, 3)
                    .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //if (intent.resolveActivity(getPackageManager()) != null) {
                context?.startActivity(timerIntent)
            //}
*/



            //abortBroadcast()

        //}
       // val volumeServiceIntent = Intent(context,VolumeService::class.java)
        //context?.startService(volumeServiceIntent)
    }




}