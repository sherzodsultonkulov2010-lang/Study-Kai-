package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.data.curriculum.MathCurriculum
import com.example.data.curriculum.MathFormulas
import com.example.data.curriculum.MathTestMeta
import com.example.data.local.AppRepository
import com.example.data.local.CustomTestEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.VerifiedBadge
import com.example.util.MathEvaluator

@Composable
fun MathHubScreen(
    appLanguage: AppLanguage,
    strings: AppStrings,
    customTests: List<CustomTestEntity>,
    onStartMathTest: (MathTestMeta) -> Unit,
    onStartCustomTest: (CustomTestEntity) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) } // 0 = Grades 0-11, 1 = Formulas, 2 = Desmos

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
                    shape = SegmentedButtonDefaults.itemShape(0, 3),
                    modifier = Modifier.testTag("math_tab_grades")
                ) {
                    Text(strings.mathGradesTab, maxLines = 1)
                }
                SegmentedButton(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    shape = SegmentedButtonDefaults.itemShape(1, 3),
                    modifier = Modifier.testTag("math_tab_formulas")
                ) {
                    Text(strings.formulasTab, maxLines = 1)
                }
                SegmentedButton(
                    selected = subTab == 2,
                    onClick = { subTab = 2 },
                    shape = SegmentedButtonDefaults.itemShape(2, 3),
                    modifier = Modifier.testTag("math_tab_desmos")
                ) {
                    Text(strings.desmosTab, maxLines = 1)
                }
            }

            when (subTab) {
                0 -> MathGradesSection(
                    appLanguage = appLanguage,
                    strings = strings,
                    customTests = customTests,
                    onStartTest = onStartMathTest,
                    onStartCustomTest = onStartCustomTest
                )
                1 -> MathFormulasSection()
                else -> DesmosCalculatorSection(strings = strings)
            }
        }
    }
}

@Composable
private fun MathGradesSection(
    appLanguage: AppLanguage,
    strings: AppStrings,
    customTests: List<CustomTestEntity>,
    onStartTest: (MathTestMeta) -> Unit,
    onStartCustomTest: (CustomTestEntity) -> Unit
) {
    var selectedGrade by remember { mutableIntStateOf(0) }
    var search by remember { mutableStateOf("") }
    val mathCustomTests = remember(customTests) { customTests.filter { it.subject == "MATH" } }

    val tests by remember(selectedGrade, search, appLanguage) {
        derivedStateOf {
            val all = MathCurriculum.getTestsForGrade(selectedGrade, appLanguage)
            val q = search.trim().lowercase()
            if (q.isEmpty()) all
            else all.filter {
                it.title.lowercase().contains(q) ||
                    it.topicBranch.lowercase().contains(q) ||
                    it.testNumber.toString() == q
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "math_banner") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_math_lab),
                    contentDescription = strings.mathGradesTab,
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
                    Text("Grades 0–11 Mathematics", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("12 Classes × 70 Tests × 30 Questions = 25,200 Problems", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        item(key = "grade_chips") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MathCurriculum.grades) { g ->
                        val label = if (strings.gradePrefix.endsWith(" ")) "${strings.gradePrefix}$g" else "$g${strings.gradePrefix}"
                        FilterChip(
                            selected = selectedGrade == g,
                            onClick = { selectedGrade = g },
                            label = { Text(label) },
                            modifier = Modifier.testTag("math_grade_chip_$g")
                        )
                    }
                }
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search 70 tests in Grade $selectedGrade...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (mathCustomTests.isNotEmpty()) {
            items(mathCustomTests, key = { it.id }) { cTest ->
                val qCount = remember(cTest.questionsJson) { AppRepository.decodeQuestions(cTest.questionsJson).size }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(strings.customAdminTestBadge, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                VerifiedBadge(size = 14.dp, contentDescription = strings.verifiedBadgeLabel)
                            }
                            Text(cTest.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${cTest.categoryKey} • $qCount Qs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { onStartCustomTest(cTest) },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("start_custom_test_${cTest.id}")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start test")
                        }
                    }
                }
            }
        }

        items(tests, key = { "${it.grade}_${it.testNumber}" }) { testMeta ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.width(56.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("#${testMeta.testNumber}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(testMeta.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Grade ${testMeta.grade} • ${strings.questionsCount30}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { onStartTest(testMeta) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("start_math_test_${testMeta.grade}_${testMeta.testNumber}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start test")
                    }
                }
            }
        }
    }
}

@Composable
private fun MathFormulasSection() {
    var branch by remember { mutableStateOf("All") }
    var search by remember { mutableStateOf("") }

    val filtered by remember(branch, search) {
        derivedStateOf {
            val q = search.trim().lowercase()
            MathFormulas.allFormulas.filter { item ->
                (branch == "All" || item.branch == branch) &&
                    (q.isEmpty() ||
                        item.name.lowercase().contains(q) ||
                        item.formula.lowercase().contains(q) ||
                        item.description.lowercase().contains(q))
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "formula_filters") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search formula or math term (e.g. Quadratic, Integral, Sine)...") },
                    leadingIcon = { Icon(Icons.Default.Functions, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("formulas_search_input")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MathFormulas.branches) { b ->
                        FilterChip(
                            selected = branch == b,
                            onClick = { branch = b },
                            label = { Text(b) }
                        )
                    }
                }
            }
        }

        items(filtered, key = { it.id }) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(
                                item.branch,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            item.formula,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Text(item.description, style = MaterialTheme.typography.bodyMedium)
                    Text("Example: ${item.example}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun DesmosCalculatorSection(strings: AppStrings) {
    var mode by remember { mutableIntStateOf(0) } // 0 = Desmos Live WebView, 1 = Offline Grapher & Evaluator

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = mode == 0,
                onClick = { mode = 0 },
                label = { Text(strings.desmosOnlineMode) },
                modifier = Modifier.testTag("desmos_live_chip")
            )
            FilterChip(
                selected = mode == 1,
                onClick = { mode = 1 },
                label = { Text(strings.offlineGraphMode) },
                modifier = Modifier.testTag("desmos_offline_chip")
            )
        }

        if (mode == 0) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()
                        loadUrl("https://www.desmos.com/calculator")
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("desmos_webview")
            )
        } else {
            OfflineGrapherView(strings = strings)
        }
    }
}

@Composable
private fun OfflineGrapherView(strings: AppStrings) {
    var expression by remember { mutableStateOf("x^2 - 4") }
    var xTest by remember { mutableStateOf("2") }
    var rangeScale by remember { mutableDoubleStateOf(10.0) }
    val presets = listOf("x^2 - 4", "sin(x) * 3", "2*x + 1", "cos(x) + 0.5*x", "sqrt(abs(x))")

    val evaluatedY = remember(expression, xTest) {
        val xVal = xTest.toDoubleOrNull() ?: 0.0
        MathEvaluator.evaluate(expression, xVal)
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = expression,
            onValueChange = { expression = it },
            label = { Text(strings.plotLabel) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("offline_graph_input")
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(presets) { fn ->
                FilterChip(
                    selected = expression == fn,
                    onClick = { expression = fn },
                    label = { Text("y = $fn") }
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                val w = size.width
                val h = size.height
                val midX = w / 2f
                val midY = h / 2f
                val scaleX = w / (2f * rangeScale.toFloat())
                val scaleY = h / (2f * rangeScale.toFloat())

                // Grid lines
                val step = (rangeScale / 5.0).toInt().coerceAtLeast(1)
                var gx = -rangeScale.toInt()
                while (gx <= rangeScale.toInt()) {
                    val px = midX + gx * scaleX
                    drawLine(gridColor, Offset(px, 0f), Offset(px, h), strokeWidth = 1f)
                    val py = midY - gx * scaleY
                    drawLine(gridColor, Offset(0f, py), Offset(w, py), strokeWidth = 1f)
                    gx += step
                }

                // Axes
                drawLine(axisColor, Offset(0f, midY), Offset(w, midY), strokeWidth = 2.5f)
                drawLine(axisColor, Offset(midX, 0f), Offset(midX, h), strokeWidth = 2.5f)

                // Plot curve
                val path = Path()
                var started = false
                val samples = 240
                for (i in 0..samples) {
                    val px = (i.toFloat() / samples) * w
                    val x = (px - midX) / scaleX
                    val y = MathEvaluator.evaluate(expression, x.toDouble())
                    if (y != null && y.isFinite() && kotlin.math.abs(y) < rangeScale * 4) {
                        val py = midY - (y.toFloat() * scaleY)
                        if (!started) {
                            path.moveTo(px, py)
                            started = true
                        } else {
                            path.lineTo(px, py)
                        }
                    } else {
                        started = false
                    }
                }
                drawPath(path, color = primaryColor, style = Stroke(width = 4f))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = xTest,
                onValueChange = { xTest = it },
                label = { Text("x =") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Card(
                modifier = Modifier.weight(1.6f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Result f($xTest)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        text = evaluatedY?.let { "%.4f".format(it).trimEnd('0').trimEnd('.') } ?: "Invalid",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5.0, 10.0, 20.0).forEach { r ->
                FilterChip(
                    selected = rangeScale == r,
                    onClick = { rangeScale = r },
                    label = { Text("[-${r.toInt()}, ${r.toInt()}]") }
                )
            }
        }
    }
}
