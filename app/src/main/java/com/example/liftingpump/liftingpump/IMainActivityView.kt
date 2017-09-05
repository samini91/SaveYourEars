package com.example.liftingpump.liftingpump

interface IMainActivityView{

    fun toggleState(state:String?)

    fun setCoolingDownTimer(coolingDownTimer: Int)
    fun setHeatingUpTimer(heatingUpTimer: Int)

    fun setMinVolProgress(minVolProgress: Int)
    fun setMaxVolProgress(maxVolProgress: Int)

    fun configureMinVolProgress(configureVal: Int)
    fun configureMaxVolProgress(configureVal: Int)

}
