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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.QuizQuestion
import com.example.data.local.AppUpdateEntity
import com.example.data.local.CustomTestEntity
import com.example.data.local.GroupEntity
import com.example.data.local.NewsEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.ProfessionBadge
import com.example.ui.components.RoleBadge
import com.example.ui.components.UserAvatar
import com.example.ui.components.VerifiedBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    appLanguage: AppLanguage,
    strings: AppStrings,
    isConfigLoaded: Boolean,
    hasPasswordConfigured: Boolean,
    isUnlocked: Boolean,
    adminMessage: String?,
    allUsers: List<UserEntity>,
    newsPosts: List<NewsEntity>,
    groups: List<GroupEntity>,
    customTests: List<CustomTestEntity>,
    appUpdates: List<AppUpdateEntity>,
    onSetupPassword: (String, String) -> Unit,
    onUnlock: (String) -> Unit,
    onUpdatePassword: (String, String, String) -> Unit,
    onSetUserVerified: (userId: String, verified: Boolean) -> Unit,
    onDeleteUser: (String) -> Unit,
    onAddNews: (title: String, body: String, category: String, pinAsDaily: Boolean) -> Unit,
    onPinDaily: (String) -> Unit,
    onDeleteNews: (String) -> Unit,
    onDeleteGroup: (String) -> Unit,
    onCreateCustomTest: (subject: String, categoryKey: String, title: String, description: String, questions: List<QuizQuestion>) -> Unit,
    onDeleteCustomTest: (String) -> Unit,
    onPublishAppUpdate: (versionName: String, title: String, changelog: String, isCritical: Boolean) -> Unit,
    onDeleteAppUpdate: (String) -> Unit,
    onLockAndClose: () -> Unit
) {
    BackHandler { onLockAndClose() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.adminPanelTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(strings.adminOwnerNote, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onLockAndClose, modifier = Modifier.testTag("admin_close_button")) {
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
            if (!isConfigLoaded) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (!isUnlocked) {
                AdminAuthGate(
                    appLanguage = appLanguage,
                    strings = strings,
                    hasPasswordConfigured = hasPasswordConfigured,
                    adminMessage = adminMessage,
                    onSetupPassword = onSetupPassword,
                    onUnlock = onUnlock
                )
            } else {
                AdminDashboard(
                    strings = strings,
                    adminMessage = adminMessage,
                    allUsers = allUsers,
                    newsPosts = newsPosts,
                    groups = groups,
                    customTests = customTests,
                    appUpdates = appUpdates,
                    onSetUserVerified = onSetUserVerified,
                    onDeleteUser = onDeleteUser,
                    onAddNews = onAddNews,
                    onPinDaily = onPinDaily,
                    onDeleteNews = onDeleteNews,
                    onDeleteGroup = onDeleteGroup,
                    onCreateCustomTest = onCreateCustomTest,
                    onDeleteCustomTest = onDeleteCustomTest,
                    onPublishAppUpdate = onPublishAppUpdate,
                    onDeleteAppUpdate = onDeleteAppUpdate,
                    onUpdatePassword = onUpdatePassword,
                    onLock = onLockAndClose
                )
            }
        }
    }
}

@Composable
private fun AdminAuthGate(
    appLanguage: AppLanguage,
    strings: AppStrings,
    hasPasswordConfigured: Boolean,
    adminMessage: String?,
    onSetupPassword: (String, String) -> Unit,
    onUnlock: (String) -> Unit
) {
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val privacyNote = when (appLanguage) {
        AppLanguage.UZBEK -> "Bu parol shifrlangan holda saqlanadi va standart parol mavjud emas. Uni faqat Sherzod Sultankulov biladi."
        AppLanguage.TURKISH -> "Bu şifre tek yönlü şifrelenir ve varsayılan bir şifre yoktur. Yalnızca Sherzod Sultankulov bilir."
        AppLanguage.RUSSIAN -> "Пароль хранится в зашифрованном виде; стандартного пароля нет. Его знает только Sherzod Sultankulov."
        AppLanguage.ENGLISH -> "Encrypted one-way hash with no default password. Known only to Sherzod Sultankulov."
    }

    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Sherzod Sultankulov", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            VerifiedBadge(size = 22.dp, contentDescription = strings.verifiedBadgeLabel)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            strings.adminLoginUsernameHint,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            if (hasPasswordConfigured) strings.adminOwnerNote else strings.createAdminPass,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                privacyNote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text(strings.password) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_password_input")
        )

        if (!hasPasswordConfigured) {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text(strings.confirmPass) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_confirm_password_input")
            )
        }

        if (!adminMessage.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(adminMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(18.dp))
        Button(
            onClick = {
                val entered = pass
                val enteredConfirm = confirm
                pass = ""
                confirm = ""
                if (hasPasswordConfigured) onUnlock(entered) else onSetupPassword(entered, enteredConfirm)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .testTag("admin_unlock_button")
        ) {
            Text(if (hasPasswordConfigured) strings.unlockAdmin else strings.createAdminPass)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboard(
    strings: AppStrings,
    adminMessage: String?,
    allUsers: List<UserEntity>,
    newsPosts: List<NewsEntity>,
    groups: List<GroupEntity>,
    customTests: List<CustomTestEntity>,
    appUpdates: List<AppUpdateEntity>,
    onSetUserVerified: (String, Boolean) -> Unit,
    onDeleteUser: (String) -> Unit,
    onAddNews: (String, String, String, Boolean) -> Unit,
    onPinDaily: (String) -> Unit,
    onDeleteNews: (String) -> Unit,
    onDeleteGroup: (String) -> Unit,
    onCreateCustomTest: (String, String, String, String, List<QuizQuestion>) -> Unit,
    onDeleteCustomTest: (String) -> Unit,
    onPublishAppUpdate: (String, String, String, Boolean) -> Unit,
    onDeleteAppUpdate: (String) -> Unit,
    onUpdatePassword: (String, String, String) -> Unit,
    onLock: () -> Unit
) {
    var tab by remember { mutableIntStateOf(0) } // 0=Users, 1=News, 2=CreateTest, 3=Updates, 4=Security
    var newsTitle by remember { mutableStateOf("") }
    var newsBody by remember { mutableStateOf("") }
    var newsCategory by remember { mutableStateOf("Announcement") }
    var pinAsDaily by remember { mutableStateOf(true) }

    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmNewPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .widthIn(max = 760.dp)
            .fillMaxSize()
    ) {
        val tabs = listOf(
            "${strings.manageUsers} (${allUsers.size})",
            strings.addNews,
            strings.adminCreateTestTab,
            strings.adminUpdatesTab,
            "Security"
        )
        PrimaryScrollableTabRow(selectedTabIndex = tab, edgePadding = 12.dp) {
            tabs.forEachIndexed { idx, label ->
                Tab(
                    selected = tab == idx,
                    onClick = { tab = idx },
                    text = { Text(label, maxLines = 1) },
                    modifier = Modifier.testTag("admin_tab_$idx")
                )
            }
        }

        if (!adminMessage.isNullOrBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(adminMessage, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.labelLarge)
            }
        }

        when (tab) {
            0 -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allUsers, key = { it.id }) { user ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                UserAvatar(user.fullName, user.avatarKey, user.avatarUri, size = 44.dp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(user.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        if (user.isVerified) {
                                            VerifiedBadge(size = 16.dp, contentDescription = strings.verifiedBadgeLabel)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        RoleBadge(user.role, strings.student, strings.teacher)
                                        if (user.profession.isNotBlank()) {
                                            ProfessionBadge(user.profession)
                                        }
                                    }
                                    Text("@${user.username} • ${user.points} pts • ${user.testsCompleted} tests", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (user.isVerified) {
                                    OutlinedButton(
                                        onClick = { onSetUserVerified(user.id, false) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 44.dp)
                                            .testTag("admin_verify_user_${user.username}")
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(strings.removeBlueTick, maxLines = 1)
                                    }
                                } else {
                                    Button(
                                        onClick = { onSetUserVerified(user.id, true) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 44.dp)
                                            .testTag("admin_verify_user_${user.username}")
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(strings.giveBlueTick, maxLines = 1)
                                    }
                                }
                                OutlinedButton(
                                    onClick = { onDeleteUser(user.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier
                                        .heightIn(min = 44.dp)
                                        .testTag("admin_delete_user_${user.username}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = strings.deleteUser, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(strings.deleteUser)
                                }
                            }
                        }
                    }
                }
            }

            1 -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "add_news_form") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(strings.addNews, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = newsTitle,
                                onValueChange = { newsTitle = it },
                                label = { Text("Headline / Title") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_news_title_input")
                            )
                            OutlinedTextField(
                                value = newsCategory,
                                onValueChange = { newsCategory = it },
                                label = { Text("Category") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newsBody,
                                onValueChange = { newsBody = it },
                                label = { Text("News Content / Daily Message") },
                                minLines = 3,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_news_body_input")
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = pinAsDaily,
                                    onCheckedChange = { pinAsDaily = it }
                                )
                                Text(strings.pinDaily, style = MaterialTheme.typography.bodyMedium)
                            }
                            Button(
                                onClick = {
                                    if (newsTitle.isNotBlank() && newsBody.isNotBlank()) {
                                        onAddNews(newsTitle, newsBody, newsCategory, pinAsDaily)
                                        newsTitle = ""
                                        newsBody = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .testTag("admin_publish_news_button")
                            ) {
                                Text(strings.addNews)
                            }
                        }
                    }
                }

                items(newsPosts, key = { it.id }) { post ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(post.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                if (post.isPinnedDaily) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.tertiaryContainer) {
                                        Text("PINNED", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(post.body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (!post.isPinnedDaily) {
                                    OutlinedButton(onClick = { onPinDaily(post.id) }, modifier = Modifier.heightIn(min = 42.dp)) {
                                        Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Pin Top")
                                    }
                                }
                                OutlinedButton(
                                    onClick = { onDeleteNews(post.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.heightIn(min = 42.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(strings.deleteUser)
                                }
                            }
                        }
                    }
                }
            }

            2 -> AdminTestCreatorTab(
                strings = strings,
                customTests = customTests,
                onCreateCustomTest = onCreateCustomTest,
                onDeleteCustomTest = onDeleteCustomTest
            )

            3 -> AdminAppUpdatesTab(
                strings = strings,
                appUpdates = appUpdates,
                onPublishUpdate = onPublishAppUpdate,
                onDeleteUpdate = onDeleteAppUpdate
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "change_admin_pass") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Update Sherzod Sultankulov's Admin Password", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                strings.adminLoginUsernameHint,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            OutlinedTextField(
                                value = oldPass,
                                onValueChange = { oldPass = it },
                                label = { Text("Current Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newPass,
                                onValueChange = { newPass = it },
                                label = { Text("New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = confirmNewPass,
                                onValueChange = { confirmNewPass = it },
                                label = { Text(strings.confirmPass) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    onUpdatePassword(oldPass, newPass, confirmNewPass)
                                    oldPass = ""
                                    newPass = ""
                                    confirmNewPass = ""
                                },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                            ) {
                                Text(strings.saveChanges)
                            }
                            OutlinedButton(
                                onClick = onLock,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                            ) {
                                Text("Lock Admin Console")
                            }
                        }
                    }
                }

                item(key = "groups_admin_header") {
                    Text("Groups (${groups.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(groups, key = { it.id }) { grp ->
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
                                Text(grp.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(grp.topicTag, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            OutlinedButton(
                                onClick = { onDeleteGroup(grp.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(strings.deleteUser)
                            }
                        }
                    }
                }
            }
        }
    }
}
