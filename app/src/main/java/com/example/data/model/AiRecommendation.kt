package com.example.data.model

data class AiRecommendation(
    val id: String,
    val stream: String,
    val subjectName: String,
    val topicName: String,
    val suggestedQuestionType: String, // "MCQ", "MSQ", "NAT", "ANY"
    val urgency: String, // "HIGH_PRIORITY", "MODERATE", "MAINTENANCE"
    val rationale: String,
    val actionText: String,
    val targetAccuracyGoal: Int = 85,
    val source: String = "AI Recommendation Engine"
)
