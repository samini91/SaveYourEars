package com.example.liftingpump.liftingpump

import android.content.BroadcastReceiver
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.media.AudioManager
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar
import android.widget.Toast


import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val mainActivity = this
    var optionModel : OptionModel? = null
    var stateBroadCastReciever : BroadcastReceiver? = null
    var state : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        optionModel = OptionModel(getSharedPreferences(OptionModel.default, Context.MODE_PRIVATE))

        timerValueEditText.setText(optionModel?.timerVal.toString())

        minVolBar.progress = optionModel?.minVolVal!!
        maxVolBar.progress = optionModel?.maxVolVal!!


        button.setOnClickListener { view ->
            onTimerStart()
        }

        setUpOptions()
        setUpStateBroadCastReceiver()

    }

    private fun setUpOptions(){

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        timerValueEditText.setOnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_DONE) {
                //if(timerValueEditText.text.contains) we can do a check here to make sure we are only getting letters
                if(timerValueEditText.text.isNotEmpty())
                    onTimerValueChange(Integer.parseInt(timerValueEditText.text.toString()))
            }
            false
        }

        minVolBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        minVolBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var currentVol = 0
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {currentVol = minVolBar.progress}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(!onMinVolumeChange(seekBar?.progress))
                    minVolBar.progress = currentVol
            }
        })


    maxVolBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    maxVolBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
        var currentVol = 0
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
        override fun onStartTrackingTouch(seekBar : SeekBar?) { currentVol = maxVolBar.progress}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if(!onMaxVolumeChange(seekBar?.progress))
                maxVolBar.progress = currentVol
        }
    })

    }

    private fun setUpStateBroadCastReceiver(){

        stateBroadCastReciever = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                onStateWasChanged(intent)
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(stateBroadCastReciever,
                IntentFilter(VolumeService.ACTION_CURRENT_STATE));
    }


    // Controller Functions

    fun onStateWasChanged(intent: Intent?){
        state = intent?.getStringExtra(VolumeService.stateMap)
    }

    fun onTimerStart() {

        mainActivity.startService(Intent(this, VolumeService::class.java))

    }
    fun onTimerValueChange(timerValue : Int?){
        if(timerValue !=null)
            optionModel?.timerVal = timerValue
        else
            optionModel?.timerVal = 30
    }

    fun onMinVolumeChange(minVolume : Int?) : Boolean{
        if (minVolume != null && minVolume > optionModel?.maxVolVal!!){
            return false
        }
        if(minVolume !=null)
            optionModel?.minVolVal = minVolume
        else
            optionModel?.minVolVal = 5
        return true
    }

    fun onMaxVolumeChange(maxVolume : Int?) : Boolean{
        if (maxVolume != null && maxVolume < optionModel?.minVolVal!!){
            return false
        }
                if(maxVolume !=null)
                    optionModel?.maxVolVal = maxVolume
                else
                    optionModel?.maxVolVal = 5
        return true
    }
}

