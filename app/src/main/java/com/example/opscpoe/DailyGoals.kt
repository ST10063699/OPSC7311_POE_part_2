package com.example.opscpoe

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Locale

class DailyGoals : AppCompatActivity() {

    private lateinit var MaximumHoursText: EditText
    private lateinit var MinimumHoursText: EditText
    private lateinit var SetGoalBtn: Button
    private lateinit var startTimeProgressBar: ProgressBar
    private lateinit var MonthlyGoalBtn: Button
    private lateinit var TotalHoursGraphBtn: Button
    private val db = FirebaseFirestore.getInstance()
    private val goalsCollection = db.collection("Goals")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_goals)

        MaximumHoursText = findViewById(R.id.MaximumHoursText)
        MinimumHoursText = findViewById(R.id.MinimumHoursText)
        SetGoalBtn = findViewById(R.id.SetGoalBtn)
        startTimeProgressBar = findViewById(R.id.startTimeProgressBar)
        MonthlyGoalBtn = findViewById(R.id.MonthlyGoalBtn)
        TotalHoursGraphBtn = findViewById(R.id.TotalHoursGraphBtn)


      //  updateProgressBar(minHours, maxHours)

        MinimumHoursText.setOnClickListener {
            showTimePickerDialog(isMinimumHours = true)
        }

        MaximumHoursText.setOnClickListener {
            showTimePickerDialog(isMinimumHours = false)
        }

        SetGoalBtn.setOnClickListener {
            setGoal()
        }

        /* MonthlyGoalBtn.setOnClickListener {
            navigateToMonthlyGoalScreen()
        }*/

    }

    private fun showTimePickerDialog(isMinimumHours: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )
                if (isMinimumHours) {
                    MinimumHoursText.setText(selectedTime)
                } else {
                    MaximumHoursText.setText(selectedTime)
                }
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun setGoal() {
        val minimumTime = MinimumHoursText.text.toString().trim()
        val maximumTime = MaximumHoursText.text.toString().trim()

        if (minimumTime.isNotEmpty() && maximumTime.isNotEmpty()) {
            val goalData = hashMapOf(
                "minimumTime" to minimumTime,
                "maximumTime" to maximumTime
            )

            goalsCollection.document().set(goalData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Goal set successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to set goal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select both minimum and maximum times", Toast.LENGTH_SHORT).show()
        }
//private fun updateProgressBar(minHours)
    }
}

    /*private fun navigateToMonthlyGoalScreen() {
        val intent = Intent(this, MonthlyGoal::class.java)
        startActivity(intent)
        finish()
    }*/


