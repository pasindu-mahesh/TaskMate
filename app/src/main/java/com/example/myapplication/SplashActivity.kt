package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        // Use Coroutine to delay for 3 seconds
        GlobalScope.launch {
            delay(3000) // Delay for 3 seconds (3000 milliseconds)
            withContext(Dispatchers.Main) {
                // Navigate to MainActivity after delay
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Close the SplashActivity
            }
        }
    }
}
