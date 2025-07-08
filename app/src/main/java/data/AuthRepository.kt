package com.example.mathq.data

import android.util.Log
import com.example.mathq.models.LeaderboardUser
import com.example.mathq.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

object AuthRepository {
    private val db = FirebaseDatabase.getInstance()
    private val userRef = db.getReference("User")
    private val leaderboardRef = db.getReference("Leaderboard")

    // ... registerUser and loginUser functions remain the same ...

    fun registerUser(name: String, email: String, password: String, onResult: (String) -> Unit) {
        val userId = userRef.push().key ?: return
        val currentTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val newUser = User(userId, name, email, password, 0, 0, 0, 0, 0, currentTime)
        userRef.child(userId).setValue(newUser)
            .addOnSuccessListener { onResult("Registered successfully!") }
            .addOnFailureListener { onResult("Database error: ${it.message}") }
    }

    fun loginUser(
        email: String,
        password: String,
        onResult: (isSuccess: Boolean, userId: String?) -> Unit
    ) {
        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnap = snapshot.children.first()
                        if (password == userSnap.child("password").getValue(String::class.java)) {
                            onResult(true, userSnap.key)
                        } else {
                            onResult(false, null)
                        }
                    } else {
                        onResult(false, null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false, null)
                }
            })
    }


    /**
     * ✅ FIX: This single function now updates ALL leaderboards.
     * It updates the top 10 for each difficulty and the top 3 for "All Time".
     */
    fun updateAllLeaderboards() {
        // Helper function to fetch and update a specific leaderboard
        fun updateMode(mode: String, scoreKey: String, limit: Int) {
            userRef.orderByChild(scoreKey).limitToLast(limit).get()
                .addOnSuccessListener { snapshot ->
                    val topUsers = snapshot.children.mapNotNull { snap ->
                        val name = snap.child("name").getValue(String::class.java)
                        val score = snap.child(scoreKey).getValue(Int::class.java)
                        if (name != null && score != null && score > 0) {
                            mapOf("name" to name, "score" to score)
                        } else null
                    }.sortedByDescending { user -> user["score"] as Int }

                    leaderboardRef.child(mode).setValue(topUsers)
                }
        }

        // Update difficulty-specific leaderboards (Top 10)
        updateMode("easy", "highest_easy_score", 10)
        updateMode("normal", "highest_normal_score", 10)
        updateMode("hard", "highest_hard_score", 10)

        // ✅ NEW: Update the "All Time" leaderboard (Top 3)
        updateMode("all_time", "highest_score", 3)
    }

    // getLeaderboardData function remains the same, it's used by the UI.
    fun getLeaderboardData(
        callback: (List<LeaderboardUser>, List<LeaderboardUser>, List<LeaderboardUser>) -> Unit
    ) {
        leaderboardRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var easyList = listOf<LeaderboardUser>()
                var normalList = listOf<LeaderboardUser>()
                var hardList = listOf<LeaderboardUser>()

                if (snapshot.exists()) {
                    easyList = snapshot.child("Easy").children.mapNotNull { child ->
                        val name = child.child("name").getValue(String::class.java)
                        val score = child.child("score").getValue(Int::class.java)
                        if (name != null && score != null) LeaderboardUser(name, score) else null
                    }

                    normalList = snapshot.child("Normal").children.mapNotNull { child ->
                        val name = child.child("name").getValue(String::class.java)
                        val score = child.child("score").getValue(Int::class.java)
                        if (name != null && score != null) LeaderboardUser(name, score) else null
                    }

                    hardList = snapshot.child("Hard").children.mapNotNull { child ->
                        val name = child.child("name").getValue(String::class.java)
                        val score = child.child("score").getValue(Int::class.java)
                        if (name != null && score != null) LeaderboardUser(name, score) else null
                    }
                }

                callback(easyList, normalList, hardList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Leaderboard", "Failed to fetch data: ${error.message}")
            }
        })
    }


    fun fetchMode(scoreKey: String, onComplete: (List<LeaderboardUser>) -> Unit) {
        userRef.orderByChild(scoreKey).limitToLast(10)
            .get()
            .addOnSuccessListener { snapshot ->
                val topUsers = snapshot.children.mapNotNull { snap ->
                    val name = snap.child("name").getValue(String::class.java)
                    val score = snap.child(scoreKey).getValue(Int::class.java)

                    if (name != null && score != null && score > 0) {
                        LeaderboardUser(name, score)
                    } else {
                        null
                    }
                }.sortedByDescending { it.score }

                Log.d("Leaderboard", "Loaded $scoreKey: ${topUsers.size} users")
                onComplete(topUsers)
            }
            .addOnFailureListener { exception ->
                Log.e("Leaderboard", "Failed to fetch $scoreKey: ${exception.message}")
                onComplete(emptyList())
            }
    }
}