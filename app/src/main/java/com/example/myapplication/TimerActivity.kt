package com.example.myapplication.com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class TimerActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var btnSetTime: Button
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnReset: Button

    private var timeInMillis: Long = 60000  // Default to 1 minute
    private var timer: CountDownTimer? = null
    private var isRunning = false

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // Initialize views
        tvTimer = findViewById(R.id.tvTimer)
        btnSetTime = findViewById(R.id.btnSetTime)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnReset = findViewById(R.id.btnReset)

        // Initialize shared preferences
        sharedPrefs = getSharedPreferences("timer_prefs", MODE_PRIVATE)

        // Load saved time from preferences
        timeInMillis = sharedPrefs.getLong("time_left", 60000)
        isRunning = sharedPrefs.getBoolean("is_running", false)

        // Set initial timer value
        updateTimerText()

        // Set time (hardcoded for simplicity, you can use a TimePickerDialog)
        btnSetTime.setOnClickListener {
            timeInMillis = 120000 // Example: 2 minutes
            updateTimerText()
        }

        // Start timer
        btnStart.setOnClickListener {
            if (!isRunning) {
                startTimer()
            }
        }

        // Stop timer
        btnStop.setOnClickListener {
            if (isRunning) {
                stopTimer()
            }
        }

        // Reset timer
        btnReset.setOnClickListener {
            resetTimer()
        }
        // Bottom Navigation View setup
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.timer // Ensure home is selected on start

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.timer -> true
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.reminder -> {
                    startActivity(Intent(this, ReminderActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                isRunning = false
                updateTimerText()
            }
        }.start()

        isRunning = true
    }

    private fun stopTimer() {
        timer?.cancel()
        isRunning = false
        saveTimerState()
    }

    private fun resetTimer() {
        timer?.cancel()
        timeInMillis = 60000  // Reset to 1 minute
        isRunning = false
        updateTimerText()
        saveTimerState()
    }

    private fun updateTimerText() {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        tvTimer.text = timeFormatted
    }

    private fun saveTimerState() {
        val editor = sharedPrefs.edit()
        editor.putLong("time_left", timeInMillis)
        editor.putBoolean("is_running", isRunning)
        editor.apply()
    }

    override fun onStop() {
        super.onStop()
        saveTimerState()
    }
}