package com.customotpview

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import java.util.*

class CountDownTextView : AppCompatTextView {
    var mHandler = Handler()
    var timerPause = false
    private var mSeconds: Long = 0
    var runnable = Runnable { updateTime() }
    var callbacks: Callbacks? = null

    constructor(context: Context?) : super(context)

    fun pauseTimer() {
        timerPause = true
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private fun updateTime() {
        mSeconds--
        Log.d("Runnable", "Handler is working $mSeconds")
        updateText()
        if (mSeconds > 0L) mHandler.postDelayed(runnable, 1000)
    }

    //it is seconds
    var duration: Long
        get() = mSeconds
        set(durationInSec) { //it is seconds
            mSeconds = durationInSec
            updateText()
            start()
        }

    private fun updateText() {
        activity!!.runOnUiThread {
            text = stringForTime(mSeconds)
            if (mSeconds == 0L) {
                stop()
            }
        }
    }

    private val activity: Activity?
        get() {
            var context = context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
            return null
        }

    private fun stringForTime(timeInSec: Long): String {
        if (timeInSec <= 0 || timeInSec >= 24 * 60 * 60) {
            return "00:00"
        }
        val seconds = timeInSec % 60
        val minutes = timeInSec / 60 % 60
        val hours = timeInSec / 3600
        val stringBuilder = StringBuilder()
        val mFormatter =
            Formatter(stringBuilder, Locale.getDefault())
        return if (hours > 0) {
            mFormatter.format(
                Locale.ENGLISH,
                "%d:%02d:%02d",
                hours,
                minutes,
                seconds
            ).toString()
        } else {
            mFormatter.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
                .toString()
        }
    }

    fun stop() {
        mHandler.removeCallbacks(runnable)
        if (callbacks != null) callbacks!!.onTimeFinish()
    }

    fun start() {
        mHandler.postDelayed(runnable, 1000)
    }

    fun setCallBack(callBack: Callbacks?) {
        callbacks = callBack
    }

    interface Callbacks {
        fun onTimeFinish()
    }
}