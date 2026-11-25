package com.doomx.floatingcontroller

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 40, 28, 28)
            setBackgroundColor(0xFF101010.toInt())
        }
        fun label(s: String, size: Float) = TextView(this).apply {
            text = s; textSize = size; setTextColor(0xFFFFFFFF.toInt())
            setPadding(8, 18, 8, 18)
        }
        root.addView(label("DOOMX", 32f))
        root.addView(label("FLOATING PRACTICE CONTROLLER", 15f))
        root.addView(label("A movable overlay with a hold-to-repeat practice counter. It does not send touches to other apps or control gameplay.", 16f))
        val enable = Button(this).apply { text = "ENABLE FLOATING KEY" }
        root.addView(enable)
        enable.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")))
            } else {
                startForegroundService(Intent(this, OverlayService::class.java))
                Toast.makeText(this, "Floating key enabled", Toast.LENGTH_SHORT).show()
            }
        }
        val stop = Button(this).apply { text = "STOP FLOATING KEY" }
        root.addView(stop)
        stop.setOnClickListener { stopService(Intent(this, OverlayService::class.java)) }
        setContentView(root)
    }
}
