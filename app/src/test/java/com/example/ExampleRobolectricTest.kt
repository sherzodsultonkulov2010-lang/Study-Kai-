package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.AppRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string and dictionary from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Study Kai", appName)

    val count = context.assets.open("dictionary_7000.tsv").bufferedReader().useLines { lines ->
      lines.drop(1).count { it.isNotBlank() }
    }
    assertTrue("Expected 7000+ words, got $count", count >= 7000)
  }

  @Test
  fun `social follow photo post like and comment repository flow works`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    try {
      val repo = AppRepository(db)
      repo.seedIfNeeded()

      val users = repo.observeAllUsers().first()
      assertTrue("Expected seeded users", users.size >= 2)
      val userA = users[0]
      val userB = users[1]

      // Toggle follow twice to verify persistence & toggle
      val initialFollowed = repo.observeAllFollows().first()
        .any { it.followerId == userA.id && it.followeeId == userB.id }
      repo.toggleFollow(userA.id, userB.id)
      val afterToggle = repo.observeAllFollows().first()
        .any { it.followerId == userA.id && it.followeeId == userB.id }
      assertEquals(!initialFollowed, afterToggle)

      // Publish photo post
      val beforePosts = repo.observeAllPosts().first().size
      repo.createPhotoPost(userA.id, "file:///tmp/sample_photo.jpg", "Calculus notes!")
      val posts = repo.observeAllPosts().first()
      assertEquals(beforePosts + 1, posts.size)
      val newPost = posts.first { it.caption == "Calculus notes!" }

      // Like & Comment on the new post
      repo.toggleLike(newPost.id, userB.id)
      assertTrue(repo.observeAllLikes().first().any { it.postId == newPost.id && it.userId == userB.id })

      repo.addComment(newPost.id, userB.id, userB.fullName, "Great shot!")
      val comments = repo.observeAllComments().first().filter { it.postId == newPost.id }
      assertEquals(1, comments.size)
      assertEquals("Great shot!", comments.first().text)

      // Delete post cascades likes & comments
      repo.deletePost(newPost.id)
      assertEquals(beforePosts, repo.observeAllPosts().first().size)
      assertTrue(repo.observeAllLikes().first().none { it.postId == newPost.id })
      assertTrue(repo.observeAllComments().first().none { it.postId == newPost.id })

      // Verify Admin profile has blue tick by default
      val adminUser = users.firstOrNull { it.id == "seed_admin_sherzod" }
      assertTrue("Expected seeded admin Sherzod Sultankulov", adminUser != null)
      assertTrue("Expected admin profile to have blue tick", adminUser!!.isVerified)

      // Register user with profession & toggle blue tick
      val registered = repo.registerAccount(
        fullName = "Ayse Demir",
        username = "ayse_avukat",
        password = "Password123",
        role = "TEACHER",
        profession = "Avukat",
        bio = "Hukuk ve dil tutkunu",
        avatarKey = "avatar_teal"
      ).getOrThrow()
      assertEquals("Avukat", registered.profession)
      assertEquals(false, registered.isVerified)

      repo.setUserVerified(registered.id, true)
      val verifiedUser = repo.observeUser(registered.id).first()!!
      assertTrue(verifiedUser.isVerified)

      repo.updateProfile(registered.id, "Ayse Demir", "Öğretmen", "Yeni bio", "avatar_teal", null)
      val updatedUser = repo.observeUser(registered.id).first()!!
      assertEquals("Öğretmen", updatedUser.profession)
    } finally {
      db.close()
    }
  }

  @Test
  fun `admin custom test creation and app update publishing work`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    try {
      val repo = AppRepository(db)
      repo.seedIfNeeded()

      val sampleQuestions = listOf(
        com.example.data.curriculum.QuizQuestion(
          id = 1,
          prompt = "What is 12 + 8?",
          options = listOf("18", "20", "22", "24"),
          correctIndex = 1,
          explanation = "12 + 8 = 20"
        ),
        com.example.data.curriculum.QuizQuestion(
          id = 2,
          prompt = "Choose the synonym of 'rapid'",
          options = listOf("slow", "fast", "quiet", "cold"),
          correctIndex = 1,
          explanation = "'Rapid' means fast."
        )
      )

      repo.createCustomTest(
        subject = "MATH",
        categoryKey = "Grade 8",
        title = "Admin Arithmetic Challenge",
        description = "Custom quiz by Sherzod",
        questions = sampleQuestions
      )

      val tests = repo.observeCustomTests().first()
      assertEquals(1, tests.size)
      val created = tests.first()
      assertEquals("Admin Arithmetic Challenge", created.title)
      assertEquals("MATH", created.subject)

      val decoded = AppRepository.decodeQuestions(created.questionsJson)
      assertEquals(2, decoded.size)
      assertEquals("What is 12 + 8?", decoded[0].prompt)
      assertEquals(1, decoded[0].correctIndex)

      repo.deleteCustomTest(created.id)
      assertTrue(repo.observeCustomTests().first().isEmpty())

      repo.publishAppUpdate(
        versionName = "2.5.0",
        title = "Social & Quiz Boost",
        changelog = "Admin test generator & top settings bar",
        isCritical = true
      )
      val updates = repo.observeAppUpdates().first()
      assertEquals(1, updates.size)
      assertEquals("2.5.0", updates.first().versionName)
      assertTrue(updates.first().isCritical)

      repo.deleteAppUpdate(updates.first().id)
      assertTrue(repo.observeAppUpdates().first().isEmpty())
    } finally {
      db.close()
    }
  }

  @Test
  fun `login requires valid password for both standard users and admin profile`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    try {
      val repo = AppRepository(db)
      repo.seedIfNeeded()

      // Register regular user and verify re-login requires exact password
      repo.registerAccount(
        fullName = "Kerem Aydin",
        username = "kerem_std",
        password = "MyPass#99",
        role = "STUDENT",
        profession = "Öğrenci",
        bio = "Matematik öğrencisi",
        avatarKey = "avatar_indigo"
      ).getOrThrow()

      val wrongUserLogin = repo.login("kerem_std", "WrongPass")
      assertTrue(wrongUserLogin.isFailure)

      val okUserLogin = repo.login("@kerem_std", "MyPass#99")
      assertTrue(okUserLogin.isSuccess)
      assertEquals("kerem_std", okUserLogin.getOrThrow().username)

      // Admin profile cannot be entered without configured PBKDF2 admin password
      val unconfiguredAdmin = repo.login("sherzod_admin", "AdminProfile#Private", adminPasswordHash = null)
      assertTrue(unconfiguredAdmin.isFailure)

      val adminHash = com.example.util.SecurityUtil.hashAdminPassword("SherzodSecret#2026")
      val wrongAdminLogin = repo.login("sherzod_admin", "WrongSecret", adminPasswordHash = adminHash)
      assertTrue(wrongAdminLogin.isFailure)

      val validAdminLogin = repo.login("sherzod_admin", "SherzodSecret#2026", adminPasswordHash = adminHash)
      assertTrue(validAdminLogin.isSuccess)
      assertEquals("seed_admin_sherzod", validAdminLogin.getOrThrow().id)
      assertTrue(validAdminLogin.getOrThrow().isVerified)
    } finally {
      db.close()
    }
  }

  @Test
  fun `image crop helper crops square region rotates and saves avatar file`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val landscape = android.graphics.Bitmap.createBitmap(100, 50, android.graphics.Bitmap.Config.ARGB_8888)
    for (x in 0 until 100) {
      val color = if (x < 50) android.graphics.Color.RED else android.graphics.Color.BLUE
      for (y in 0 until 50) {
        landscape.setPixel(x, y, color)
      }
    }

    // Pan all the way right (+0.5f) so crop shifts to left edge (RED)
    val leftCrop = com.example.util.ImageCropHelper.cropSquareBitmap(
      source = landscape,
      zoom = 1f,
      panNormX = 0.5f,
      panNormY = 0f,
      outputSizePx = 50
    )
    assertEquals(50, leftCrop.width)
    assertEquals(50, leftCrop.height)
    assertEquals(android.graphics.Color.RED, leftCrop.getPixel(25, 25))

    // Pan all the way left (-0.5f) so crop shifts to right edge (BLUE) and verify scaling to 128x128
    val rightScaled = com.example.util.ImageCropHelper.cropSquareBitmap(
      source = landscape,
      zoom = 1f,
      panNormX = -0.5f,
      panNormY = 0f,
      outputSizePx = 128
    )
    assertEquals(128, rightScaled.width)
    assertEquals(128, rightScaled.height)

    // Rotate 90 degrees turns 100x50 into 50x100
    val rotated = com.example.util.ImageCropHelper.rotateBitmap(landscape, 90)
    assertEquals(50, rotated.width)
    assertEquals(100, rotated.height)

    // Save cropped avatar to app filesDir/avatars
    val savedUri = com.example.util.ImageCropHelper.saveCroppedAvatar(context, rightScaled)
    assertTrue(!savedUri.isNullOrBlank())
    val file = java.io.File(android.net.Uri.parse(savedUri).path!!)
    assertTrue(file.exists() && file.length() > 0L)
    file.delete()
  }

  @Test
  fun `notifications track unread direct messages group messages and new groups`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    try {
      val repo = AppRepository(db)
      repo.seedIfNeeded()

      val aziza = "seed_teacher_aziza"
      val elena = "seed_student_elena"
      val mehmet = "seed_teacher_mehmet"

      // Send a DM from Aziza to Elena and create a new group by Mehmet
      repo.sendMessage(repo.directChannelId(aziza, elena), aziza, "Aziza Karimova", "Hi Elena, ready for C1 test?")
      val newGroup = repo.createGroup("IELTS Speaking Club", "Weekly speaking practice", "Speaking", mehmet)
      repo.joinOrLeaveGroup(newGroup.id, elena)
      repo.sendMessage("grp_${newGroup.id}", mehmet, "Mehmet Yilmaz", "Welcome to the speaking club!")

      val groups = repo.observeGroups().first()
      val messages = repo.observeAllMessages().first()

      // Before Elena checks notifications (lastSeenAt = 0), she has unread DM + group msg + new group alerts
      val unreadNotifs = AppRepository.buildNotifications(
        currentUserId = elena,
        groups = groups,
        allMessages = messages,
        lastSeenAt = 0L
      )
      assertTrue(unreadNotifs.any { it.type == com.example.data.local.NotificationType.DIRECT_MESSAGE && it.peerUserId == aziza && it.isUnread })
      assertTrue(unreadNotifs.any { it.type == com.example.data.local.NotificationType.GROUP_MESSAGE && it.groupId == newGroup.id && it.isUnread })
      assertTrue(unreadNotifs.any { it.type == com.example.data.local.NotificationType.NEW_GROUP && it.groupId == newGroup.id && it.isUnread })

      // After marking read at latest timestamp, unread count drops to 0
      val latestTs = unreadNotifs.maxOf { it.timestamp }
      val afterRead = AppRepository.buildNotifications(
        currentUserId = elena,
        groups = groups,
        allMessages = messages,
        lastSeenAt = latestTs
      )
      assertEquals(0, afterRead.count { it.isUnread })
    } finally {
      db.close()
    }
  }
}
