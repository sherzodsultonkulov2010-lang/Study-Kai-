package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AppUpdateEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isSignedIn: Boolean,
    currentLanguage: AppLanguage,
    darkMode: Boolean?,
    installedVersionName: String,
    acknowledgedUpdateId: String?,
    appUpdates: List<AppUpdateEntity>,
    strings: AppStrings,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean?) -> Unit,
    onApplyUpdate: (AppUpdateEntity) -> Unit,
    onOpenAdmin: () -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val latestUpdate = appUpdates.firstOrNull()
    val hasPendingUpdate = latestUpdate != null && latestUpdate.id != acknowledgedUpdateId

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(strings.settingsTitle, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.backLabel)
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
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. 4-Language Settings
                item(key = "settings_lang_card") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(strings.languageSettings, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(AppLanguage.entries) { lang ->
                                    FilterChip(
                                        selected = currentLanguage == lang,
                                        onClick = { onLanguageChange(lang) },
                                        label = { Text("${lang.badge} • ${lang.nativeName}") },
                                        modifier = Modifier.testTag("settings_lang_${lang.code}")
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Theme Settings
                item(key = "settings_theme_card") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(strings.themeSettings, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                SegmentedButton(
                                    selected = darkMode == null,
                                    onClick = { onDarkModeChange(null) },
                                    shape = SegmentedButtonDefaults.itemShape(0, 3)
                                ) { Text(strings.systemDefault, maxLines = 1) }
                                SegmentedButton(
                                    selected = darkMode == false,
                                    onClick = { onDarkModeChange(false) },
                                    shape = SegmentedButtonDefaults.itemShape(1, 3)
                                ) { Text(strings.lightMode, maxLines = 1) }
                                SegmentedButton(
                                    selected = darkMode == true,
                                    onClick = { onDarkModeChange(true) },
                                    shape = SegmentedButtonDefaults.itemShape(2, 3)
                                ) { Text(strings.darkMode, maxLines = 1) }
                            }
                        }
                    }
                }

                // 3. App Version & Admin-Published Updates
                item(key = "settings_updates_card") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text(strings.adminUpdatesTab, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = "${strings.appVersionLabel}: v$installedVersionName",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (hasPendingUpdate && latestUpdate != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "v${latestUpdate.versionName} • ${latestUpdate.title}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = latestUpdate.changelog,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Button(
                                            onClick = { onApplyUpdate(latestUpdate) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 46.dp)
                                                .testTag("settings_apply_update_button")
                                        ) {
                                            Text(strings.applyUpdateBtn)
                                        }
                                    }
                                }
                            } else if (latestUpdate != null) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "v${latestUpdate.versionName} — ${latestUpdate.title}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Admin Console & Sign Out
                item(key = "settings_account_actions") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                onBack()
                                onOpenAdmin()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp)
                                .testTag("settings_admin_button")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(strings.adminPortalBtn)
                        }

                        if (isSignedIn) {
                            OutlinedButton(
                                onClick = {
                                    onBack()
                                    onSignOut()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp)
                                    .testTag("sign_out_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(strings.signOut)
                            }
                        }
                    }
                }
            }
        }
    }
}
