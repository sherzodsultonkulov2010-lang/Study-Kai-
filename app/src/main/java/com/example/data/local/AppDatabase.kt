package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.util.SecurityUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [
        UserEntity::class,
        NewsEntity::class,
        GroupEntity::class,
        MessageEntity::class,
        TestResultEntity::class,
        FollowEntity::class,
        PostEntity::class,
        PostLikeEntity::class,
        PostCommentEntity::class,
        CustomTestEntity::class,
        AppUpdateEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun newsDao(): NewsDao
    abstract fun groupDao(): GroupDao
    abstract fun messageDao(): MessageDao
    abstract fun testResultDao(): TestResultDao
    abstract fun followDao(): FollowDao
    abstract fun socialPostDao(): SocialPostDao
    abstract fun customTestDao(): CustomTestDao
    abstract fun appUpdateDao(): AppUpdateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `follows` (`followerId` TEXT NOT NULL, `followeeId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`followerId`, `followeeId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `social_posts` (`id` TEXT NOT NULL, `authorId` TEXT NOT NULL, `imageUri` TEXT, `fallbackResName` TEXT, `caption` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_social_posts_authorId_createdAt` ON `social_posts` (`authorId`, `createdAt`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `post_likes` (`postId` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`postId`, `userId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `post_comments` (`id` TEXT NOT NULL, `postId` TEXT NOT NULL, `authorId` TEXT NOT NULL, `authorName` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_post_comments_postId_createdAt` ON `post_comments` (`postId`, `createdAt`)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `users` ADD COLUMN `profession` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `users` ADD COLUMN `isVerified` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `users` SET `isVerified` = 1 WHERE LOWER(`fullName`) LIKE '%sherzod sultankulov%' OR LOWER(`username`) IN ('sherzod_admin', 'admin')")
                val adminHash = SecurityUtil.sha256("AdminProfile#Private")
                val now = System.currentTimeMillis()
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO `users` (`id`, `username`, `fullName`, `passwordHash`, `role`, `profession`, `bio`, `avatarKey`, `avatarUri`, `isVerified`, `points`, `testsCompleted`, `createdAt`)
                    VALUES ('seed_admin_sherzod', 'sherzod_admin', 'Sherzod Sultankulov', '$adminHash', 'TEACHER', 'Platform Admin', 'Study Kai Platform Yöneticisi & Eğitmen.', 'avatar_indigo', NULL, 1, 3200, 55, $now)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `custom_tests` (`id` TEXT NOT NULL, `subject` TEXT NOT NULL, `categoryKey` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `questionsJson` TEXT NOT NULL, `authorName` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `app_updates` (`id` TEXT NOT NULL, `versionName` TEXT NOT NULL, `title` TEXT NOT NULL, `changelog` TEXT NOT NULL, `isCritical` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edusphere_global.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        suspend fun seedInitialCommunityIfEmpty(db: AppDatabase) = withContext(Dispatchers.IO) {
            val userDao = db.userDao()
            val newsDao = db.newsDao()
            val groupDao = db.groupDao()
            val msgDao = db.messageDao()

            if (userDao.countUsers() == 0) {
                val now = System.currentTimeMillis()
                val seedUsers = listOf(
                    UserEntity(
                        id = "seed_admin_sherzod",
                        username = "sherzod_admin",
                        fullName = "Sherzod Sultankulov",
                        passwordHash = SecurityUtil.sha256("AdminProfile#Private"),
                        role = "TEACHER",
                        profession = "Platform Admin",
                        bio = "Study Kai kurucusu ve platform yöneticisi. Küresel eğitim topluluğuna hoş geldiniz!",
                        avatarKey = "avatar_indigo",
                        isVerified = true,
                        points = 3200,
                        testsCompleted = 55,
                        createdAt = now - 86400000L * 7
                    ),
                    UserEntity(
                        id = "seed_teacher_aziza",
                        username = "aziza_english",
                        fullName = "Aziza Karimova",
                        passwordHash = SecurityUtil.sha256("EduTeacher#2026"),
                        role = "TEACHER",
                        profession = "Öğretmen",
                        bio = "IELTS & CEFR C2 Mentor from Tashkent. Helping learners master English grammar and speaking.",
                        avatarKey = "avatar_teal",
                        isVerified = true,
                        points = 2450,
                        testsCompleted = 42,
                        createdAt = now - 86400000L * 5
                    ),
                    UserEntity(
                        id = "seed_teacher_mehmet",
                        username = "mehmet_math",
                        fullName = "Mehmet Yilmaz",
                        passwordHash = SecurityUtil.sha256("MathMaster#2026"),
                        role = "TEACHER",
                        profession = "Matematik Öğretmeni",
                        bio = "Mathematics Olympiad Coach (Grades 7–11). Calculus, Geometry, and Desmos graphing enthusiast.",
                        avatarKey = "avatar_indigo",
                        isVerified = false,
                        points = 2180,
                        testsCompleted = 38,
                        createdAt = now - 86400000L * 4
                    ),
                    UserEntity(
                        id = "seed_student_elena",
                        username = "elena_polyglot",
                        fullName = "Elena Petrova",
                        passwordHash = SecurityUtil.sha256("ElenaLearn#2026"),
                        role = "STUDENT",
                        profession = "Öğrenci",
                        bio = "Grade 10 student learning English B2 and Trigonometry. Love solving 30-question challenges!",
                        avatarKey = "avatar_amber",
                        isVerified = false,
                        points = 1640,
                        testsCompleted = 29,
                        createdAt = now - 86400000L * 3
                    ),
                    UserEntity(
                        id = "seed_student_jasur",
                        username = "jasur_dev",
                        fullName = "Jasur Alimov",
                        passwordHash = SecurityUtil.sha256("JasurStudy#2026"),
                        role = "STUDENT",
                        profession = "Yazılımcı",
                        bio = "Preparing for university entrance exams in Math & English. Open to study group chats!",
                        avatarKey = "avatar_rose",
                        isVerified = false,
                        points = 1420,
                        testsCompleted = 25,
                        createdAt = now - 86400000L * 2
                    )
                )
                seedUsers.forEach { userDao.insertUser(it) }
            }

            if (newsDao.countNews() == 0) {
                val now = System.currentTimeMillis()
                newsDao.insertNews(
                    NewsEntity(
                        id = "news_daily_welcome",
                        title = "Welcome to Study Kai Global Campus!",
                        body = "Daily Message from Admin Sherzod Sultankulov: Complete at least 1 English topic (30 Qs) and 1 Math test today to climb the global leaderboard. Check out our 7,000+ audio dictionary and Desmos graphing lab!",
                        authorName = "Sherzod Sultankulov (Admin)",
                        isPinnedDaily = true,
                        category = "Daily Message",
                        createdAt = now
                    )
                )
                newsDao.insertNews(
                    NewsEntity(
                        id = "news_english_500",
                        title = "500 English Topics (A1–C2) & Audio Dictionary Live",
                        body = "All 500 CEFR English lessons with 30-question topic assessments and over 8,700 multilingual words with voice pronunciation are now available in Uzbek, Turkish, English, and Russian.",
                        authorName = "Sherzod Sultankulov (Admin)",
                        isPinnedDaily = false,
                        category = "Curriculum",
                        createdAt = now - 3600000L * 4
                    )
                )
                newsDao.insertNews(
                    NewsEntity(
                        id = "news_math_k11",
                        title = "Grades 0–11 Mathematics & Desmos Lab",
                        body = "Explore 70 targeted tests per class from Grade 0 to Grade 11, complete formula reference sheets, and the interactive Desmos graphing calculator.",
                        authorName = "Sherzod Sultankulov (Admin)",
                        isPinnedDaily = false,
                        category = "Mathematics",
                        createdAt = now - 3600000L * 10
                    )
                )
            }

            if (groupDao.countGroups() == 0) {
                val now = System.currentTimeMillis()
                val groups = listOf(
                    GroupEntity(
                        id = "grp_english_c1",
                        name = "English A1–C2 Fluency Hub",
                        description = "Daily vocabulary, grammar doubts, and 30-question test discussions.",
                        topicTag = "English",
                        creatorId = "seed_teacher_aziza",
                        memberIdsCsv = "seed_teacher_aziza,seed_student_elena,seed_student_jasur",
                        createdAt = now - 7200000L
                    ),
                    GroupEntity(
                        id = "grp_math_olympiad",
                        name = "Grades 0–11 Math & Desmos Lab",
                        description = "Share formulas, quadratic graphs, and Grade 9–11 calculus tips.",
                        topicTag = "Mathematics",
                        creatorId = "seed_teacher_mehmet",
                        memberIdsCsv = "seed_teacher_mehmet,seed_student_elena,seed_student_jasur",
                        createdAt = now - 3600000L
                    ),
                    GroupEntity(
                        id = "grp_polyglot_4lang",
                        name = "Uzbek • Turkish • English • Russian",
                        description = "Practice 4-language translations and audio pronunciation together!",
                        topicTag = "Languages",
                        creatorId = "seed_student_elena",
                        memberIdsCsv = "seed_student_elena,seed_teacher_aziza,seed_teacher_mehmet",
                        createdAt = now - 1800000L
                    )
                )
                groups.forEach { groupDao.insertGroup(it) }

                msgDao.insertMessage(
                    MessageEntity(
                        id = "msg_seed_1",
                        channelId = "grp_grp_english_c1",
                        senderId = "seed_teacher_aziza",
                        senderName = "Aziza Karimova",
                        text = "Welcome everyone! Which A1–C2 topic are you practicing today?",
                        createdAt = now - 3000000L
                    )
                )
                msgDao.insertMessage(
                    MessageEntity(
                        id = "msg_seed_2",
                        channelId = "grp_grp_math_olympiad",
                        senderId = "seed_teacher_mehmet",
                        senderName = "Mehmet Yilmaz",
                        text = "Tip: Try graphing y = x^2 - 4 in the Desmos Calculator tab to visualize roots!",
                        createdAt = now - 2400000L
                    )
                )
            }

            val followDao = db.followDao()
            if (followDao.countFollows() == 0) {
                val now = System.currentTimeMillis()
                listOf(
                    FollowEntity("seed_student_elena", "seed_teacher_aziza", now),
                    FollowEntity("seed_student_elena", "seed_teacher_mehmet", now),
                    FollowEntity("seed_student_jasur", "seed_teacher_aziza", now),
                    FollowEntity("seed_student_jasur", "seed_student_elena", now),
                    FollowEntity("seed_teacher_aziza", "seed_teacher_mehmet", now)
                ).forEach { followDao.follow(it) }
            }

            val postDao = db.socialPostDao()
            if (postDao.countPosts() == 0) {
                val now = System.currentTimeMillis()
                postDao.insertPost(
                    PostEntity(
                        id = "post_seed_eng",
                        authorId = "seed_teacher_aziza",
                        fallbackResName = "img_english_studio",
                        caption = "Preparing C1 & C2 speaking notes today! Which English topic from the 500 topics is your favorite?",
                        createdAt = now - 7200000L
                    )
                )
                postDao.insertPost(
                    PostEntity(
                        id = "post_seed_math",
                        authorId = "seed_teacher_mehmet",
                        fallbackResName = "img_math_lab",
                        caption = "Grade 10 & 11 Trigonometry & Calculus practice session is ready! Graph sin(x) in Desmos to see the wave.",
                        createdAt = now - 3600000L
                    )
                )
                postDao.insertPost(
                    PostEntity(
                        id = "post_seed_campus",
                        authorId = "seed_student_elena",
                        fallbackResName = "img_hero_banner",
                        caption = "Just scored 29/30 on Grade 10 Math Test #4 and learned 25 new words in the audio dictionary!",
                        createdAt = now - 1800000L
                    )
                )
                postDao.insertLike(PostLikeEntity("post_seed_eng", "seed_student_elena", now))
                postDao.insertLike(PostLikeEntity("post_seed_eng", "seed_student_jasur", now))
                postDao.insertLike(PostLikeEntity("post_seed_math", "seed_teacher_aziza", now))
                postDao.insertComment(
                    PostCommentEntity(
                        id = "cmt_seed_1",
                        postId = "post_seed_eng",
                        authorId = "seed_student_elena",
                        authorName = "Elena Petrova",
                        text = "Topic #340 Academic Discourse helped me a lot today!",
                        createdAt = now - 5400000L
                    )
                )
                postDao.insertComment(
                    PostCommentEntity(
                        id = "cmt_seed_2",
                        postId = "post_seed_math",
                        authorId = "seed_student_jasur",
                        authorName = "Jasur Alimov",
                        text = "Desmos graphing makes parabolas super clear!",
                        createdAt = now - 2000000L
                    )
                )
            }
        }
    }
}
