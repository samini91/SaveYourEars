package com.example.liftingpump.liftingpump

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , IMainActivityView{

    var mainActivityViewPresenter : MainActivityViewPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewPresenter = MainActivityViewPresenter(this,this)

        mainActivityViewPresenter?.setUpDefaults()

        setUpButtons()
        setUpOptions()

        mainActivityViewPresenter?.setUpStateBroadCastReceiver()
        mainActivityViewPresenter?.startService()

    }

    private fun setUpButtons(){
        coolingDownButton.setOnClickListener { view ->
            mainActivityViewPresenter?.coolingDown()
        }
        heatingUpButton.setOnClickListener{ view ->
            mainActivityViewPresenter?.heatingUp()
        }
    }

    private fun setUpOptions(){

        coolingDownTimerValue.setOnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_DONE) {
                // Doing an extra check here just in case (already forcing the text input to be nums only)
                var numsOnly = true
                coolingDownTimerValue.text.forEach { c -> if (c > '9' || c < '0') numsOnly = false }
                if (numsOnly && coolingDownTimerValue.text.isNotEmpty())
                    mainActivityViewPresenter?.setCoolingDownTimer(Integer.parseInt(coolingDownTimerValue.text.toString()))
            }
            false
        }

        heatingUpTimerValue.setOnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_DONE) {
                // Doing an extra check here just in case (already forcing the text input to be nums only)
                var numsOnly = true
                heatingUpTimerValue.text.forEach { c -> if (c > '9' || c < '0') numsOnly = false }
                if (numsOnly && heatingUpTimerValue.text.isNotEmpty())
                    mainActivityViewPresenter?.setHeatingUpTimerVal(Integer.parseInt(heatingUpTimerValue.text.toString()))
            }
            false
        }

        minVolBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var currentVol = 0
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {currentVol = minVolBar.progress}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mainActivityViewPresenter?.setMinVolume(currentVol,seekBar?.progress)
            }
        })

        maxVolBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var currentVol = 0
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(seekBar : SeekBar?) { currentVol = maxVolBar.progress}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mainActivityViewPresenter?.setMaxVolume(currentVol,seekBar?.progress)
            }
        })

    }

    // Interface Functions

    override fun toggleState(state : String?)  {
        if(state == VolumeService.coolingDown){
            coolingDownButton.isEnabled = false
            coolingDownButton.alpha = .2f
            heatingUpButton.isEnabled = true
            heatingUpButton.alpha = 1f
        }
        else{
            coolingDownButton.isEnabled = true
            coolingDownButton.alpha = 1f
            heatingUpButton.isEnabled = false
            heatingUpButton.alpha = .2f
        }
    }

    override fun setCoolingDownTimer(coolingDownTimer: Int) {
        coolingDownTimerValue.setText(Integer.toString(coolingDownTimer))
    }

    override fun setHeatingUpTimer(heatingUpTimer: Int) {
        heatingUpTimerValue.setText(Integer.toString(heatingUpTimer))
    }

    override fun configureMinVolProgress(configureVal: Int) {
        minVolBar.max = configureVal
    }

    override fun configureMaxVolProgress(configureVal: Int) {
        maxVolBar.max = configureVal
    }

    override fun setMinVolProgress(minVolProgress: Int) {
        minVolBar.progress = minVolProgress
    }

    override fun setMaxVolProgress(maxVolProgress: Int) {
        maxVolBar.progress = maxVolProgress
    }
}

