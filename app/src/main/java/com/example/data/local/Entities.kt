package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val passwordHash: String,
    val role: String, // "STUDENT" or "TEACHER"
    val profession: String = "",
    val bio: String = "",
    val avatarKey: String = "avatar_teal",
    val avatarUri: String? = null,
    val isVerified: Boolean = false,
    val points: Int = 0,
    val testsCompleted: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "news_posts")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val authorName: String,
    val isPinnedDaily: Boolean = false,
    val category: String = "Announcement",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val topicTag: String,
    val creatorId: String,
    val memberIdsCsv: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "messages",
    indices = [Index(value = ["channelId", "createdAt"])]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val channelId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "test_results",
    indices = [Index(value = ["userId", "completedAt"])]
)
data class TestResultEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val subject: String, // "ENGLISH" or "MATH"
    val categoryKey: String, // e.g., "A1".."C2" or "GRADE_0".."GRADE_11"
    val testIndex: Int,
    val testTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val pointsEarned: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "follows",
    primaryKeys = ["followerId", "followeeId"]
)
data class FollowEntity(
    val followerId: String,
    val followeeId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "social_posts",
    indices = [Index(value = ["authorId", "createdAt"])]
)
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val imageUri: String? = null,
    val fallbackResName: String? = null,
    val caption: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "post_likes",
    primaryKeys = ["postId", "userId"]
)
data class PostLikeEntity(
    val postId: String,
    val userId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "post_comments",
    indices = [Index(value = ["postId", "createdAt"])]
)
data class PostCommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_tests")
data class CustomTestEntity(
    @PrimaryKey val id: String,
    val subject: String, // "ENGLISH" or "MATH"
    val categoryKey: String,
    val title: String,
    val description: String,
    val questionsJson: String,
    val authorName: String = "Sherzod Sultankulov",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_updates")
data class AppUpdateEntity(
    @PrimaryKey val id: String,
    val versionName: String,
    val title: String,
    val changelog: String,
    val isCritical: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

