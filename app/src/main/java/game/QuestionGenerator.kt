package com.example.mathq.game

import com.example.mathq.models.Question
import java.util.Random

object QuestionGenerator {
    // ... generateEasyQuestion() and generateNormalQuestion() remain the same ...

    fun generateEasyQuestion(): Question {
        val random = Random()
        val a = random.nextInt(20) + 1
        val b = random.nextInt(20) + 1
        val correctAnswer: Int
        val questionText: String

        if (random.nextBoolean()) {
            correctAnswer = a + b
            questionText = "$a + $b = ?"
        } else {
            if (a < b) {
                correctAnswer = b - a
                questionText = "$b - $a = ?"
            } else {
                correctAnswer = a - b
                questionText = "$a - $b = ?"
            }
        }
        val options = generateOptions(correctAnswer, 10)
        return Question(questionText, options, correctAnswer.toString())
    }

    fun generateNormalQuestion(): Question {
        val random = Random()
        val op = listOf("+", "-", "×").random()
        val a: Int
        val b: Int
        val correctAnswer: Int
        val questionText: String

        when (op) {
            "+" -> {
                a = random.nextInt(50) + 10
                b = random.nextInt(50) + 10
                correctAnswer = a + b
                questionText = "$a + $b = ?"
            }
            "-" -> {
                a = random.nextInt(50) + 20
                b = random.nextInt(40) + 10
                if (a < b) {
                    correctAnswer = b - a
                    questionText = "$b - $a = ?"
                } else {
                    correctAnswer = a - b
                    questionText = "$a - $b = ?"
                }
            }
            else -> { // "×"
                a = random.nextInt(12) + 2
                b = random.nextInt(12) + 2
                correctAnswer = a * b
                questionText = "$a × $b = ?"
            }
        }
        val options = generateOptions(correctAnswer, 20)
        return Question(questionText, options, correctAnswer.toString())
    }

    // ✅ FIX: Using your more advanced hard question logic.
    fun generateHardQuestion(): Question {
        val random = Random()
        val operators = listOf("+", "-", "×", "÷")
        val op = operators.random()
        val a: Int
        val b: Int
        val questionText: String
        val correctAnswer: Int

        when (op) {
            "+" -> {
                a = random.nextInt(201) - 100 // -100 to 100
                b = random.nextInt(201) - 100
                correctAnswer = a + b
                questionText = "$a + $b = ?"
            }
            "-" -> {
                a = random.nextInt(201) - 100
                b = random.nextInt(201) - 100
                correctAnswer = a - b
                questionText = "$a - $b = ?"
            }
            "×" -> {
                a = random.nextInt(21) - 10 // -10 to 10
                b = random.nextInt(21) - 10
                correctAnswer = a * b
                questionText = "$a × $b = ?"
            }
            else -> { // "÷"
                val divisor = random.nextInt(10) + 1 // 1 to 10
                val result = random.nextInt(21) - 10  // -10 to 10
                a = divisor * result
                b = divisor
                correctAnswer = result
                questionText = "$a ÷ $b = ?"
            }
        }

        val options = mutableSetOf(correctAnswer)
        while (options.size < 4) {
            val wrong = correctAnswer + random.nextInt(21) - 10
            if (wrong != correctAnswer) {
                options.add(wrong)
            }
        }

        return Question(
            question = questionText,
            options = options.map { it.toString() }.shuffled(),
            answer = correctAnswer.toString()
        )
    }


    private fun generateOptions(correctAnswer: Int, range: Int): List<String> {
        val options = mutableSetOf(correctAnswer)
        val random = Random()
        while (options.size < 4) {
            val offset = random.nextInt(range * 2) - range
            val wrongAnswer = correctAnswer + offset
            if (wrongAnswer != correctAnswer && wrongAnswer >= 0) {
                options.add(wrongAnswer)
            }
        }
        return options.map { it.toString() }.shuffled()
    }
}
