package com.example.liftingpump.liftingpump

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.support.v4.content.LocalBroadcastManager


class MainActivityViewPresenter (context: Context,iMainActivityView: IMainActivityView){

    var iMainActivityView : IMainActivityView? = null
    var context : Context? = null
    val optionModel = OptionModel(context.getSharedPreferences(OptionModel.default,Context.MODE_PRIVATE))
    var stateBroadCastReceiver : BroadcastReceiver? = null

    init {
        this.iMainActivityView = iMainActivityView
        this.context = context
}
    fun coolingDown(){

        val intent = Intent(context,VolumeService::class.java)
        intent.putExtra(VolumeService.stateMap,VolumeService.coolingDown)
        context?.startService(intent)

        iMainActivityView?.toggleState(optionModel.stateVal)
}

    fun heatingUp(){
        val intent = Intent(context,VolumeService::class.java)
        intent.putExtra(VolumeService.stateMap,VolumeService.heatingUp)
        context?.startService(intent)

        iMainActivityView?.toggleState(optionModel.stateVal)

    }

    fun setUpDefaults(){

        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        iMainActivityView?.toggleState(optionModel.stateVal)

        iMainActivityView?.setCoolingDownTimer(optionModel.coolingDownTimerVal)
        iMainActivityView?.setHeatingUpTimer(optionModel.heatingUpTimerVal)

        iMainActivityView?.setMinVolProgress(optionModel.minVolVal)
        iMainActivityView?.setMaxVolProgress(optionModel.maxVolVal)

        iMainActivityView?.configureMinVolProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        iMainActivityView?.configureMaxVolProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))

    }

    fun setCoolingDownTimer(coolDownTimerVal: Int?){
        if(coolDownTimerVal !=null)
            optionModel.coolingDownTimerVal = coolDownTimerVal
    }

    fun setHeatingUpTimerVal(heatingUpTimerVal: Int?){
        if(heatingUpTimerVal !=null)
            optionModel.heatingUpTimerVal= heatingUpTimerVal
    }

    fun setMinVolume(before : Int? , after : Int?){

        if(before == null || after == null)
            return

        if ( after > optionModel?.maxVolVal!!){
            iMainActivityView?.setMinVolProgress(before)
        }
        else
            optionModel.minVolVal = after
    }

    fun setMaxVolume(before : Int? , after : Int?){
        if(before == null|| after == null)
            return

        if(after < optionModel?.minVolVal)
            iMainActivityView?.setMaxVolProgress(before)
        else
            optionModel.maxVolVal = after
    }

    fun setUpStateBroadCastReceiver(){

        stateBroadCastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                iMainActivityView?.toggleState(intent?.getStringExtra(VolumeService.stateMap))
            }
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(stateBroadCastReceiver,
                IntentFilter(VolumeService.ACTION_CURRENT_STATE))
    }
}