package com.example.opscpoe

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class categoryHoursSummary : AppCompatActivity() {

    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_hours_summary)

        // Initialize views and set listeners
        initializeViews()
        setListeners()
    }

    private fun initializeViews() {
        startDateEditText = findViewById(R.id.startDateHistory)
        endDateEditText = findViewById(R.id.endDateHistory)
        searchButton = findViewById(R.id.buttonSearch)
        tableLayout = findViewById(R.id.tableLayout)

        // Set initial text for date EditTexts
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        startDateEditText.setText(dateFormat.format(currentDate))
        endDateEditText.setText(dateFormat.format(currentDate))
    }

    private fun setListeners() {
        startDateEditText.setOnClickListener {
            showDatePickerDialog(startDateEditText)
        }

        endDateEditText.setOnClickListener {
            showDatePickerDialog(endDateEditText)
        }

        searchButton.setOnClickListener {
            val startDateString = startDateEditText.text.toString()
            val endDateString = endDateEditText.text.toString()

            if (startDateString.isNotEmpty() && endDateString.isNotEmpty()) {
                val startDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(startDateString)
                val endDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(endDateString)

                if (startDate != null && endDate != null) {
                    val selectedCategory = "Your Selected Category" // Get the selected category from your UI
                    retrieveCategoryTotalHours(startDate, endDate, selectedCategory)
                } else {
                    showToast("Please enter valid dates.")
                }
            } else {
                showToast("Please enter both start and end dates.")
            }
        }
    }

    private fun showDatePickerDialog(dateEditText: EditText) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                currentDate.set(selectedYear, selectedMonth, selectedDay)
                dateEditText.setText(dateFormat.format(currentDate.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun retrieveCategoryTotalHours(startDate: Date, endDate: Date, selectedCategory: String) {
        // Replace with your Firestore instance
        val db = FirebaseFirestore.getInstance()

        db.collection("timesheetEntries")
            .whereEqualTo("category", selectedCategory)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var totalHours = 0.0

                for (document in querySnapshot) {
                    val startTime = document.getString("startTime") ?: "00:00"
                    val endTime = document.getString("endTime") ?: "00:00"
                    val hoursWorked = calculateHoursWorked(startTime, endTime)
                    totalHours += hoursWorked
                }

                showToast("Total hours worked on $selectedCategory: $totalHours")
            }
            .addOnFailureListener { exception ->
                showToast("Error retrieving data: ${exception.message}")
            }
    }

    private fun calculateHoursWorked(startTime: String, endTime: String): Double {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startDate = format.parse(startTime) ?: Date()
        val endDate = format.parse(endTime) ?: Date()
        val diff = endDate.time - startDate.time
        return diff / (1000 * 60 * 60).toDouble() // Convert milliseconds to hours
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
