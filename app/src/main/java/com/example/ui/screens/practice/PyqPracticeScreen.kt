package com.example.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.QuestionEntity
import com.example.ui.components.StreamSelectorChips
import com.example.ui.theme.GateError
import com.example.ui.theme.GatePrimaryLight
import com.example.ui.theme.GateSecondaryLight
import com.example.ui.theme.GateSuccess
import com.example.ui.theme.GateTertiaryLight
import com.example.ui.theme.GateWarning
import com.example.ui.viewmodel.GateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PyqPracticeScreen(
    viewModel: GateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedStream by viewModel.selectedStream.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()

    var selectedYear by remember { mutableStateOf("ALL") }
    var selectedType by remember { mutableStateOf("ALL") } // "ALL", "MCQ", "MSQ", "NAT"

    val filteredQuestions = questions.filter { q ->
        val matchStream = selectedStream == "BOTH" || q.stream == selectedStream
        val matchYear = selectedYear == "ALL" || q.year.toString() == selectedYear
        val matchType = selectedType == "ALL" || q.questionType == selectedType
        matchStream && matchYear && matchType
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GATE PYQ Practice", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StreamSelectorChips(
                selectedStream = selectedStream,
                onSelectStream = { viewModel.setStream(it) }
            )

            // Year & Question Type Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "2024", "2023", "2022").forEach { year ->
                    FilterChip(
                        selected = selectedYear == year,
                        onClick = { selectedYear = year },
                        label = { Text(if (year == "ALL") "All Years" else "GATE $year", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "MCQ", "MSQ", "NAT").forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (filteredQuestions.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No questions match this filter", fontWeight = FontWeight.Bold)
                                Text("Try changing stream, year, or question type above.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else {
                    items(filteredQuestions, key = { it.id }) { question ->
                        QuestionInteractiveCard(
                            question = question,
                            onBookmarkToggle = { viewModel.toggleQuestionBookmark(question.id, question.isBookmarked) },
                            onSubmit = { answers ->
                                viewModel.submitQuestionAnswer(
                                    question = question,
                                    selectedAnswers = answers,
                                    timeSpent = 45,
                                    onResult = {}
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun QuestionInteractiveCard(
    question: QuestionEntity,
    onBookmarkToggle: () -> Unit,
    onSubmit: (List<String>) -> Unit
) {
    // Parse options: JSON array like ["A. ...", "B. ..."]
    val optionsList = remember(question.optionsJson) {
        if (question.optionsJson.isBlank() || question.optionsJson == "[]") emptyList()
        else {
            question.optionsJson
                .removeSurrounding("[", "]")
                .split("\", \"", "\",\"", "\",")
                .map { it.replace("\"", "").trim() }
                .filter { it.isNotBlank() }
        }
    }

    var selectedOptions = remember(question.id) { mutableStateMapOf<String, Boolean>() }
    var natInputText by remember(question.id) { mutableStateOf("") }
    var hasRevealedAnswer by remember(question.id) { mutableStateOf(question.isAttempted) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_card_${question.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Tags: Year, Stream, Subject, Type, Marks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "GATE ${question.year} (${question.stream})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (question.questionType) {
                            "MCQ" -> GatePrimaryLight.copy(alpha = 0.15f)
                            "MSQ" -> Color(0xFF8B5CF6).copy(alpha = 0.15f)
                            else -> GateSecondaryLight.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = "${question.questionType} • ${question.marks}M",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = when (question.questionType) {
                                "MCQ" -> GatePrimaryLight
                                "MSQ" -> Color(0xFF8B5CF6)
                                else -> GateSecondaryLight
                            }
                        )
                    }
                }

                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (question.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (question.isBookmarked) GateTertiaryLight else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${question.subjectName} • ${question.topic}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Options handling (MCQ vs MSQ vs NAT)
            if (question.questionType == "NAT") {
                OutlinedTextField(
                    value = natInputText,
                    onValueChange = { natInputText = it },
                    label = { Text("Enter Numerical Value") },
                    singleLine = true,
                    enabled = !hasRevealedAnswer,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    optionsList.forEach { option ->
                        val optKey = option.take(1).uppercase()
                        val isChecked = selectedOptions[optKey] == true

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !hasRevealedAnswer) {
                                    if (question.questionType == "MCQ") {
                                        selectedOptions.clear()
                                        selectedOptions[optKey] = true
                                    } else {
                                        selectedOptions[optKey] = !isChecked
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (question.questionType == "MCQ") {
                                    RadioButton(
                                        selected = isChecked,
                                        onClick = null
                                    )
                                } else {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = null
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action / Submit button
            if (!hasRevealedAnswer) {
                Button(
                    onClick = {
                        val answers = if (question.questionType == "NAT") listOf(natInputText)
                        else selectedOptions.filter { it.value }.keys.toList()

                        if (answers.isNotEmpty()) {
                            hasRevealedAnswer = true
                            onSubmit(answers)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit & Verify Answer")
                }
            } else {
                // Result Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (question.isCorrect) GateSuccess.copy(alpha = 0.15f) else GateError.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (question.isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (question.isCorrect) GateSuccess else GateError
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (question.isCorrect) "Correct! +${question.marks} Marks" else "Incorrect! -${if (question.questionType == "MCQ") question.negativeMarks else 0.0f} Marks",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (question.isCorrect) GateSuccess else GateError
                            )
                            Text(
                                text = "Correct Answer: ${question.correctAnswersJson.replace("\"", "").replace("[", "").replace("]", "")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Detailed Explanation
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Detailed Solution & Derivation",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
