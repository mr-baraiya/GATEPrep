package com.example.ui.screens.revision

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.example.data.model.TopicEntity
import com.example.ui.components.SectionHeader
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
fun RevisionScreen(
    viewModel: GateViewModel,
    onNavigateToFlashcards: () -> Unit,
    onNavigateToFormulas: () -> Unit,
    onNavigateToMistakes: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedStream by viewModel.selectedStream.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val mistakeQuestions by viewModel.mistakeQuestions.collectAsStateWithLifecycle()
    val bookmarkedQuestions by viewModel.bookmarkedQuestions.collectAsStateWithLifecycle()

    val reviseTodayTopics = topics.filter { it.difficultyLevel == "WEAK" || (it.isCompleted && it.revisionCount < 2) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spaced Revision Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                StreamSelectorChips(
                    selectedStream = selectedStream,
                    onSelectStream = { viewModel.setStream(it) }
                )
            }

            // Quick Revision Tools Cards
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RevisionHubCard(
                        title = "Concept Flashcards",
                        subtitle = "Active recall cards for quick definitions, complexities & theorem conditions",
                        icon = Icons.Default.Psychology,
                        iconTint = GatePrimaryLight,
                        count = "${topics.size * 2} Cards",
                        onClick = onNavigateToFlashcards
                    )

                    RevisionHubCard(
                        title = "Formula Sheets & Quick Notes",
                        subtitle = "Summary tables for Math, OS, Networks, DBMS, ML & AI formulas",
                        icon = Icons.Default.Calculate,
                        iconTint = Color(0xFFEC4899),
                        count = "Core Formulas",
                        onClick = onNavigateToFormulas
                    )

                    RevisionHubCard(
                        title = "Mistake Notebook",
                        subtitle = "Critical vault of questions where you incurred negative marks",
                        icon = Icons.Default.Warning,
                        iconTint = GateError,
                        count = "${mistakeQuestions.size} Mistakes",
                        onClick = onNavigateToMistakes
                    )
                }
            }

            // Revise Today Queue
            item {
                SectionHeader(
                    title = "Revise Today (${reviseTodayTopics.size} Topics)",
                    subtitle = "Recommended by spaced-repetition algorithm"
                )
            }

            if (reviseTodayTopics.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("✨ Great work!", fontWeight = FontWeight.Bold)
                            Text(
                                "No urgent topics scheduled for revision today.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(reviseTodayTopics, key = { it.id }) { topic ->
                    ReviseTodayTopicCard(
                        topic = topic,
                        onIncrementRevision = { viewModel.incrementTopicRevision(topic.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun RevisionHubCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    count: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = count,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun ReviseTodayTopicCard(
    topic: TopicEntity,
    onIncrementRevision: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (topic.difficultyLevel) {
                            "WEAK" -> GateError.copy(alpha = 0.15f)
                            "STRONG" -> GateSuccess.copy(alpha = 0.15f)
                            else -> GateWarning.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = "${topic.stream} • ${topic.difficultyLevel}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = when (topic.difficultyLevel) {
                                "WEAK" -> GateError
                                "STRONG" -> GateSuccess
                                else -> GateWarning
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Revised ${topic.revisionCount}x",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Button(
                onClick = onIncrementRevision,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Revised")
            }
        }
    }
}
