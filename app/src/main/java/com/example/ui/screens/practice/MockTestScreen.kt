package com.example.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MockTestEntity
import com.example.data.model.QuestionEntity
import com.example.ui.theme.GateError
import com.example.ui.theme.GatePrimaryLight
import com.example.ui.theme.GateSuccess
import com.example.ui.theme.GateWarning
import com.example.ui.viewmodel.GateViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockTestScreen(
    testId: String,
    viewModel: GateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val allQuestions by viewModel.questions.collectAsStateWithLifecycle()

    val currentTest = mockTests.firstOrNull { it.id == testId } ?: mockTests.firstOrNull()
    val testQuestions = remember(allQuestions, currentTest) {
        if (currentTest == null) emptyList()
        else {
            val qStream = currentTest.stream
            val pool = if (qStream == "BOTH") allQuestions else allQuestions.filter { it.stream == qStream }
            if (pool.isNotEmpty()) pool else allQuestions
        }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var secondsRemaining by remember(currentTest) {
        mutableIntStateOf((currentTest?.durationMinutes ?: 60) * 60)
    }
    var isSubmitted by remember { mutableStateOf(false) }
    var showSubmitConfirmation by remember { mutableStateOf(false) }

    // Map of questionIndex to user answers list
    val userAnswers = remember { mutableStateMapOf<Int, List<String>>() }

    // Timer countdown
    LaunchedEffect(isSubmitted) {
        if (!isSubmitted) {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
            }
            if (secondsRemaining <= 0) {
                isSubmitted = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(currentTest?.title ?: "GATE Mock Test", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        val mins = secondsRemaining / 60
                        val secs = secondsRemaining % 60
                        Text(
                            text = if (isSubmitted) "Test Completed" else "Time Remaining: ${String.format("%02d:%02d", mins, secs)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (mins < 5 && !isSubmitted) GateError else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isSubmitted) {
                        Button(
                            onClick = { showSubmitConfirmation = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Submit Test")
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (isSubmitted) {
            // Result View
            MockTestResultView(
                test = currentTest,
                questions = testQuestions,
                userAnswers = userAnswers,
                onRetake = {
                    isSubmitted = false
                    secondsRemaining = (currentTest?.durationMinutes ?: 60) * 60
                    userAnswers.clear()
                    currentIndex = 0
                },
                onExit = onNavigateBack,
                modifier = Modifier.padding(paddingValues)
            )
        } else if (testQuestions.isNotEmpty()) {
            val q = testQuestions[currentIndex]
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Question Palette
                Text("Question Palette", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(testQuestions) { index, item ->
                        val isAnswered = userAnswers.containsKey(index) && userAnswers[index]?.isNotEmpty() == true
                        val isCurrent = index == currentIndex
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isCurrent -> MaterialTheme.colorScheme.primary
                                        isAnswered -> GateSuccess
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .clickable { currentIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent || isAnswered) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Question Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Question ${currentIndex + 1} of ${testQuestions.size}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${q.questionType} • ${q.marks} Mark(s) (Neg: -${if (q.questionType == "MCQ") q.negativeMarks else 0.0f})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render Options
                        if (q.questionType == "NAT") {
                            var natVal by remember(currentIndex) {
                                mutableStateOf(userAnswers[currentIndex]?.firstOrNull() ?: "")
                            }
                            OutlinedTextField(
                                value = natVal,
                                onValueChange = {
                                    natVal = it
                                    if (it.isNotBlank()) userAnswers[currentIndex] = listOf(it)
                                    else userAnswers.remove(currentIndex)
                                },
                                label = { Text("Enter Numeric Answer") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            val options = q.optionsJson
                                .removeSurrounding("[", "]")
                                .split("\", \"", "\",\"", "\",")
                                .map { it.replace("\"", "").trim() }
                                .filter { it.isNotBlank() }

                            options.forEach { opt ->
                                val key = opt.take(1).uppercase()
                                val selected = userAnswers[currentIndex]?.contains(key) == true

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (q.questionType == "MCQ") {
                                                userAnswers[currentIndex] = listOf(key)
                                            } else {
                                                val currentList = userAnswers[currentIndex]?.toMutableList() ?: mutableListOf()
                                                if (currentList.contains(key)) currentList.remove(key)
                                                else currentList.add(key)
                                                userAnswers[currentIndex] = currentList
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (q.questionType == "MCQ") {
                                            RadioButton(selected = selected, onClick = null)
                                        } else {
                                            Checkbox(checked = selected, onCheckedChange = null)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = opt, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                // Prev / Next Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    if (currentIndex < testQuestions.size - 1) {
                        Button(
                            onClick = { currentIndex++ },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = { showSubmitConfirmation = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GateSuccess),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Finish Test")
                        }
                    }
                }
            }
        }

        if (showSubmitConfirmation) {
            val answeredCount = userAnswers.size
            val unattemptedCount = (testQuestions.size - answeredCount).coerceAtLeast(0)

            AlertDialog(
                onDismissRequest = { showSubmitConfirmation = false },
                title = { Text("Submit Mock Test?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("You have answered $answeredCount questions and left $unattemptedCount unattempted. Do you want to submit and view your score analysis?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSubmitConfirmation = false
                            isSubmitted = true
                            // Submit to ViewModel
                            var correct = 0
                            var wrong = 0
                            var score = 0f

                            testQuestions.forEachIndexed { idx, q ->
                                val ans = userAnswers[idx]
                                if (ans != null && ans.isNotEmpty()) {
                                    val correctAnswers = q.correctAnswersJson
                                        .replace("[", "")
                                        .replace("]", "")
                                        .replace("\"", "")
                                        .split(",")
                                        .map { it.trim().uppercase() }

                                    val isQCorrect = when (q.questionType) {
                                        "MCQ" -> correctAnswers.contains(ans.first().trim().uppercase())
                                        "MSQ" -> ans.map { it.trim().uppercase() }.toSet() == correctAnswers.toSet()
                                        "NAT" -> {
                                            val uVal = ans.first().toDoubleOrNull()
                                            uVal != null && correctAnswers.any { it.toDoubleOrNull()?.let { c -> kotlin.math.abs(c - uVal) < 0.05 } ?: false }
                                        }
                                        else -> false
                                    }

                                    if (isQCorrect) {
                                        correct++
                                        score += q.marks
                                    } else {
                                        wrong++
                                        if (q.questionType == "MCQ") {
                                            score -= q.negativeMarks
                                        }
                                    }
                                }
                            }

                            val totalAttempted = correct + wrong
                            val accuracy = if (totalAttempted > 0) (correct.toFloat() * 100) / totalAttempted else 0f
                            currentTest?.let {
                                viewModel.submitMockTest(it.id, score.coerceAtLeast(0f), accuracy, correct, wrong, unattemptedCount)
                            }
                        }
                    ) {
                        Text("Confirm Submit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubmitConfirmation = false }) {
                        Text("Resume Test")
                    }
                }
            )
        }
    }
}

@Composable
private fun MockTestResultView(
    test: MockTestEntity?,
    questions: List<QuestionEntity>,
    userAnswers: Map<Int, List<String>>,
    onRetake: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var correctCount = 0
    var wrongCount = 0
    var totalScore = 0f

    questions.forEachIndexed { idx, q ->
        val ans = userAnswers[idx]
        if (ans != null && ans.isNotEmpty()) {
            val correctAnswers = q.correctAnswersJson
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(",")
                .map { it.trim().uppercase() }

            val isQCorrect = when (q.questionType) {
                "MCQ" -> correctAnswers.contains(ans.first().trim().uppercase())
                "MSQ" -> ans.map { it.trim().uppercase() }.toSet() == correctAnswers.toSet()
                "NAT" -> {
                    val uVal = ans.first().toDoubleOrNull()
                    uVal != null && correctAnswers.any { it.toDoubleOrNull()?.let { c -> kotlin.math.abs(c - uVal) < 0.05 } ?: false }
                }
                else -> false
            }

            if (isQCorrect) {
                correctCount++
                totalScore += q.marks
            } else {
                wrongCount++
                if (q.questionType == "MCQ") {
                    totalScore -= q.negativeMarks
                }
            }
        }
    }

    val totalAttempted = correctCount + wrongCount
    val unattempted = questions.size - totalAttempted
    val accuracy = if (totalAttempted > 0) (correctCount.toFloat() * 100) / totalAttempted else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("TEST RESULT & SCORE ANALYSIS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${String.format("%.2f", totalScore.coerceAtLeast(0f))} / ${test?.totalMarks ?: 100}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Accuracy: ${String.format("%.1f", accuracy)}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (accuracy >= 75) GateSuccess else GateWarning
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResultStatCard(title = "Correct", value = "$correctCount", color = GateSuccess, modifier = Modifier.weight(1f))
                ResultStatCard(title = "Wrong", value = "$wrongCount", color = GateError, modifier = Modifier.weight(1f))
                ResultStatCard(title = "Skipped", value = "$unattempted", color = MaterialTheme.colorScheme.outline, modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = onRetake, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                    Text("Retake Test")
                }
                OutlinedButton(onClick = onExit, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                    Text("Back to Practice")
                }
            }
        }
    }
}

@Composable
private fun ResultStatCard(title: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
