package group.skytale.app

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import group.skytale.app.data.AppLanguage
import group.skytale.app.data.AppSettings
import group.skytale.app.data.AuthMode
import group.skytale.app.data.ChatModel
import group.skytale.app.data.ContactModel
import group.skytale.app.data.FeedPostModel
import group.skytale.app.data.HomeTab
import group.skytale.app.data.MessageModel
import group.skytale.app.data.SearchUserModel
import group.skytale.app.data.SkytaleRepository
import group.skytale.app.data.StoredSession
import group.skytale.app.data.ThemeMode
import group.skytale.app.sync.SkytaleSyncService
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SkytaleUiState(
    val ready: Boolean = false,
    val busy: Boolean = false,
    val session: StoredSession? = null,
    val settings: AppSettings = AppSettings(),
    val language: AppLanguage = AppLanguage.EN,
    val authMode: AuthMode = AuthMode.REGISTER,
    val authStep: Int = 0,
    val username: String = "",
    val password: String = "",
    val nickname: String = "",
    val about: String = "",
    val lastSeenVisibility: String = "contacts",
    val usernameDiscoverable: Boolean = true,
    val selectedTab: HomeTab = HomeTab.CHATS,
    val chats: List<ChatModel> = emptyList(),
    val contacts: List<ContactModel> = emptyList(),
    val feed: List<FeedPostModel> = emptyList(),
    val messages: List<MessageModel> = emptyList(),
    val searchUsers: List<SearchUserModel> = emptyList(),
    val chatSearchResults: List<MessageModel> = emptyList(),
    val selectedComposerMedia: List<DraftMediaSelection> = emptyList(),
    val selectedProfileAvatar: DraftMediaSelection? = null,
    val removeProfileAvatar: Boolean = false,
    val pendingUploads: List<PendingMediaUpload> = emptyList(),
    val hasMoreMessages: Boolean = true,
    val loadingOlderMessages: Boolean = false,
    val chatInitialLoading: Boolean = false,
    val openedChatId: String? = null,
    val contactPreview: ContactModel? = null,
    val contactPreviewChatId: String? = null,
    val contactPreviewMessages: List<MessageModel> = emptyList(),
    val replyToMessageId: String? = null,
    val chatSearchQuery: String = "",
    val addContactQuery: String = "",
    val chatListQuery: String = "",
    val authError: String? = null,
    val typingUserIdByChat: Map<String, String> = emptyMap(),
    val offlineMessage: String? = null,
    val errorMessage: String? = null,
    val pendingSoundEvent: UiSoundEvent? = null,
)

data class UiSoundEvent(
    val type: SoundEffect,
    val nonce: Long = System.nanoTime(),
)

enum class SoundEffect {
    SENT,
    INCOMING,
}

data class DraftMediaSelection(
    val uri: String,
    val fileName: String,
    val mimeType: String,
    val compressImage: Boolean = true,
    val stripMetadata: Boolean = true,
)

data class PendingMediaUpload(
    val localId: String,
    val chatId: String,
    val localUri: String,
    val progress: Float = 0f,
)

class SkytaleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SkytaleRepository = (application as SkytaleApp).graph.repository
    private val state = MutableStateFlow(SkytaleUiState())
    val uiState: StateFlow<SkytaleUiState> = state.asStateFlow()

    private var chatJob: Job? = null
    private val uploadJobs = linkedMapOf<String, Job>()
    private var pendingIntentChatId: String? = null
    private var appInForeground: Boolean = false

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { settings ->
                state.value = state.value.copy(
                    settings = settings,
                    language = AppLanguage.fromCode(settings.language),
                )
                if (state.value.session != null) {
                    if (settings.instantSyncEnabled) ensureSyncService() else stopSyncService()
                }
            }
        }
        viewModelScope.launch {
            repository.sessionFlow.collect { session ->
                state.value = state.value.copy(
                    session = session,
                    nickname = session?.nickname.orEmpty(),
                    username = session?.username.orEmpty(),
                    about = session?.about.orEmpty(),
                    lastSeenVisibility = session?.lastSeenVisibility ?: "contacts",
                    usernameDiscoverable = session?.usernameDiscoverable ?: true,
                    ready = true,
                )
                if (session != null) {
                    restore()
                    ensureSyncService()
                    repository.startRealtime(viewModelScope)
                    pendingIntentChatId?.let { chatId ->
                        pendingIntentChatId = null
                        openChat(chatId)
                    }
                } else {
                    state.value = state.value.copy(messages = emptyList(), openedChatId = null)
                    stopSyncService()
                    repository.stopRealtime()
                    pendingIntentChatId = null
                }
            }
        }
        viewModelScope.launch {
            repository.observeChats().collect { chats ->
                state.value = state.value.copy(chats = chats)
            }
        }
        viewModelScope.launch {
            repository.observeContacts().collect { contacts ->
                state.value = state.value.copy(contacts = contacts)
            }
        }
        viewModelScope.launch {
            repository.observeFeed().collect { feed ->
                state.value = state.value.copy(feed = feed)
            }
        }
        viewModelScope.launch {
            repository.typingFlow.collect { typing ->
                state.value = state.value.copy(typingUserIdByChat = typing)
            }
        }
        viewModelScope.launch {
            repository.incomingAlerts.collect {
                if (state.value.settings.soundEnabled && appInForeground) {
                    state.value = state.value.copy(
                        pendingSoundEvent = UiSoundEvent(SoundEffect.INCOMING),
                    )
                }
            }
        }
    }

    fun setAuthMode(mode: AuthMode) {
        state.value = state.value.copy(authMode = mode, authError = null)
    }

    fun setLanguage(language: AppLanguage) {
        repository.updateSettings { it.copy(language = language.code) }
        state.value = state.value.copy(language = language)
    }

    fun nextAuthStep() {
        val current = state.value
        when (current.authMode) {
            AuthMode.REGISTER -> when (current.authStep) {
                0 -> state.value = current.copy(authStep = 1, authError = null)
                1 -> state.value = current.copy(authStep = 2, authError = null)
                2 -> {
                    val error = validateUsername(current.username)
                    if (error == null) {
                        state.value = current.copy(authStep = 3, authError = null)
                    } else {
                        state.value = current.copy(authError = error)
                    }
                }
                3 -> {
                    val error = validatePassword(current.password)
                    if (error == null) {
                        state.value = current.copy(authStep = 4, authError = null)
                    } else {
                        state.value = current.copy(authError = error)
                    }
                }
                else -> submitAuth()
            }

            AuthMode.LOGIN -> when (current.authStep) {
                0 -> state.value = current.copy(authStep = 1, authError = null)
                1 -> state.value = current.copy(authStep = 2, authError = null)
                2 -> {
                    val error = validateUsername(current.username)
                    if (error == null) {
                        state.value = current.copy(authStep = 3, authError = null)
                    } else {
                        state.value = current.copy(authError = error)
                    }
                }
                else -> submitAuth()
            }
        }
    }

    fun previousAuthStep() {
        state.value = state.value.copy(authStep = (state.value.authStep - 1).coerceAtLeast(0), authError = null)
    }

    fun updateUsername(value: String) {
        state.value = state.value.copy(username = value, authError = null)
    }

    fun updatePassword(value: String) {
        state.value = state.value.copy(password = value, authError = null)
    }

    fun updateNickname(value: String) {
        state.value = state.value.copy(nickname = value, authError = null)
    }

    fun updateAbout(value: String) {
        state.value = state.value.copy(about = value)
    }

    fun selectComposerMedia(uri: String, fileName: String, mimeType: String) {
        state.value = state.value.copy(
            selectedComposerMedia = state.value.selectedComposerMedia + DraftMediaSelection(uri = uri, fileName = fileName, mimeType = mimeType),
            errorMessage = null,
            offlineMessage = null,
        )
    }

    fun selectComposerMediaBatch(items: List<DraftMediaSelection>) {
        if (items.isEmpty()) return
        state.value = state.value.copy(
            selectedComposerMedia = state.value.selectedComposerMedia + items,
            errorMessage = null,
            offlineMessage = null,
        )
    }

    fun clearComposerMedia() {
        state.value = state.value.copy(selectedComposerMedia = emptyList())
    }

    fun updateComposerMediaOptions(compressImage: Boolean, stripMetadata: Boolean) {
        if (state.value.selectedComposerMedia.isEmpty()) return
        state.value = state.value.copy(
            selectedComposerMedia = state.value.selectedComposerMedia.map { media ->
                media.copy(
                    compressImage = compressImage,
                    stripMetadata = if (compressImage) true else stripMetadata,
                )
            },
        )
    }

    fun selectProfileAvatar(uri: String, fileName: String, mimeType: String) {
        state.value = state.value.copy(
            selectedProfileAvatar = DraftMediaSelection(uri = uri, fileName = fileName, mimeType = mimeType),
            removeProfileAvatar = false,
            errorMessage = null,
            offlineMessage = null,
        )
    }

    fun clearProfileAvatarSelection() {
        state.value = state.value.copy(
            selectedProfileAvatar = null,
            removeProfileAvatar = state.value.session?.avatarUrl?.isNotBlank() == true || state.value.session?.avatarThumbUrl?.isNotBlank() == true,
        )
    }

    fun updateLastSeenVisibility(value: String) {
        state.value = state.value.copy(lastSeenVisibility = value)
    }

    fun updateUsernameDiscoverable(value: Boolean) {
        state.value = state.value.copy(usernameDiscoverable = value)
    }

    fun updateGhostMode(enabled: Boolean) {
        state.value = state.value.copy(
            lastSeenVisibility = if (enabled) "ghost" else "contacts",
            usernameDiscoverable = if (enabled) false else true,
        )
    }

    fun updateAddContactQuery(value: String) {
        state.value = state.value.copy(addContactQuery = value, searchUsers = emptyList())
    }

    fun updateChatListQuery(value: String) {
        state.value = state.value.copy(chatListQuery = value)
    }

    fun updateChatSearchQuery(value: String) {
        state.value = state.value.copy(chatSearchQuery = value)
    }

    fun selectTab(tab: HomeTab) {
        state.value = state.value.copy(selectedTab = tab)
    }

    fun updateThemeMode(mode: ThemeMode) = updateSettings { it.copy(themeMode = mode) }

    fun generatePassword() {
        val charset = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#\$%^&*"
        val generated = buildString {
            repeat(20) {
                append(charset[Random.nextInt(charset.length)])
            }
        }
        state.value = state.value.copy(password = generated, authError = null)
    }

    fun submitAuth() {
        val current = state.value
        val validationError = when (current.authMode) {
            AuthMode.REGISTER -> validateUsername(current.username)
                ?: validatePassword(current.password)
                ?: validateNickname(current.nickname.ifBlank { current.username })
            AuthMode.LOGIN -> validateUsername(current.username)
                ?: validatePassword(current.password)
        }
        if (validationError != null) {
            state.value = current.copy(authError = validationError)
            return
        }
        viewModelScope.launch {
            runBusy {
                if (state.value.authMode == AuthMode.LOGIN) {
                    repository.login(
                        username = state.value.username.trim(),
                        password = state.value.password,
                        deviceName = buildDeviceName(),
                    )
                } else {
                    repository.register(
                        username = state.value.username.trim(),
                        password = state.value.password,
                        language = state.value.language,
                        nickname = state.value.nickname.ifBlank { state.value.username.trim() },
                        deviceName = buildDeviceName(),
                    )
                }
                ensureSyncService()
                repository.refreshBootstrap()
                state.value = state.value.copy(authStep = 0, authError = null)
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            runBusy {
                repository.refreshBootstrap()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            runBusy {
                repository.logout()
            }
        }
    }

    fun logoutOthers() {
        viewModelScope.launch {
            runBusy {
                repository.logoutOthers()
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            runBusy {
                if (newPassword.length < 10) {
                    error("Password must be at least 10 characters.")
                }
                repository.changePassword(currentPassword = currentPassword, newPassword = newPassword)
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            runBusy {
                repository.deleteAccount()
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            runBusy {
                var avatarUrl = state.value.session?.avatarUrl.orEmpty()
                var avatarThumbUrl = state.value.session?.avatarThumbUrl.orEmpty()
                if (state.value.removeProfileAvatar) {
                    avatarUrl = ""
                    avatarThumbUrl = ""
                } else {
                    state.value.selectedProfileAvatar?.let { selection ->
                        val prepared = prepareImageUpload(selection)
                        val uploaded = repository.uploadImage(
                            bytes = prepared.bytes,
                            fileName = prepared.fileName,
                            mimeType = prepared.mimeType,
                            purpose = "avatar",
                        )
                        avatarUrl = uploaded.url
                        avatarThumbUrl = uploaded.thumbUrl.ifBlank { uploaded.previewUrl }
                    }
                }
                repository.updateProfile(
                    username = state.value.username,
                    nickname = state.value.nickname,
                    about = state.value.about,
                    language = state.value.language,
                    avatarUrl = avatarUrl,
                    avatarThumbUrl = avatarThumbUrl,
                    lastSeenVisibility = state.value.lastSeenVisibility,
                    usernameDiscoverable = state.value.usernameDiscoverable,
                )
                state.value = state.value.copy(selectedProfileAvatar = null, removeProfileAvatar = false)
            }
        }
    }

    fun searchPeople() {
        val query = state.value.addContactQuery.trim()
        if (query.length < 3) {
            state.value = state.value.copy(searchUsers = emptyList())
            return
        }
        viewModelScope.launch {
            runBusy {
                val results = repository.searchUsers(query)
                state.value = state.value.copy(searchUsers = results)
            }
        }
    }

    fun addContactAndOpen(userId: String) {
        viewModelScope.launch {
            runBusy {
                val chatId = repository.addContact(userId = userId)
                openChat(chatId)
            }
        }
    }

    fun openContactChat(userId: String) {
        viewModelScope.launch {
            runBusy {
                val chatId = repository.ensureDirectChat(userId)
                openChat(chatId)
            }
        }
    }

    fun saveContactNicknameOverride(userId: String, value: String) {
        val normalized = value.trim().replace(Regex("\\s+"), " ")
        if (normalized.isNotEmpty()) {
            val validationError = validateNickname(normalized)
            if (validationError != null) {
                state.value = state.value.copy(errorMessage = validationError)
                return
            }
        }
        viewModelScope.launch {
            runBusy {
                if (normalized.isBlank()) {
                    repository.clearUserNicknameOverride(userId)
                } else {
                    repository.setUserNicknameOverride(userId, normalized)
                }
            }
        }
    }

    fun openChat(chatId: String) {
        state.value = state.value.copy(
            openedChatId = chatId,
            replyToMessageId = null,
            hasMoreMessages = true,
            loadingOlderMessages = false,
            chatInitialLoading = false,
            chatSearchResults = emptyList(),
            chatSearchQuery = "",
        )
        repository.setActiveChat(chatId)
        if (state.value.lastSeenVisibility != "ghost") {
            viewModelScope.launch {
                repository.markRead(chatId, getApplication())
            }
        }
        chatJob?.cancel()
        val currentUserId = state.value.session?.userId.orEmpty()
        chatJob = viewModelScope.launch {
            repository.observeMessages(chatId, currentUserId).collect { messages ->
                if (state.value.openedChatId != chatId) return@collect
                state.value = state.value.copy(messages = messages)
            }
        }
        viewModelScope.launch {
            runCatching {
                val loaded = repository.loadMessages(
                    chatId = chatId,
                    limit = 15,
                    replace = true,
                    markRead = false,
                )
                state.value = state.value.copy(hasMoreMessages = loaded >= 15)
            }.onFailure {
                Unit
            }
        }
    }

    fun closeChat() {
        repository.setActiveChat(null)
        state.value = state.value.copy(openedChatId = null, chatSearchResults = emptyList(), chatSearchQuery = "", replyToMessageId = null, selectedComposerMedia = emptyList(), chatInitialLoading = false)
    }

    fun handleNotificationChatIntent(chatId: String?) {
        val normalized = chatId?.takeIf { it.isNotBlank() } ?: return
        if (state.value.session != null) {
            openChat(normalized)
        } else {
            pendingIntentChatId = normalized
        }
    }

    fun setAppForeground(isForeground: Boolean) {
        appInForeground = isForeground
        repository.setAppForeground(isForeground)
        val activeChatId = if (isForeground) state.value.openedChatId else null
        repository.setActiveChat(activeChatId)
        if (isForeground && activeChatId != null && state.value.lastSeenVisibility != "ghost") {
            viewModelScope.launch {
                repository.markRead(activeChatId, getApplication())
            }
        }
    }

    fun sendMessage(text: String) {
        val chatId = state.value.openedChatId ?: return
        val replyToId = state.value.replyToMessageId
        val mediaSelections = state.value.selectedComposerMedia
        val trimmed = text.trim()
        if (trimmed.isBlank() && mediaSelections.isEmpty()) return
        if (mediaSelections.isEmpty()) {
            viewModelScope.launch {
                runCatching { repository.sendMessage(chatId, trimmed, replyToId) }
                    .onSuccess {
                        if (state.value.settings.soundEnabled) {
                            state.value = state.value.copy(pendingSoundEvent = UiSoundEvent(SoundEffect.SENT))
                        }
                    }
                    .onFailure { publishError(it) }
                state.value = state.value.copy(replyToMessageId = null)
            }
            return
        }

        val pendingUploads = mediaSelections.map { mediaSelection ->
            PendingMediaUpload(
                localId = UUID.randomUUID().toString(),
                chatId = chatId,
                localUri = mediaSelection.uri,
            )
        }
        state.value = state.value.copy(
            selectedComposerMedia = emptyList(),
            replyToMessageId = null,
            pendingUploads = state.value.pendingUploads + pendingUploads,
        )
        pendingUploads.forEachIndexed { index, pending ->
            val mediaSelection = mediaSelections[index]
            uploadJobs[pending.localId] = viewModelScope.launch {
                try {
                    val prepared = prepareImageUpload(mediaSelection)
                    val uploaded = repository.uploadImage(
                        bytes = prepared.bytes,
                        fileName = prepared.fileName,
                        mimeType = prepared.mimeType,
                        purpose = "chat",
                    ) { progress ->
                        updatePendingUploadProgress(pending.localId, progress)
                    }
                    repository.sendMessage(
                        chatId = chatId,
                        text = if (index == 0) trimmed else "",
                        replyToId = if (index == 0) replyToId else null,
                        media = uploaded,
                    )
                    if (state.value.settings.soundEnabled && index == 0) {
                        state.value = state.value.copy(pendingSoundEvent = UiSoundEvent(SoundEffect.SENT))
                    }
                    removePendingUpload(pending.localId)
                } catch (cancelled: CancellationException) {
                    removePendingUpload(pending.localId)
                    throw cancelled
                } catch (throwable: Throwable) {
                    removePendingUpload(pending.localId)
                    publishError(throwable)
                } finally {
                    uploadJobs.remove(pending.localId)
                }
            }
        }
    }

    fun cancelPendingUpload(localId: String) {
        uploadJobs.remove(localId)?.cancel()
        removePendingUpload(localId)
    }

    fun editMessage(messageId: String, text: String) {
        viewModelScope.launch {
            runCatching { repository.editMessage(messageId, text) }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            runCatching { repository.deleteMessage(messageId) }
        }
    }

    fun clearCurrentChat() {
        val chatId = state.value.openedChatId ?: return
        viewModelScope.launch {
            runBusy {
                repository.clearChat(chatId)
                state.value = state.value.copy(messages = emptyList())
            }
        }
    }

    fun removeCurrentContact() {
        val userId = state.value.chats.firstOrNull { it.id == state.value.openedChatId }?.peer?.id ?: return
        viewModelScope.launch {
            runBusy {
                repository.removeContact(userId)
                closeChat()
            }
        }
    }

    fun toggleCurrentContactBlocked() {
        val userId = state.value.chats.firstOrNull { it.id == state.value.openedChatId }?.peer?.id ?: return
        val blocked = state.value.contacts.firstOrNull { it.user.id == userId }?.isBlocked ?: false
        viewModelScope.launch {
            runBusy {
                repository.toggleBlock(userId, !blocked)
            }
        }
    }

    fun openContactPreview(contactId: String) {
        val contact = state.value.contacts.firstOrNull { it.user.id == contactId } ?: return
        viewModelScope.launch {
            runBusy {
                val chatId = repository.ensureDirectChat(contact.user.id)
                val previewMessages = repository.previewMessages(chatId)
                state.value = state.value.copy(
                    contactPreview = contact,
                    contactPreviewChatId = chatId,
                    contactPreviewMessages = previewMessages,
                )
            }
        }
    }

    fun closeContactPreview() {
        state.value = state.value.copy(contactPreview = null, contactPreviewChatId = null, contactPreviewMessages = emptyList())
    }

    fun openPreviewChat() {
        val chatId = state.value.contactPreviewChatId ?: return
        closeContactPreview()
        openChat(chatId)
    }

    fun markPreviewChatUnread() {
        val chatId = state.value.contactPreviewChatId ?: return
        viewModelScope.launch {
            runBusy {
                repository.setChatMarkedUnread(chatId, true)
            }
        }
    }

    fun startReply(messageId: String) {
        state.value = state.value.copy(replyToMessageId = messageId)
    }

    fun clearReply() {
        state.value = state.value.copy(replyToMessageId = null)
    }

    fun searchMessages() {
        val chatId = state.value.openedChatId ?: return
        val query = state.value.chatSearchQuery.trim()
        if (query.isBlank()) {
            state.value = state.value.copy(chatSearchResults = emptyList())
            return
        }
        viewModelScope.launch {
            runBusy {
                state.value = state.value.copy(chatSearchResults = repository.searchMessages(chatId, query))
            }
        }
    }

    fun loadOlderMessages() {
        val chatId = state.value.openedChatId ?: return
        val oldestTimestamp = state.value.messages.firstOrNull()?.createdAt ?: return
        if (state.value.loadingOlderMessages || !state.value.hasMoreMessages) return
        viewModelScope.launch {
            state.value = state.value.copy(loadingOlderMessages = true)
            runCatching {
                val loaded = repository.loadMessages(
                    chatId = chatId,
                    before = oldestTimestamp,
                    limit = 20,
                    replace = false,
                    markRead = false,
                )
                state.value = state.value.copy(
                    hasMoreMessages = loaded >= 20,
                    loadingOlderMessages = false,
                )
            }.onFailure {
                state.value = state.value.copy(
                    loadingOlderMessages = false,
                    errorMessage = resolveErrorMessage(it),
                )
            }
        }
    }

    fun ensureMessageLoaded(messageId: String, createdAt: Long) {
        val chatId = state.value.openedChatId ?: return
        if (state.value.messages.any { it.id == messageId }) return
        viewModelScope.launch {
            runCatching {
                repository.loadMessages(
                    chatId = chatId,
                    before = createdAt + 1,
                    limit = 40,
                    replace = false,
                    markRead = false,
                )
            }
        }
    }

    fun toggleArchive(chatId: String, enabled: Boolean) {
        viewModelScope.launch { runCatching { repository.setChatArchived(chatId, enabled) } }
    }

    fun togglePin(chatId: String, enabled: Boolean) {
        viewModelScope.launch { runCatching { repository.setChatPinned(chatId, enabled) } }
    }

    fun toggleMute(chatId: String, enabled: Boolean) {
        viewModelScope.launch { runCatching { repository.setChatMuted(chatId, enabled) } }
    }

    fun toggleUnread(chatId: String, enabled: Boolean) {
        viewModelScope.launch { runCatching { repository.setChatMarkedUnread(chatId, enabled) } }
    }

    fun setTyping(isTyping: Boolean) {
        val chatId = state.value.openedChatId ?: return
        viewModelScope.launch { repository.setTyping(chatId, isTyping) }
    }

    fun updateNotifications(enabled: Boolean) = updateSettings { it.copy(notificationsEnabled = enabled) }
    fun updatePreviews(enabled: Boolean) = updateSettings { it.copy(previewsEnabled = enabled) }
    fun updateSound(enabled: Boolean) = updateSettings { it.copy(soundEnabled = enabled) }
    fun updateInstantSync(enabled: Boolean) = updateSettings { it.copy(instantSyncEnabled = enabled, instantSyncConfigured = true) }
    fun updateCompact(enabled: Boolean) = updateSettings { it.copy(compactMode = enabled) }
    fun updateHaptics(enabled: Boolean) = updateSettings { it.copy(hapticsEnabled = enabled) }
    fun updateAppLock(enabled: Boolean) = updateSettings { it.copy(appLockEnabled = enabled) }
    fun updateArchivedChats(enabled: Boolean) = updateSettings { it.copy(showArchivedChats = enabled) }

    fun dismissErrorDialogs() {
        state.value = state.value.copy(errorMessage = null, offlineMessage = null)
    }

    fun consumeSoundEvent() {
        state.value = state.value.copy(pendingSoundEvent = null)
    }

    private fun updateSettings(transform: (AppSettings) -> AppSettings) {
        repository.updateSettings(transform)
        ensureSyncService()
    }

    private suspend fun runBusy(block: suspend () -> Unit) {
        state.value = state.value.copy(busy = true, errorMessage = null, offlineMessage = null)
        runCatching { block() }.onFailure {
            publishError(it)
        }
        state.value = state.value.copy(busy = false)
    }

    private fun buildDeviceName(): String = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

    private fun ensureSyncService() {
        if (state.value.session == null) return
        if (!state.value.settings.instantSyncEnabled) {
            stopSyncService()
            return
        }
        ContextCompat.startForegroundService(
            getApplication(),
            Intent(getApplication(), SkytaleSyncService::class.java),
        )
    }

    private fun stopSyncService() {
        getApplication<Application>().stopService(Intent(getApplication(), SkytaleSyncService::class.java))
    }

    private fun validateUsername(value: String): String? {
        val normalized = value.trim().replace(Regex("\\s+"), " ")
        if (normalized.length !in 3..32) {
            return "Username must be 3-32 characters."
        }
        if (!Regex("^[\\p{L}\\p{N}._\\- ]+$").matches(normalized)) {
            return "Username may contain letters, digits, spaces, dots, underscores, and dashes."
        }
        return null
    }

    private fun validatePassword(value: String): String? {
        if (value.length < 10) {
            return "Password must be at least 10 characters."
        }
        return null
    }

    private fun validateNickname(value: String): String? {
        if (value.trim().length < 2) {
            return "Nickname must be at least 2 characters."
        }
        return null
    }

    private fun publishError(throwable: Throwable) {
        val resolvedMessage = resolveErrorMessage(throwable)
        val offlineMessage = if (isOfflineError(throwable)) resolvedMessage else null
        state.value = state.value.copy(
            errorMessage = if (offlineMessage == null) resolvedMessage else null,
            offlineMessage = offlineMessage,
            authError = if (state.value.session == null) resolvedMessage else state.value.authError,
        )
    }

    private fun resolveErrorMessage(throwable: Throwable): String {
        if (throwable is HttpException) {
            val body = throwable.response()?.errorBody()?.string().orEmpty()
            val match = Regex("\"error\"\\s*:\\s*\"([^\"]+)\"").find(body)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        if (isOfflineError(throwable)) {
            return "No internet connection. Check the network and try again."
        }
        if (throwable is SerializationException) {
            return "The server returned an unexpected response. Please retry."
        }
        return throwable.message ?: "Unexpected error"
    }

    private fun isOfflineError(throwable: Throwable): Boolean =
        throwable is UnknownHostException ||
            throwable is SocketTimeoutException ||
            throwable is IOException

    private fun updatePendingUploadProgress(localId: String, progress: Float) {
        state.value = state.value.copy(
            pendingUploads = state.value.pendingUploads.map {
                if (it.localId == localId) it.copy(progress = progress.coerceIn(0f, 1f)) else it
            },
        )
    }

    private fun removePendingUpload(localId: String) {
        state.value = state.value.copy(
            pendingUploads = state.value.pendingUploads.filterNot { it.localId == localId },
        )
    }

    private suspend fun prepareImageUpload(selection: DraftMediaSelection): PreparedImageUpload = withContext(Dispatchers.IO) {
        val resolver = getApplication<Application>().contentResolver
        val uri = Uri.parse(selection.uri)
        val sourceBytes = resolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("Unable to read selected image.")
        val normalizedMime = selection.mimeType.lowercase().substringBefore(';').ifBlank { "image/jpeg" }
        val canPassthroughOriginal = !selection.compressImage &&
            !selection.stripMetadata &&
            normalizedMime in setOf("image/jpeg", "image/png", "image/gif", "image/webp") &&
            sourceBytes.size <= 8 * 1024 * 1024
        if (canPassthroughOriginal) {
            return@withContext PreparedImageUpload(
                bytes = sourceBytes,
                fileName = selection.fileName.ifBlank { "image.jpg" },
                mimeType = normalizedMime,
            )
        }

        val decoded = runCatching {
            val source = ImageDecoder.createSource(resolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
        }.getOrNull() ?: run {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(sourceBytes, 0, sourceBytes.size, options)
            val sampled = BitmapFactory.Options().apply {
                inSampleSize = calculateSampleSize(
                    options.outWidth,
                    options.outHeight,
                    if (selection.compressImage) 1600 else maxOf(options.outWidth, options.outHeight, 1),
                )
            }
            BitmapFactory.decodeByteArray(sourceBytes, 0, sourceBytes.size, sampled)
        }
        if (decoded == null) {
            error("Unsupported file type.")
        }

        val scaled = if (selection.compressImage) scaleBitmapIfNeeded(decoded, 1600) else decoded
        if (scaled !== decoded) decoded.recycle()
        val hasAlpha = scaled.hasAlpha()
        val format = if (hasAlpha) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        val mimeType = if (hasAlpha) "image/png" else "image/jpeg"
        val output = ByteArrayOutputStream()
        val quality = if (hasAlpha) 100 else if (selection.compressImage) 90 else 98
        scaled.compress(format, quality, output)
        scaled.recycle()
        PreparedImageUpload(
            bytes = output.toByteArray(),
            fileName = normalizeFileName(selection.fileName, hasAlpha),
            mimeType = mimeType,
        )
    }

    private fun calculateSampleSize(width: Int, height: Int, maxSize: Int): Int {
        if (width <= 0 || height <= 0) return 1
        var sampleSize = 1
        var targetWidth = width
        var targetHeight = height
        while (targetWidth > maxSize || targetHeight > maxSize) {
            sampleSize *= 2
            targetWidth /= 2
            targetHeight /= 2
        }
        return sampleSize.coerceAtLeast(1)
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxSize: Int): Bitmap {
        val maxDimension = maxOf(bitmap.width, bitmap.height)
        if (maxDimension <= maxSize) return bitmap
        val ratio = maxSize.toFloat() / maxDimension.toFloat()
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * ratio).toInt().coerceAtLeast(1),
            (bitmap.height * ratio).toInt().coerceAtLeast(1),
            true,
        )
    }

    private fun normalizeFileName(original: String, hasAlpha: Boolean): String {
        val base = original.substringBeforeLast('.').ifBlank { "image" }
        return if (hasAlpha) "$base.png" else "$base.jpg"
    }

    private data class PreparedImageUpload(
        val bytes: ByteArray,
        val fileName: String,
        val mimeType: String,
    )
}
