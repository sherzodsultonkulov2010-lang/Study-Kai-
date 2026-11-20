package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.FollowEntity
import com.example.data.local.PostCommentEntity
import com.example.data.local.PostEntity
import com.example.data.local.PostLikeEntity
import com.example.data.local.TestResultEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.ui.components.AvatarPresets
import com.example.ui.components.CreatePhotoPostDialog
import com.example.ui.components.InstagramProfileBlock
import com.example.ui.components.ProfileCameraCropDialog

@Composable
fun ProfileSettingsScreen(
    currentUser: UserEntity,
    allUsers: List<UserEntity>,
    userPosts: List<PostEntity>,
    allFollows: List<FollowEntity>,
    postLikes: List<PostLikeEntity>,
    postComments: List<PostCommentEntity>,
    userResults: List<TestResultEntity>,
    currentLanguage: AppLanguage,
    darkMode: Boolean?,
    strings: AppStrings,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean?) -> Unit,
    onPickPhotoUri: (Uri, (String) -> Unit) -> Unit,
    onPickPostPhotoUri: (Uri, (String) -> Unit) -> Unit,
    onUpdateProfile: (fullName: String, profession: String, bio: String, avatarKey: String, avatarUri: String?) -> Unit,
    onPublishPost: (imageUri: String?, caption: String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onAddComment: (postId: String, text: String) -> Unit,
    onDeletePost: (String) -> Unit,
    onOpenAdmin: () -> Unit,
    onSignOut: () -> Unit
) {
    var editName by remember(currentUser.id) { mutableStateOf(currentUser.fullName) }
    var editProfession by remember(currentUser.id) { mutableStateOf(currentUser.profession) }
    var editBio by remember(currentUser.id) { mutableStateOf(currentUser.bio) }
    var editAvatarKey by remember(currentUser.id) { mutableStateOf(currentUser.avatarKey) }
    var editAvatarUri by remember(currentUser.id) { mutableStateOf(currentUser.avatarUri) }
    var savedBanner by remember { mutableStateOf(false) }
    var showEditPanel by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var showCameraCropDialog by remember { mutableStateOf(false) }
    var pendingCropUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pendingCropUri = uri
            showCameraCropDialog = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Instagram-Style Profile Card with Avatar Ring, Stats (Posts / Followers / Following), Bio & 3-Col Grid
            item(key = "ig_profile_card") {
                InstagramProfileBlock(
                    user = currentUser,
                    currentUser = currentUser,
                    userPosts = userPosts,
                    allFollows = allFollows,
                    allUsers = allUsers,
                    postLikes = postLikes,
                    postComments = postComments,
                    strings = strings,
                    onToggleFollow = onToggleFollow,
                    onToggleLike = onToggleLike,
                    onAddComment = onAddComment,
                    onDeletePost = onDeletePost,
                    onSharePhotoClick = { showCreatePostDialog = true },
                    onEditProfileClick = { showEditPanel = !showEditPanel },
                    onAvatarCameraClick = {
                        pendingCropUri = null
                        showCameraCropDialog = true
                    }
                )
            }

            // Expandable Edit Profile Panel
            item(key = "edit_profile_panel") {
                AnimatedVisibility(visible = showEditPanel) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = strings.editProfile,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        pendingCropUri = null
                                        showCameraCropDialog = true
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .testTag("take_profile_photo_button")
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(strings.takePhoto)
                                }
                                OutlinedButton(
                                    onClick = {
                                        photoPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .testTag("pick_profile_photo_button")
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(strings.pickPhoto)
                                }
                            }
                            if (editAvatarUri != null) {
                                OutlinedButton(
                                    onClick = {
                                        editAvatarUri = null
                                        onUpdateProfile(editName, editProfession, editBio, editAvatarKey, null)
                                    },
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text("Use Preset")
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AvatarPresets.forEach { preset ->
                                    val selected = editAvatarUri == null && editAvatarKey == preset.key
                                    Surface(
                                        onClick = {
                                            editAvatarKey = preset.key
                                            editAvatarUri = null
                                        },
                                        shape = CircleShape,
                                        border = if (selected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
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

                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it; savedBanner = false },
                                label = { Text(strings.fullName) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = editProfession,
                                onValueChange = { editProfession = it; savedBanner = false },
                                label = { Text(strings.professionLabel) },
                                placeholder = { Text(strings.professionHint) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_profession_input")
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(strings.professionSuggestions) { suggestion ->
                                    FilterChip(
                                        selected = editProfession.equals(suggestion, ignoreCase = true),
                                        onClick = {
                                            editProfession = suggestion
                                            savedBanner = false
                                        },
                                        label = { Text(suggestion) }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = editBio,
                                onValueChange = { editBio = it; savedBanner = false },
                                label = { Text(strings.bioLabel) },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_bio_input")
                            )

                            Button(
                                onClick = {
                                    onUpdateProfile(editName, editProfession, editBio, editAvatarKey, editAvatarUri)
                                    savedBanner = true
                                    showEditPanel = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .testTag("save_profile_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(strings.saveChanges)
                            }

                            if (savedBanner) {
                                Text(
                                    "Profile saved.",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // 4-Language Settings Card (Uzbek, Turkish, English, Russian)
            item(key = "language_card") {
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

            // Theme Settings Card
            item(key = "theme_card") {
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

            // Admin Console & Sign Out
            item(key = "account_actions") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onOpenAdmin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("settings_admin_button")
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.adminPortalBtn)
                    }

                    OutlinedButton(
                        onClick = onSignOut,
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

            // Recent Test History
            if (userResults.isNotEmpty()) {
                item(key = "history_header") {
                    Text(
                        "${strings.testsDoneLabel} (${userResults.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(userResults.take(20), key = { it.id }) { res ->
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
                                Text(res.testTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("${res.subject} • ${res.categoryKey}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${res.score}/${res.totalQuestions}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("+${res.pointsEarned} pts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreatePostDialog) {
        CreatePhotoPostDialog(
            strings = strings,
            onPickPostPhotoUri = onPickPostPhotoUri,
            onPublish = onPublishPost,
            onDismiss = { showCreatePostDialog = false }
        )
    }

    if (showCameraCropDialog) {
        ProfileCameraCropDialog(
            strings = strings,
            initialImageUri = pendingCropUri,
            onAvatarSaved = { croppedUri ->
                editAvatarUri = croppedUri
                onUpdateProfile(editName, editProfession, editBio, editAvatarKey, croppedUri)
                savedBanner = true
                showCameraCropDialog = false
                pendingCropUri = null
            },
            onDismiss = {
                showCameraCropDialog = false
                pendingCropUri = null
            }
        )
    }
}
