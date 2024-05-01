package com.example.opscpoe

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.util.UUID
import java.util.Calendar
import java.util.Locale

class TimesheetEntry : AppCompatActivity() {

    private lateinit var StartTimeEntry: EditText
    private lateinit var endTimeEntry: EditText
    private lateinit var categoryDropdownBox: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var timesheetImageView1: ImageView
    private lateinit var AddImageBtn: Button
    private lateinit var createEntryBtn: Button
    private lateinit var TimesheetEntryHistory: Button
    private lateinit var startTimeProgressBarTimesheetEntry: ProgressBar
    private lateinit var StartDateTextbox: EditText
    private lateinit var timerCount:TextView
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timesheet_entry)

        initializeViews()
        loadCategories()
        setListeners()
    }

    private fun initializeViews() {
        StartTimeEntry = findViewById(R.id.StartTimeEntry)
        endTimeEntry = findViewById(R.id.endTimeEntry)
        categoryDropdownBox = findViewById(R.id.categoryDropdownBox)
        descriptionEditText = findViewById(R.id.InputDescription)
        timesheetImageView1 = findViewById(R.id.timesheetImageView1)
        AddImageBtn = findViewById(R.id.AddImageBtn)
        createEntryBtn = findViewById(R.id.createEntryBtn)
        TimesheetEntryHistory = findViewById(R.id.TimesheetEntryHistory)
        startTimeProgressBarTimesheetEntry = findViewById(R.id.startTimeProgressBarTimesheetEntry)
        StartDateTextbox = findViewById(R.id.StartDateTextbox)
        timerCount = findViewById(R.id.timerCount)
    }

    private fun setListeners() {
        StartDateTextbox.setOnClickListener {
            showDatePickerDialog()
        }
        StartTimeEntry.setOnClickListener {
            showTimePickerDialog(isStartTime = true)
        }
        endTimeEntry.setOnClickListener {
            showTimePickerDialog(isStartTime = false)
        }
        createEntryBtn.setOnClickListener {
            saveTimesheetEntry()
        }
        AddImageBtn.setOnClickListener {
            pickImageFromGallery()
        }
        TimesheetEntryHistory.setOnClickListener {
            TimesheetEntriesRecords()
        }
    }

    private fun loadCategories() {
        val categoriesList = ArrayList<String>()
        db.collection("category")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val categoryName = document.data["name"] as? String
                    categoryName?.let { categoriesList.add(it) }
                }
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    categoriesList
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categoryDropdownBox.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error getting categories: $exception",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveTimesheetEntry() {
        val selectCategory = categoryDropdownBox.selectedItem.toString()
        val date = StartDateTextbox.text.toString().trim()
        val start = StartTimeEntry.text.toString().trim()
        val end = endTimeEntry.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (date.isNotEmpty() && start.isNotEmpty() && end.isNotEmpty() && description.isNotEmpty()) {
            val timesheetEntry = hashMapOf(
                "date" to date,
                "startTime" to start,
                "endTime" to end,
                "category" to selectCategory,
                "description" to description
            )
            db.collection("timesheetEntries")
                .add(timesheetEntry)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                }

            // Start the timer and update progress bar
            val startTimeSeconds = convertTimeToSeconds(start)
            val endTimeSeconds = convertTimeToSeconds(end)
            val durationSeconds = endTimeSeconds - startTimeSeconds
            Toast.makeText(this, "Timer has started", Toast.LENGTH_SHORT).show()
            startTimer(durationSeconds)

        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }


    private fun convertTimeToSeconds(time: String): Long {
        val timeParts = time.split(":")
        val hours = timeParts[0].toLong()
        val minutes = timeParts[1].toLong()
        return hours * 3600 + minutes * 60
    }

    private fun startTimer(durationSeconds: Long) {
        val endTime = System.currentTimeMillis() + durationSeconds * 1000

        val countUpTimer = object : CountDownTimer(durationSeconds * 1000, 100) { // Change tick interval to 100 milliseconds
            override fun onTick(millisUntilFinished: Long) {
                val currentTime = System.currentTimeMillis()
                val remainingTime = endTime - currentTime

                // Ensure that the remaining time is not negative
                if (remainingTime >= 0) {
                    // Calculate progress for the progress bar
                    val progress = (((durationSeconds * 1000) - remainingTime).toFloat() / (durationSeconds * 1000).toFloat() * 100).toInt()
                    startTimeProgressBarTimesheetEntry.progress = progress

                    // Update timerCount TextView
                    val remainingSeconds = remainingTime / 1000
                    val remainingHours = remainingSeconds / 3600
                    val remainingMinutes = (remainingSeconds % 3600) / 60
                    val remainingSecondsWithoutMinutes = (remainingSeconds % 3600) % 60
                    val remainingTimeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", remainingHours, remainingMinutes, remainingSecondsWithoutMinutes)
                    timerCount.text = remainingTimeString
                }
            }

            override fun onFinish() {
                // Timer finished
                startTimeProgressBarTimesheetEntry.progress = 100
                timerCount.text = "00:00:00"
                Toast.makeText(applicationContext, "Timer has Ended", Toast.LENGTH_SHORT).show()

                // Vibrate when timer finishes
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator?
                if (vibrator?.hasVibrator() == true) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 500, 100, 500, 100), -1))
                    } else {
                        // Deprecated in API 26
                        vibrator.vibrate(longArrayOf(0, 100, 500, 100, 500, 100), -1)
                    }
                }
            }
        }
        countUpTimer.start()
    }

    private fun showDatePickerDialog() {
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
                StartDateTextbox.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
    private fun showTimePickerDialog(isStartTime: Boolean) {
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
                if (isStartTime) {
                    StartTimeEntry.setText(selectedTime)
                } else {
                    endTimeEntry.setText(selectedTime)
                }
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun pickImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                val chosenImageUri = data.data!!
                val resizedBitmap = resizeImage(chosenImageUri, 200, 200)
                timesheetImageView1.setImageBitmap(resizedBitmap)
                uploadImageToFirebase(chosenImageUri)
            } catch (e: Exception) {
                Log.e("ProfilePage", "Error getting selected files: ${e.message}")
                Toast.makeText(this, "Error getting selected files", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = storage.reference.child("images/$fileName")

        refStorage.putFile(fileUri)
            .addOnSuccessListener {
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("ProfilePage", "Image upload failed: ${e.message}")
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun resizeImage(imageUri: Uri, maxWidth: Int, maxHeight: Int): Bitmap {
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)
            val imageWidth = outWidth
            val imageHeight = outHeight
            val scaleFactor = Math.min(imageWidth / maxWidth, imageHeight / maxHeight)
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        inputStream?.close()
        return BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri), null, options)
            ?: throw IllegalArgumentException("Failed to decode bitmap")
    }

    private fun TimesheetEntriesRecords() {
        val intent = Intent(this, timesheet_history::class.java)
        startActivity(intent)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
