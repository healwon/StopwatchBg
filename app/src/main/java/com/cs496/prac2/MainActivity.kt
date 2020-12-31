package com.cs496.prac2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.size
import java.util.*

class MainActivity : AppCompatActivity() {

    private var isRunning = false
    private var isNotZero = false
    private var timerTask: Timer? = null
    private var index :Int = 0

    private var timeText: TextView? = null
    var startBtn: Button? = null
    var recodeBtn: Button? = null
    var stopBtn: Button? = null
    var resetBtn: Button? = null
    var recodeText: LinearLayout? = null

    private lateinit var mService: MyService
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.MyBinder
            mService = binder.getService()
            mBound = true

            isRunning = mService.getIsRunning()
            isNotZero = mService.getIsNotZero()
            if (isNotZero) {
                resume()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeText = findViewById(R.id.time_text)
        startBtn = findViewById(R.id.start_btn)
        recodeBtn = findViewById(R.id.recode_btn)
        stopBtn = findViewById(R.id.stop_btn)
        resetBtn = findViewById(R.id.reset_btn)
        recodeText = findViewById(R.id.recode_text)
        val container: LinearLayout = findViewById(R.id.container)
        var num: Int = container.size

        startBtn?.setOnClickListener {
            isRunning = !isRunning
            isNotZero = true
            if (isRunning) start() else pause()
        }

        recodeBtn?.setOnClickListener {
            if(isRunning) {
                val lapTime = mService.getTime()
                lapTime(lapTime)
            }
        }

        stopBtn?.setOnClickListener {
            when (num) {
                0-> {
                    val textView = TextView(this).apply {
                        setTextSize(20f)
                    }
                    textView.text = "new tv"
                    container.addView(textView,0)
                    num++
                }
                1-> {
                    container.removeAllViews()
                    num--
                }
            }

        }

        resetBtn?.setOnClickListener{
            reset();
        }

        startService(Intent(this, MyService::class.java))
        bindService(Intent(this, MyService::class.java), connection, Context.BIND_AUTO_CREATE)


    }

    private fun setTime() {
        val setTime = mService.getTime()
        var milli = setTime % 100
        var sec = (setTime/100) % 60
        var min = setTime / 6000
        timeText?.text = "$min:$sec.$milli"
    }

    private fun lapTime(lapTime: Int) {
        mService.addRecord(lapTime)
        val textView = TextView(this).apply {
            setTextSize(20f)
        }
        textView.text = "${lapTime / 6000}:${(lapTime/100) % 60}.${lapTime % 100}"

        recodeText?.addView(textView,0)
        index++
    }

    private fun start() {
        mService.startAndPause()
        timerTask = kotlin.concurrent.timer(period = 10) {
            runOnUiThread {
                setTime()
            }
        }
    }

    private fun resume() {
        when (isRunning) {
            true -> {
                timerTask = kotlin.concurrent.timer(period = 10) {
                    runOnUiThread {
                        setTime()
                    }
                }
            }
            false -> {
                setTime()
            }
        }
        var size = mService.getRecordSize()
        for (i in 0 until size) {
            lapTime(mService.getRecord(i))
        }
    }

    private fun pause() {
        mService.startAndPause()
        timerTask?.cancel()
    }

    private fun reset() {
        mService.reset()
        timerTask?.cancel()

        isRunning = false
        isNotZero = false
        timeText?.text = "00:00.00"

        recodeText?.removeAllViews()
        index = 0
    }

    override fun onDestroy() {
        if (!isNotZero) {
            stopService(Intent(this, MyService::class.java))
        }
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
    }
}