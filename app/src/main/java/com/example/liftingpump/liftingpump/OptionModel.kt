package com.example.liftingpump.liftingpump

import android.content.SharedPreferences

class OptionModel(private val sharedPreferences: SharedPreferences){

    companion object {
        val minVolumeMap = "minVolume"
        val maxVolumeMap = "maxVolume"
        val timerMap = "timer"
        val stateMap = "state"
        val default = "liftingPumpDefault"

    }

    var minVolVal: Int
        get() = sharedPreferences.getInt(minVolumeMap,0)
        set(value){sharedPreferences.edit().putInt(minVolumeMap, value).commit()}

    var maxVolVal: Int
        get() = sharedPreferences.getInt(maxVolumeMap,0)
        set(value){sharedPreferences.edit().putInt(maxVolumeMap, value).commit()}

    var timerVal: Int
        get() = sharedPreferences.getInt(timerMap,30)
        set(value){sharedPreferences.edit().putInt(timerMap, value).commit()}

    var stateVal : String
        get() = sharedPreferences.getString(stateMap, VolumeService.coolingDown)
        set(value) {sharedPreferences.edit().putString(stateMap, value).commit()}


    fun toggleState(changeState : String? = null ){
        if(changeState == null) {
            stateVal = if (stateVal == VolumeService.heatingUp)
                VolumeService.coolingDown
            else
                VolumeService.heatingUp
        }
        else {
            var validState = false
            VolumeService.validStates.forEach { if (it == changeState) {validState=true} }

            if(validState)
                stateVal = changeState
        }
    }
}




