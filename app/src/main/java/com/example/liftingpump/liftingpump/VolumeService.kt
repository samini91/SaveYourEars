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
import android.widget.RemoteViews



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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onCreate() {
        super.onCreate()

        //val a = Thread(Runnable { Log.i("MyService","Post") })

        //a.start()
        // something to do when the service is created
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        optionModel = OptionModel(getSharedPreferences(OptionModel.default, Context.MODE_PRIVATE))!!
        //val mediaBrowserServiceCompat = MediaBrowserServiceCompat
        timerHandler.removeCallbacks(timerRunnable)

        val audioService = getSystemService(AUDIO_SERVICE) as AudioManager
        val minVol = optionModel?.minVolVal
        val maxVol = audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val timerValue = optionModel?.timerVal
        val startTime = System.currentTimeMillis()

        var delta = maxVol - minVol!!

        val interval = if(delta !=0) {(timerValue!!.toLong() *1000) / delta} else { 0 }

        optionModel?.toggleState()

        sendBroadCastToActivity()
        updateWidget()

        timerRunnable = object : Runnable {

            override fun run() {

                val audioVolume = audioService.getStreamVolume(AudioManager.STREAM_MUSIC)
                val millis = System.currentTimeMillis() - startTime
                var seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60

                if(optionModel?.stateVal == heatingUp)
                    audioService.setStreamVolume(AudioManager.STREAM_MUSIC, audioVolume + 1, 0)
                else
                    audioService.setStreamVolume(AudioManager.STREAM_MUSIC, audioVolume - 1, 0)

                if(seconds >= timerValue!!
                        || (audioVolume >= maxVol && optionModel?.stateVal == heatingUp) || (audioVolume <= minVol && optionModel?.stateVal == coolingDown)) {
                    timerHandler.removeCallbacks(this)
                    return
                }

                timerHandler.postDelayed(this, interval)
            }
        }

        if(interval != 0.toLong())
            timerHandler.postDelayed(timerRunnable, interval)

        return super.onStartCommand(intent, flags, startId)
    }


    override fun bindService(service: Intent?, conn: ServiceConnection?, flags: Int): Boolean {
        return super.bindService(service, conn, flags)
    }


    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacks(timerRunnable)
        optionModel?.stateVal = heatingUp
    }

    fun sendBroadCastToActivity(){
        val intent = Intent(ACTION_CURRENT_STATE)
        intent.putExtra(stateMap,optionModel?.stateVal)

        LocalBroadcastManager.getInstance(application).sendBroadcast(intent)
        Toast.makeText(this,optionModel?.stateVal,Toast.LENGTH_SHORT).show()
    }

    fun updateWidget(){

//        val remoteView = RemoteViews(packageName, R.layout.state_widget)
        val stateWidget = ComponentName(this, StateWidget::class.java!!)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        //appWidgetManager.updateAppWidget(stateWidget, remoteView)

        val intent = Intent(this, StateWidget::class.java)
        intent.action = "android.appwidget.action.APPWIDGET_UPDATE"

        val appWidgetIds = appWidgetManager.getAppWidgetIds(stateWidget)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        intent.putExtra(StateWidget.sendingProcessType,StateWidget.doNotRunService)
        sendBroadcast(intent)


    }
}

