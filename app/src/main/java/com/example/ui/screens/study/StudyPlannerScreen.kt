package com.example.ui.screens.study

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudyTaskEntity
import com.example.ui.components.SectionHeader
import com.example.ui.theme.GateError
import com.example.ui.theme.GatePrimaryLight
import com.example.ui.theme.GateSuccess
import com.example.ui.theme.GateWarning
import com.example.ui.viewmodel.GateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(
    viewModel: GateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val studyTasks by viewModel.studyTasks.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Planner & Timetable", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_task")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val pendingTasks = studyTasks.filter { !it.isCompleted }
            val completedTasks = studyTasks.filter { it.isCompleted }

            item {
                SectionHeader(
                    title = "Pending Study Targets (${pendingTasks.size})",
                    subtitle = "Complete daily slots to maintain your streak"
                )
            }

            if (pendingTasks.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎉 All caught up!", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "No pending tasks. Tap '+' below to schedule your next topic.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(pendingTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggleDone = { viewModel.toggleStudyTaskCompletion(task) },
                        onDelete = { viewModel.deleteStudyTask(task) }
                    )
                }
            }

            if (completedTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionHeader(
                        title = "Completed Today (${completedTasks.size})",
                        subtitle = "Logged towards your daily GATE target"
                    )
                }

                items(completedTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggleDone = { viewModel.toggleStudyTaskCompletion(task) },
                        onDelete = { viewModel.deleteStudyTask(task) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { subject, topic, duration, priority, notes ->
                    viewModel.addStudyTask(
                        subjectName = subject,
                        topicTitle = topic,
                        targetDateMillis = System.currentTimeMillis(),
                        durationMinutes = duration,
                        priority = priority,
                        notes = notes
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: StudyTaskEntity,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onToggleDone) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Done",
                        tint = if (task.isCompleted) GateSuccess else MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = task.topicTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${task.subjectName} • ${task.durationMinutes} mins",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (task.priority) {
                        "HIGH" -> GateError.copy(alpha = 0.15f)
                        "LOW" -> GateSuccess.copy(alpha = 0.15f)
                        else -> GateWarning.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = task.priority,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = when (task.priority) {
                            "HIGH" -> GateError
                            "LOW" -> GateSuccess
                            else -> GateWarning
                        }
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Int, String, String) -> Unit
) {
    var subject by remember { mutableStateOf("Algorithms") }
    var topic by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("60") }
    var priority by remember { mutableStateOf("HIGH") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Study Target", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (e.g. Operating Systems / ML)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic / Chapter (e.g. Virtual Memory / Bayes Rule)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (Minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Priority:", style = MaterialTheme.typography.bodySmall)
                    listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (priority == p) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            TextButton(onClick = { priority = p }) {
                                Text(
                                    text = p,
                                    color = if (priority == p) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (topic.isNotBlank()) {
                        val duration = durationText.toIntOrNull() ?: 60
                        onAdd(subject, topic, duration, priority, notes)
                    }
                }
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
