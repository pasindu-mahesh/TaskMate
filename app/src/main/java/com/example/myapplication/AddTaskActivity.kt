package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAddTaskBinding

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val task = intent.getParcelableExtra<Task>("task")
        task?.let {
            binding.etTask.setText(it.name)
            binding.etDescription.setText(it.description)
        }

        binding.btnSave.setOnClickListener {
            val newTask = Task(
                id = task?.id ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                name = binding.etTask.text.toString(),
                description = binding.etDescription.text.toString()
            )

            val resultIntent = intent.apply {
                putExtra("task", newTask)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // This will close AddTaskActivity and return to MainActivity
        }
    }
}
