package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.FollowEntity
import com.example.data.local.GroupEntity
import com.example.data.local.MessageEntity
import com.example.data.local.PostCommentEntity
import com.example.data.local.PostEntity
import com.example.data.local.PostLikeEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppStrings
import com.example.ui.components.CreatePhotoPostDialog
import com.example.ui.components.InstagramPostCard
import com.example.ui.components.PostCommentsDialog
import com.example.ui.components.ProfessionBadge
import com.example.ui.components.RoleBadge
import com.example.ui.components.UserAvatar
import com.example.ui.components.UserInstagramProfileDialog
import com.example.ui.components.VerifiedBadge
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    currentUser: UserEntity,
    allUsers: List<UserEntity>,
    groups: List<GroupEntity>,
    socialPosts: List<PostEntity>,
    allFollows: List<FollowEntity>,
    postLikes: List<PostLikeEntity>,
    postComments: List<PostCommentEntity>,
    strings: AppStrings,
    observeMessages: (String) -> Flow<List<MessageEntity>>,
    directChannelId: (String) -> String,
    onSendDirectMessage: (otherUserId: String, text: String) -> Unit,
    onSendGroupMessage: (groupId: String, text: String) -> Unit,
    onCreateGroup: (name: String, description: String, topicTag: String) -> Unit,
    onToggleJoinGroup: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onPickPostPhotoUri: (Uri, (String) -> Unit) -> Unit,
    onPublishPost: (imageUri: String?, caption: String) -> Unit,
    onToggleLike: (String) -> Unit,
    onAddComment: (postId: String, text: String) -> Unit,
    onDeletePost: (String) -> Unit,
    pendingChatUserId: String? = null,
    pendingGroupId: String? = null,
    onConsumePendingNav: () -> Unit = {}
) {
    var subTab by remember { mutableIntStateOf(0) } // 0 = Feed, 1 = Search, 2 = Chats, 3 = Groups
    var activeChatUser by remember { mutableStateOf<UserEntity?>(null) }
    var activeGroup by remember { mutableStateOf<GroupEntity?>(null) }
    var inspectedUser by remember { mutableStateOf<UserEntity?>(null) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var commentsPostTarget by remember { mutableStateOf<PostEntity?>(null) }

    LaunchedEffect(pendingChatUserId, pendingGroupId, allUsers, groups) {
        if (pendingChatUserId != null) {
            val targetUser = allUsers.firstOrNull { it.id == pendingChatUserId }
            if (targetUser != null) {
                activeGroup = null
                activeChatUser = targetUser
                subTab = 2
            }
            onConsumePendingNav()
        } else if (pendingGroupId != null) {
            val targetGroup = groups.firstOrNull { it.id == pendingGroupId }
            if (targetGroup != null) {
                activeChatUser = null
                activeGroup = targetGroup
                subTab = 3
            }
            onConsumePendingNav()
        }
    }

    if (activeChatUser != null) {
        val peer = activeChatUser!!
        BackHandler { activeChatUser = null }
        val channel = remember(peer.id, currentUser.id) { directChannelId(peer.id) }
        val messages by observeMessages(channel).collectAsState(initial = emptyList())
        ChatThreadView(
            title = peer.fullName,
            subtitle = "@${peer.username} • ${if (peer.role == "TEACHER") strings.teacher else strings.student}",
            currentUserId = currentUser.id,
            messages = messages,
            hint = strings.sendMessageHint,
            onBack = { activeChatUser = null },
            onSend = { text -> onSendDirectMessage(peer.id, text) }
        )
        return
    }

    if (activeGroup != null) {
        val group = groups.firstOrNull { it.id == activeGroup!!.id } ?: activeGroup!!
        BackHandler { activeGroup = null }
        val messages by observeMessages("grp_${group.id}").collectAsState(initial = emptyList())
        ChatThreadView(
            title = group.name,
            subtitle = "${group.topicTag} • ${group.memberIdsCsv.split(",").count { it.isNotBlank() }} members",
            currentUserId = currentUser.id,
            messages = messages,
            hint = strings.sendMessageHint,
            onBack = { activeGroup = null },
            onSend = { text -> onSendGroupMessage(group.id, text) }
        )
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
        ) {
            val tabTitles = listOf(
                strings.feedTab to "community_tab_feed",
                strings.searchUsersTab to "community_tab_search",
                strings.directChatsTab to "community_tab_chats",
                strings.groupsTab to "community_tab_groups"
            )
            PrimaryTabRow(selectedTabIndex = subTab) {
                tabTitles.forEachIndexed { idx, (label, tag) ->
                    Tab(
                        selected = subTab == idx,
                        onClick = { subTab = idx },
                        text = {
                            Text(
                                text = label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        modifier = Modifier.testTag(tag)
                    )
                }
            }

            when (subTab) {
                0 -> SocialFeedSection(
                    currentUser = currentUser,
                    allUsers = allUsers,
                    socialPosts = socialPosts,
                    allFollows = allFollows,
                    postLikes = postLikes,
                    postComments = postComments,
                    strings = strings,
                    onShowCreatePost = { showCreatePostDialog = true },
                    onInspectUser = { inspectedUser = it },
                    onToggleFollow = onToggleFollow,
                    onToggleLike = onToggleLike,
                    onOpenComments = { commentsPostTarget = it },
                    onDeletePost = onDeletePost
                )
                1 -> UserSearchSection(
                    currentUserId = currentUser.id,
                    allUsers = allUsers,
                    allFollows = allFollows,
                    strings = strings,
                    onInspect = { inspectedUser = it },
                    onToggleFollow = onToggleFollow,
                    onStartChat = { activeChatUser = it }
                )
                2 -> DirectChatsListSection(
                    currentUserId = currentUser.id,
                    allUsers = allUsers,
                    strings = strings,
                    onOpenChat = { activeChatUser = it }
                )
                else -> GroupsListSection(
                    currentUserId = currentUser.id,
                    groups = groups,
                    strings = strings,
                    onShowCreate = { showCreateGroupDialog = true },
                    onToggleJoin = onToggleJoinGroup,
                    onOpenGroup = { activeGroup = it }
                )
            }
        }
    }

    if (inspectedUser != null) {
        val target = allUsers.firstOrNull { it.id == inspectedUser!!.id } ?: inspectedUser!!
        UserInstagramProfileDialog(
            targetUser = target,
            currentUser = currentUser,
            allUsers = allUsers,
            socialPosts = socialPosts,
            allFollows = allFollows,
            postLikes = postLikes,
            postComments = postComments,
            strings = strings,
            onToggleFollow = onToggleFollow,
            onToggleLike = onToggleLike,
            onAddComment = onAddComment,
            onDeletePost = onDeletePost,
            onSharePhotoClick = { showCreatePostDialog = true },
            onMessageUser = { peer -> activeChatUser = peer },
            onDismiss = { inspectedUser = null }
        )
    }

    if (showCreatePostDialog) {
        CreatePhotoPostDialog(
            strings = strings,
            onPickPostPhotoUri = onPickPostPhotoUri,
            onPublish = onPublishPost,
            onDismiss = { showCreatePostDialog = false }
        )
    }

    if (commentsPostTarget != null) {
        val targetPost = commentsPostTarget!!
        PostCommentsDialog(
            post = targetPost,
            comments = postComments.filter { it.postId == targetPost.id },
            strings = strings,
            onAddComment = { text -> onAddComment(targetPost.id, text) },
            onDismiss = { commentsPostTarget = null }
        )
    }

    if (showCreateGroupDialog) {
        CreateGroupDialog(
            strings = strings,
            onDismiss = { showCreateGroupDialog = false },
            onCreate = { name, desc, tag ->
                onCreateGroup(name, desc, tag)
                showCreateGroupDialog = false
            }
        )
    }
}

@Composable
private fun SocialFeedSection(
    currentUser: UserEntity,
    allUsers: List<UserEntity>,
    socialPosts: List<PostEntity>,
    allFollows: List<FollowEntity>,
    postLikes: List<PostLikeEntity>,
    postComments: List<PostCommentEntity>,
    strings: AppStrings,
    onShowCreatePost: () -> Unit,
    onInspectUser: (UserEntity) -> Unit,
    onToggleFollow: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onOpenComments: (PostEntity) -> Unit,
    onDeletePost: (String) -> Unit
) {
    val usersMap = remember(allUsers) { allUsers.associateBy { it.id } }
    val followedIds = remember(allFollows, currentUser.id) {
        allFollows.filter { it.followerId == currentUser.id }.map { it.followeeId }.toSet()
    }
    val storiesUsers = remember(allUsers, currentUser.id) {
        listOf(currentUser) + allUsers.filter { it.id != currentUser.id }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Stories / Campus Members Row
        item(key = "ig_stories_row") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(storiesUsers, key = { it.id }) { member ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(68.dp)
                            .clickable { onInspectUser(member) }
                            .testTag("story_user_${member.username}")
                    ) {
                        UserAvatar(
                            fullName = member.fullName,
                            avatarKey = member.avatarKey,
                            avatarUri = member.avatarUri,
                            size = 58.dp
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = member.fullName.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (member.isVerified) {
                                VerifiedBadge(size = 12.dp, contentDescription = strings.verifiedBadgeLabel)
                            }
                        }
                    }
                }
            }
        }

        // Share Photo Button
        item(key = "share_photo_cta") {
            Button(
                onClick = onShowCreatePost,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("share_photo_button")
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(strings.sharePhotoBtn)
            }
        }

        // Posts Feed
        items(socialPosts, key = { it.id }) { post ->
            val author = usersMap[post.authorId]
            val likes = postLikes.filter { it.postId == post.id }
            val comments = postComments.filter { it.postId == post.id }
            InstagramPostCard(
                post = post,
                author = author,
                currentUser = currentUser,
                isFollowingAuthor = followedIds.contains(post.authorId),
                likes = likes,
                comments = comments,
                strings = strings,
                onOpenAuthorProfile = onInspectUser,
                onToggleFollow = onToggleFollow,
                onToggleLike = onToggleLike,
                onOpenComments = onOpenComments,
                onDeletePost = onDeletePost
            )
        }
    }
}

@Composable
private fun UserSearchSection(
    currentUserId: String,
    allUsers: List<UserEntity>,
    allFollows: List<FollowEntity>,
    strings: AppStrings,
    onInspect: (UserEntity) -> Unit,
    onToggleFollow: (String) -> Unit,
    onStartChat: (UserEntity) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val followedIds = remember(allFollows, currentUserId) {
        allFollows.filter { it.followerId == currentUserId }.map { it.followeeId }.toSet()
    }
    val filtered by remember(allUsers, query) {
        derivedStateOf {
            val q = query.trim().lowercase()
            if (q.isEmpty()) allUsers
            else allUsers.filter {
                it.fullName.lowercase().contains(q) ||
                    it.username.lowercase().contains(q) ||
                    it.profession.lowercase().contains(q) ||
                    it.bio.lowercase().contains(q) ||
                    it.role.lowercase().contains(q)
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "user_search_bar") {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(strings.searchPeopleHint) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("community_user_search_input")
            )
        }

        items(filtered, key = { it.id }) { user ->
            val isFollowing = followedIds.contains(user.id)
            Card(
                onClick = { onInspect(user) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UserAvatar(user.fullName, user.avatarKey, user.avatarUri, size = 48.dp)
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(user.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (user.isVerified) {
                                VerifiedBadge(size = 15.dp, contentDescription = strings.verifiedBadgeLabel)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            RoleBadge(user.role, strings.student, strings.teacher)
                            if (user.profession.isNotBlank()) {
                                ProfessionBadge(user.profession)
                            }
                        }
                        Text("@${user.username} • ${user.points} pts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        if (user.bio.isNotBlank()) {
                            Text(user.bio, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    if (user.id != currentUserId) {
                        if (isFollowing) {
                            OutlinedButton(
                                onClick = { onToggleFollow(user.id) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("search_follow_${user.username}")
                            ) {
                                Text(strings.followingBtn, style = MaterialTheme.typography.labelSmall)
                            }
                        } else {
                            Button(
                                onClick = { onToggleFollow(user.id) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("search_follow_${user.username}")
                            ) {
                                Text(strings.followBtn, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        IconButton(
                            onClick = { onStartChat(user) },
                            modifier = Modifier.testTag("chat_user_${user.username}")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat with ${user.fullName}", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectChatsListSection(
    currentUserId: String,
    allUsers: List<UserEntity>,
    strings: AppStrings,
    onOpenChat: (UserEntity) -> Unit
) {
    val peers = allUsers.filter { it.id != currentUserId }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(peers, key = { it.id }) { user ->
            Card(
                onClick = { onOpenChat(user) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserAvatar(user.fullName, user.avatarKey, user.avatarUri, size = 46.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(user.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            if (user.isVerified) {
                                VerifiedBadge(size = 15.dp, contentDescription = strings.verifiedBadgeLabel)
                            }
                        }
                        val meta = buildString {
                            append("@${user.username}")
                            if (user.profession.isNotBlank()) append(" • ${user.profession}")
                            append(" • ${if (user.role == "TEACHER") strings.teacher else strings.student}")
                        }
                        Text(meta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Open chat", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun GroupsListSection(
    currentUserId: String,
    groups: List<GroupEntity>,
    strings: AppStrings,
    onShowCreate: () -> Unit,
    onToggleJoin: (String) -> Unit,
    onOpenGroup: (GroupEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "create_grp_btn") {
            Button(
                onClick = onShowCreate,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp)
                    .testTag("create_group_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(strings.createGroupBtn)
            }
        }

        items(groups, key = { it.id }) { grp ->
            val members = grp.memberIdsCsv.split(",").filter { it.isNotBlank() }
            val isMember = members.contains(currentUserId)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(grp.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${grp.topicTag} • ${members.size} members", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (grp.description.isNotBlank()) {
                        Text(grp.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onToggleJoin(grp.id) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                        ) {
                            Text(if (isMember) strings.leaveGroup else strings.joinGroup)
                        }
                        Button(
                            onClick = { onOpenGroup(grp) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("open_group_chat_${grp.id}")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.directChatsTab)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatThreadView(
    title: String,
    subtitle: String,
    currentUserId: String,
    messages: List<MessageEntity>,
    hint: String,
    onBack: () -> Unit,
    onSend: (String) -> Unit
) {
    var draft by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 760.dp)
    ) {
        Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMine = msg.senderId == currentUserId
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                ) {
                    if (!isMine) {
                        Text(msg.senderName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                timeFmt.format(Date(msg.createdAt)),
                                style = MaterialTheme.typography.labelSmall,
                                color = (if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text(hint) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input")
                )
                FilledIconButton(
                    onClick = {
                        if (draft.isNotBlank()) {
                            onSend(draft)
                            draft = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("chat_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
private fun CreateGroupDialog(
    strings: AppStrings,
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("English") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.createGroupBtn) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Group Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Topic Tag (English / Math / General)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.trim().length >= 2) onCreate(name, desc, tag) }
            ) {
                Text(strings.createGroupBtn)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.backLabel) }
        }
    )
}
