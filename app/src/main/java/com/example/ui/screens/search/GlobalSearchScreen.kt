package com.example.ui.screens.search

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.GateViewModel

data class SearchResultItem(
    val category: String, // "TOPIC", "FORMULA", "PYQ", "NOTE"
    val title: String,
    val subtitle: String,
    val stream: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    viewModel: GateViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPyq: () -> Unit,
    onNavigateToFormulas: () -> Unit,
    onNavigateToNotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf("ALL") }

    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val formulas by viewModel.formulas.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    val results = remember(query, filterCategory, topics, formulas, questions, notes) {
        if (query.isBlank()) emptyList()
        else {
            val list = mutableListOf<SearchResultItem>()

            if (filterCategory == "ALL" || filterCategory == "TOPIC") {
                topics.filter { it.title.contains(query, ignoreCase = true) || it.subtopics.contains(query, ignoreCase = true) }
                    .forEach {
                        list.add(SearchResultItem("TOPIC", it.title, "Syllabus Topic • ${it.subtopics}", it.stream))
                    }
            }

            if (filterCategory == "ALL" || filterCategory == "FORMULA") {
                formulas.filter { it.title.contains(query, ignoreCase = true) || it.formulaText.contains(query, ignoreCase = true) || it.explanation.contains(query, ignoreCase = true) }
                    .forEach {
                        list.add(SearchResultItem("FORMULA", it.title, "${it.subjectName}: ${it.formulaText}", it.stream))
                    }
            }

            if (filterCategory == "ALL" || filterCategory == "PYQ") {
                questions.filter { it.questionText.contains(query, ignoreCase = true) || it.topic.contains(query, ignoreCase = true) }
                    .forEach {
                        list.add(SearchResultItem("PYQ", "GATE ${it.year} ${it.questionType} (${it.stream})", "${it.subjectName}: ${it.questionText.take(80)}...", it.stream))
                    }
            }

            if (filterCategory == "ALL" || filterCategory == "NOTE") {
                notes.filter { it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true) }
                    .forEach {
                        list.add(SearchResultItem("NOTE", it.title, "${it.subjectName}: ${it.content.take(80)}...", it.stream))
                    }
            }

            list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Search", fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search topics, formulas, PYQs, personal notes...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("global_search_input")
            )

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("ALL", "All"),
                    Pair("TOPIC", "Topics"),
                    Pair("FORMULA", "Formulas"),
                    Pair("PYQ", "PYQs"),
                    Pair("NOTE", "Notes")
                ).forEach { (cat, label) ->
                    FilterChip(
                        selected = filterCategory == cat,
                        onClick = { filterCategory = cat },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (query.isBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Type to search", fontWeight = FontWeight.Bold)
                                Text("Search across CSE & DA syllabus, PYQ bank, formula sheets & notes.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else if (results.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No matches found", fontWeight = FontWeight.Bold)
                                Text("Try another keyword or search category.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else {
                    items(results) { item ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (item.category) {
                                        "PYQ" -> onNavigateToPyq()
                                        "FORMULA" -> onNavigateToFormulas()
                                        "NOTE" -> onNavigateToNotes()
                                        else -> {}
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = item.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(text = item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
