package com.example.mathq.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import checkAndSendNotifications
import com.example.mathq.common.AppBackground
import com.example.mathq.data.AuthRepository
import com.example.mathq.game.QuestionGenerator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay

@Composable
fun EasyGameScreen(userId: String, navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseDatabase.getInstance() // Get database instance

    var score by remember { mutableStateOf(0) }
    var wrongAnswers by remember { mutableStateOf(0) }
    val maxWrong = 3
    var currentQuestion by remember { mutableStateOf(QuestionGenerator.generateEasyQuestion()) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    fun endGame() {
        if (isGameOver) return
        isGameOver = true

        val userRef = db.getReference("User").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val oldTotal = snapshot.child("total_score").getValue(Int::class.java) ?: 0
            val oldHigh = snapshot.child("highest_score").getValue(Int::class.java) ?: 0
            val oldEasyHigh = snapshot.child("highest_easy_score").getValue(Int::class.java) ?: 0
            val username = snapshot.child("username").getValue(String::class.java) ?: "Unknown Player"

            val updates = mutableMapOf<String, Any>()
            updates["total_score"] = oldTotal + score

            var beatGlobalHighScore = false
            var beatEasyHighScore = false

            if (score > oldHigh) {
                updates["highest_score"] = score
                beatGlobalHighScore = true
            }
            if (score > oldEasyHigh) {
                updates["highest_easy_score"] = score
                beatEasyHighScore = true
            }

            if (updates.isNotEmpty()) {
                userRef.updateChildren(updates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Check if we need to notify others that this user beat their score
                        checkAndSendNotifications(
                            userId = userId,
                            userName = username, // Replace with real username if available
                            currentScore = score,
                            beatGlobalHighScore = beatGlobalHighScore,
                            beatEasyHighScore = beatEasyHighScore,
                            beatNormalHighScore = false, // Hard mode doesn't affect normal high score
                            beatHardHighScore = false,   // So set these to false
                            scoreType = "easy"
                        )
                        AuthRepository.updateAllLeaderboards()
                    }
                    Toast.makeText(context, "Game Over! Final Score: $score", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }
            } else {
                Toast.makeText(context, "Game Over! Final Score: $score", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
        }
    }

    LaunchedEffect(wrongAnswers) {
        if (wrongAnswers >= maxWrong) {
            endGame()
        }
    }

    LaunchedEffect(selectedOption) {
        if (selectedOption != null) {
            delay(1000)
            if (!isGameOver) {
                currentQuestion = QuestionGenerator.generateEasyQuestion()
                selectedOption = null
                isAnswered = false
            }
        }
    }

    fun handleAnswer(option: String) {
        if (isAnswered) return
        isAnswered = true
        selectedOption = option

        if (option == currentQuestion.answer) {
            score += 10
        } else {
            wrongAnswers++
        }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Score: $score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Wrong: $wrongAnswers / $maxWrong", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(currentQuestion.question, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))

            currentQuestion.options.forEach { option ->
                val isSelected = selectedOption == option
                val isCorrect = currentQuestion.answer == option
                val buttonColor by animateColorAsState(
                    targetValue = if (isAnswered && isCorrect) Color(0xFF4CAF50) else if (isAnswered && isSelected) Color(0xFFF44336) else MaterialTheme.colorScheme.surface,
                    label = "button color"
                )
                Button(
                    onClick = { handleAnswer(option) },
                    modifier = Modifier.fillMaxWidth().height(60.dp).padding(vertical = 4.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    enabled = !isAnswered
                ) {
                    Text(option, fontSize = 20.sp, color = if (isAnswered) Color.White else MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}