package com.example.opscpoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Category : AppCompatActivity() {

    private lateinit var categoryTextView: TextView
    private lateinit var categoryTextBox: EditText
    private lateinit var categorySubmitBtn: Button
    private lateinit var categoryHoursSummaryHistoryBtn: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Initialize views
        categoryTextView = findViewById(R.id.CategoryTextView)
        categoryTextBox = findViewById(R.id.CategoryTextBox)
        categorySubmitBtn = findViewById(R.id.categorySubmitBtn)
        categoryHoursSummaryHistoryBtn = findViewById(R.id.categoryHoursSummaryHistoryBtn)

        // Set click listener for the submit button
        categorySubmitBtn.setOnClickListener {
            val categoryName = categoryTextBox.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                saveCategory(categoryName)
            }
        }

        // Set click listener for the hours summary button
        categoryHoursSummaryHistoryBtn.setOnClickListener {
            navigateToHoursSummary()
        }
    }

    private fun saveCategory(name: String) {
        val category = hashMapOf("name" to name)
        db.collection("category").add(category)
            .addOnSuccessListener {
                Toast.makeText(this, "Category has been added successfully", Toast.LENGTH_SHORT).show()
                categoryTextBox.setText("")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add category: ${e.message}", Toast.LENGTH_SHORT).show()
                categoryTextBox.setText("")
            }
    }

    private fun navigateToHoursSummary() {
        val intent = Intent(this, categoryHoursSummary::class.java)
        startActivity(intent)
    }
}
