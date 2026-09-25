package com.example.ui.screens.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.ui.components.StreamSelectorChips
import com.example.ui.theme.GateError
import com.example.ui.theme.GatePrimaryLight
import com.example.ui.theme.GateSuccess
import com.example.ui.theme.GateWarning
import com.example.ui.viewmodel.GateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySubjectsScreen(
    viewModel: GateViewModel,
    onNavigateToPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedStream by viewModel.selectedStream.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("ALL") } // "ALL", "PENDING", "WEAK", "COMPLETED"
    var expandedSubjectId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Syllabus & Subjects", fontWeight = FontWeight.Bold) }
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

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search topics, algorithms, concepts...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("search_topics_field")
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("ALL", "All"),
                    Pair("PENDING", "Pending"),
                    Pair("WEAK", "Weak Areas"),
                    Pair("COMPLETED", "Done")
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = filterStatus == key,
                        onClick = { filterStatus = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val filteredSubjects = subjects.filter { sub ->
                    val subTopics = topics.filter { it.subjectId == sub.id }
                    if (searchQuery.isBlank() && filterStatus == "ALL") true
                    else {
                        sub.name.contains(searchQuery, ignoreCase = true) ||
                        subTopics.any { t ->
                            val matchSearch = searchQuery.isBlank() || t.title.contains(searchQuery, ignoreCase = true) || t.subtopics.contains(searchQuery, ignoreCase = true)
                            val matchFilter = when (filterStatus) {
                                "PENDING" -> !t.isCompleted
                                "WEAK" -> t.difficultyLevel == "WEAK"
                                "COMPLETED" -> t.isCompleted
                                else -> true
                            }
                            matchSearch && matchFilter
                        }
                    }
                }

                items(filteredSubjects, key = { it.id }) { subject ->
                    val subjectTopics = topics.filter { it.subjectId == subject.id }
                    val completedCount = subjectTopics.count { it.isCompleted }
                    val progressFraction = if (subjectTopics.isNotEmpty()) completedCount.toFloat() / subjectTopics.size else 0f
                    val isExpanded = expandedSubjectId == subject.id || searchQuery.isNotBlank()

                    SubjectCard(
                        subject = subject,
                        totalTopics = subjectTopics.size,
                        completedTopics = completedCount,
                        progress = progressFraction,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedSubjectId = if (expandedSubjectId == subject.id) null else subject.id
                        },
                        topics = subjectTopics.filter { t ->
                            val matchSearch = searchQuery.isBlank() || t.title.contains(searchQuery, ignoreCase = true) || t.subtopics.contains(searchQuery, ignoreCase = true)
                            val matchFilter = when (filterStatus) {
                                "PENDING" -> !t.isCompleted
                                "WEAK" -> t.difficultyLevel == "WEAK"
                                "COMPLETED" -> t.isCompleted
                                else -> true
                            }
                            matchSearch && matchFilter
                        },
                        onToggleCompletion = { topicId, current ->
                            viewModel.toggleTopicCompletion(topicId, current)
                        },
                        onIncrementRevision = { topicId ->
                            viewModel.incrementTopicRevision(topicId)
                        },
                        onUpdateDifficulty = { topicId, diff ->
                            viewModel.updateTopicDifficulty(topicId, diff)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectEntity,
    totalTopics: Int,
    completedTopics: Int,
    progress: Float,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    topics: List<TopicEntity>,
    onToggleCompletion: (String, Boolean) -> Unit,
    onIncrementRevision: (String) -> Unit,
    onUpdateDifficulty: (String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_card_${subject.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = subject.code,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weightage: ~${subject.weightagePercent}%  •  $completedTopics/$totalTopics done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand"
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    topics.forEach { topic ->
                        TopicItemRow(
                            topic = topic,
                            onToggleCompletion = { onToggleCompletion(topic.id, topic.isCompleted) },
                            onIncrementRevision = { onIncrementRevision(topic.id) },
                            onToggleDifficulty = {
                                val next = when (topic.difficultyLevel) {
                                    "WEAK" -> "MODERATE"
                                    "MODERATE" -> "STRONG"
                                    else -> "WEAK"
                                }
                                onUpdateDifficulty(topic.id, next)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicItemRow(
    topic: TopicEntity,
    onToggleCompletion: () -> Unit,
    onIncrementRevision: () -> Unit,
    onToggleDifficulty: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onToggleCompletion,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (topic.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Completed",
                            tint = if (topic.isCompleted) GateSuccess else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (topic.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Difficulty Chip Toggle
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (topic.difficultyLevel) {
                            "WEAK" -> GateError.copy(alpha = 0.15f)
                            "STRONG" -> GateSuccess.copy(alpha = 0.15f)
                            else -> GateWarning.copy(alpha = 0.15f)
                        },
                        modifier = Modifier.clickable { onToggleDifficulty() }
                    ) {
                        Text(
                            text = topic.difficultyLevel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            color = when (topic.difficultyLevel) {
                                "WEAK" -> GateError
                                "STRONG" -> GateSuccess
                                else -> GateWarning
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Revision Count badge
                    IconButton(
                        onClick = onIncrementRevision,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Revise",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${topic.revisionCount}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (topic.subtopics.isNotBlank()) {
                Text(
                    text = topic.subtopics,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 34.dp, top = 2.dp)
                )
            }
        }
    }
}
