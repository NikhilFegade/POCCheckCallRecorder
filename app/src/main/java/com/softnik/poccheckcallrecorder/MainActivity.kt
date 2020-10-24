package com.softnik.poccheckcallrecorder

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), PhoneStateReceiver.OnCallEndListener {
    var filePath = "/storage/emulated/0/CallRecordings"
    var newFilePath :String =""

    var formattedDate:String?=null

    var receiver:BroadcastReceiver?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                10
            )

        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                11
            )

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS),
                12
            );
        }

        val c: Date = Calendar.getInstance().getTime()
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formattedDate = df.format(c)

        newFilePath  = "/storage/emulated/0/CallRecordings/"+formattedDate
        //getLatestFilefromDir(newFilePath)
    }


    fun btnCallClicked(view: View) {
        val isAppInstalled: Boolean = appInstalledOrNot("com.appstar.callrecorder")
        if (isAppInstalled) {
            try {

                val filter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
                 receiver = PhoneStateReceiver()
                this.registerReceiver(receiver, filter)


                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:" + "9503000180")

                this.startActivity(intent)

            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    applicationContext,
                    "yourActivity is not founded",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            try {
                var viewIntent = Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("https://play.google.com/store/apps/details?id=com.appstar.callrecorder")
                );
                startActivity(viewIntent);
            } catch (e: Exception) {
                Toast.makeText(
                    getApplicationContext(), "Unable to Connect Try Again...",
                    Toast.LENGTH_LONG
                ).show();
                e.printStackTrace()
            }
        }

    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }


    override fun onResume() {
        super.onResume()
        PhoneStateReceiver.onCallEndListener = this
    }

  /*  override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }*/


    override fun onCallEnded(isEnded: Boolean) {
        if(isEnded == true){
            getLatestFilefromDir(newFilePath)


        }
    }


    private fun getLatestFilefromDir(dirPath: String):File?{
        val dir = File(dirPath)
        val files = dir.listFiles()
        if (files == null || files.size == 0) {
            return null
        }
        var lastModifiedFile = files[0]
        for (i in 1 until files.size) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i]
            }
        }

        var fileName = lastModifiedFile.name
        var filePath=lastModifiedFile.absolutePath

        //Toast.makeText(this,"=$fileName===$filePath",Toast.LENGTH_LONG).show()

        Log.d("==MainActivity",fileName)
        Log.d("==MainActivity",filePath)

        tvFileName.text = fileName
        tvFilePath.text = filePath

        return lastModifiedFile

    }

}