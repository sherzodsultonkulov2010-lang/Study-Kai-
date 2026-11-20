package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY points DESC, createdAt ASC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET points = points + :delta, testsCompleted = testsCompleted + 1 WHERE id = :userId")
    suspend fun addPointsAndTestCount(userId: String, delta: Int)

    @Query("UPDATE users SET isVerified = :verified WHERE id = :userId")
    suspend fun setUserVerified(userId: String, verified: Boolean)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int
}

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_posts ORDER BY isPinnedDaily DESC, createdAt DESC")
    fun observeAllNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity)

    @Query("UPDATE news_posts SET isPinnedDaily = 0 WHERE isPinnedDaily = 1")
    suspend fun clearPinnedDaily()

    @Query("UPDATE news_posts SET isPinnedDaily = :pinned WHERE id = :id")
    suspend fun setPinnedDaily(id: String, pinned: Boolean)

    @Query("DELETE FROM news_posts WHERE id = :id")
    suspend fun deleteNewsById(id: String)

    @Query("SELECT COUNT(*) FROM news_posts")
    suspend fun countNews(): Int
}

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun observeAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE id = :id LIMIT 1")
    suspend fun getGroupById(id: String): GroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Query("DELETE FROM groups WHERE id = :id")
    suspend fun deleteGroupById(id: String)

    @Query("SELECT COUNT(*) FROM groups")
    suspend fun countGroups(): Int
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE channelId = :channelId ORDER BY createdAt ASC")
    fun observeMessages(channelId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    fun observeAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE channelId = :channelId")
    suspend fun deleteChannelMessages(channelId: String)
}

@Dao
interface TestResultDao {
    @Query("SELECT * FROM test_results WHERE userId = :userId ORDER BY completedAt DESC")
    fun observeUserResults(userId: String): Flow<List<TestResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TestResultEntity)
}

@Dao
interface FollowDao {
    @Query("SELECT * FROM follows")
    fun observeAllFollows(): Flow<List<FollowEntity>>

    @Query("SELECT COUNT(*) FROM follows WHERE followerId = :followerId AND followeeId = :followeeId")
    suspend fun isFollowing(followerId: String, followeeId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun follow(entry: FollowEntity)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followeeId = :followeeId")
    suspend fun unfollow(followerId: String, followeeId: String)

    @Query("DELETE FROM follows WHERE followerId = :userId OR followeeId = :userId")
    suspend fun deleteFollowsForUser(userId: String)

    @Query("SELECT COUNT(*) FROM follows")
    suspend fun countFollows(): Int
}

@Dao
interface SocialPostDao {
    @Query("SELECT * FROM social_posts ORDER BY createdAt DESC")
    fun observeAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Query("DELETE FROM social_posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    @Query("SELECT COUNT(*) FROM social_posts")
    suspend fun countPosts(): Int

    @Query("SELECT * FROM post_likes")
    fun observeAllLikes(): Flow<List<PostLikeEntity>>

    @Query("SELECT COUNT(*) FROM post_likes WHERE postId = :postId AND userId = :userId")
    suspend fun hasLiked(postId: String, userId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLike(like: PostLikeEntity)

    @Query("DELETE FROM post_likes WHERE postId = :postId AND userId = :userId")
    suspend fun deleteLike(postId: String, userId: String)

    @Query("DELETE FROM post_likes WHERE postId = :postId")
    suspend fun deleteLikesForPost(postId: String)

    @Query("SELECT * FROM post_comments ORDER BY createdAt ASC")
    fun observeAllComments(): Flow<List<PostCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: PostCommentEntity)

    @Query("DELETE FROM post_comments WHERE postId = :postId")
    suspend fun deleteCommentsForPost(postId: String)
}

@Dao
interface CustomTestDao {
    @Query("SELECT * FROM custom_tests ORDER BY createdAt DESC")
    fun observeAllCustomTests(): Flow<List<CustomTestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomTest(test: CustomTestEntity)

    @Query("DELETE FROM custom_tests WHERE id = :testId")
    suspend fun deleteCustomTestById(testId: String)
}

@Dao
interface AppUpdateDao {
    @Query("SELECT * FROM app_updates ORDER BY createdAt DESC")
    fun observeAllUpdates(): Flow<List<AppUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: AppUpdateEntity)

    @Query("DELETE FROM app_updates WHERE id = :updateId")
    suspend fun deleteUpdateById(updateId: String)
}

