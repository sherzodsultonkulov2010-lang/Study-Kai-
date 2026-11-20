package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.AvatarPresets
import com.example.ui.components.ProfessionBadge
import com.example.ui.components.RoleBadge
import com.example.ui.components.UserAvatar
import com.example.ui.components.VerifiedBadge

@Composable
fun EntryAuthScreen(
    currentLanguage: AppLanguage,
    strings: AppStrings,
    savedUsers: List<UserEntity>,
    authMessage: String?,
    onLanguageChange: (AppLanguage) -> Unit,
    onSignIn: (String, String) -> Unit,
    onRegister: (fullName: String, username: String, password: String, role: String, profession: String, bio: String, avatarKey: String) -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenSettings: () -> Unit,
    onClearError: () -> Unit
) {
    val quickSwitchUsers = remember(savedUsers) { savedUsers.filter { it.id != "seed_admin_sherzod" } }
    var selectedTab by remember { mutableIntStateOf(0) } // Default to Sign In (username + password)
    var role by remember { mutableStateOf("STUDENT") }
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedAvatarKey by remember { mutableStateOf(AvatarPresets.first().key) }
    var showPassword by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 580.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Row: 4-Language Selector Strip + Top-Right Settings Gear Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(AppLanguage.entries) { lang ->
                            FilterChip(
                                selected = lang == currentLanguage,
                                onClick = { onLanguageChange(lang) },
                                label = { Text("${lang.badge} • ${lang.nativeName}", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.testTag("entry_lang_${lang.code}")
                            )
                        }
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("entry_top_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = strings.settingsTitle,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(164.dp)
                        .clip(RoundedCornerShape(22.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = strings.appTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x330F172A), Color(0xCC0F172A))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = strings.appTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.tagline,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                // Sign In / Create Account Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0; onClearError() },
                                shape = SegmentedButtonDefaults.itemShape(0, 2),
                                modifier = Modifier.testTag("auth_tab_signin")
                            ) {
                                Text(strings.loginTab)
                            }
                            SegmentedButton(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1; onClearError() },
                                shape = SegmentedButtonDefaults.itemShape(1, 2),
                                modifier = Modifier.testTag("auth_tab_register")
                            ) {
                                Text(strings.registerTab)
                            }
                        }

                        if (selectedTab == 1) {
                            Text(
                                strings.chooseRole,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RolePickTile(
                                    title = strings.student,
                                    icon = Icons.Default.School,
                                    selected = role == "STUDENT",
                                    onClick = { role = "STUDENT" },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("role_student_button")
                                )
                                RolePickTile(
                                    title = strings.teacher,
                                    icon = Icons.Default.Badge,
                                    selected = role == "TEACHER",
                                    onClick = { role = "TEACHER" },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("role_teacher_button")
                                )
                            }
                        }

                        if (selectedTab == 1) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it; onClearError() },
                                label = { Text(strings.fullName) },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_fullname_input")
                            )
                        }

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; onClearError() },
                            label = { Text(strings.username) },
                            placeholder = { Text("e.g. aziza_english") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_username_input")
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; onClearError() },
                            label = { Text(strings.password) },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )

                        if (selectedTab == 1) {
                            OutlinedTextField(
                                value = profession,
                                onValueChange = { profession = it },
                                label = { Text(strings.professionLabel) },
                                placeholder = { Text(strings.professionHint) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_profession_input")
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(strings.professionSuggestions) { suggestion ->
                                    FilterChip(
                                        selected = profession.equals(suggestion, ignoreCase = true),
                                        onClick = { profession = suggestion },
                                        label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text(strings.bioLabel) },
                                minLines = 2,
                                maxLines = 3,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_bio_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarPresets.forEach { preset ->
                                    val chosen = preset.key == selectedAvatarKey
                                    Surface(
                                        onClick = { selectedAvatarKey = preset.key },
                                        shape = CircleShape,
                                        border = if (chosen) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Brush.linearGradient(listOf(preset.start, preset.end)))
                                        )
                                    }
                                }
                            }
                        }

                        if (!authMessage.isNullOrBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = authMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (selectedTab == 0) {
                                    onSignIn(username, password)
                                } else {
                                    onRegister(fullName, username, password, role, profession, bio, selectedAvatarKey)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag("auth_submit_button")
                        ) {
                            Text(if (selectedTab == 0) strings.signInBtn else strings.createAccountBtn)
                        }
                    }
                }

                // Saved Accounts (tap fills username only — password is always required to sign in)
                if (quickSwitchUsers.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            strings.savedAccounts,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            strings.savedAccountPasswordHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(quickSwitchUsers, key = { it.id }) { user ->
                            Card(
                                modifier = Modifier
                                    .width(196.dp)
                                    .clickable {
                                        selectedTab = 0
                                        username = user.username
                                        password = ""
                                        onClearError()
                                    }
                                    .testTag("saved_account_${user.username}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    UserAvatar(
                                        fullName = user.fullName,
                                        avatarKey = user.avatarKey,
                                        avatarUri = user.avatarUri,
                                        size = 42.dp
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                user.fullName,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            if (user.isVerified) {
                                                VerifiedBadge(size = 14.dp, contentDescription = strings.verifiedBadgeLabel)
                                            }
                                        }
                                        Text(
                                            "@${user.username}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            RoleBadge(user.role, strings.student, strings.teacher)
                                        }
                                        if (user.profession.isNotBlank()) {
                                            Spacer(Modifier.height(3.dp))
                                            ProfessionBadge(user.profession)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Sherzod Sultankulov Private Admin Access
                OutlinedButton(
                    onClick = onOpenAdmin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                        .testTag("entry_admin_portal_button")
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(strings.adminPortalBtn)
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RolePickTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = modifier.heightIn(min = 56.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
