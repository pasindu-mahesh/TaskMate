package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.com.example.myapplication.ReminderActivity
import com.example.myapplication.com.example.myapplication.TimerActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnAddTask: Button
    private val PREFS_NAME = "tasks_prefs"
    private val TASKS_KEY = "tasks"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Initialize the TaskAdapter
        taskAdapter = TaskAdapter(
            onEditClick = { task ->
                val intent = Intent(this, AddTaskActivity::class.java).apply {
                    putExtra("task", task)
                }
                startActivity(intent)
            },
            onDeleteClick = { task ->
                showDeleteConfirmation(task)
            }
        )
        recyclerView.adapter = taskAdapter

        // Button to add a new task
        btnAddTask = findViewById(R.id.btnAddTask)
        btnAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        // Bottom Navigation View setup
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.home // Ensure home is selected on start

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.timer -> {
                    startActivity(Intent(this, TimerActivity::class.java))
                    true
                }
                R.id.reminder -> {
                    startActivity(Intent(this, ReminderActivity::class.java))
                    true
                }
                else -> false
            }
        }

        changeStatusBarColor()
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        GlobalScope.launch {
            try {
                val tasks = loadTasksFromSharedPreferences()
                runOnUiThread {
                    taskAdapter.setTasks(tasks)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error loading tasks: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadTasksFromSharedPreferences(): List<Task> {
        val json = sharedPreferences.getString(TASKS_KEY, "[]") ?: "[]"
        return Gson().fromJson(json, Array<Task>::class.java).toList()
    }

    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ -> deleteTask(task) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteTask(task: Task) {
        GlobalScope.launch {
            try {
                val tasks = loadTasksFromSharedPreferences().toMutableList()
                tasks.removeIf { it.id == task.id }
                saveTasksToSharedPreferences(tasks)
                runOnUiThread {
                    taskAdapter.setTasks(tasks)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error deleting task: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveTasksToSharedPreferences(tasks: List<Task>) {
        val json = Gson().toJson(tasks)
        sharedPreferences.edit().putString(TASKS_KEY, json).apply()
    }

    private fun changeStatusBarColor() {
        // Get the window of the current activity
        val window: Window = window
        // Set the status bar color
        window.statusBarColor = getColor(com.google.android.material.R.color.design_default_color_error)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newTask = data?.getParcelableExtra<Task>("task")
            newTask?.let {
                // Load current tasks
                val currentTasks = loadTasksFromSharedPreferences().toMutableList()
                currentTasks.add(it)  // Add the new task
                saveTasksToSharedPreferences(currentTasks) // Save to SharedPreferences
                taskAdapter.setTasks(currentTasks) // Update RecyclerView
            }
        }
    }

    companion object {
        const val ADD_TASK_REQUEST_CODE = 1 // Request code to identify the task adding request
    }
}
