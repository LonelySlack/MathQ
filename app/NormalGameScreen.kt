package com.example.mathq.screens

import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mathq.game.QuestionGenerator.generateNormalQuestion
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import com.example.mathq.model.Question

@Composable
fun NormalGameScreen(userId: String, navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseDatabase.getInstance()

    var score by remember { mutableStateOf(0) }
    var wrongAnswers by remember { mutableStateOf(0) }
    var currentQuestion by remember { mutableStateOf(generateNormalQuestion()) }
    var timeLeft by remember { mutableStateOf(60) }

    val maxWrong = 3
    val sessionId = remember { db.getReference("GameSession").push().key ?: UUID.randomUUID().toString() }
    val sessionRef = remember { db.getReference("GameSession").child(sessionId) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    fun endGame() {
        timer?.cancel()
        sessionRef.child("score").setValue(score)

        val userRef = db.getReference("User").child(userId)
        userRef.get().addOnSuccessListener {
            val oldTotal = it.child("total_score").getValue(Int::class.java) ?: 0
            val oldHigh = it.child("highest_score").getValue(Int::class.java) ?: 0
            val oldNormalHigh = it.child("highest_normal_score").getValue(Int::class.java) ?: 0

            userRef.child("total_score").setValue(oldTotal + score)

            if (score > oldHigh) {
                userRef.child("highest_score").setValue(score)
            }

            if (score > oldNormalHigh) {
                userRef.child("highest_normal_score").setValue(score)
            }
        }

        Toast.makeText(context, "Game Over. Score: $score", Toast.LENGTH_LONG).show()
        onFinish()
    }

    fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                wrongAnswers++
                timeLeft = 60
                if (wrongAnswers >= maxWrong) {
                    endGame()
                } else {
                    currentQuestion = generateNormalQuestion()
                    startTimer()
                }
            }
        }.start()
    }

    LaunchedEffect(Unit) {
        val sessionData = mapOf(
            "user_id" to userId,
            "score" to 0,
            "difficulty_level" to "normal",
            "session_date" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        sessionRef.setValue(sessionData)
        startTimer()
    }

    if (wrongAnswers >= maxWrong) {
        endGame()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Score: $score | Wrong: $wrongAnswers/$maxWrong", style = MaterialTheme.typography.bodyLarge)
        Text("Time Left: $timeLeft seconds", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(currentQuestion.question, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        currentQuestion.options.forEach { option ->
            Button(
                onClick = {
                    val isCorrect = option == currentQuestion.answer
                    if (isCorrect) score += 10 else wrongAnswers++

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
                    } else {
                        currentQuestion = generateNormalQuestion()
                        startTimer()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(option)
            }
        }
    }
}