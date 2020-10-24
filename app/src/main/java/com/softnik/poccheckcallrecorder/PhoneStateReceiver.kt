package com.softnik.poccheckcallrecorder

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log


class PhoneStateReceiver : BroadcastReceiver() {
    private val TAG = "PhoneStatReceiver"

    override fun onReceive(context: Context, intent: Intent) {

        if (onCallEndListener != null) {
            onCallEndListener!!.onCallEnded(isCallEnded(context, intent))
        }
    }

    private fun isCallEnded(context: Context, intent: Intent): Boolean {
        var isCallCut: Boolean? = null

        val tm =
            context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        when (tm.callState) {

            TelephonyManager.CALL_STATE_RINGING -> {
                Log.i(TAG, "RINGING :")
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.i(TAG, "incoming ACCEPT")
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.i(TAG, "incoming IDLE")
                isCallCut = true
            }
        }

        return isCallCut ?: false
    }

    interface OnCallEndListener {
        fun onCallEnded(isEnded: Boolean)
    }

    companion object {
        var onCallEndListener: OnCallEndListener? = null
    }
}


