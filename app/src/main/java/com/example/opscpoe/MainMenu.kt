package com.example.opscpoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainMenu : AppCompatActivity() {

    private lateinit var categoryCreationButton: Button
    private lateinit var timesheetEntryButton: Button
    private lateinit var dailyGoalsBtn: Button
    // private lateinit var rewardsBtn: Button
    // private lateinit var settingsBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        timesheetEntryButton = findViewById(R.id.timesheetEntryButton)
        categoryCreationButton = findViewById(R.id.categoryCreationButton)
        dailyGoalsBtn = findViewById(R.id.dailyGoalsBtn)
        // rewardsBtn = findViewById(R.id.rewardsBtn)
        // settingsBtn = findViewById(R.id.settingsBtn)

        timesheetEntryButton.setOnClickListener {
            navigateToTimesheetEntry()
        }

        categoryCreationButton.setOnClickListener {
            navigateToCategoryCreation()
        }

        dailyGoalsBtn.setOnClickListener {
            navigateToDailyGoals()
        }

        // rewardsBtn.setOnClickListener {
        //    navigateToRewards()
        // }

        // settingsBtn.setOnClickListener {
        // navigateToSettings()
        // }
    }

    private fun navigateToTimesheetEntry() {
        val intent = Intent(this, TimesheetEntry::class.java)
        startActivity(intent)
    }

    private fun navigateToCategoryCreation() {
        val intent = Intent(this, Category::class.java)
        startActivity(intent)
    }

    private fun navigateToDailyGoals() {
        val intent = Intent(this, DailyGoals::class.java)
        startActivity(intent)
    }

    // private fun navigateToRewards() {
    // val intent = Intent(this, Rewards::class.java)
    //startActivity(intent)
}

// private fun navigateToSettings() {
//val intent = Intent(this, Settings::class.java)
//startActivity(intent)
