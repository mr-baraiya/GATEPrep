package com.example.ui.screens.revision

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GateError
import com.example.ui.theme.GateSuccess
import com.example.ui.viewmodel.GateViewModel

data class FlashcardItem(
    val stream: String,
    val subject: String,
    val prompt: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    viewModel: GateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cards = remember {
        listOf(
            FlashcardItem(
                "CSE", "Operating Systems",
                "What is the condition for Deadlock Avoidance in Banker's Algorithm?",
                "System is in a Safe State if there exists a safe execution sequence <P1, P2, ..., Pn> such that for each Pi, Need_i <= Available + Sum(Allocated_j for j < i)."
            ),
            FlashcardItem(
                "CSE", "Algorithms",
                "What is the worst-case time complexity of QuickSort and how can it be avoided?",
                "Worst case is O(n^2) when the pivot selected is consistently the minimum or maximum element (e.g. already sorted array). Avoided using Randomized Pivot or Median-of-Medians (O(n log n))."
            ),
            FlashcardItem(
                "CSE", "Databases",
                "What is the test for Lossless Join Decomposition of R into R1 and R2?",
                "The decomposition is lossless iff: (R1 ∩ R2) -> R1 OR (R1 ∩ R2) -> R2 in F+. That is, common attributes must form a superkey of at least one subrelation."
            ),
            FlashcardItem(
                "DA", "Machine Learning",
                "What is the difference between L1 (Lasso) and L2 (Ridge) Regularization?",
                "L1 adds lambda * sum(|w_i|), producing sparse weights (feature selection). L2 adds lambda * sum(w_i^2), shrinking weights toward zero without setting them strictly to zero."
            ),
            FlashcardItem(
                "DA", "Linear Algebra",
                "What are the properties of Eigenvalues for a Symmetric Real Matrix?",
                "1. All eigenvalues are strictly REAL numbers.\n2. Eigenvectors corresponding to distinct eigenvalues are mutually ORTHOGONAL."
            ),
            FlashcardItem(
                "DA", "Probability & Statistics",
                "State the Central Limit Theorem (CLT).",
                "For independent identically distributed (i.i.d.) random variables with mean mu and variance sigma^2, the sample mean converges in distribution to Normal N(mu, sigma^2 / n) as n -> infinity."
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 350)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Recall Flashcards", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        val currentCard = cards[currentIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Card ${currentIndex + 1} of ${cards.size} • Tap card to flip",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Flashcard
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFlipped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable { isFlipped = !isFlipped }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${currentCard.stream} • ${currentCard.subject}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!isFlipped) {
                            Text(
                                text = currentCard.prompt,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = currentCard.answer,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        isFlipped = false
                        if (currentIndex > 0) currentIndex--
                    },
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous")
                }

                Button(
                    onClick = {
                        isFlipped = false
                        if (currentIndex < cards.size - 1) currentIndex++
                        else currentIndex = 0
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (currentIndex < cards.size - 1) "Next Card" else "Restart")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
