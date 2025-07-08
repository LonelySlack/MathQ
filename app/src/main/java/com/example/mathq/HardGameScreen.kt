package com.example.mathq.screens

import android.os.CountDownTimer
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
import com.example.mathq.common.AppBackground
import com.example.mathq.game.QuestionGenerator.generateHardQuestion
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import com.example.mathq.model.Question
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun HardGameScreen(userId: String, navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseDatabase.getInstance()

    var score by remember { mutableStateOf(0) }
    var wrongAnswers by remember { mutableStateOf(0) }
    var currentQuestion by remember { mutableStateOf(generateHardQuestion()) }
    var timeLeft by remember { mutableStateOf(30) }
    var isAnswered by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    val maxWrong = 3
    val sessionId = remember { db.getReference("GameSession").push().key ?: UUID.randomUUID().toString() }
    val sessionRef = remember { db.getReference("GameSession").child(sessionId) }

    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    fun endGame() {
        timer?.cancel()
        sessionRef.child("score").setValue(score)

        val userRef = db.getReference("User").child(userId)
        userRef.get().addOnSuccessListener {
            val oldTotal = it.child("total_score").getValue(Int::class.java) ?: 0
            val oldHigh = it.child("highest_score").getValue(Int::class.java) ?: 0
            val oldHardHigh = it.child("highest_hard_score").getValue(Int::class.java) ?: 0

            val updates = mutableMapOf<String, Any>()
            updates["total_score"] = oldTotal + score
            if (score > oldHigh) updates["highest_score"] = score
            if (score > oldHardHigh) updates["highest_hard_score"] = score

            if (updates.isNotEmpty()) {
                userRef.updateChildren(updates)
            }

            Toast.makeText(context, "Game Over. Final Score: $score", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer((timeLeft * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                wrongAnswers++
                if (wrongAnswers >= maxWrong) {
                    endGame()
                } else {
                    timeLeft = 30
                    currentQuestion = generateHardQuestion()
                    startTimer()
                }
            }
        }.start()
    }

    LaunchedEffect(Unit) {
        val sessionData = mapOf(
            "user_id" to userId,
            "score" to 0,
            "difficulty_level" to "hard",
            "session_date" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        sessionRef.setValue(sessionData)
        startTimer()
    }

    LaunchedEffect(selectedOption) {
        if (selectedOption != null) {
            launch {
                delay(1000)
                if (!isAnswered) return@launch
                currentQuestion = generateHardQuestion()
                selectedOption = null
                isAnswered = false
                startTimer()
            }
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

            Text("Time Left: $timeLeft seconds", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Text(currentQuestion.question, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.weight(1f))

            currentQuestion.options.forEach { option ->
                val isSelected = selectedOption == option
                val isCorrect = currentQuestion.answer == option
                val buttonColor by animateColorAsState(
                    targetValue = when {
                        isAnswered && isCorrect -> Color(0xFF4CAF50)
                        isAnswered && isSelected -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    label = "button color"
                )

                Button(
                    onClick = {
                        if (isAnswered) return@Button

                        isAnswered = true
                        selectedOption = option

                        val isCorrect = option == currentQuestion.answer

                        if (isCorrect) score += 20 else wrongAnswers++

                        // Log the answer
                        val logId = db.getReference("AnswerLog").push().key ?: UUID.randomUUID().toString()
                        val logData = mapOf(
                            "session_id" to sessionId,
                            "question_text" to currentQuestion.question,
                            "option_a" to currentQuestion.options[0],
                            "option_b" to currentQuestion.options[1],
                            "option_c" to currentQuestion.options[2],
                            "option_d" to currentQuestion.options[3],
                            "user_answer" to option,
                            "correct_answer" to currentQuestion.answer,
                            "is_correct" to isCorrect
                        )
                        db.getReference("AnswerLog").child(logId).setValue(logData)

                        if (wrongAnswers >= maxWrong) {
                            endGame()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    enabled = !isAnswered
                ) {
                    Text(
                        option,
                        fontSize = 20.sp,
                        color = if (isAnswered) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}