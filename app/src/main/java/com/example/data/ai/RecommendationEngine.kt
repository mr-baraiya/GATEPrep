package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.AiRecommendation
import com.example.data.model.MockTestEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TopicEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RecommendationEngine {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    suspend fun getRecommendations(
        topics: List<TopicEntity>,
        questions: List<QuestionEntity>,
        mockTests: List<MockTestEntity>,
        stream: String
    ): List<AiRecommendation> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (!apiKey.isNullOrBlank()) {
            try {
                val geminiRecs = callGeminiApi(apiKey, topics, questions, mockTests, stream)
                if (geminiRecs.isNotEmpty()) {
                    return@withContext geminiRecs
                }
            } catch (e: Exception) {
                // Fall back gracefully to smart heuristic engine
            }
        }

        generateHeuristicRecommendations(topics, questions, mockTests, stream)
    }

    fun generateHeuristicRecommendations(
        topics: List<TopicEntity>,
        questions: List<QuestionEntity>,
        mockTests: List<MockTestEntity>,
        stream: String
    ): List<AiRecommendation> {
        val recommendations = mutableListOf<AiRecommendation>()

        // 1. Weak Topics Analysis
        val weakTopics = topics.filter {
            (stream == "BOTH" || it.stream == stream) &&
            (it.difficultyLevel == "WEAK" || (!it.isCompleted && it.revisionCount == 0))
        }

        if (weakTopics.isNotEmpty()) {
            val topWeak = weakTopics.first()
            recommendations.add(
                AiRecommendation(
                    id = "rec_weak_${topWeak.id}",
                    stream = topWeak.stream,
                    subjectName = getSubjectNameFromId(topWeak.subjectId),
                    topicName = topWeak.title,
                    suggestedQuestionType = "MCQ",
                    urgency = "HIGH_PRIORITY",
                    rationale = "You flagged '${topWeak.title}' as a weak or unreviewed area. In GATE ${topWeak.stream}, this topic carries consistent 2-mark weightage and foundational prerequisites for advanced problems.",
                    actionText = "Review Short Notes & Practice 5 Foundational MCQs",
                    targetAccuracyGoal = 80,
                    source = "GATE Diagnostic Heuristic"
                )
            )
        }

        // 2. Question Types & Mistake Analysis (MSQ & NAT focus)
        val streamQuestions = questions.filter { stream == "BOTH" || it.stream == stream }
        val mistakeQuestions = streamQuestions.filter { it.isMistake || (it.isAttempted && !it.isCorrect) }
        val msqQuestions = streamQuestions.filter { it.questionType == "MSQ" }
        val msqAttempted = msqQuestions.filter { it.isAttempted }
        val msqCorrect = msqAttempted.count { it.isCorrect }

        if (mistakeQuestions.isNotEmpty()) {
            val topMistake = mistakeQuestions.first()
            recommendations.add(
                AiRecommendation(
                    id = "rec_mistake_${topMistake.id}",
                    stream = topMistake.stream,
                    subjectName = topMistake.subjectName,
                    topicName = topMistake.topic,
                    suggestedQuestionType = topMistake.questionType,
                    urgency = "HIGH_PRIORITY",
                    rationale = "You previously missed a ${topMistake.year} ${topMistake.questionType} question in '${topMistake.subjectName} (${topMistake.topic})'. Re-attempting this question and reviewing its derivation prevents recurring conceptual errors.",
                    actionText = "Resolve from Mistake Notebook",
                    targetAccuracyGoal = 90,
                    source = "Mistake Notebook Analyzer"
                )
            )
        }

        if (msqAttempted.isNotEmpty() && (msqCorrect.toFloat() / msqAttempted.size) < 0.6f) {
            val focusTopic = if (stream == "DA") "Machine Learning & AI" else "Theory of Computation"
            recommendations.add(
                AiRecommendation(
                    id = "rec_msq_accuracy",
                    stream = if (stream == "BOTH") "CSE" else stream,
                    subjectName = if (stream == "DA") "Machine Learning" else "Theory of Computation",
                    topicName = focusTopic,
                    suggestedQuestionType = "MSQ",
                    urgency = "MODERATE",
                    rationale = "Multiple Select Questions (MSQs) carry no negative marking, but require complete precision across all options. Your current MSQ accuracy is below 60%. Focusing on theoretical boundary conditions will boost your rank.",
                    actionText = "Practice 10 High-Yield MSQ Sets",
                    targetAccuracyGoal = 85,
                    source = "Pattern & Question Type Engine"
                )
            )
        } else {
            // NAT Recommendation
            val targetSubject = if (stream == "DA") "Probability & Statistics" else "Algorithms"
            val targetTopic = if (stream == "DA") "Bayes Rule & Distributions" else "Dynamic Programming & Recurrences"
            recommendations.add(
                AiRecommendation(
                    id = "rec_nat_target",
                    stream = if (stream == "BOTH") "DA" else stream,
                    subjectName = targetSubject,
                    topicName = targetTopic,
                    suggestedQuestionType = "NAT",
                    urgency = "MODERATE",
                    rationale = "Numerical Answer Type (NAT) questions test exact mathematical computation without option elimination. Mastering formulas in $targetTopic guarantees solid 2-mark gains.",
                    actionText = "Practice Numerical Calculations & Tolerances",
                    targetAccuracyGoal = 80,
                    source = "GATE Syllabus Optimization"
                )
            )
        }

        // 3. Mock Test Benchmark Recommendation
        val completedMocks = mockTests.filter { it.isCompleted && (stream == "BOTH" || it.stream == stream || it.stream == "BOTH") }
        if (completedMocks.isNotEmpty()) {
            val latestMock = completedMocks.maxByOrNull { it.completedAtMillis } ?: completedMocks.first()
            if (latestMock.wrongCount > 8) {
                recommendations.add(
                    AiRecommendation(
                        id = "rec_mock_negative_marks",
                        stream = latestMock.stream,
                        subjectName = "Speed & Negative Marking Control",
                        topicName = "Elimination & Risk Assessment",
                        suggestedQuestionType = "MCQ",
                        urgency = "HIGH_PRIORITY",
                        rationale = "In '${latestMock.title}', ${latestMock.wrongCount} questions resulted in negative mark penalties (~${String.format("%.2f", latestMock.wrongCount * 0.5f)} marks lost). Aim to leave uncertain 1/3 penalty MCQs unattempted.",
                        actionText = "Review Exam Strategy & Analysis",
                        targetAccuracyGoal = 85,
                        source = "Mock Test Performance Engine"
                    )
                )
            }
        }

        return recommendations.take(3)
    }

    private fun callGeminiApi(
        apiKey: String,
        topics: List<TopicEntity>,
        questions: List<QuestionEntity>,
        mockTests: List<MockTestEntity>,
        stream: String
    ): List<AiRecommendation> {
        val completedTopicsCount = topics.count { it.isCompleted }
        val weakTopicsNames = topics.filter { it.difficultyLevel == "WEAK" }.take(3).map { it.title }
        val mistakeCount = questions.count { it.isMistake }
        val recentScore = mockTests.filter { it.isCompleted }.map { it.score }.average().let {
            if (it.isNaN()) 0.0 else it
        }

        val prompt = """
            You are an expert GATE preparation mentor for GATE 2027 CSE & DA.
            Analyze this student profile:
            - Target Stream: $stream
            - Completed Topics: $completedTopicsCount / ${topics.size}
            - Identified Weak Topics: $weakTopicsNames
            - Total Mistakes in PYQ: $mistakeCount
            - Mock Test Avg Score: $recentScore
            
            Return a JSON array of 3 prioritized recommendations. Each item must have:
            - id (string)
            - stream (string: "CSE" or "DA")
            - subjectName (string)
            - topicName (string)
            - suggestedQuestionType (string: "MCQ", "MSQ", or "NAT")
            - urgency (string: "HIGH_PRIORITY", "MODERATE", or "MAINTENANCE")
            - rationale (string: concise explanation with GATE weightage rationale)
            - actionText (string: short actionable recommendation)
            - targetAccuracyGoal (int: between 75 and 95)
            
            Return ONLY valid JSON array with no markdown code blocks.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return emptyList()

        val responseBody = response.body?.string() ?: return emptyList()
        val json = JSONObject(responseBody)
        val text = json.getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")

        val cleanJson = text.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val array = JSONArray(cleanJson)
        val list = mutableListOf<AiRecommendation>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                AiRecommendation(
                    id = obj.optString("id", "gemini_rec_$i"),
                    stream = obj.optString("stream", stream),
                    subjectName = obj.optString("subjectName", "Core Subject"),
                    topicName = obj.optString("topicName", "High-Weightage Topic"),
                    suggestedQuestionType = obj.optString("suggestedQuestionType", "MCQ"),
                    urgency = obj.optString("urgency", "HIGH_PRIORITY"),
                    rationale = obj.optString("rationale", "Crucial topic for GATE score improvement."),
                    actionText = obj.optString("actionText", "Solve 5 focused practice problems"),
                    targetAccuracyGoal = obj.optInt("targetAccuracyGoal", 85),
                    source = "Gemini 3.5 Flash AI"
                )
            )
        }
        return list
    }

    private fun getSubjectNameFromId(subjectId: String): String = when (subjectId) {
        "cse_em" -> "Engineering Mathematics"
        "cse_dl" -> "Digital Logic"
        "cse_coa" -> "Computer Organization & Architecture"
        "cse_pds" -> "Programming & Data Structures"
        "cse_algo" -> "Algorithms"
        "cse_toc" -> "Theory of Computation"
        "cse_cd" -> "Compiler Design"
        "cse_os" -> "Operating Systems"
        "cse_dbms" -> "Databases"
        "cse_cn" -> "Computer Networks"
        "da_ps" -> "Probability & Statistics"
        "da_la" -> "Linear Algebra"
        "da_calc" -> "Calculus & Optimization"
        "da_pdsa" -> "Programming, DS & Algorithms"
        "da_db" -> "Database Management"
        "da_ml" -> "Machine Learning"
        "da_ai" -> "Artificial Intelligence"
        "da_dl" -> "Deep Learning & Neural Networks"
        else -> "Core GATE Subject"
    }
}
