package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.i18n.StringsProvider
import com.example.ui.MainViewModel
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.QuizRunnerDialog
import com.example.ui.components.VerifiedBadge
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.EnglishHubScreen
import com.example.ui.screens.EntryAuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MathHubScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.EduSphereTheme
import com.example.util.TtsHelper

private data class NavTab(
    val index: Int,
    val label: String,
    val icon: ImageVector,
    val tag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduSphereRoot()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduSphereRoot(vm: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val ttsHelper = remember { TtsHelper(context) }
    DisposableEffect(Unit) {
        onDispose { ttsHelper.shutdown() }
    }

    val appLanguage by vm.appLanguage.collectAsStateWithLifecycle()
    val darkModePref by vm.darkMode.collectAsStateWithLifecycle()
    val currentUser by vm.currentUser.collectAsStateWithLifecycle()
    val allUsers by vm.allUsers.collectAsStateWithLifecycle()
    val newsPosts by vm.newsPosts.collectAsStateWithLifecycle()
    val groups by vm.groups.collectAsStateWithLifecycle()
    val allFollows by vm.allFollows.collectAsStateWithLifecycle()
    val socialPosts by vm.socialPosts.collectAsStateWithLifecycle()
    val postLikes by vm.postLikes.collectAsStateWithLifecycle()
    val postComments by vm.postComments.collectAsStateWithLifecycle()
    val customTests by vm.customTests.collectAsStateWithLifecycle()
    val appUpdates by vm.appUpdates.collectAsStateWithLifecycle()
    val acknowledgedUpdateId by vm.acknowledgedUpdateId.collectAsStateWithLifecycle()
    val installedVersionName by vm.installedVersionName.collectAsStateWithLifecycle()
    val userResults by vm.userResults.collectAsStateWithLifecycle()
    val dictionaryEntries by vm.dictionaryEntries.collectAsStateWithLifecycle()
    val dictionaryLoaded by vm.dictionaryLoaded.collectAsStateWithLifecycle()
    val activeTest by vm.activeTest.collectAsStateWithLifecycle()
    val authMessage by vm.authMessage.collectAsStateWithLifecycle()
    val showAdminModal by vm.showAdminModal.collectAsStateWithLifecycle()
    val adminConfigLoaded by vm.adminConfigLoaded.collectAsStateWithLifecycle()
    val isAdminUnlocked by vm.isAdminUnlocked.collectAsStateWithLifecycle()
    val adminPasswordHash by vm.adminPasswordHash.collectAsStateWithLifecycle()
    val adminMessage by vm.adminMessage.collectAsStateWithLifecycle()
    val notifications by vm.notifications.collectAsStateWithLifecycle()
    val unreadNotificationCount by vm.unreadNotificationCount.collectAsStateWithLifecycle()

    var showSettingsScreen by rememberSaveable { mutableStateOf(false) }

    val strings = remember(appLanguage) { StringsProvider.get(appLanguage) }
    val useDark = darkModePref ?: isSystemInDarkTheme()
    val latestUpdate = appUpdates.firstOrNull()
    val hasPendingUpdate = latestUpdate != null && latestUpdate.id != acknowledgedUpdateId

    EduSphereTheme(darkTheme = useDark) {
        when {
            showAdminModal -> {
                AdminPanelScreen(
                    appLanguage = appLanguage,
                    strings = strings,
                    isConfigLoaded = adminConfigLoaded,
                    hasPasswordConfigured = !adminPasswordHash.isNullOrBlank(),
                    isUnlocked = isAdminUnlocked,
                    adminMessage = adminMessage,
                    allUsers = allUsers,
                    newsPosts = newsPosts,
                    groups = groups,
                    customTests = customTests,
                    appUpdates = appUpdates,
                    onSetupPassword = vm::setupAdminPassword,
                    onUnlock = vm::unlockAdmin,
                    onUpdatePassword = vm::updateAdminPassword,
                    onSetUserVerified = vm::adminSetUserVerified,
                    onDeleteUser = vm::adminDeleteUser,
                    onAddNews = vm::adminAddNews,
                    onPinDaily = vm::adminPinDaily,
                    onDeleteNews = vm::adminDeleteNews,
                    onDeleteGroup = vm::adminDeleteGroup,
                    onCreateCustomTest = vm::adminCreateCustomTest,
                    onDeleteCustomTest = vm::adminDeleteCustomTest,
                    onPublishAppUpdate = vm::adminPublishAppUpdate,
                    onDeleteAppUpdate = vm::adminDeleteAppUpdate,
                    onLockAndClose = vm::lockAdmin
                )
            }

            showSettingsScreen -> {
                SettingsScreen(
                    isSignedIn = currentUser != null,
                    currentLanguage = appLanguage,
                    darkMode = darkModePref,
                    installedVersionName = installedVersionName,
                    acknowledgedUpdateId = acknowledgedUpdateId,
                    appUpdates = appUpdates,
                    strings = strings,
                    onLanguageChange = vm::setLanguage,
                    onDarkModeChange = vm::setDarkMode,
                    onApplyUpdate = vm::applyAppUpdate,
                    onOpenAdmin = vm::openAdminModal,
                    onSignOut = vm::signOut,
                    onBack = { showSettingsScreen = false }
                )
            }

            activeTest != null -> {
                val session = activeTest!!
                QuizRunnerDialog(
                    session = session,
                    strings = strings,
                    onSubmitScore = { score -> vm.submitTestScore(session, score) },
                    onDismiss = vm::closeActiveTest
                )
            }

            currentUser == null -> {
                EntryAuthScreen(
                    currentLanguage = appLanguage,
                    strings = strings,
                    savedUsers = allUsers,
                    authMessage = authMessage,
                    onLanguageChange = vm::setLanguage,
                    onSignIn = vm::signIn,
                    onRegister = vm::registerAccount,
                    onOpenAdmin = vm::openAdminModal,
                    onOpenSettings = { showSettingsScreen = true },
                    onClearError = vm::clearAuthMessage
                )
            }

            else -> {
                val user = currentUser!!
                var selectedTab by rememberSaveable { mutableIntStateOf(0) }
                var showNotificationsDialog by remember { mutableStateOf(false) }
                var pendingChatUserId by remember { mutableStateOf<String?>(null) }
                var pendingGroupId by remember { mutableStateOf<String?>(null) }
                val navTabs = remember(strings) {
                    listOf(
                        NavTab(0, strings.navHome, Icons.Default.Home, "nav_home"),
                        NavTab(1, strings.navEnglish, Icons.Default.Translate, "nav_english"),
                        NavTab(2, strings.navMath, Icons.Default.Calculate, "nav_math"),
                        NavTab(3, strings.navCommunity, Icons.Default.Groups, "nav_community"),
                        NavTab(4, strings.navProfile, Icons.Default.Person, "nav_profile")
                    )
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val useRail = maxWidth >= 600.dp

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = strings.appTitle,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (user.isVerified) {
                                            VerifiedBadge(size = 18.dp, contentDescription = strings.verifiedBadgeLabel)
                                        }
                                    }
                                },
                                actions = {
                                    if (isAdminUnlocked) {
                                        IconButton(
                                            onClick = vm::openAdminModal,
                                            modifier = Modifier.testTag("top_admin_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AdminPanelSettings,
                                                contentDescription = strings.adminPortalBtn,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { showNotificationsDialog = true },
                                        modifier = Modifier.testTag("top_notifications_button")
                                    ) {
                                        BadgedBox(
                                            badge = {
                                                if (unreadNotificationCount > 0) {
                                                    Badge {
                                                        Text(
                                                            if (unreadNotificationCount > 9) "9+"
                                                            else unreadNotificationCount.toString()
                                                        )
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = strings.notificationsTitle
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { showSettingsScreen = true },
                                        modifier = Modifier.testTag("top_settings_button")
                                    ) {
                                        BadgedBox(
                                            badge = {
                                                if (hasPendingUpdate) {
                                                    Badge()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = strings.settingsTitle
                                            )
                                        }
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            if (!useRail) {
                                NavigationBar {
                                    navTabs.forEach { tab ->
                                        NavigationBarItem(
                                            selected = selectedTab == tab.index,
                                            onClick = { selectedTab = tab.index },
                                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                                            label = { Text(tab.label, maxLines = 1) },
                                            modifier = Modifier.testTag(tab.tag)
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (useRail) {
                                NavigationRail {
                                    navTabs.forEach { tab ->
                                        NavigationRailItem(
                                            selected = selectedTab == tab.index,
                                            onClick = { selectedTab = tab.index },
                                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                                            label = { Text(tab.label, maxLines = 1) },
                                            modifier = Modifier.testTag(tab.tag)
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                                when (selectedTab) {
                                    0 -> HomeScreen(
                                        currentUser = user,
                                        allUsers = allUsers,
                                        newsPosts = newsPosts,
                                        latestUpdate = latestUpdate,
                                        acknowledgedUpdateId = acknowledgedUpdateId,
                                        strings = strings,
                                        onApplyUpdate = vm::applyAppUpdate,
                                        onNavigateToEnglish = { selectedTab = 1 },
                                        onNavigateToMath = { selectedTab = 2 },
                                        onNavigateToCommunity = { selectedTab = 3 }
                                    )
                                    1 -> EnglishHubScreen(
                                        strings = strings,
                                        dictionaryEntries = dictionaryEntries,
                                        dictionaryLoaded = dictionaryLoaded,
                                        customTests = customTests,
                                        onStartTopicTest = vm::startEnglishTest,
                                        onStartCustomTest = vm::startCustomTest,
                                        onSpeakWord = { text, slow -> ttsHelper.speak(text, slow) }
                                    )
                                    2 -> MathHubScreen(
                                        appLanguage = appLanguage,
                                        strings = strings,
                                        customTests = customTests,
                                        onStartMathTest = vm::startMathTest,
                                        onStartCustomTest = vm::startCustomTest
                                    )
                                    3 -> CommunityScreen(
                                        currentUser = user,
                                        allUsers = allUsers,
                                        groups = groups,
                                        socialPosts = socialPosts,
                                        allFollows = allFollows,
                                        postLikes = postLikes,
                                        postComments = postComments,
                                        strings = strings,
                                        observeMessages = vm::observeMessages,
                                        directChannelId = vm::directChannelId,
                                        onSendDirectMessage = vm::sendDirectMessage,
                                        onSendGroupMessage = vm::sendGroupMessage,
                                        onCreateGroup = vm::createGroup,
                                        onToggleJoinGroup = vm::toggleJoinGroup,
                                        onToggleFollow = vm::toggleFollow,
                                        onPickPostPhotoUri = vm::savePickedPostImage,
                                        onPublishPost = vm::publishPhotoPost,
                                        onToggleLike = vm::toggleLikePost,
                                        onAddComment = vm::addPostComment,
                                        onDeletePost = vm::deletePhotoPost,
                                        pendingChatUserId = pendingChatUserId,
                                        pendingGroupId = pendingGroupId,
                                        onConsumePendingNav = {
                                            pendingChatUserId = null
                                            pendingGroupId = null
                                        }
                                    )
                                    else -> ProfileSettingsScreen(
                                        currentUser = user,
                                        allUsers = allUsers,
                                        userPosts = socialPosts.filter { it.authorId == user.id },
                                        allFollows = allFollows,
                                        postLikes = postLikes,
                                        postComments = postComments,
                                        userResults = userResults,
                                        currentLanguage = appLanguage,
                                        darkMode = darkModePref,
                                        strings = strings,
                                        onLanguageChange = vm::setLanguage,
                                        onDarkModeChange = vm::setDarkMode,
                                        onPickPhotoUri = vm::savePickedProfileImage,
                                        onPickPostPhotoUri = vm::savePickedPostImage,
                                        onUpdateProfile = vm::updateProfile,
                                        onPublishPost = vm::publishPhotoPost,
                                        onToggleFollow = vm::toggleFollow,
                                        onToggleLike = vm::toggleLikePost,
                                        onAddComment = vm::addPostComment,
                                        onDeletePost = vm::deletePhotoPost,
                                        onOpenAdmin = vm::openAdminModal,
                                        onSignOut = vm::signOut
                                    )
                                }
                            }
                        }
                    }
                }

                if (showNotificationsDialog) {
                    NotificationsDialog(
                        notifications = notifications,
                        unreadCount = unreadNotificationCount,
                        strings = strings,
                        onMarkAllRead = vm::markAllNotificationsRead,
                        onSelectNotification = { item ->
                            vm.markAllNotificationsRead()
                            showNotificationsDialog = false
                            if (item.peerUserId != null) {
                                pendingChatUserId = item.peerUserId
                                selectedTab = 3
                            } else if (item.groupId != null) {
                                pendingGroupId = item.groupId
                                selectedTab = 3
                            }
                        },
                        onDismiss = { showNotificationsDialog = false }
                    )
                }
            }
        }
    }
}
