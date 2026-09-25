package com.example.ui.screens.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiRecommendationCard
import com.example.ui.components.CountdownCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.StreamSelectorChips
import com.example.ui.theme.GateError
import com.example.ui.theme.GatePrimaryLight
import com.example.ui.theme.GateSecondaryLight
import com.example.ui.theme.GateSuccess
import com.example.ui.theme.GateTertiaryLight
import com.example.ui.viewmodel.GateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GateViewModel,
    onNavigateToExamInfo: () -> Unit,
    onNavigateToStudyPlanner: () -> Unit,
    onNavigateToPyq: () -> Unit,
    onNavigateToMockTest: () -> Unit,
    onNavigateToRevision: () -> Unit,
    onNavigateToFormulas: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAiRecs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val countdown by viewModel.countdown.collectAsStateWithLifecycle()
    val selectedStream by viewModel.selectedStream.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val aiRecommendations by viewModel.aiRecommendations.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (aiRecommendations.isEmpty()) {
            viewModel.refreshAiRecommendations()
        }
    }

    val solvedQuestionsCount = questions.count { it.isAttempted }
    val correctQuestionsCount = questions.count { it.isCorrect }
    val accuracy = if (solvedQuestionsCount > 0) (correctQuestionsCount * 100) / solvedQuestionsCount else 0

    val completedMocks = mockTests.filter { it.isCompleted }
    val avgMockScore = if (completedMocks.isNotEmpty()) {
        completedMocks.map { it.score }.average()
    } else 0.0

    val totalTopics = topics.size.coerceAtLeast(1)
    val completedTopics = topics.count { it.isCompleted }
    val prepPercentage = (completedTopics * 100) / totalTopics

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "GATE 2027 Prep",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Master CSE & Data Science / AI",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier.testTag("action_search")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(
                        onClick = onNavigateToExamInfo,
                        modifier = Modifier.testTag("action_exam_info")
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Exam Info")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stream Selector (CSE / DA / Both)
            item {
                StreamSelectorChips(
                    selectedStream = selectedStream,
                    onSelectStream = { viewModel.setStream(it) }
                )
            }

            // Big readable countdown
            item {
                CountdownCard(
                    countdown = countdown,
                    onViewMilestonesClick = onNavigateToExamInfo
                )
            }

            // Target & Streak Progress Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = GateTertiaryLight,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${userStats?.currentStreakDays ?: 1}-Day Streak",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GateSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$prepPercentage% Syllabus",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = GateSuccess
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { prepPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Today's Target: ${userStats?.dailyStudyGoalMinutes?.div(60) ?: 3} hrs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$completedTopics of $totalTopics topics done",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Stats 2x2 Grid
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Questions Solved",
                        value = "$solvedQuestionsCount",
                        subtitle = "$accuracy% accuracy",
                        icon = Icons.Default.Quiz,
                        iconTint = GatePrimaryLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Avg Mock Score",
                        value = String.format("%.1f", avgMockScore),
                        subtitle = "${completedMocks.size} tests taken",
                        icon = Icons.Default.Timer,
                        iconTint = GateSecondaryLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Actions Navigation Matrix
            item {
                SectionHeader(
                    title = "Quick Prep Actions",
                    subtitle = "High-priority revision & practice tools"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Default.Description,
                        label = "Study Planner",
                        color = GatePrimaryLight,
                        onClick = onNavigateToStudyPlanner,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.Quiz,
                        label = "Solve PYQs",
                        color = GateSecondaryLight,
                        onClick = onNavigateToPyq,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.Timer,
                        label = "Mock Test",
                        color = GateTertiaryLight,
                        onClick = onNavigateToMockTest,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Default.Repeat,
                        label = "Revision",
                        color = Color(0xFF6366F1),
                        onClick = onNavigateToRevision,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.Calculate,
                        label = "Formulas",
                        color = Color(0xFFEC4899),
                        onClick = onNavigateToFormulas,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.EditNote,
                        label = "My Notes",
                        color = Color(0xFF14B8A6),
                        onClick = onNavigateToNotes,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // AI Recommendation Engine Section
            item {
                SectionHeader(
                    title = "AI Focus Recommendation",
                    subtitle = "Adaptive guidance based on your accuracy & weightage",
                    actionText = if (isAiLoading) "Analyzing..." else "Refresh",
                    onActionClick = { viewModel.refreshAiRecommendations() }
                )

                if (isAiLoading) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Evaluating syllabus weightage & performance...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else if (aiRecommendations.isNotEmpty()) {
                    aiRecommendations.take(2).forEach { rec ->
                        AiRecommendationCard(
                            recommendation = rec,
                            onActionClick = {
                                if (rec.suggestedQuestionType.isNotEmpty()) {
                                    onNavigateToPyq()
                                } else {
                                    onNavigateToStudyPlanner()
                                }
                            }
                        )
                    }
                }
            }

            // Motivational Tip
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "\"Consistent solving of 10 high-quality PYQs daily with error analysis will deliver more score improvement than 100 pages of passive reading.\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("action_${label.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
