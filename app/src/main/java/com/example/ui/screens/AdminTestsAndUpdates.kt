package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.QuizQuestion
import com.example.data.local.AppRepository
import com.example.data.local.AppUpdateEntity
import com.example.data.local.CustomTestEntity
import com.example.i18n.AppStrings

@Composable
fun AdminTestCreatorTab(
    strings: AppStrings,
    customTests: List<CustomTestEntity>,
    onCreateCustomTest: (subject: String, categoryKey: String, title: String, description: String, questions: List<QuizQuestion>) -> Unit,
    onDeleteCustomTest: (String) -> Unit
) {
    var subject by remember { mutableStateOf("ENGLISH") }
    var categoryKey by remember { mutableStateOf("B2") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var qPrompt by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIdx by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }

    val draftQuestions = remember { mutableStateListOf<QuizQuestion>() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "test_creator_card") {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(strings.adminCreateTestTab, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = subject == "ENGLISH",
                            onClick = {
                                subject = "ENGLISH"
                                if (categoryKey.startsWith("Grade")) categoryKey = "B2"
                            },
                            shape = SegmentedButtonDefaults.itemShape(0, 2)
                        ) { Text(strings.navEnglish, maxLines = 1) }
                        SegmentedButton(
                            selected = subject == "MATH",
                            onClick = {
                                subject = "MATH"
                                if (!categoryKey.startsWith("Grade")) categoryKey = "Grade 9"
                            },
                            shape = SegmentedButtonDefaults.itemShape(1, 2)
                        ) { Text(strings.navMath, maxLines = 1) }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Test Title") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_test_title_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = categoryKey,
                            onValueChange = { categoryKey = it },
                            label = { Text("Level / Class") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Topic Summary") },
                            singleLine = true,
                            modifier = Modifier.weight(1.4f)
                        )
                    }

                    // Auto-generate 5 questions quickly button
                    OutlinedButton(
                        onClick = {
                            val topicName = title.ifBlank { if (subject == "ENGLISH") "Grammar & Vocabulary" else "Algebra & Equations" }
                            val baseId = draftQuestions.size + 1
                            val generated = if (subject == "MATH") {
                                (0 until 5).map { idx ->
                                    val a = idx + 3
                                    val b = idx + 5
                                    val ans = a * b + idx
                                    QuizQuestion(
                                        id = baseId + idx,
                                        prompt = "$topicName (#${idx + 1}): Calculate ($a × $b) + $idx",
                                        options = listOf("${ans}", "${ans + 2}", "${ans - 3}", "${ans + 7}"),
                                        correctIndex = 0,
                                        explanation = "($a × $b) + $idx = $ans"
                                    )
                                }
                            } else {
                                (0 until 5).map { idx ->
                                    val pairs = listOf(
                                        Triple("Choose the correct form in '$topicName': She ___ every morning.", listOf("studies", "study", "studying", "studied to"), 0),
                                        Triple("Which word best fits '$topicName'?", listOf("resilient", "fragilely", "unspeaking", "slowed"), 0),
                                        Triple("Select the accurate preposition for '$topicName': Interested ___ science.", listOf("in", "at", "on", "to"), 0),
                                        Triple("Choose the synonym of 'accurate':", listOf("precise", "vague", "distant", "casual"), 0),
                                        Triple("Identify the grammatically correct sentence:", listOf("They have completed the test.", "They has complete test.", "They having completed.", "They was complete."), 0)
                                    )
                                    val (p, opts, ans) = pairs[idx]
                                    QuizQuestion(
                                        id = baseId + idx,
                                        prompt = p,
                                        options = opts,
                                        correctIndex = ans,
                                        explanation = "$topicName practice question #${idx + 1}"
                                    )
                                }
                            }
                            draftQuestions.addAll(generated)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 46.dp)
                            .testTag("admin_auto_generate_questions_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Auto-Generate 5 Questions (+5)")
                    }

                    // Manual question builder
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Add Question (${draftQuestions.size} added)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = qPrompt,
                                onValueChange = { qPrompt = it },
                                label = { Text("Question Prompt") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_question_prompt_input")
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("A") }, singleLine = true, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("B") }, singleLine = true, modifier = Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("C") }, singleLine = true, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("D") }, singleLine = true, modifier = Modifier.weight(1f))
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Correct:", style = MaterialTheme.typography.labelMedium)
                                listOf("A", "B", "C", "D").forEachIndexed { idx, lbl ->
                                    FilterChip(
                                        selected = correctIdx == idx,
                                        onClick = { correctIdx = idx },
                                        label = { Text(lbl) }
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = {
                                    if (qPrompt.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                                        val opts = listOf(
                                            optA.trim(),
                                            optB.trim(),
                                            optC.trim().ifBlank { "Option C" },
                                            optD.trim().ifBlank { "Option D" }
                                        )
                                        draftQuestions.add(
                                            QuizQuestion(
                                                id = draftQuestions.size + 1,
                                                prompt = qPrompt.trim(),
                                                options = opts,
                                                correctIndex = correctIdx,
                                                explanation = explanation.trim()
                                            )
                                        )
                                        qPrompt = ""
                                        optA = ""
                                        optB = ""
                                        optC = ""
                                        optD = ""
                                        explanation = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Add Question to Draft")
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && draftQuestions.isNotEmpty()) {
                                onCreateCustomTest(subject, categoryKey, title, description, draftQuestions.toList())
                                title = ""
                                description = ""
                                draftQuestions.clear()
                            }
                        },
                        enabled = title.isNotBlank() && draftQuestions.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("admin_publish_test_button")
                    ) {
                        Text("${strings.publishBtn} (${draftQuestions.size} Qs)")
                    }
                }
            }
        }

        items(customTests, key = { it.id }) { test ->
            val qCount = remember(test.questionsJson) { AppRepository.decodeQuestions(test.questionsJson).size }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(test.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${test.subject} • ${test.categoryKey} • $qCount Qs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(
                        onClick = { onDeleteCustomTest(test.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = strings.deleteUser, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAppUpdatesTab(
    strings: AppStrings,
    appUpdates: List<AppUpdateEntity>,
    onPublishUpdate: (versionName: String, title: String, changelog: String, isCritical: Boolean) -> Unit,
    onDeleteUpdate: (String) -> Unit
) {
    var versionName by remember { mutableStateOf("1.1.0") }
    var title by remember { mutableStateOf("") }
    var changelog by remember { mutableStateOf("") }
    var isCritical by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "publish_update_form") {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(strings.adminUpdatesTab, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    OutlinedTextField(
                        value = versionName,
                        onValueChange = { versionName = it },
                        label = { Text("Version (e.g. 1.1.0)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_update_version_input")
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Update Title") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_update_title_input")
                    )
                    OutlinedTextField(
                        value = changelog,
                        onValueChange = { changelog = it },
                        label = { Text("Release Notes / What's New") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_update_changelog_input")
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCritical, onCheckedChange = { isCritical = it })
                        Text("Highlight as Major Update", style = MaterialTheme.typography.bodyMedium)
                    }
                    Button(
                        onClick = {
                            if (versionName.isNotBlank() && title.isNotBlank()) {
                                onPublishUpdate(versionName, title, changelog, isCritical)
                                title = ""
                                changelog = ""
                            }
                        },
                        enabled = versionName.isNotBlank() && title.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("admin_publish_update_button")
                    ) {
                        Text(strings.publishUpdateBtn)
                    }
                }
            }
        }

        items(appUpdates, key = { it.id }) { upd ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("v${upd.versionName} • ${upd.title}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(upd.changelog, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(
                        onClick = { onDeleteUpdate(upd.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = strings.deleteUser, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
