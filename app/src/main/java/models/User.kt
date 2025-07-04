// This file is located at: com/example/mathq/models/User.kt

package com.example.mathq.models

// ✅ The User data class now lives here, clean and reusable.
data class User(
    val user_id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "", // Stored in plain-text for demo only
    val total_score: Int = 0,
    val highest_score: Int = 0,
    val highest_easy_score: Int = 0,
    val highest_normal_score: Int = 0,
    val highest_hard_score: Int = 0,
    val created_at: String = ""
)