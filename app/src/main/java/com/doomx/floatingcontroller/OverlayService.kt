package com.doomx.floatingcontroller

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.*
import android.widget.TextView

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: TextView? = null
    private var params: WindowManager.LayoutParams? = null
    private val handler = Handler(Looper.getMainLooper())
    private var held = false
    private var count = 0
    private val maxCount = 25
    private val intervalMs = 120L

    private val repeat = object : Runnable {
        override fun run() {
            if (!held) return
            count++
            bubble?.text = "$count"
            if (count >= maxCount) {
                stopRepeating()
                bubble?.text = "DONE"
            } else handler.postDelayed(this, intervalMs)
        }
    }

    override fun onCreate() {
        super.onCreate()
        startAsForeground()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
    }

    private fun startAsForeground() {
        val channelId = "doomx_overlay"
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(channelId, "DOOMX Floating Controller",
                NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = if (Build.VERSION.SDK_INT >= 26)
            Notification.Builder(this, channelId)
        else Notification.Builder(this)
        val n = notification.setContentTitle("DOOMX Floating Controller")
            .setContentText("Floating practice key is active")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
        startForeground(7, n)
    }

    private fun showBubble() {
        val view = TextView(this).apply {
            text = "HOLD"
            textSize = 13f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setBackgroundColor(0xFFB71C1C.toInt())
            elevation = 12f
        }
        val type = if (Build.VERSION.SDK_INT >= 26)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        val p = WindowManager.LayoutParams(
            92, 92, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40; y = 220
        }
        params = p
        view.setOnTouchListener(object : View.OnTouchListener {
            var downX = 0f; var downY = 0f
            var startX = 0; var startY = 0
            var moved = false
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = e.rawX; downY = e.rawY
                        startX = p.x; startY = p.y
                        moved = false
                        count = 0
                        held = true
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        v.post { v as TextView; v.text = "0" }
                        handler.removeCallbacks(repeat)
                        handler.post(repeat)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (e.rawX - downX).toInt()
                        val dy = (e.rawY - downY).toInt()
                        if (kotlin.math.abs(dx) > 8 || kotlin.math.abs(dy) > 8) moved = true
                        p.x = startX + dx; p.y = startY + dy
                        wm.updateViewLayout(v, p)
                        return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        stopRepeating()
                        (v as TextView).text = "HOLD"
                        return true
                    }
                }
                return false
            }
        })
        bubble = view
        wm.addView(view, p)
    }

    private fun stopRepeating() {
        held = false
        handler.removeCallbacks(repeat)
    }

    override fun onDestroy() {
        stopRepeating()
        bubble?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        bubble = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
