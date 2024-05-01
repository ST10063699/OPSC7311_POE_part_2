package com.example.opscpoe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        Handler().postDelayed({
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            finish() // Finish MainActivity to prevent returning to it when pressing back
        }, 2000)
    }
}