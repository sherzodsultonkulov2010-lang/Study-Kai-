package com.example.data.curriculum

data class QuizQuestion(
    val id: Int,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class ActiveTestSession(
    val subject: String, // "ENGLISH" or "MATH"
    val categoryKey: String, // "A1".."C2" or "GRADE_0".."GRADE_11"
    val testIndex: Int,
    val title: String,
    val subtitle: String,
    val questions: List<QuizQuestion>
)
