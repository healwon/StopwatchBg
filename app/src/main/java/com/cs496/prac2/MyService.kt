package com.cs496.prac2

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class MyService: Service() {

    private var time = 0
    private var isNotZero = false
    private var isRunning = false
    private var timerTask: Timer? = null
    private var id: Int = 0

    val records: ArrayList<Int> = ArrayList<Int>()

    private val myBinder = MyBinder()

    inner class MyBinder: Binder() {
        fun getService(): MyService = this@MyService
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("service($id)","onBind()")
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("service($id)","onStartCommand()")
        return Service.START_STICKY
    }

    override fun onCreate() {
        id = Random().nextInt(100)
        Log.d("service($id)","onCreate()")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d("service($id)","onDestroy()")
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("service($id)","onUnbind()")
        return super.onUnbind(intent)
    }

    fun startAndPause() {
        when (isRunning) {
            false -> timerTask = kotlin.concurrent.timer(period = 10) {
                    time++ }
            true -> timerTask?.cancel()
        }
        isRunning = !isRunning
        isNotZero = true
    }

    fun reset() {
        isRunning = false
        isNotZero = false
        time = 0
        records.clear()
    }

    fun addRecord(time: Int) {
        records.add(time)
    }

    fun getRecordSize(): Int {
        return records.size
    }

    fun getRecord(index: Int): Int {
        return records[index]
    }

    fun getIsRunning(): Boolean {
        return isRunning
    }

    fun getIsNotZero(): Boolean {
        return isNotZero
    }

    fun getTime(): Int {
        return time
    }
}