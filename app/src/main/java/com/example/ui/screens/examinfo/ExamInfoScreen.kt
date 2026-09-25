package com.example.ui.screens.examinfo

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.ExamMilestoneEntity
import com.example.ui.components.SectionHeader
import com.example.ui.theme.GateError
import com.example.ui.theme.GatePrimaryLight
import com.example.ui.theme.GateSecondaryLight
import com.example.ui.theme.GateSuccess
import com.example.ui.theme.GateTertiaryLight
import com.example.ui.theme.GateWarning
import com.example.ui.viewmodel.GateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamInfoScreen(
    viewModel: GateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedSyllabusStream by remember { mutableStateOf("CSE") }
    val milestones by viewModel.milestones.collectAsStateWithLifecycle()

    val tabTitles = listOf("Pattern & Scheme", "Syllabus & Trends", "Important Dates", "Eligibility")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GATE 2027 Information", fontWeight = FontWeight.Bold) },
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
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> ExamPatternAndSchemeTab()
                1 -> SyllabusAndWeightageTab(
                    selectedStream = selectedSyllabusStream,
                    onSelectStream = { selectedSyllabusStream = it }
                )
                2 -> ImportantDatesTab(
                    milestones = milestones,
                    onToggleReminder = { id, cur -> viewModel.toggleMilestoneReminder(id, cur) },
                    onSendTestNotification = { title, desc -> viewModel.sendTestNotification(title, desc) }
                )
                3 -> EligibilityAndCombinationTab()
            }
        }
    }
}

@Composable
private fun ExamPatternAndSchemeTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Examination Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoRow(label = "Mode of Examination", value = "Computer Based Test (CBT)")
                    InfoRow(label = "Duration", value = "180 Minutes (3 Hours)")
                    InfoRow(label = "Total Questions", value = "65 Questions")
                    InfoRow(label = "Total Marks", value = "100 Marks")
                    InfoRow(label = "Sections", value = "General Aptitude (15 Marks) + Subject / Technical (85 Marks)")
                    InfoRow(label = "Negative Marking", value = "Applicable ONLY to MCQs")
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Question Types (3 Categories)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    QuestionTypeItem(
                        tag = "MCQ",
                        title = "Multiple Choice Questions",
                        description = "1 correct option out of 4. Negative marking applies (-0.33 for 1-mark, -0.66 for 2-mark).",
                        badgeColor = GatePrimaryLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    QuestionTypeItem(
                        tag = "MSQ",
                        title = "Multiple Select Questions",
                        description = "1 or more correct options. NO partial marking. NO negative marking. Requires thorough theoretical clarity.",
                        badgeColor = Color(0xFF8B5CF6)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    QuestionTypeItem(
                        tag = "NAT",
                        title = "Numerical Answer Type",
                        description = "Answer entered using virtual numeric keypad. Real number with precision range. ZERO negative marking.",
                        badgeColor = GateSecondaryLight
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alert",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Pro Tip: Since MSQ and NAT have 0 negative marking, NEVER leave any MSQ or NAT unattempted in GATE 2027!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SyllabusAndWeightageTab(
    selectedStream: String,
    onSelectStream: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStream == "CSE",
                    onClick = { onSelectStream("CSE") },
                    label = { Text("GATE 2027 CSE Syllabus") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedStream == "DA",
                    onClick = { onSelectStream("DA") },
                    label = { Text("GATE 2027 DA Syllabus") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (selectedStream == "CSE") {
            item {
                Text(
                    text = "Computer Science & Information Technology (CS)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val cseWeightages = listOf(
                Pair("General Aptitude (GA)", "15% (15 Marks)"),
                Pair("Engineering Mathematics (EM)", "13% (13 Marks)"),
                Pair("Programming & Data Structures", "10% (~10 Marks)"),
                Pair("Algorithms", "8% (~8 Marks)"),
                Pair("Operating Systems", "8% (~8 Marks)"),
                Pair("Databases (DBMS)", "8% (~8 Marks)"),
                Pair("Computer Networks (CN)", "8% (~8 Marks)"),
                Pair("Theory of Computation (TOC)", "8% (~8 Marks)"),
                Pair("Computer Organization (COA)", "8% (~8 Marks)"),
                Pair("Digital Logic (DL)", "5% (~5 Marks)"),
                Pair("Compiler Design (CD)", "5% (~5 Marks)")
            )

            items(cseWeightages) { (subject, weight) ->
                WeightageCard(subject = subject, weightage = weight)
            }
        } else {
            item {
                Text(
                    text = "Data Science & Artificial Intelligence (DA)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val daWeightages = listOf(
                Pair("General Aptitude (GA)", "15% (15 Marks)"),
                Pair("Probability & Statistics", "15% (~15 Marks)"),
                Pair("Machine Learning", "15% (~15 Marks)"),
                Pair("Linear Algebra", "10% (~10 Marks)"),
                Pair("Artificial Intelligence", "10% (~10 Marks)"),
                Pair("Programming, DS & Algorithms", "10% (~10 Marks)"),
                Pair("Deep Learning & Neural Networks", "10% (~10 Marks)"),
                Pair("Database Management & Warehousing", "8% (~8 Marks)"),
                Pair("Calculus & Optimization", "7% (~7 Marks)")
            )

            items(daWeightages) { (subject, weight) ->
                WeightageCard(subject = subject, weightage = weight)
            }
        }
    }
}

@Composable
private fun ImportantDatesTab(
    milestones: List<ExamMilestoneEntity>,
    onToggleReminder: (String, Boolean) -> Unit,
    onSendTestNotification: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Notification",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Customize notifications for each GATE 2027 milestone below. Tap notification icon to toggle alert or test it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(milestones) { milestone ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (milestone.category) {
                                "EXAM" -> GateError.copy(alpha = 0.15f)
                                "APPLICATION" -> GatePrimaryLight.copy(alpha = 0.15f)
                                "ADMIT_CARD" -> GateTertiaryLight.copy(alpha = 0.15f)
                                else -> GateSuccess.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = milestone.dateDisplay,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = when (milestone.category) {
                                    "EXAM" -> GateError
                                    "APPLICATION" -> GatePrimaryLight
                                    "ADMIT_CARD" -> GateTertiaryLight
                                    else -> GateSuccess
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = milestone.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = milestone.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            onToggleReminder(milestone.id, milestone.isReminderEnabled)
                            onSendTestNotification(
                                "GATE 2027: ${milestone.title}",
                                "${milestone.dateDisplay} - ${milestone.description}"
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (milestone.isReminderEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Toggle Reminder",
                            tint = if (milestone.isReminderEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EligibilityAndCombinationTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Eligibility Criteria",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Candidates currently studying in 3rd or final year of any undergraduate degree (B.E. / B.Tech / MCA / M.Sc / equivalent) are ELIGIBLE.\n• Candidates who have already completed any government-approved degree program in Engineering / Technology / Science are ELIGIBLE.\n• There is NO AGE LIMIT for appearing in GATE 2027.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Two-Paper Combination (CSE + DA)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "GATE allows candidates to appear for TWO PAPERS in mutually non-overlapping sessions.\n\n• Primary Paper: Computer Science (CS)\n  Allowed Secondary Paper: Data Science & AI (DA) or Mathematics (MA) or Electronics (EC).\n\n• Primary Paper: Data Science & AI (DA)\n  Allowed Secondary Paper: Computer Science (CS) or Mathematics (MA) or Statistics (ST).\n\nAppearing for both CSE and DA maximizes your M.Tech / Direct PhD opportunities across IISc, IITs, IIITs and PSU selections.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun QuestionTypeItem(tag: String, title: String, description: String, badgeColor: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = badgeColor.copy(alpha = 0.15f)
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                color = badgeColor
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeightageCard(subject: String, weightage: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subject,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = weightage,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
