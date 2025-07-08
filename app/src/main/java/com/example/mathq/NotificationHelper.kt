import android.icu.text.SimpleDateFormat
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import models.NotificationItem
import java.sql.Date
import java.util.UUID
import java.util.Locale

fun checkAndSendNotifications(
    userId: String,
    userName: String,
    currentScore: Int,
    beatGlobalHighScore: Boolean,
    beatHardHighScore: Boolean,
    beatNormalHighScore: Boolean,
    beatEasyHighScore: Boolean,
    scoreType: String
) {
    val notifRef = FirebaseDatabase.getInstance().getReference("Notifications")
    val usersRef = FirebaseDatabase.getInstance().getReference("User")

    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("NotificationDebug", "Sending notification from user: $userName")
            for (userSnapshot in snapshot.children) {
                val otherUserId = userSnapshot.key ?: continue
                if (otherUserId == userId) continue // Skip self

                // Global High Score
                val otherGlobalHigh = userSnapshot.child("highest_score").getValue(Int::class.java) ?: 0
                if (beatGlobalHighScore && currentScore > otherGlobalHigh) {
                    sendNotification(notifRef, otherUserId, userId, userName, "global")
                }

                // Hard Mode High Score
                val otherHardHigh = userSnapshot.child("highest_hard_score").getValue(Int::class.java) ?: 0
                if (beatHardHighScore && currentScore > otherHardHigh) {
                    sendNotification(notifRef, otherUserId, userId, userName, "hard")
                }

                // Normal Mode High Score
                val otherNormalHigh = userSnapshot.child("highest_normal_score").getValue(Int::class.java) ?: 0
                if (beatNormalHighScore && currentScore > otherNormalHigh) {
                    sendNotification(notifRef, otherUserId, userId, userName, "normal")
                }

                // Easy Mode High Score
                val otherEasyHigh = userSnapshot.child("highest_easy_score").getValue(Int::class.java) ?: 0
                if (beatEasyHighScore && currentScore > otherEasyHigh) {
                    sendNotification(notifRef, otherUserId, userId, userName, "easy")
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Database error: ${error.message}")
        }
    })
}

private fun sendNotification(
    notifRef: DatabaseReference,
    targetUserId: String,
    scorerId: String,
    userName: String,
    scoreType: String
) {
    val newNotif = NotificationItem(
        id = notifRef.push().key ?: UUID.randomUUID().toString(),
        title = "High Score Beaten",
        message = "User $userName beat your high score!",
        timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(java.util.Date())
    )

    notifRef.child(targetUserId).child(newNotif.id).setValue(newNotif)
}