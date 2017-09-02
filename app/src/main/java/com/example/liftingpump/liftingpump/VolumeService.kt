package com.example.liftingpump.liftingpump

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log


interface CustomEvent {

    fun CustomEvent()

}
class VolumeService : Service() {

    var customEvent : CustomEvent? = null


    override fun onBind(intent: Intent): IBinder? {

        return null

    }

    override fun onCreate() {
        super.onCreate()

        val mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        customEvent?.CustomEvent()

        val a = Thread(Runnable { Log.i("MyService","Post") })

        a.start()

        // something to do when the service is created


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }


    override fun bindService(service: Intent?, conn: ServiceConnection?, flags: Int): Boolean {
        return super.bindService(service, conn, flags)
    }


    override fun onDestroy() {
        super.onDestroy()
        // something to do when the service is destroyed
    }
}

