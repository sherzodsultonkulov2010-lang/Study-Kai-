package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.ActiveTestSession
import com.example.i18n.AppStrings
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizRunnerDialog(
    session: ActiveTestSession,
    strings: AppStrings,
    onSubmitScore: suspend (Int) -> Int,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val scope = rememberCoroutineScope()
    var currentIndex by remember(session) { mutableIntStateOf(0) }
    val selectedAnswers = remember(session) { mutableStateMapOf<Int, Int>() }
    var isFinished by remember(session) { mutableStateOf(false) }
    var earnedPoints by remember(session) { mutableIntStateOf(0) }
    var finalScore by remember(session) { mutableIntStateOf(0) }

    val total = session.questions.size
    val currentQ = session.questions[currentIndex]
    val answeredCount = selectedAnswers.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(session.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                        Text(
                            "${session.subtitle} • $answeredCount/$total",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_quiz_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = strings.backLabel)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isFinished) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(96.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = strings.testCompletedTitle,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        strings.testCompletedTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        session.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$finalScore / $total", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(strings.scoreLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val pct = if (total > 0) (finalScore * 100) / total else 0
                                Text("$pct%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                Text(strings.accuracyLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("+$earnedPoints", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                Text(strings.pointsLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .testTag("quiz_done_button")
                    ) {
                        Text(strings.backLabel)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .widthIn(max = 640.dp)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / total.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(Modifier.height(10.dp))

                    // Question number quick-jump strip (1..30)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(total) { idx ->
                            val isCurrent = idx == currentIndex
                            val chosen = selectedAnswers[idx]
                            val q = session.questions[idx]
                            val chipColor = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                chosen == null -> MaterialTheme.colorScheme.surfaceVariant
                                chosen == q.correctIndex -> SuccessGreen.copy(alpha = 0.22f)
                                else -> MaterialTheme.colorScheme.errorContainer
                            }
                            val textColor = when {
                                isCurrent -> MaterialTheme.colorScheme.onPrimary
                                chosen != null && chosen == q.correctIndex -> SuccessGreen
                                chosen != null -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            Surface(
                                onClick = { currentIndex = idx },
                                shape = RoundedCornerShape(10.dp),
                                color = chipColor,
                                modifier = Modifier.heightIn(min = 36.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${idx + 1}", style = MaterialTheme.typography.labelMedium, color = textColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    strings.questionOfFmt.format(currentIndex + 1, total),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    currentQ.prompt,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        val selectedForCurrent = selectedAnswers[currentIndex]
                        val labels = listOf("A", "B", "C", "D")

                        currentQ.options.forEachIndexed { optIdx, optionText ->
                            val isSelected = selectedForCurrent == optIdx
                            val isCorrectOpt = optIdx == currentQ.correctIndex
                            val showFeedback = selectedForCurrent != null

                            val borderColor = when {
                                showFeedback && isCorrectOpt -> SuccessGreen
                                showFeedback && isSelected && !isCorrectOpt -> MaterialTheme.colorScheme.error
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                            }
                            val containerColor = when {
                                showFeedback && isCorrectOpt -> SuccessGreen.copy(alpha = 0.12f)
                                showFeedback && isSelected && !isCorrectOpt -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            Surface(
                                onClick = {
                                    if (selectedAnswers[currentIndex] == null) {
                                        selectedAnswers[currentIndex] = optIdx
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = containerColor,
                                border = BorderStroke(if (isSelected || (showFeedback && isCorrectOpt)) 2.dp else 1.dp, borderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .heightIn(min = 52.dp)
                                    .testTag("quiz_option_$optIdx")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            labels.getOrElse(optIdx) { "?" },
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        optionText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (showFeedback && isCorrectOpt) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = SuccessGreen)
                                    } else if (showFeedback && isSelected) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = "Incorrect", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }

                        if (selectedForCurrent != null) {
                            Spacer(Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentQ.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (currentIndex > 0) currentIndex-- },
                            enabled = currentIndex > 0,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("quiz_prev_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.prevQuestion)
                        }

                        if (currentIndex < total - 1) {
                            Button(
                                onClick = { currentIndex++ },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("quiz_next_button")
                            ) {
                                Text(strings.nextQuestion)
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        } else {
                            Button(
                                onClick = {
                                    val score = session.questions.indices.count { i ->
                                        selectedAnswers[i] == session.questions[i].correctIndex
                                    }
                                    finalScore = score
                                    scope.launch {
                                        earnedPoints = onSubmitScore(score)
                                        isFinished = true
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("quiz_finish_button")
                            ) {
                                Text(strings.finishTest)
                            }
                        }
                    }
                }
            }
        }
    }
}
