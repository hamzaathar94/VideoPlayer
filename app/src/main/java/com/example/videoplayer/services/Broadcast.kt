package com.example.videoplayer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class Broadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_POWER_CONNECTED) {
            Toast.makeText(context, "Power Connected", Toast.LENGTH_SHORT).show()
        } else if (intent?.action == Intent.ACTION_POWER_DISCONNECTED) {
            Toast.makeText(context, "Power DisConnected", Toast.LENGTH_SHORT).show()
        }
    }
}