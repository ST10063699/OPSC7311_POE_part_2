package com.example.opscpoe

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class timesheet_history : AppCompatActivity() {

    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: Button
    private lateinit var firebase: FirebaseFirestore
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet_history)

        // Initialize views
        startDateEditText = findViewById(R.id.startDateHistory)
        endDateEditText = findViewById(R.id.endDateHistory)
        recyclerView = findViewById(R.id.RecyclerView)
        searchButton = findViewById(R.id.searchBtnTimesheet)

        // Initialize Firebase Firestore
        firebase = FirebaseFirestore.getInstance()

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Adapter(emptyList()) // Initially, use an empty list
        recyclerView.adapter = adapter

        // Set onClickListener for startDateEditText to show date picker
        startDateEditText.setOnClickListener {
            showDatePickerDialog(startDateEditText)
        }

        // Set onClickListener for endDateEditText to show date picker
        endDateEditText.setOnClickListener {
            showDatePickerDialog(endDateEditText)
        }

        // Set onClickListener for searchButton to retrieve and display timesheet entries
        searchButton.setOnClickListener {
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()

            retrieveTimesheetEntries(startDate, endDate)
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectYear, selectedMonth, selectedDay ->
                val selectedDate = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    selectedMonth + 1,
                    selectedDay,
                    selectYear
                )
                editText.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun retrieveTimesheetEntries(startDate: String, endDate: String) {
        // Clear existing data in adapter
        adapter.updateData(emptyList())

        // Query Firestore for timesheet entries within the selected date range
        firebase.collection("timesheetHistory")
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .orderBy("date")
            .get()
            .addOnSuccessListener { documents ->
                val timesheetList = mutableListOf<Models>()
                for (document in documents) {
                    val category = document.getString("category") ?: ""
                    val date = document.getDate("date")
                    val description = document.getString("description") ?: ""
                    val startTime = document.getString("startTime") ?: ""
                    val endTime = document.getString("endTime") ?: ""
                    val uri = document.getString("uri") ?: ""

                    val timesheetEntry =
                        Models(category, date, description, endTime, startTime, uri)
                    timesheetList.add(timesheetEntry)
                }
                // Update adapter with retrieved data
                adapter.updateData(timesheetList)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error retrieving timesheet entries", exception)
            }
    }
}
