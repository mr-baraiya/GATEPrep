package com.example.ui.screens.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.notification.GateNotificationHelper
import com.example.ui.components.SectionHeader
import com.example.ui.theme.GateError
import com.example.ui.theme.GateSuccess
import com.example.ui.viewmodel.GateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val selectedStream by viewModel.selectedStream.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }

    // Notification permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.sendTestNotification("Notifications Enabled", "You will receive reminders for GATE 2027 deadlines and daily targets.")
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings", fontWeight = FontWeight.Bold) },
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
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Target Stream Selection
            item {
                SectionHeader(title = "Target Paper", subtitle = "Choose your preparation stream")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("CSE", "DA", "BOTH").forEach { streamKey ->
                        FilterChip(
                            selected = selectedStream == streamKey,
                            onClick = { viewModel.setStream(streamKey) },
                            label = { Text(if (streamKey == "BOTH") "Both (CSE + DA)" else "GATE $streamKey") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Daily Study Goal
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Daily Study Target", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val currentHours = (userStats?.dailyStudyGoalMinutes ?: 180) / 60
                        Text(
                            text = "$currentHours Hours per day",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Slider(
                            value = currentHours.toFloat(),
                            onValueChange = { viewModel.updateDailyGoal((it.toInt() * 60)) },
                            valueRange = 1f..10f,
                            steps = 8,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Notification System Controls (Explicit Requirement)
            item {
                SectionHeader(title = "Notification Preferences", subtitle = "Custom reminders for GATE 2027 milestones")

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NotificationToggleRow(
                            title = "Exam Milestones & Deadlines",
                            subtitle = "Reminders for application open, regular deadline, admit card release & exam days",
                            isChecked = userStats?.enableExamAlerts ?: true,
                            onCheckedChange = { checked ->
                                userStats?.let {
                                    viewModel.updateNotificationSettings(
                                        it.enableDailyReminder, it.dailyReminderHour, it.dailyReminderMinute,
                                        enableExamAlerts = checked, enableRevisionAlerts = it.enableRevisionAlerts
                                    )
                                }
                            }
                        )

                        NotificationToggleRow(
                            title = "Daily Study & Streak Reminder",
                            subtitle = "Daily evening prompt to maintain streak and hit study targets (8:00 PM)",
                            isChecked = userStats?.enableDailyReminder ?: true,
                            onCheckedChange = { checked ->
                                userStats?.let {
                                    viewModel.updateNotificationSettings(
                                        enableDaily = checked, it.dailyReminderHour, it.dailyReminderMinute,
                                        enableExamAlerts = it.enableExamAlerts, enableRevisionAlerts = it.enableRevisionAlerts
                                    )
                                }
                            }
                        )

                        NotificationToggleRow(
                            title = "Spaced Revision Reminders",
                            subtitle = "Prompts when saved formulas and weak topics require scheduled review",
                            isChecked = userStats?.enableRevisionAlerts ?: true,
                            onCheckedChange = { checked ->
                                userStats?.let {
                                    viewModel.updateNotificationSettings(
                                        it.enableDailyReminder, it.dailyReminderHour, it.dailyReminderMinute,
                                        enableExamAlerts = it.enableExamAlerts, enableRevisionAlerts = checked
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    !GateNotificationHelper.hasNotificationPermission(context)
                                ) {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.sendTestNotification(
                                        "GATE 2027: Application Deadline Reminder",
                                        "Regular registration closes Sept 26, 2026. Keep your documents ready!"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_test_notification")
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send Test GATE Notification")
                        }
                    }
                }
            }

            // Theme Preferences
            item {
                SectionHeader(title = "Appearance", subtitle = "App color theme")
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("SYSTEM", "System Default"),
                            Pair("LIGHT", "Light"),
                            Pair("DARK", "Dark")
                        ).forEach { (themeKey, themeLabel) ->
                            FilterChip(
                                selected = (userStats?.themeMode ?: "SYSTEM") == themeKey,
                                onClick = { viewModel.updateTheme(themeKey) },
                                label = { Text(themeLabel) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Reset Data Action
            item {
                SectionHeader(title = "Data & Storage", subtitle = "Manage local preparation records")
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Reset Progress",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GateError
                        )
                        Text(
                            text = "Reset all completed topics, test scores, and question history back to zero.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GateError),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset All Progress")
                        }
                    }
                }
            }

            // About Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GATE 2027 Prep", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Version 1.0 • Dedicated preparation companion for GATE CSE and GATE DA aspirants.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset All Progress?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("This will reset all your topic checkmarks, PYQ attempts, and mock test scores. This action cannot be undone.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetAllProgress()
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GateError)
                    ) {
                        Text("Yes, Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
