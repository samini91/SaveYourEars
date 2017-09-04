package com.example.liftingpump.liftingpump

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast

import java.util.Random

/**
 * Created by Mugen on 9/3/2017.
 */

class StateWidget : AppWidgetProvider() {

    companion object {
        val sendingProcessType = StateWidget::javaClass.toString() + "sendingProcessType"
        val doNotRunService = "doNotRunService"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.getStringExtra(sendingProcessType) == doNotRunService){

            Log.i("Widget", "Updating from Service")

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val stateWidget = ComponentName(context, StateWidget::class.java!!)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(stateWidget)

            updateWidget(context!!,appWidgetManager ,appWidgetIds,false)
        }
        else {
            Log.i("Widget", "Updating from broadcast")
            super.onReceive(context, intent)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

          updateWidget(context,appWidgetManager,appWidgetIds, true)
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, runService:Boolean){

        val optionModel = OptionModel(context.getSharedPreferences(OptionModel.default,Context.MODE_PRIVATE))
        val count = appWidgetIds.size

        for (i in 0..count - 1) {
            val widgetId = appWidgetIds[i]

            val remoteViews = RemoteViews(context.packageName, R.layout.state_widget)

            setCoolingDown(context,appWidgetIds,remoteViews)
            setHeatingUp(context,appWidgetIds,remoteViews)

            // If you dont want to have a toggle then remove these conditionals
            if(optionModel?.stateVal == VolumeService.coolingDown ){
                remoteViews.setBoolean(R.id.coolingDown, "setEnabled", false)
                remoteViews.setBoolean(R.id.heatingUp, "setEnabled", true)
            }
            else if(optionModel?.stateVal == VolumeService.heatingUp){
                remoteViews.setBoolean(R.id.coolingDown, "setEnabled", true)
                remoteViews.setBoolean(R.id.heatingUp, "setEnabled", false)
            }

            if(runService)
                context.startService(Intent(context, VolumeService::class.java))

            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }



    }


    private fun setCoolingDown(context: Context, appWidgetIds: IntArray, remoteViews:RemoteViews){
        val intent = Intent(context, StateWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

        val pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        remoteViews.setOnClickPendingIntent(R.id.coolingDown, pendingIntent)

    }

    private fun setHeatingUp(context: Context, appWidgetIds: IntArray, remoteViews:RemoteViews){

        val intent = Intent(context, StateWidget::class.java)

        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

        val pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        remoteViews.setOnClickPendingIntent(R.id.heatingUp, pendingIntent)

    }

}
