package com.example.myapplication.com.example.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var etHour: EditText
    private lateinit var etMinute: EditText
    private lateinit var btnSetReminder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        // Initialize UI components
        etHour = findViewById(R.id.etHour)
        etMinute = findViewById(R.id.etMinute)
        btnSetReminder = findViewById(R.id.btnSetReminder)

        // Set reminder button click listener
        btnSetReminder.setOnClickListener {
            val hour = etHour.text.toString().toIntOrNull()
            val minute = etMinute.text.toString().toIntOrNull()

            if (hour != null && minute != null) {
                setReminder(hour, minute)
            } else {
                Toast.makeText(this, "Please enter valid hour and minute", Toast.LENGTH_SHORT).show()
            }
        }

        // Bottom Navigation View setup
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.reminder // Ensure home is selected on start

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.reminder -> true
                R.id.timer -> {
                    startActivity(Intent(this, TimerActivity::class.java))
                    true
                }
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setReminder(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Create an intent to the broadcast receiver
        val intent = Intent(this, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Get the AlarmManager
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Reminder set for $hour:$minute", Toast.LENGTH_SHORT).show()
    }
}
