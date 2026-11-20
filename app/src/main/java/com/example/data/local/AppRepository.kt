package com.example.data.local

import com.example.data.curriculum.QuizQuestion
import com.example.util.SecurityUtil
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AppRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val newsDao = db.newsDao()
    private val groupDao = db.groupDao()
    private val messageDao = db.messageDao()
    private val testResultDao = db.testResultDao()
    private val followDao = db.followDao()
    private val socialPostDao = db.socialPostDao()
    private val customTestDao = db.customTestDao()
    private val appUpdateDao = db.appUpdateDao()

    fun observeAllUsers(): Flow<List<UserEntity>> = userDao.observeAllUsers()
    fun observeUser(id: String): Flow<UserEntity?> = userDao.observeUserById(id)
    fun observeNews(): Flow<List<NewsEntity>> = newsDao.observeAllNews()
    fun observeGroups(): Flow<List<GroupEntity>> = groupDao.observeAllGroups()
    fun observeMessages(channelId: String): Flow<List<MessageEntity>> = messageDao.observeMessages(channelId)
    fun observeAllMessages(): Flow<List<MessageEntity>> = messageDao.observeAllMessages()
    fun observeUserResults(userId: String): Flow<List<TestResultEntity>> = testResultDao.observeUserResults(userId)
    fun observeAllFollows(): Flow<List<FollowEntity>> = followDao.observeAllFollows()
    fun observeAllPosts(): Flow<List<PostEntity>> = socialPostDao.observeAllPosts()
    fun observeAllLikes(): Flow<List<PostLikeEntity>> = socialPostDao.observeAllLikes()
    fun observeAllComments(): Flow<List<PostCommentEntity>> = socialPostDao.observeAllComments()
    fun observeCustomTests(): Flow<List<CustomTestEntity>> = customTestDao.observeAllCustomTests()
    fun observeAppUpdates(): Flow<List<AppUpdateEntity>> = appUpdateDao.observeAllUpdates()

    suspend fun seedIfNeeded() {
        AppDatabase.seedInitialCommunityIfEmpty(db)
    }

    suspend fun registerAccount(
        fullName: String,
        username: String,
        password: String,
        role: String,
        profession: String,
        bio: String,
        avatarKey: String
    ): Result<UserEntity> = runCatching {
        val cleanUser = username.trim().lowercase()
        require(fullName.trim().length >= 2) { "Name must be at least 2 characters." }
        require(cleanUser.length >= 3 && cleanUser.matches(Regex("^[a-z0-9_]+$"))) {
            "Username must be 3+ chars (a-z, 0-9, _)."
        }
        require(password.length >= 4) { "Password must be at least 4 characters." }
        val existing = userDao.getUserByUsername(cleanUser)
        require(existing == null) { "Username '@$cleanUser' is already taken." }

        val user = UserEntity(
            id = "usr_" + UUID.randomUUID().toString().take(12),
            username = cleanUser,
            fullName = fullName.trim(),
            passwordHash = SecurityUtil.sha256(password),
            role = role,
            profession = profession.trim(),
            bio = bio.trim().ifEmpty {
                if (role == "TEACHER") "Educator on Study Kai Global Campus." else "Learner exploring English & Mathematics."
            },
            avatarKey = avatarKey,
            isVerified = false,
            points = 50,
            testsCompleted = 0
        )
        userDao.insertUser(user)
        user
    }

    suspend fun login(
        username: String,
        password: String,
        adminPasswordHash: String? = null
    ): Result<UserEntity> = runCatching {
        val cleanUser = username.trim().lowercase().removePrefix("@")
        val user = userDao.getUserByUsername(cleanUser)
            ?: throw IllegalArgumentException("Account '@$cleanUser' not found.")
        if (user.id == "seed_admin_sherzod") {
            require(!adminPasswordHash.isNullOrBlank()) {
                "ADMIN_PASSWORD_NOT_CONFIGURED"
            }
            val verified = SecurityUtil.verifyAdminPassword(password, adminPasswordHash)
            require(verified) { "Incorrect password." }
        } else {
            val hash = SecurityUtil.sha256(password)
            require(user.passwordHash == hash) { "Incorrect password." }
        }
        user
    }

    suspend fun updateProfile(
        userId: String,
        fullName: String,
        profession: String,
        bio: String,
        avatarKey: String,
        avatarUri: String?
    ): Result<Unit> = runCatching {
        val current = userDao.getUserById(userId) ?: error("User not found")
        userDao.updateUser(
            current.copy(
                fullName = fullName.trim().ifEmpty { current.fullName },
                profession = profession.trim(),
                bio = bio.trim(),
                avatarKey = avatarKey,
                avatarUri = avatarUri
            )
        )
    }

    suspend fun setUserVerified(userId: String, verified: Boolean) {
        userDao.setUserVerified(userId, verified)
    }

    suspend fun recordTestCompletion(
        userId: String,
        subject: String,
        categoryKey: String,
        testIndex: Int,
        testTitle: String,
        score: Int,
        totalQuestions: Int
    ): Int {
        val pointsEarned = (score * 10) + if (score >= 24) 50 else 0
        testResultDao.insertResult(
            TestResultEntity(
                id = "res_" + UUID.randomUUID().toString().take(12),
                userId = userId,
                subject = subject,
                categoryKey = categoryKey,
                testIndex = testIndex,
                testTitle = testTitle,
                score = score,
                totalQuestions = totalQuestions,
                pointsEarned = pointsEarned
            )
        )
        userDao.addPointsAndTestCount(userId, pointsEarned)
        return pointsEarned
    }

    suspend fun deleteUserByAdmin(userId: String) {
        followDao.deleteFollowsForUser(userId)
        userDao.deleteUserById(userId)
    }

    suspend fun addNews(title: String, body: String, category: String, pinAsDaily: Boolean) {
        if (pinAsDaily) {
            newsDao.clearPinnedDaily()
        }
        newsDao.insertNews(
            NewsEntity(
                id = "news_" + UUID.randomUUID().toString().take(10),
                title = title.trim(),
                body = body.trim(),
                authorName = "Sherzod Sultankulov (Admin)",
                isPinnedDaily = pinAsDaily,
                category = category.ifBlank { "Announcement" }
            )
        )
    }

    suspend fun pinNewsAsDaily(newsId: String) {
        newsDao.clearPinnedDaily()
        newsDao.setPinnedDaily(newsId, true)
    }

    suspend fun deleteNews(newsId: String) {
        newsDao.deleteNewsById(newsId)
    }

    suspend fun createGroup(name: String, description: String, topicTag: String, creatorId: String): GroupEntity {
        val group = GroupEntity(
            id = "grp_" + UUID.randomUUID().toString().take(10),
            name = name.trim(),
            description = description.trim(),
            topicTag = topicTag.trim().ifEmpty { "General" },
            creatorId = creatorId,
            memberIdsCsv = creatorId
        )
        groupDao.insertGroup(group)
        return group
    }

    suspend fun joinOrLeaveGroup(groupId: String, userId: String) {
        val group = groupDao.getGroupById(groupId) ?: return
        val members = group.memberIdsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        if (members.contains(userId)) {
            if (group.creatorId != userId) members.remove(userId)
        } else {
            members.add(userId)
        }
        groupDao.updateGroup(group.copy(memberIdsCsv = members.joinToString(",")))
    }

    suspend fun deleteGroup(groupId: String) {
        groupDao.deleteGroupById(groupId)
        messageDao.deleteChannelMessages("grp_$groupId")
    }

    suspend fun sendMessage(channelId: String, senderId: String, senderName: String, text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        messageDao.insertMessage(
            MessageEntity(
                id = "msg_" + UUID.randomUUID().toString().take(12),
                channelId = channelId,
                senderId = senderId,
                senderName = senderName,
                text = clean
            )
        )
    }

    fun directChannelId(uidA: String, uidB: String): String {
        return if (uidA <= uidB) "dm_${uidA}_${uidB}" else "dm_${uidB}_${uidA}"
    }

    suspend fun toggleFollow(followerId: String, followeeId: String) {
        if (followerId == followeeId) return
        if (followDao.isFollowing(followerId, followeeId) > 0) {
            followDao.unfollow(followerId, followeeId)
        } else {
            followDao.follow(FollowEntity(followerId = followerId, followeeId = followeeId))
        }
    }

    suspend fun createPhotoPost(authorId: String, imageUri: String?, caption: String) {
        if (imageUri.isNullOrBlank() && caption.isBlank()) return
        socialPostDao.insertPost(
            PostEntity(
                id = "post_" + UUID.randomUUID().toString().take(12),
                authorId = authorId,
                imageUri = imageUri,
                fallbackResName = if (imageUri.isNullOrBlank()) "img_hero_banner" else null,
                caption = caption.trim()
            )
        )
    }

    suspend fun deletePost(postId: String) {
        socialPostDao.deleteLikesForPost(postId)
        socialPostDao.deleteCommentsForPost(postId)
        socialPostDao.deletePostById(postId)
    }

    suspend fun toggleLike(postId: String, userId: String) {
        if (socialPostDao.hasLiked(postId, userId) > 0) {
            socialPostDao.deleteLike(postId, userId)
        } else {
            socialPostDao.insertLike(PostLikeEntity(postId = postId, userId = userId))
        }
    }

    suspend fun addComment(postId: String, authorId: String, authorName: String, text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        socialPostDao.insertComment(
            PostCommentEntity(
                id = "cmt_" + UUID.randomUUID().toString().take(12),
                postId = postId,
                authorId = authorId,
                authorName = authorName,
                text = clean
            )
        )
    }

    suspend fun createCustomTest(
        subject: String,
        categoryKey: String,
        title: String,
        description: String,
        questions: List<QuizQuestion>
    ) {
        if (title.isBlank() || questions.isEmpty()) return
        customTestDao.insertCustomTest(
            CustomTestEntity(
                id = "ctest_" + UUID.randomUUID().toString().take(10),
                subject = subject,
                categoryKey = categoryKey.trim().ifBlank { if (subject == "MATH") "Grade 9" else "B2" },
                title = title.trim(),
                description = description.trim(),
                questionsJson = encodeQuestions(questions)
            )
        )
    }

    suspend fun deleteCustomTest(testId: String) {
        customTestDao.deleteCustomTestById(testId)
    }

    suspend fun publishAppUpdate(
        versionName: String,
        title: String,
        changelog: String,
        isCritical: Boolean
    ) {
        if (versionName.isBlank() || title.isBlank()) return
        appUpdateDao.insertUpdate(
            AppUpdateEntity(
                id = "upd_" + UUID.randomUUID().toString().take(10),
                versionName = versionName.trim(),
                title = title.trim(),
                changelog = changelog.trim().ifBlank { title.trim() },
                isCritical = isCritical
            )
        )
    }

    suspend fun deleteAppUpdate(updateId: String) {
        appUpdateDao.deleteUpdateById(updateId)
    }

    companion object {
        fun encodeQuestions(questions: List<QuizQuestion>): String {
            val arr = JSONArray()
            questions.forEach { q ->
                val obj = JSONObject()
                obj.put("id", q.id)
                obj.put("prompt", q.prompt)
                obj.put("options", JSONArray(q.options))
                obj.put("correctIndex", q.correctIndex)
                obj.put("explanation", q.explanation)
                arr.put(obj)
            }
            return arr.toString()
        }

        fun decodeQuestions(json: String): List<QuizQuestion> {
            return try {
                val arr = JSONArray(json)
                (0 until arr.length()).map { idx ->
                    val obj = arr.getJSONObject(idx)
                    val optArr = obj.getJSONArray("options")
                    val opts = (0 until optArr.length()).map { optArr.getString(it) }
                    QuizQuestion(
                        id = obj.optInt("id", idx + 1),
                        prompt = obj.getString("prompt"),
                        options = opts,
                        correctIndex = obj.optInt("correctIndex", 0).coerceIn(0, (opts.size - 1).coerceAtLeast(0)),
                        explanation = obj.optString("explanation", "")
                    )
                }
            } catch (_: Exception) {
                emptyList()
            }
        }

        fun buildNotifications(
            currentUserId: String,
            groups: List<GroupEntity>,
            allMessages: List<MessageEntity>,
            lastSeenAt: Long
        ): List<AppNotificationItem> {
            val groupsById = groups.associateBy { it.id }
            val items = mutableListOf<AppNotificationItem>()

            // Direct messages & Group messages from other users
            allMessages.forEach { msg ->
                if (msg.senderId == currentUserId) return@forEach
                when {
                    msg.channelId.startsWith("dm_") -> {
                        val expectedChannel = if (currentUserId <= msg.senderId) {
                            "dm_${currentUserId}_${msg.senderId}"
                        } else {
                            "dm_${msg.senderId}_${currentUserId}"
                        }
                        if (msg.channelId == expectedChannel) {
                            items.add(
                                AppNotificationItem(
                                    id = "notif_${msg.id}",
                                    type = NotificationType.DIRECT_MESSAGE,
                                    title = msg.senderName,
                                    body = msg.text,
                                    timestamp = msg.createdAt,
                                    isUnread = msg.createdAt > lastSeenAt,
                                    peerUserId = msg.senderId
                                )
                            )
                        }
                    }

                    msg.channelId.startsWith("grp_") -> {
                        val grpId = msg.channelId.removePrefix("grp_")
                        val group = groupsById[grpId]
                        if (group != null) {
                            val members = group.memberIdsCsv.split(",").filter { it.isNotBlank() }
                            if (members.contains(currentUserId) || group.creatorId == currentUserId) {
                                items.add(
                                    AppNotificationItem(
                                        id = "notif_${msg.id}",
                                        type = NotificationType.GROUP_MESSAGE,
                                        title = group.name,
                                        body = "${msg.senderName}: ${msg.text}",
                                        timestamp = msg.createdAt,
                                        isUnread = msg.createdAt > lastSeenAt,
                                        groupId = group.id
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // New group interactions (groups created by other members)
            groups.forEach { group ->
                if (group.creatorId != currentUserId) {
                    items.add(
                        AppNotificationItem(
                            id = "notif_grp_${group.id}",
                            type = NotificationType.NEW_GROUP,
                            title = group.name,
                            body = group.description.ifBlank { group.topicTag },
                            timestamp = group.createdAt,
                            isUnread = group.createdAt > lastSeenAt,
                            groupId = group.id
                        )
                    )
                }
            }

            return items.sortedByDescending { it.timestamp }.take(40)
        }
    }
}

enum class NotificationType {
    DIRECT_MESSAGE,
    GROUP_MESSAGE,
    NEW_GROUP
}

data class AppNotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timestamp: Long,
    val isUnread: Boolean,
    val peerUserId: String? = null,
    val groupId: String? = null
)
