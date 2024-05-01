package com.example.opscpoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Registration : AppCompatActivity() {

    private lateinit var regUsername: EditText
    private lateinit var regPassword: EditText
    private lateinit var regBtn: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        regUsername = findViewById(R.id.regUsername)
        regPassword = findViewById(R.id.regPassword)
        regBtn = findViewById(R.id.regBtn)
        firebaseAuth = FirebaseAuth.getInstance()


        regBtn.setOnClickListener {
            val userEmail = regUsername.text.toString().trim()
            val password = regPassword.text.toString().trim()

            // Create user with email and password
            firebaseAuth.createUserWithEmailAndPassword(userEmail, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    // Method to navigate to the login screen
    fun redirectToLogin(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
