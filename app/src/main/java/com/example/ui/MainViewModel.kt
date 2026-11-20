package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.curriculum.ActiveTestSession
import com.example.data.curriculum.DictionaryEntry
import com.example.data.curriculum.DictionaryRepository
import com.example.data.curriculum.EnglishCurriculum
import com.example.data.curriculum.EnglishTopic
import com.example.data.curriculum.MathCurriculum
import com.example.data.curriculum.MathTestMeta
import com.example.data.curriculum.QuizQuestion
import com.example.data.local.AppDatabase
import com.example.data.local.AppNotificationItem
import com.example.data.local.AppRepository
import com.example.data.local.AppUpdateEntity
import com.example.data.local.CustomTestEntity
import com.example.data.local.FollowEntity
import com.example.data.local.GroupEntity
import com.example.data.local.MessageEntity
import com.example.data.local.NewsEntity
import com.example.data.local.PostCommentEntity
import com.example.data.local.PostEntity
import com.example.data.local.PostLikeEntity
import com.example.data.local.SessionPreferences
import com.example.data.local.TestResultEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.util.SecurityUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repository = AppRepository(db)
    private val prefs = SessionPreferences(application)
    private val dictionaryRepo = DictionaryRepository(application)

    val appLanguage: StateFlow<AppLanguage> = prefs.appLanguage
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.ENGLISH)

    val darkMode: StateFlow<Boolean?> = prefs.darkMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val adminPasswordHash: StateFlow<String?> = prefs.adminPasswordHash
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val loggedInUserId: StateFlow<String?> = prefs.loggedInUserId
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val currentUser: StateFlow<UserEntity?> = loggedInUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.observeUser(id)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val allUsers: StateFlow<List<UserEntity>> = repository.observeAllUsers()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val newsPosts: StateFlow<List<NewsEntity>> = repository.observeNews()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val groups: StateFlow<List<GroupEntity>> = repository.observeGroups()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allFollows: StateFlow<List<FollowEntity>> = repository.observeAllFollows()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val socialPosts: StateFlow<List<PostEntity>> = repository.observeAllPosts()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val postLikes: StateFlow<List<PostLikeEntity>> = repository.observeAllLikes()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val postComments: StateFlow<List<PostCommentEntity>> = repository.observeAllComments()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val customTests: StateFlow<List<CustomTestEntity>> = repository.observeCustomTests()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val appUpdates: StateFlow<List<AppUpdateEntity>> = repository.observeAppUpdates()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val acknowledgedUpdateId: StateFlow<String?> = prefs.acknowledgedUpdateId
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val installedVersionName: StateFlow<String> = prefs.installedVersionName
        .stateIn(viewModelScope, SharingStarted.Eagerly, "1.0.0")

    val userResults: StateFlow<List<TestResultEntity>> = loggedInUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.observeUserResults(id)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val lastSeenNotificationAt: StateFlow<Long> = loggedInUserId.flatMapLatest { id ->
        if (id == null) flowOf(0L) else prefs.observeNotificationsLastSeen(id)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    val notifications: StateFlow<List<AppNotificationItem>> = combine(
        loggedInUserId,
        groups,
        repository.observeAllMessages(),
        lastSeenNotificationAt
    ) { uid, grpList, msgList, seenAt ->
        if (uid == null) emptyList()
        else AppRepository.buildNotifications(uid, grpList, msgList, seenAt)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val unreadNotificationCount: StateFlow<Int> = notifications
        .map { list -> list.count { it.isUnread } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _dictionaryEntries = MutableStateFlow<List<DictionaryEntry>>(emptyList())
    val dictionaryEntries: StateFlow<List<DictionaryEntry>> = _dictionaryEntries.asStateFlow()

    private val _dictionaryLoaded = MutableStateFlow(false)
    val dictionaryLoaded: StateFlow<Boolean> = _dictionaryLoaded.asStateFlow()

    private val _adminConfigLoaded = MutableStateFlow(false)
    val adminConfigLoaded: StateFlow<Boolean> = _adminConfigLoaded.asStateFlow()

    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _showAdminModal = MutableStateFlow(false)
    val showAdminModal: StateFlow<Boolean> = _showAdminModal.asStateFlow()

    private var failedAdminAttempts = 0
    private var adminLockoutUntilMs = 0L

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage.asStateFlow()

    private val _adminMessage = MutableStateFlow<String?>(null)
    val adminMessage: StateFlow<String?> = _adminMessage.asStateFlow()

    private val _activeTest = MutableStateFlow<ActiveTestSession?>(null)
    val activeTest: StateFlow<ActiveTestSession?> = _activeTest.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.adminPasswordHash.first()
            _adminConfigLoaded.value = true
        }
        viewModelScope.launch {
            repository.seedIfNeeded()
        }
        viewModelScope.launch {
            val loaded = dictionaryRepo.loadAll()
            _dictionaryEntries.value = loaded
            _dictionaryLoaded.value = true
        }
    }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch { prefs.setLanguage(lang) }
    }

    fun setDarkMode(enabled: Boolean?) {
        viewModelScope.launch { prefs.setDarkMode(enabled) }
    }

    fun clearAuthMessage() {
        _authMessage.value = null
    }

    fun clearAdminMessage() {
        _adminMessage.value = null
    }

    fun registerAccount(
        fullName: String,
        username: String,
        password: String,
        role: String,
        profession: String,
        bio: String,
        avatarKey: String
    ) {
        viewModelScope.launch {
            val res = repository.registerAccount(fullName, username, password, role, profession, bio, avatarKey)
            res.onSuccess { user ->
                prefs.setLoggedInUserId(user.id)
                _authMessage.value = null
            }.onFailure { err ->
                _authMessage.value = err.message ?: "Registration failed."
            }
        }
    }

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            val adminHash = prefs.adminPasswordHash.first()
            val res = withContext(Dispatchers.Default) {
                repository.login(username, password, adminHash)
            }
            res.onSuccess { user ->
                _isAdminUnlocked.value = (user.id == "seed_admin_sherzod")
                prefs.setLoggedInUserId(user.id)
                _authMessage.value = null
            }.onFailure { err ->
                _isAdminUnlocked.value = false
                if (err.message == "ADMIN_PASSWORD_NOT_CONFIGURED") {
                    _authMessage.value = when (appLanguage.value) {
                        AppLanguage.UZBEK -> "Avval pastdagi Admin portalidan parolingizni yarating (foydalanuvchi nomi: sherzod_admin)."
                        AppLanguage.TURKISH -> "Önce Admin Portalından yönetici şifrenizi oluşturun (kullanıcı adı: sherzod_admin)."
                        AppLanguage.RUSSIAN -> "Сначала создайте пароль администратора в Админ-портале (логин: sherzod_admin)."
                        AppLanguage.ENGLISH -> "Create your admin password in Admin Portal first (username: sherzod_admin)."
                    }
                } else {
                    _authMessage.value = err.message ?: "Sign in failed."
                }
            }
        }
    }

    fun signOut() {
        _isAdminUnlocked.value = false
        _showAdminModal.value = false
        viewModelScope.launch {
            prefs.setLoggedInUserId(null)
        }
    }

    fun updateProfile(fullName: String, profession: String, bio: String, avatarKey: String, avatarUri: String?) {
        val uid = loggedInUserId.value ?: return
        viewModelScope.launch {
            repository.updateProfile(uid, fullName, profession, bio, avatarKey, avatarUri)
        }
    }

    fun savePickedProfileImage(sourceUri: Uri, onSaved: (String) -> Unit) {
        val ctx = getApplication<Application>()
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                try {
                    val dir = File(ctx.filesDir, "avatars").apply { mkdirs() }
                    val dest = File(dir, "avatar_${UUID.randomUUID().toString().take(8)}.jpg")
                    ctx.contentResolver.openInputStream(sourceUri)?.use { input ->
                        dest.outputStream().use { output -> input.copyTo(output) }
                    }
                    Uri.fromFile(dest).toString()
                } catch (_: Exception) {
                    null
                }
            }
            if (path != null) {
                onSaved(path)
            }
        }
    }

    fun savePickedPostImage(sourceUri: Uri, onSaved: (String) -> Unit) {
        val ctx = getApplication<Application>()
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                try {
                    val dir = File(ctx.filesDir, "posts").apply { mkdirs() }
                    val dest = File(dir, "post_${UUID.randomUUID().toString().take(8)}.jpg")
                    ctx.contentResolver.openInputStream(sourceUri)?.use { input ->
                        dest.outputStream().use { output -> input.copyTo(output) }
                    }
                    Uri.fromFile(dest).toString()
                } catch (_: Exception) {
                    null
                }
            }
            if (path != null) {
                onSaved(path)
            }
        }
    }

    fun toggleFollow(targetUserId: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleFollow(me.id, targetUserId)
        }
    }

    fun publishPhotoPost(imageUri: String?, caption: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.createPhotoPost(me.id, imageUri, caption)
        }
    }

    fun deletePhotoPost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }

    fun toggleLikePost(postId: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleLike(postId, me.id)
        }
    }

    fun addPostComment(postId: String, text: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.addComment(postId, me.id, me.fullName, text)
        }
    }

    fun startEnglishTest(topic: EnglishTopic) {
        val lang = appLanguage.value
        val questions = EnglishCurriculum.generateTestQuestions(topic, lang)
        val topicLabel = when (lang) {
            AppLanguage.UZBEK -> "Mavzu #${topic.id}: ${topic.title}"
            AppLanguage.TURKISH -> "Konu #${topic.id}: ${topic.title}"
            AppLanguage.RUSSIAN -> "Тема #${topic.id}: ${topic.title}"
            AppLanguage.ENGLISH -> "Topic #${topic.id}: ${topic.title}"
        }
        _activeTest.value = ActiveTestSession(
            subject = "ENGLISH",
            categoryKey = topic.cefr,
            testIndex = topic.id,
            title = topicLabel,
            subtitle = "${topic.cefr} • ${topic.grammarFocus}",
            questions = questions
        )
    }

    fun startMathTest(meta: MathTestMeta) {
        val lang = appLanguage.value
        val questions = MathCurriculum.generateTestQuestions(meta, lang)
        val gradeTestTitle = when (lang) {
            AppLanguage.UZBEK -> "${meta.grade}-sinf • ${meta.testNumber}-test"
            AppLanguage.TURKISH -> "${meta.grade}. Sınıf • Test #${meta.testNumber}"
            AppLanguage.RUSSIAN -> "${meta.grade} класс • Тест #${meta.testNumber}"
            AppLanguage.ENGLISH -> "Grade ${meta.grade} • Test #${meta.testNumber}"
        }
        _activeTest.value = ActiveTestSession(
            subject = "MATH",
            categoryKey = "GRADE_${meta.grade}",
            testIndex = meta.testNumber,
            title = gradeTestTitle,
            subtitle = meta.title,
            questions = questions
        )
    }

    fun closeActiveTest() {
        _activeTest.value = null
    }

    fun startCustomTest(test: CustomTestEntity) {
        val qs = AppRepository.decodeQuestions(test.questionsJson)
        if (qs.isEmpty()) return
        _activeTest.value = ActiveTestSession(
            subject = test.subject,
            categoryKey = test.categoryKey,
            testIndex = 999,
            title = test.title,
            subtitle = "${test.categoryKey} • ${test.authorName}",
            questions = qs
        )
    }

    fun applyAppUpdate(update: AppUpdateEntity) {
        viewModelScope.launch {
            prefs.markUpdateInstalled(update.id, update.versionName)
        }
    }

    suspend fun submitTestScore(session: ActiveTestSession, score: Int): Int {
        val uid = loggedInUserId.value ?: return 0
        return repository.recordTestCompletion(
            userId = uid,
            subject = session.subject,
            categoryKey = session.categoryKey,
            testIndex = session.testIndex,
            testTitle = session.title,
            score = score,
            totalQuestions = session.questions.size
        )
    }

    // Messaging & Groups
    fun observeMessages(channelId: String): Flow<List<MessageEntity>> =
        repository.observeMessages(channelId)

    fun directChannelId(otherUserId: String): String {
        val me = loggedInUserId.value ?: "guest"
        return repository.directChannelId(me, otherUserId)
    }

    fun sendDirectMessage(otherUserId: String, text: String) {
        val me = currentUser.value ?: return
        val channelId = repository.directChannelId(me.id, otherUserId)
        viewModelScope.launch {
            repository.sendMessage(channelId, me.id, me.fullName, text)
        }
    }

    fun sendGroupMessage(groupId: String, text: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.sendMessage("grp_$groupId", me.id, me.fullName, text)
        }
    }

    fun createGroup(name: String, description: String, topicTag: String) {
        val me = currentUser.value ?: return
        if (name.trim().length < 2) return
        viewModelScope.launch {
            repository.createGroup(name, description, topicTag, me.id)
        }
    }

    fun toggleJoinGroup(groupId: String) {
        val me = currentUser.value ?: return
        viewModelScope.launch {
            repository.joinOrLeaveGroup(groupId, me.id)
        }
    }

    fun markAllNotificationsRead() {
        val uid = loggedInUserId.value ?: return
        val latestAlert = notifications.value.maxOfOrNull { it.timestamp } ?: 0L
        val seenAt = maxOf(System.currentTimeMillis(), latestAlert)
        viewModelScope.launch {
            prefs.setNotificationsLastSeen(uid, seenAt)
        }
    }

    // Admin Portal (Sherzod Sultankulov)
    fun openAdminModal() {
        _adminMessage.value = null
        _showAdminModal.value = true
    }

    fun closeAdminModal() {
        _showAdminModal.value = false
        _adminMessage.value = null
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
        _showAdminModal.value = false
    }

    fun setupAdminPassword(password: String, confirm: String) {
        val lang = appLanguage.value
        if (password.length < 6) {
            _adminMessage.value = when (lang) {
                AppLanguage.UZBEK -> "Parol kamida 6 ta belgidan iborat bo'lishi kerak."
                AppLanguage.TURKISH -> "Yönetici şifresi en az 6 karakter olmalıdır."
                AppLanguage.RUSSIAN -> "Пароль админа должен содержать минимум 6 символов."
                AppLanguage.ENGLISH -> "Admin password must be at least 6 characters."
            }
            return
        }
        if (password != confirm) {
            _adminMessage.value = when (lang) {
                AppLanguage.UZBEK -> "Parollar bir-biriga mos emas."
                AppLanguage.TURKISH -> "Şifreler eşleşmiyor."
                AppLanguage.RUSSIAN -> "Пароли не совпадают."
                AppLanguage.ENGLISH -> "Passwords do not match."
            }
            return
        }
        viewModelScope.launch {
            if (prefs.adminPasswordHash.first() != null) {
                _adminMessage.value = when (lang) {
                    AppLanguage.UZBEK -> "Admin paroli allaqachon o'rnatilgan."
                    AppLanguage.TURKISH -> "Yönetici şifresi zaten oluşturulmuş."
                    AppLanguage.RUSSIAN -> "Пароль админа уже установлен."
                    AppLanguage.ENGLISH -> "Admin password is already configured."
                }
                return@launch
            }
            val saltedHash = withContext(Dispatchers.Default) {
                SecurityUtil.hashAdminPassword(password)
            }
            prefs.setAdminPasswordHash(saltedHash)
            failedAdminAttempts = 0
            adminLockoutUntilMs = 0L
            _isAdminUnlocked.value = true
            _adminMessage.value = when (lang) {
                AppLanguage.UZBEK -> "Shaxsiy admin paroli saqlandi. Uni faqat siz bilasiz."
                AppLanguage.TURKISH -> "Özel yönetici şifreniz kaydedildi. Yalnızca siz bilirsiniz."
                AppLanguage.RUSSIAN -> "Личный пароль админа сохранён. Знаете его только вы."
                AppLanguage.ENGLISH -> "Private admin password saved for Sherzod Sultankulov."
            }
        }
    }

    fun unlockAdmin(password: String) {
        val lang = appLanguage.value
        val now = System.currentTimeMillis()
        if (now < adminLockoutUntilMs) {
            val secs = ((adminLockoutUntilMs - now) / 1000L).coerceAtLeast(1L)
            _adminMessage.value = when (lang) {
                AppLanguage.UZBEK -> "Juda ko'p xato urinish. $secs soniyadan keyin qayta urining."
                AppLanguage.TURKISH -> "Çok fazla hatalı deneme. $secs saniye sonra tekrar deneyin."
                AppLanguage.RUSSIAN -> "Слишком много неверных попыток. Повторите через $secs сек."
                AppLanguage.ENGLISH -> "Too many failed attempts. Wait ${secs}s before trying again."
            }
            return
        }
        val stored = adminPasswordHash.value
        if (stored == null) {
            _adminMessage.value = when (lang) {
                AppLanguage.UZBEK -> "Avval admin parolini yarating."
                AppLanguage.TURKISH -> "Önce yönetici şifresi oluşturun."
                AppLanguage.RUSSIAN -> "Сначала создайте пароль админа."
                AppLanguage.ENGLISH -> "Create your admin password first."
            }
            return
        }
        viewModelScope.launch {
            val verified = withContext(Dispatchers.Default) {
                SecurityUtil.verifyAdminPassword(password, stored)
            }
            if (verified) {
                failedAdminAttempts = 0
                adminLockoutUntilMs = 0L
                _isAdminUnlocked.value = true
                _adminMessage.value = null
            } else {
                failedAdminAttempts++
                if (failedAdminAttempts >= 5) {
                    adminLockoutUntilMs = System.currentTimeMillis() + 30_000L
                    failedAdminAttempts = 0
                    _adminMessage.value = when (lang) {
                        AppLanguage.UZBEK -> "5 marta noto'g'ri parol kiritildi. 30 soniya bloklandi."
                        AppLanguage.TURKISH -> "5 hatalı deneme. 30 saniye kilitlendi."
                        AppLanguage.RUSSIAN -> "5 неверных попыток. Блокировка на 30 секунд."
                        AppLanguage.ENGLISH -> "5 incorrect attempts. Locked for 30 seconds."
                    }
                } else {
                    _adminMessage.value = when (lang) {
                        AppLanguage.UZBEK -> "Admin paroli noto'g'ri."
                        AppLanguage.TURKISH -> "Yönetici şifresi yanlış."
                        AppLanguage.RUSSIAN -> "Неверный пароль администратора."
                        AppLanguage.ENGLISH -> "Incorrect admin password."
                    }
                }
            }
        }
    }

    fun updateAdminPassword(oldPassword: String, newPassword: String, confirm: String) {
        val lang = appLanguage.value
        if (!_isAdminUnlocked.value) return
        val stored = adminPasswordHash.value ?: return
        if (newPassword.length < 6) {
            _adminMessage.value = when (lang) {
                AppLanguage.UZBEK -> "Yangi parol kamida 6 ta belgi bo'lishi kerak."
                AppLanguage.TURKISH -> "Yeni şifre en az 6 karakter olmalıdır."
                AppLanguage.RUSSIAN -> "Новый пароль должен быть не короче 6 символов."
                AppLanguage.ENGLISH -> "New password must be at least 6 characters."
            }
            return
        }
        if (newPassword != confirm) {
            _adminMessage.value = when (lang) {
                AppLanguage.UZBEK -> "Yangi parollar mos emas."
                AppLanguage.TURKISH -> "Yeni şifreler eşleşmiyor."
                AppLanguage.RUSSIAN -> "Новые пароли не совпадают."
                AppLanguage.ENGLISH -> "New passwords do not match."
            }
            return
        }
        viewModelScope.launch {
            val ok = withContext(Dispatchers.Default) {
                SecurityUtil.verifyAdminPassword(oldPassword, stored)
            }
            if (!ok) {
                _adminMessage.value = when (lang) {
                    AppLanguage.UZBEK -> "Amaldagi parol noto'g'ri."
                    AppLanguage.TURKISH -> "Mevcut şifre yanlış."
                    AppLanguage.RUSSIAN -> "Текущий пароль неверен."
                    AppLanguage.ENGLISH -> "Current password is incorrect."
                }
                return@launch
            }
            val newHash = withContext(Dispatchers.Default) {
                SecurityUtil.hashAdminPassword(newPassword)
            }
            prefs.setAdminPasswordHash(newHash)
            _adminMessage.value = when (lang) {
                AppLanguage.UZBEK -> "Admin paroli yangilandi."
                AppLanguage.TURKISH -> "Yönetici şifresi güncellendi."
                AppLanguage.RUSSIAN -> "Пароль админа обновлён."
                AppLanguage.ENGLISH -> "Admin password updated."
            }
        }
    }

    fun adminSetUserVerified(userId: String, verified: Boolean) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.setUserVerified(userId, verified)
        }
    }

    fun adminDeleteUser(userId: String) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.deleteUserByAdmin(userId)
            if (loggedInUserId.value == userId) {
                prefs.setLoggedInUserId(null)
            }
        }
    }

    fun adminAddNews(title: String, body: String, category: String, pinAsDaily: Boolean) {
        if (!_isAdminUnlocked.value) return
        if (title.isBlank() || body.isBlank()) return
        viewModelScope.launch {
            repository.addNews(title, body, category, pinAsDaily)
            _adminMessage.value = when (appLanguage.value) {
                AppLanguage.UZBEK -> "Yangilik joylandi!"
                AppLanguage.TURKISH -> "Haber yayınlandı!"
                AppLanguage.RUSSIAN -> "Новость опубликована!"
                AppLanguage.ENGLISH -> "News published!"
            }
        }
    }

    fun adminPinDaily(newsId: String) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.pinNewsAsDaily(newsId)
        }
    }

    fun adminDeleteNews(newsId: String) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.deleteNews(newsId)
        }
    }

    fun adminDeleteGroup(groupId: String) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.deleteGroup(groupId)
        }
    }

    fun adminCreateCustomTest(
        subject: String,
        categoryKey: String,
        title: String,
        description: String,
        questions: List<QuizQuestion>
    ) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.createCustomTest(subject, categoryKey, title, description, questions)
            _adminMessage.value = when (appLanguage.value) {
                AppLanguage.UZBEK -> "Test yaratildi va joylandi!"
                AppLanguage.TURKISH -> "Test oluşturuldu ve yayınlandı!"
                AppLanguage.RUSSIAN -> "Тест создан и опубликован!"
                AppLanguage.ENGLISH -> "Custom test published!"
            }
        }
    }

    fun adminDeleteCustomTest(testId: String) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.deleteCustomTest(testId)
        }
    }

    fun adminPublishAppUpdate(
        versionName: String,
        title: String,
        changelog: String,
        isCritical: Boolean
    ) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.publishAppUpdate(versionName, title, changelog, isCritical)
            _adminMessage.value = when (appLanguage.value) {
                AppLanguage.UZBEK -> "Ilova yangilanishi chiqarildi!"
                AppLanguage.TURKISH -> "Uygulama güncellemesi yayınlandı!"
                AppLanguage.RUSSIAN -> "Обновление приложения выпущено!"
                AppLanguage.ENGLISH -> "App update published!"
            }
        }
    }

    fun adminDeleteAppUpdate(updateId: String) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            repository.deleteAppUpdate(updateId)
        }
    }
}
