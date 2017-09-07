package com.example.liftingpump.liftingpump

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import android.content.ComponentName
import android.os.Binder

class VolumeService : Service() {

    companion object {
        val ACTION_CURRENT_STATE = VolumeService::class.java.toString() + "BroadCast"

        val heatingUp = "HeatingUp"
        val coolingDown= "CoolingDown"
        val stateMap = "state"

        val validStates = arrayOf(heatingUp, coolingDown)
    }

    val timerHandler = Handler()
    var timerRunnable : Runnable? = null
    var optionModel : OptionModel? = null

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        optionModel = OptionModel(getSharedPreferences(OptionModel.default, Context.MODE_PRIVATE))!!
        timerHandler.removeCallbacks(timerRunnable)

        val audioService = getSystemService(AUDIO_SERVICE) as AudioManager
        val audioVolume = audioService.getStreamVolume(AudioManager.STREAM_MUSIC)
        val minVol = optionModel?.minVolVal
        val maxVol = optionModel?.maxVolVal

        optionModel?.toggleState(intent?.getStringExtra(stateMap))

        val timerValue =
                if(optionModel?.stateVal == heatingUp)
                    optionModel?.heatingUpTimerVal
                else
                    optionModel?.coolingDownTimerVal

        val startTime = System.currentTimeMillis()

        var delta =
                if (optionModel?.stateVal == heatingUp)
                    maxVol!! - audioVolume
                else
                    audioVolume - minVol!!

        val interval =
                if (delta > 0)
                    (timerValue!!.toLong() * 1000) / delta
                else
                    0

        sendBroadCastToActivity()
        updateWidget()

        timerRunnable = object : Runnable {

            override fun run() {

                val audioVolume = audioService.getStreamVolume(AudioManager.STREAM_MUSIC)

                val millis = System.currentTimeMillis() - startTime
                var seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60

                if ((audioVolume >= maxVol!! && optionModel?.stateVal == heatingUp) || (audioVolume <= minVol!! && optionModel?.stateVal == coolingDown)) {
                    timerHandler.removeCallbacks(this)
                    return
                }

                if(optionModel?.stateVal == heatingUp)
                    audioService.setStreamVolume(AudioManager.STREAM_MUSIC, audioVolume + 1, 0)
                else
                    audioService.setStreamVolume(AudioManager.STREAM_MUSIC, audioVolume - 1, 0)

                timerHandler.postDelayed(this, interval)
            }
        }
        if(interval != 0.toLong())
            timerHandler.postDelayed(timerRunnable, interval)

        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun bindService(service: Intent?, conn: ServiceConnection?, flags: Int): Boolean {
        return super.bindService(service, conn, flags)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacks(timerRunnable)
    }

    fun sendBroadCastToActivity(){
        val intent = Intent(ACTION_CURRENT_STATE)
        intent.putExtra(stateMap,optionModel?.stateVal)

        LocalBroadcastManager.getInstance(application).sendBroadcast(intent)
        Toast.makeText(this,optionModel?.stateVal,Toast.LENGTH_SHORT).show()
    }

    fun updateWidget(){

        val stateWidget = ComponentName(this, StateWidget::class.java!!)
        val appWidgetManager = AppWidgetManager.getInstance(this)

        val intent = Intent(this, StateWidget::class.java)
        intent.action = "android.appwidget.action.APPWIDGET_UPDATE"

        val appWidgetIds = appWidgetManager.getAppWidgetIds(stateWidget)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        intent.putExtra(StateWidget.sendingProcessType,StateWidget.doNotRunService)
        sendBroadcast(intent)

    }
}

