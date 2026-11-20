package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.FollowEntity
import com.example.data.local.PostCommentEntity
import com.example.data.local.PostEntity
import com.example.data.local.PostLikeEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppStrings
import kotlinx.coroutines.delay

@Composable
fun PostPhotoMedia(
    post: PostEntity,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (!post.imageUri.isNullOrBlank()) {
        AsyncImage(
            model = post.imageUri,
            contentDescription = post.caption.ifBlank { "Shared photo" },
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        val resId = when (post.fallbackResName) {
            "img_english_studio" -> R.drawable.img_english_studio
            "img_math_lab" -> R.drawable.img_math_lab
            else -> R.drawable.img_hero_banner
        }
        Image(
            painter = painterResource(resId),
            contentDescription = post.caption.ifBlank { "Shared photo" },
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

@Composable
fun InstagramPostCard(
    post: PostEntity,
    author: UserEntity?,
    currentUser: UserEntity,
    isFollowingAuthor: Boolean,
    likes: List<PostLikeEntity>,
    comments: List<PostCommentEntity>,
    strings: AppStrings,
    onOpenAuthorProfile: (UserEntity) -> Unit,
    onToggleFollow: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onOpenComments: (PostEntity) -> Unit,
    onDeletePost: (String) -> Unit
) {
    val isLiked = remember(likes, currentUser.id) { likes.any { it.userId == currentUser.id } }
    var showDoubleTapHeart by remember { mutableStateOf(false) }

    LaunchedEffect(showDoubleTapHeart) {
        if (showDoubleTapHeart) {
            delay(650)
            showDoubleTapHeart = false
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}")
    ) {
        Column {
            // Post Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = author != null) { author?.let(onOpenAuthorProfile) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UserAvatar(
                        fullName = author?.fullName ?: "Member",
                        avatarKey = author?.avatarKey ?: "avatar_teal",
                        avatarUri = author?.avatarUri,
                        size = 40.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = author?.fullName ?: "Member",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (author?.isVerified == true) {
                                VerifiedBadge(size = 16.dp, contentDescription = strings.verifiedBadgeLabel)
                            }
                        }
                        val sub = buildString {
                            append("@${author?.username ?: "user"}")
                            if (!author?.profession.isNullOrBlank()) {
                                append(" • ${author?.profession}")
                            }
                        }
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (author != null && author.id != currentUser.id) {
                    if (isFollowingAuthor) {
                        OutlinedButton(
                            onClick = { onToggleFollow(author.id) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .heightIn(min = 36.dp)
                                .testTag("post_follow_${author.id}")
                        ) {
                            Text(strings.followingBtn, style = MaterialTheme.typography.labelMedium)
                        }
                    } else {
                        Button(
                            onClick = { onToggleFollow(author.id) },
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier
                                .heightIn(min = 36.dp)
                                .testTag("post_follow_${author.id}")
                        ) {
                            Text(strings.followBtn, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                } else if (post.authorId == currentUser.id) {
                    IconButton(
                        onClick = { onDeletePost(post.id) },
                        modifier = Modifier.testTag("delete_post_${post.id}")
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete post",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Photo Canvas (supports double tap to like)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(post.id, isLiked) {
                        detectTapGestures(
                            onDoubleTap = {
                                showDoubleTapHeart = true
                                if (!isLiked) onToggleLike(post.id)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                PostPhotoMedia(
                    post = post,
                    modifier = Modifier.fillMaxSize()
                )
                androidx.compose.animation.AnimatedVisibility(
                    visible = showDoubleTapHeart,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.92f),
                        modifier = Modifier.size(84.dp)
                    )
                }
            }

            // Action Bar: Like & Comment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onToggleLike(post.id) },
                    modifier = Modifier.testTag("like_post_${post.id}")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like post",
                        tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = { onOpenComments(post) },
                    modifier = Modifier.testTag("comment_post_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = strings.commentsLabel
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = String.format(strings.likesCountFmt, likes.size),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }

            // Caption & Comment Preview
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (post.caption.isNotBlank()) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(author?.username ?: "member")
                                append("  ")
                            }
                            append(post.caption)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (comments.isNotEmpty()) {
                    val lastComment = comments.last()
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                append(lastComment.authorName)
                                append(": ")
                            }
                            append(lastComment.text)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${strings.commentsLabel} (${comments.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onOpenComments(post) }
                        .padding(vertical = 4.dp)
                        .testTag("open_comments_${post.id}")
                )
            }
        }
    }
}

@Composable
fun PostCommentsDialog(
    post: PostEntity,
    comments: List<PostCommentEntity>,
    strings: AppStrings,
    onAddComment: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var commentDraft by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${strings.commentsLabel} (${comments.size})", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (comments.isEmpty()) {
                    Text(
                        text = "—",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(comments, key = { it.id }) { comment ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Text(
                                        text = comment.authorName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = comment.text,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = commentDraft,
                        onValueChange = { commentDraft = it },
                        placeholder = { Text(strings.addCommentHint) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_input_field")
                    )
                    FilledIconButton(
                        onClick = {
                            if (commentDraft.isNotBlank()) {
                                onAddComment(commentDraft)
                                commentDraft = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("comment_send_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = strings.publishBtn)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.backLabel)
            }
        }
    )
}

@Composable
fun CreatePhotoPostDialog(
    strings: AppStrings,
    onPickPostPhotoUri: (Uri, (String) -> Unit) -> Unit,
    onPublish: (imageUri: String?, caption: String) -> Unit,
    onDismiss: () -> Unit
) {
    var pickedUri by remember { mutableStateOf<String?>(null) }
    var caption by remember { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onPickPostPhotoUri(uri) { savedPath ->
                pickedUri = savedPath
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(strings.sharePhotoBtn, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                        .clickable {
                            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (pickedUri != null) {
                        AsyncImage(
                            model = pickedUri,
                            contentDescription = "Selected photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = strings.pickPhoto,
                                modifier = Modifier.size(38.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(strings.pickPhoto, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("pick_post_photo_button")
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.pickPhoto)
                }

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    placeholder = { Text(strings.writeCaptionHint) },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_caption_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pickedUri != null || caption.isNotBlank()) {
                        onPublish(pickedUri, caption)
                        onDismiss()
                    }
                },
                enabled = pickedUri != null || caption.isNotBlank(),
                modifier = Modifier.testTag("publish_post_button")
            ) {
                Text(strings.publishBtn)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.backLabel)
            }
        }
    )
}

@Composable
fun InstagramProfileBlock(
    user: UserEntity,
    currentUser: UserEntity,
    userPosts: List<PostEntity>,
    allFollows: List<FollowEntity>,
    allUsers: List<UserEntity>,
    postLikes: List<PostLikeEntity>,
    postComments: List<PostCommentEntity>,
    strings: AppStrings,
    onToggleFollow: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onAddComment: (postId: String, text: String) -> Unit,
    onDeletePost: (String) -> Unit,
    onSharePhotoClick: () -> Unit,
    onEditProfileClick: (() -> Unit)? = null,
    onMessageClick: (() -> Unit)? = null,
    onAvatarCameraClick: (() -> Unit)? = null
) {
    val isOwnProfile = user.id == currentUser.id
    val followersCount = remember(allFollows, user.id) { allFollows.count { it.followeeId == user.id } }
    val followingCount = remember(allFollows, user.id) { allFollows.count { it.followerId == user.id } }
    val isFollowing = remember(allFollows, currentUser.id, user.id) {
        allFollows.any { it.followerId == currentUser.id && it.followeeId == user.id }
    }

    var selectedPostForViewer by remember { mutableStateOf<PostEntity?>(null) }
    var commentsPostTarget by remember { mutableStateOf<PostEntity?>(null) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // IG Header: Circular Avatar on Left + Posts | Followers | Following on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Box(
                    modifier = if (isOwnProfile && onAvatarCameraClick != null) {
                        Modifier
                            .clickable(onClick = onAvatarCameraClick)
                            .testTag("profile_avatar_camera_button")
                    } else {
                        Modifier
                    }
                ) {
                    UserAvatar(
                        fullName = user.fullName,
                        avatarKey = user.avatarKey,
                        avatarUri = user.avatarUri,
                        size = 82.dp
                    )
                    if (isOwnProfile && onAvatarCameraClick != null) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = strings.takePhoto,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatItem(count = userPosts.size, label = strings.postsLabel)
                    ProfileStatItem(count = followersCount, label = strings.followersLabel)
                    ProfileStatItem(count = followingCount, label = strings.followingLabel)
                }
            }

            // Name, Verified Badge, Role, Profession, Handle, Bio
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (user.isVerified) {
                        VerifiedBadge(size = 18.dp, contentDescription = strings.verifiedBadgeLabel)
                    }
                    RoleBadge(user.role, strings.student, strings.teacher)
                }
                if (user.profession.isNotBlank()) {
                    ProfessionBadge(user.profession)
                }
                Text(
                    text = "@${user.username} • ${user.points} ${strings.pointsLabel} • ${user.testsCompleted} ${strings.testsDoneLabel}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (user.bio.isNotBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // IG Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isOwnProfile) {
                    if (onEditProfileClick != null) {
                        OutlinedButton(
                            onClick = onEditProfileClick,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("ig_edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.editProfile, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    Button(
                        onClick = onSharePhotoClick,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("ig_share_photo_button")
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.sharePhotoBtn, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                } else {
                    Button(
                        onClick = { onToggleFollow(user.id) },
                        colors = if (isFollowing) {
                            ButtonDefaults.outlinedButtonColors()
                        } else {
                            ButtonDefaults.buttonColors()
                        },
                        border = if (isFollowing) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("profile_follow_button")
                    ) {
                        Text(if (isFollowing) strings.followingBtn else strings.followBtn, maxLines = 1)
                    }
                    if (onMessageClick != null) {
                        OutlinedButton(
                            onClick = onMessageClick,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("profile_message_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.directChatsTab, maxLines = 1)
                        }
                    }
                }
            }

            HorizontalDivider()

            // 3-Column Instagram Photo Grid
            if (userPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "${strings.postsLabel}: 0",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    userPosts.chunked(3).forEach { rowPosts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            rowPosts.forEach { post ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedPostForViewer = post }
                                        .testTag("profile_grid_post_${post.id}")
                                ) {
                                    PostPhotoMedia(
                                        post = post,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            repeat(3 - rowPosts.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedPostForViewer != null) {
        val currentPost = userPosts.firstOrNull { it.id == selectedPostForViewer!!.id }
        if (currentPost == null) {
            selectedPostForViewer = null
        } else {
            val author = allUsers.firstOrNull { it.id == currentPost.authorId } ?: user
            val likes = postLikes.filter { it.postId == currentPost.id }
            val comments = postComments.filter { it.postId == currentPost.id }
            Dialog(onDismissRequest = { selectedPostForViewer = null }) {
                InstagramPostCard(
                    post = currentPost,
                    author = author,
                    currentUser = currentUser,
                    isFollowingAuthor = allFollows.any { it.followerId == currentUser.id && it.followeeId == author.id },
                    likes = likes,
                    comments = comments,
                    strings = strings,
                    onOpenAuthorProfile = {},
                    onToggleFollow = onToggleFollow,
                    onToggleLike = onToggleLike,
                    onOpenComments = { commentsPostTarget = it },
                    onDeletePost = {
                        onDeletePost(it)
                        selectedPostForViewer = null
                    }
                )
            }
        }
    }

    if (commentsPostTarget != null) {
        val targetPost = commentsPostTarget!!
        PostCommentsDialog(
            post = targetPost,
            comments = postComments.filter { it.postId == targetPost.id },
            strings = strings,
            onAddComment = { txt -> onAddComment(targetPost.id, txt) },
            onDismiss = { commentsPostTarget = null }
        )
    }
}

@Composable
private fun ProfileStatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInstagramProfileDialog(
    targetUser: UserEntity,
    currentUser: UserEntity,
    allUsers: List<UserEntity>,
    socialPosts: List<PostEntity>,
    allFollows: List<FollowEntity>,
    postLikes: List<PostLikeEntity>,
    postComments: List<PostCommentEntity>,
    strings: AppStrings,
    onToggleFollow: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onAddComment: (postId: String, text: String) -> Unit,
    onDeletePost: (String) -> Unit,
    onSharePhotoClick: () -> Unit,
    onMessageUser: (UserEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val userPosts = remember(socialPosts, targetUser.id) {
        socialPosts.filter { it.authorId == targetUser.id }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BackHandler { onDismiss() }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("@${targetUser.username}", fontWeight = FontWeight.Bold)
                            if (targetUser.isVerified) {
                                VerifiedBadge(size = 18.dp, contentDescription = strings.verifiedBadgeLabel)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
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
                    item(key = "user_ig_profile") {
                        InstagramProfileBlock(
                            user = targetUser,
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
                            onSharePhotoClick = onSharePhotoClick,
                            onMessageClick = {
                                onDismiss()
                                onMessageUser(targetUser)
                            }
                        )
                    }
                }
            }
        }
    }
}
