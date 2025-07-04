package com.example.mathq

import com.example.mathq.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase



fun registerUser(name: String, email: String, password: String, onResult: (String) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseDatabase.getInstance().getReference("User")

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                val currentTime = System.currentTimeMillis().toString()

                val newUser = User(
                    user_id = userId,
                    name = name,
                    email = email,
                    total_score = 0,
                    highest_score = 0,
                    created_at = currentTime
                )

                db.child(userId).setValue(newUser)
                    .addOnSuccessListener {
                        onResult("Registered successfully!")
                    }
                    .addOnFailureListener {
                        onResult("Database error: ${it.message}")
                    }
            } else {
                onResult("Auth failed: ${task.exception?.message}")
            }
        }
}
