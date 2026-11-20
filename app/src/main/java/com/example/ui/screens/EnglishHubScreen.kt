package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.curriculum.DictionaryEntry
import com.example.data.curriculum.EnglishCurriculum
import com.example.data.curriculum.EnglishTopic
import com.example.data.local.AppRepository
import com.example.data.local.CustomTestEntity
import com.example.i18n.AppStrings
import com.example.ui.components.VerifiedBadge

@Composable
fun EnglishHubScreen(
    strings: AppStrings,
    dictionaryEntries: List<DictionaryEntry>,
    dictionaryLoaded: Boolean,
    customTests: List<CustomTestEntity>,
    onStartTopicTest: (EnglishTopic) -> Unit,
    onStartCustomTest: (CustomTestEntity) -> Unit,
    onSpeakWord: (text: String, slow: Boolean) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) } // 0 = 500 Topics & Tests, 1 = 7000+ Audio Dictionary

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                SegmentedButton(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    shape = SegmentedButtonDefaults.itemShape(0, 2),
                    modifier = Modifier.testTag("english_tab_topics")
                ) {
                    Text(strings.englishTopicsTab, maxLines = 1)
                }
                SegmentedButton(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    shape = SegmentedButtonDefaults.itemShape(1, 2),
                    modifier = Modifier.testTag("english_tab_dictionary")
                ) {
                    Text(strings.dictionaryTab, maxLines = 1)
                }
            }

            if (subTab == 0) {
                EnglishTopicsSection(
                    strings = strings,
                    customTests = customTests,
                    onStartTopicTest = onStartTopicTest,
                    onStartCustomTest = onStartCustomTest
                )
            } else {
                DictionaryAudioSection(
                    strings = strings,
                    entries = dictionaryEntries,
                    loaded = dictionaryLoaded,
                    onSpeak = onSpeakWord
                )
            }
        }
    }
}

@Composable
private fun EnglishTopicsSection(
    strings: AppStrings,
    customTests: List<CustomTestEntity>,
    onStartTopicTest: (EnglishTopic) -> Unit,
    onStartCustomTest: (CustomTestEntity) -> Unit
) {
    var selectedCefr by remember { mutableStateOf("ALL") }
    var query by remember { mutableStateOf("") }
    var expandedTopicId by remember { mutableStateOf<Int?>(null) }
    val engCustomTests = remember(customTests) { customTests.filter { it.subject == "ENGLISH" } }

    val filteredTopics by remember(selectedCefr, query) {
        derivedStateOf {
            val base = EnglishCurriculum.getTopicsByCefr(selectedCefr)
            val q = query.trim().lowercase()
            if (q.isEmpty()) base
            else base.filter {
                it.title.lowercase().contains(q) ||
                    it.grammarFocus.lowercase().contains(q) ||
                    it.id.toString() == q ||
                    it.keyWords.any { w -> w.lowercase().contains(q) }
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "eng_banner") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_english_studio),
                    contentDescription = strings.englishTopicsTab,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(Color(0xCC0F172A), Color(0x550F172A))))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                ) {
                    Text("A1 → C2 • 500 Topics", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("500 Real-Topic Tests × 30 Questions Each", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        item(key = "eng_filters") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(strings.searchTopicsHint) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("english_topic_search")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EnglishCurriculum.cefrLevels) { level ->
                        val count = if (level == "ALL") 500 else EnglishCurriculum.getTopicsByCefr(level).size
                        FilterChip(
                            selected = selectedCefr == level,
                            onClick = { selectedCefr = level },
                            label = { Text("$level ($count)") },
                            modifier = Modifier.testTag("cefr_chip_$level")
                        )
                    }
                }
            }
        }

        if (engCustomTests.isNotEmpty()) {
            items(engCustomTests, key = { it.id }) { cTest ->
                val qCount = remember(cTest.questionsJson) { AppRepository.decodeQuestions(cTest.questionsJson).size }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primary) {
                                Text(
                                    "${strings.customAdminTestBadge} • ${cTest.categoryKey}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            VerifiedBadge(size = 16.dp, contentDescription = strings.verifiedBadgeLabel)
                            Spacer(Modifier.weight(1f))
                            Text("$qCount Qs", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(cTest.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (cTest.description.isNotBlank()) {
                            Text(cTest.description, style = MaterialTheme.typography.bodySmall)
                        }
                        Button(
                            onClick = { onStartCustomTest(cTest) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("start_custom_test_${cTest.id}")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("${strings.startTest30.substringBefore("(").trim()} ($qCount Qs)")
                        }
                    }
                }
            }
        }

        items(filteredTopics, key = { it.id }) { topic ->
            val isExpanded = expandedTopicId == topic.id
            Card(
                onClick = { expandedTopicId = if (isExpanded) null else topic.id },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                "${topic.cefr} #${topic.id}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(topic.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(topic.grammarFocus, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand topic"
                        )
                    }

                    if (isExpanded) {
                        Spacer(Modifier.height(10.dp))
                        Text(topic.lessonSummary, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Key words: ${topic.keyWords.joinToString(" • ")}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(3.dp))
                                Text("Example: \"${topic.sampleSentence}\"", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { onStartTopicTest(topic) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("start_english_test_${topic.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(strings.startTest30)
                    }
                }
            }
        }
    }
}

@Composable
private fun DictionaryAudioSection(
    strings: AppStrings,
    entries: List<DictionaryEntry>,
    loaded: Boolean,
    onSpeak: (text: String, slow: Boolean) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var cefrFilter by remember { mutableStateOf("ALL") }
    var limit by remember { mutableIntStateOf(80) }

    val filtered by remember(entries, search, cefrFilter) {
        derivedStateOf {
            val q = search.trim().lowercase()
            entries.filter { e ->
                (cefrFilter == "ALL" || e.cefr == cefrFilter) &&
                    (q.isEmpty() ||
                        e.word.lowercase().contains(q) ||
                        e.uzbek.lowercase().contains(q) ||
                        e.turkish.lowercase().contains(q) ||
                        e.russian.lowercase().contains(q))
            }
        }
    }

    if (!loaded) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "dict_header") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "${entries.size} words indexed • EN ↔ UZ • TR • RU with Voice Audio",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it; limit = 80 },
                    placeholder = { Text(strings.searchWordsHint) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dictionary_search_input")
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EnglishCurriculum.cefrLevels) { level ->
                        FilterChip(
                            selected = cefrFilter == level,
                            onClick = { cefrFilter = level; limit = 80 },
                            label = { Text(level) }
                        )
                    }
                }
            }
        }

        val visibleSlice = filtered.take(limit)
        items(visibleSlice, key = { "${it.word}_${it.partOfSpeech}_${it.cefr}" }) { entry ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(entry.word, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                    Text(
                                        "${entry.cefr} • ${entry.partOfSpeech}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (entry.ipa.isNotBlank()) {
                                Text(entry.ipa, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { onSpeak(entry.word, false) },
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = strings.listenAudio, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(strings.listenAudio, style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = { onSpeak(entry.word, true) },
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Icon(Icons.Default.SlowMotionVideo, contentDescription = strings.slowSpeed, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("0.65x", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    TranslationRow("UZ", entry.uzbek)
                    TranslationRow("TR", entry.turkish)
                    TranslationRow("RU", entry.russian)

                    if (entry.example.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "\"${entry.example}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(
                                    onClick = { onSpeak(entry.example, false) },
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    modifier = Modifier.heightIn(min = 36.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen example", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (filtered.size > limit) {
            item(key = "load_more_dict") {
                Button(
                    onClick = { limit += 100 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text("Load more (${filtered.size - limit} remaining)")
                }
            }
        }
    }
}

@Composable
private fun TranslationRow(code: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(code, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
