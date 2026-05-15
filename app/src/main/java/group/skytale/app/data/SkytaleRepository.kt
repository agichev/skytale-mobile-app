package group.skytale.app.data

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import group.skytale.app.BuildConfig
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import group.skytale.app.sync.SkytaleNotificationManager

class AppGraph(context: Context) {
    private val appContext = context.applicationContext
    val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
        encodeDefaults = true
    }
    val secureStore = SecureStore(appContext, json)
    val deviceCrypto = DeviceCrypto()
    val database: SkytaleDatabase = Room.databaseBuilder(
        appContext,
        SkytaleDatabase::class.java,
        "skytale.db",
    ).fallbackToDestructiveMigration().build()

    private val authInterceptor = okhttp3.Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        secureStore.currentSession?.token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val api: SkytaleApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(SkytaleApi::class.java)

    val repository = SkytaleRepository(
        database = database,
        api = api,
        secureStore = secureStore,
        deviceCrypto = deviceCrypto,
        json = json,
    )
}

class SkytaleRepository(
    private val database: SkytaleDatabase,
    private val api: SkytaleApi,
    private val secureStore: SecureStore,
    private val deviceCrypto: DeviceCrypto,
    private val json: Json,
) {
    private val contacts = database.contactDao()
    private val chats = database.chatDao()
    private val messages = database.messageDao()
    private val feed = database.feedDao()

    private val activeChatState = MutableStateFlow<String?>(null)
    private val appForegroundState = MutableStateFlow(false)
    private val typingState = MutableStateFlow<Map<String, String>>(emptyMap())
    private val incomingAlertEvents = MutableSharedFlow<IncomingAlert>(extraBufferCapacity = 8)
    private val realtimeClient = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    @Volatile
    private var realtimeSocket: WebSocket? = null
    private var realtimeJob: Job? = null

    val sessionFlow: StateFlow<StoredSession?> = secureStore.sessionFlow
    val settingsFlow: StateFlow<AppSettings> = secureStore.settingsFlow
    val activeChatFlow: StateFlow<String?> = activeChatState
    val typingFlow: StateFlow<Map<String, String>> = typingState
    val incomingAlerts: SharedFlow<IncomingAlert> = incomingAlertEvents.asSharedFlow()

    fun observeContacts(): Flow<List<ContactModel>> = combine(
        contacts.observeAll(),
        database.userNicknameOverrideDao().observeAllOverrides()
    ) { list, overrides ->
        val overrideMap = overrides.associateBy { it.userId }
        list.map {
            val nickname = overrideMap[it.id]?.displayName ?: it.nickname
            ContactModel(
                user = UserModel(
                    id = it.id,
                    username = it.username,
                    nickname = nickname,
                    about = it.about,
                    language = AppLanguage.fromCode(it.language),
                    avatarUrl = it.avatarUrl,
                    avatarThumbUrl = it.avatarThumbUrl,
                    createdAt = it.createdAt,
                    lastSeenAt = it.lastSeenAt,
                    isOnline = it.isOnline,
                    lastSeenVisibility = "contacts",
                    usernameDiscoverable = true,
                ),
                alias = it.alias,
                createdAt = it.createdAt,
                isBlocked = it.isBlocked,
                isFavorite = it.isFavorite,
            )
        }
    }.flowOn(Dispatchers.Default).distinctUntilChanged()

    fun observeChats(): Flow<List<ChatModel>> = combine(
        chats.observeAll(),
        settingsFlow,
        database.userNicknameOverrideDao().observeAllOverrides()
    ) { list, settings, overrides ->
        val overrideMap = overrides.associateBy { it.userId }
        list.filter { settings.showArchivedChats || !it.isArchived }.map { entity ->
            val model = entity.toModel(deviceCrypto)
            if (model.peer != null) {
                val overriddenNickname = overrideMap[model.peer.id]?.displayName
                if (overriddenNickname != null) {
                    model.copy(
                        title = overriddenNickname,
                        peer = model.peer.copy(nickname = overriddenNickname),
                    )
                } else {
                    model
                }
            } else {
                model
            }
        }
    }.flowOn(Dispatchers.Default).distinctUntilChanged()

    fun observeFeed(): Flow<List<FeedPostModel>> = feed.observeAll().map { list ->
        list.map {
            FeedPostModel(
                id = it.id,
                title = it.title,
                body = it.body,
                icon = it.icon,
                accent = it.accent,
                createdAt = it.createdAt,
            )
        }
    }.flowOn(Dispatchers.Default).distinctUntilChanged()

    fun observeMessages(chatId: String, currentUserId: String): Flow<List<MessageModel>> = combine(
        messages.observeByChat(chatId),
        database.userNicknameOverrideDao().observeAllOverrides()
    ) { list, overrides ->
        val overrideMap = overrides.associateBy { it.userId }
        list.map {
            val senderName = overrideMap[it.senderId]?.displayName ?: it.senderName
            MessageModel(
                id = it.id,
                chatId = it.chatId,
                senderId = it.senderId,
                senderName = senderName,
                text = deviceCrypto.decrypt(it.bodyCiphertext),
                media = it.toMediaModel(),
                replyToId = it.replyToId,
                createdAt = it.createdAt,
                editedAt = it.editedAt,
                deletedAt = it.deletedAt,
                status = it.status,
                isOwn = it.senderId == currentUserId,
            )
        }
    }.flowOn(Dispatchers.Default).distinctUntilChanged()

    suspend fun getCachedMessages(chatId: String, currentUserId: String, limit: Int = 15): List<MessageModel> =
        messages.latestByChat(chatId, limit)
            .asReversed()
            .map {
                MessageModel(
                    id = it.id,
                    chatId = it.chatId,
                    senderId = it.senderId,
                    senderName = it.senderName,
                    text = deviceCrypto.decrypt(it.bodyCiphertext),
                    media = it.toMediaModel(),
                    replyToId = it.replyToId,
                    createdAt = it.createdAt,
                    editedAt = it.editedAt,
                    deletedAt = it.deletedAt,
                    status = it.status,
                    isOwn = it.senderId == currentUserId,
                )
            }
            .let { applyNicknameOverrides(it) }

    suspend fun refreshBootstrap() {
        val session = secureStore.currentSession ?: return
        val bootstrap = api.bootstrap()
        applyBootstrap(bootstrap, session.token)
    }

    suspend fun register(
        username: String,
        password: String,
        language: AppLanguage,
        nickname: String,
        deviceName: String,
    ) {
        val response = api.register(
            RegisterRequest(
                username = username,
                password = password,
                language = language.code,
                nickname = nickname,
                deviceName = deviceName,
            ),
        )
        applyBootstrap(response.bootstrap, response.token)
    }

    suspend fun login(username: String, password: String, deviceName: String) {
        val response = api.login(LoginRequest(username, password, deviceName))
        applyBootstrap(response.bootstrap, response.token)
    }

    suspend fun logout() {
        runCatching { api.logout() }
        secureStore.clearSession()
        clearLocalData()
    }

    suspend fun logoutOthers() {
        api.logoutOthers()
    }

    suspend fun changePassword(currentPassword: String, newPassword: String) {
        api.changePassword(ChangePasswordRequest(currentPassword = currentPassword, newPassword = newPassword))
    }

    suspend fun deleteAccount() {
        runCatching { api.deleteAccount() }
        secureStore.clearSession()
        clearLocalData()
    }

    suspend fun updateProfile(
        username: String,
        nickname: String,
        about: String,
        language: AppLanguage,
        avatarUrl: String,
        avatarThumbUrl: String,
        lastSeenVisibility: String,
        usernameDiscoverable: Boolean,
    ) {
        api.updateProfile(
            UpdateProfileRequest(
                username = username,
                nickname = nickname,
                about = about,
                language = language.code,
                avatarUrl = avatarUrl,
                avatarThumbUrl = avatarThumbUrl,
                lastSeenVisibility = lastSeenVisibility,
                usernameDiscoverable = usernameDiscoverable,
            ),
        )
        refreshBootstrap()
    }

    suspend fun searchUsers(query: String): List<SearchUserModel> {
        return api.searchUsers(query).map {
            SearchUserModel(
                id = it.id,
                username = it.username,
                nickname = it.nickname,
                avatarThumbUrl = it.avatarThumbUrl,
                isOnline = it.isOnline,
            )
        }
    }

    suspend fun addContact(userId: String? = null, username: String? = null): String {
        val result = api.addContact(AddContactRequest(userId = userId, username = username))
        refreshContacts()
        refreshChats()
        return result.chatId
    }

    suspend fun removeContact(userId: String) {
        api.removeContact(userId)
        refreshContacts()
        refreshChats()
    }

    suspend fun toggleBlock(userId: String, blocked: Boolean) {
        api.blockContact(userId, BlockContactRequest(blocked))
        refreshContacts()
    }

    suspend fun ensureDirectChat(userId: String): String {
        val result = api.ensureDirectChat(EnsureDirectChatRequest(userId))
        refreshChats()
        return result.chatId
    }

    suspend fun refreshChats(query: String? = null) {
        val list = api.chats(query = query, archived = true)
        database.withTransaction {
            chats.upsertAll(list.map { it.toEntity(deviceCrypto) })
        }
    }

    suspend fun refreshContacts() {
        val list = api.contacts()
        database.withTransaction {
            contacts.clear()
            contacts.upsertAll(list.map { it.toEntity() })
        }
    }

    suspend fun refreshFeed() {
        val list = api.feed()
        database.withTransaction {
            feed.clear()
            feed.upsertAll(list.map { FeedPostEntity(it.id, it.title, it.body, it.icon, it.accent, it.createdAt) })
        }
    }

    suspend fun loadMessages(
        chatId: String,
        before: Long? = null,
        limit: Int = 15,
        replace: Boolean = before == null,
        markRead: Boolean = true,
    ): Int {
        val list = api.messages(chatId, before, limit)
        database.withTransaction {
            if (replace && list.isNotEmpty()) {
                messages.deleteForChat(chatId)
            }
            messages.upsertAll(list.map { it.toEntity(deviceCrypto) })
        }
        if (markRead) {
            markRead(chatId)
        }
        return list.size
    }

    suspend fun uploadImage(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
        purpose: String = "chat",
        onProgress: (Float) -> Unit = {},
    ): MediaModel = withContext(Dispatchers.IO) {
        val mediaType = runCatching { mimeType.toMediaType() }.getOrDefault("image/jpeg".toMediaType())
        val body = ProgressRequestBody(bytes, mediaType, onProgress)
        val part = MultipartBody.Part.createFormData("file", fileName, body)
        val uploaded = api.uploadImage(part, purpose.toRequestBody("text/plain".toMediaType()))
        uploaded.toModel()
    }

    suspend fun sendMessage(chatId: String, text: String, replyToId: String? = null, media: MediaModel? = null): ApiMessage {
        val message = api.sendMessage(chatId, SendMessageRequest(text = text, media = media?.toApi(), replyToId = replyToId))
        messages.upsert(message.toEntity(deviceCrypto))
        updateChatPreviewLocally(message)
        return message
    }

    suspend fun insertLocalTextMessage(chatId: String, localId: String, senderId: String, senderName: String, text: String, replyToId: String?) {
        val createdAt = System.currentTimeMillis() / 1000
        val entity = MessageEntity(
            id = localId,
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            bodyCiphertext = deviceCrypto.encrypt(text),
            mediaUrl = "",
            mediaPreviewUrl = "",
            mediaThumbUrl = "",
            mediaMimeType = "",
            mediaFileName = "",
            mediaWidth = 0,
            mediaHeight = 0,
            mediaSizeBytes = 0L,
            replyToId = replyToId,
            createdAt = createdAt,
            editedAt = 0L,
            deletedAt = 0L,
            status = "sent",
        )
        messages.upsert(entity)
        chats.getById(chatId)?.let { existing ->
            chats.upsert(
                existing.copy(
                    lastMessageId = localId,
                    lastMessagePreviewCiphertext = deviceCrypto.encrypt(text),
                    lastMessageCreatedAt = createdAt,
                    lastMessageSenderId = senderId,
                    lastMessageStatus = "sent",
                    updatedAt = createdAt,
                    unreadCount = 0,
                    isMarkedUnread = false,
                    pinnedSort = if (existing.isPinned) createdAt else 0L,
                ),
            )
        }
    }

    suspend fun replaceLocalMessage(localId: String, remote: ApiMessage) {
        messages.deleteById(localId)
        messages.upsert(remote.toEntity(deviceCrypto))
        updateChatPreviewLocally(remote)
    }

    suspend fun deleteLocalMessage(localId: String) {
        messages.deleteById(localId)
    }

    suspend fun editMessage(messageId: String, text: String) {
        val message = api.updateMessage(messageId, UpdateMessageRequest(text))
        messages.upsert(message.toEntity(deviceCrypto))
        refreshChats()
    }

    suspend fun deleteMessage(messageId: String) {
        api.deleteMessage(messageId)
        messages.getById(messageId)?.let { existing ->
            messages.upsert(
                existing.copy(
                    bodyCiphertext = "",
                    mediaUrl = "",
                    mediaPreviewUrl = "",
                    mediaThumbUrl = "",
                    mediaMimeType = "",
                    mediaFileName = "",
                    mediaWidth = 0,
                    mediaHeight = 0,
                    mediaSizeBytes = 0L,
                    deletedAt = System.currentTimeMillis() / 1000,
                ),
            )
        }
        refreshChats()
    }

    suspend fun clearChat(chatId: String) {
        api.clearChat(chatId)
        messages.deleteForChat(chatId)
        refreshChats()
    }

    suspend fun previewMessages(chatId: String): List<MessageModel> {
        val currentUserId = secureStore.currentSession?.userId.orEmpty()
        return applyNicknameOverrides(
            api.messages(chatId, limit = 8).map { dto -> dto.toModel(deviceCrypto, currentUserId) },
        )
    }

    suspend fun searchMessages(chatId: String, query: String): List<MessageModel> {
        val currentUserId = secureStore.currentSession?.userId.orEmpty()
        return applyNicknameOverrides(
            api.searchMessages(chatId, query).map { dto ->
                val entity = dto.toEntity(deviceCrypto)
                MessageModel(
                    id = entity.id,
                    chatId = entity.chatId,
                    senderId = entity.senderId,
                    senderName = entity.senderName,
                    text = deviceCrypto.decrypt(entity.bodyCiphertext),
                    media = entity.toMediaModel(),
                    replyToId = entity.replyToId,
                    createdAt = entity.createdAt,
                    editedAt = entity.editedAt,
                    deletedAt = entity.deletedAt,
                    status = entity.status,
                    isOwn = entity.senderId == currentUserId,
                )
            },
        )
    }

    suspend fun setChatArchived(chatId: String, enabled: Boolean) {
        api.archiveChat(chatId, EnabledRequest(enabled))
        refreshChats()
    }

    suspend fun setChatPinned(chatId: String, enabled: Boolean) {
        val existing = chats.getById(chatId)
        val pinnedSort = if (enabled) (existing?.updatedAt ?: (System.currentTimeMillis() / 1000)) else 0L
        chats.updatePinned(chatId, enabled, pinnedSort)
        api.pinChat(chatId, EnabledRequest(enabled))
        refreshChats()
    }

    suspend fun setChatMuted(chatId: String, enabled: Boolean) {
        api.muteChat(chatId, EnabledRequest(enabled))
        refreshChats()
    }

    suspend fun setChatMarkedUnread(chatId: String, enabled: Boolean) {
        chats.updateMarkedUnread(chatId, enabled)
        api.markUnread(chatId, EnabledRequest(enabled))
    }

    suspend fun markRead(chatId: String, notificationContext: Context? = null) {
        chats.markReadLocally(chatId)
        runCatching { api.markRead(chatId) }
        notificationContext?.let { SkytaleNotificationManager.cancelChatNotifications(it, chatId) }
    }

    suspend fun markReadLocally(chatId: String, notificationContext: Context? = null) {
        chats.markReadLocally(chatId)
        notificationContext?.let { SkytaleNotificationManager.cancelChatNotifications(it, chatId) }
    }

    suspend fun setTyping(chatId: String, isTyping: Boolean) {
        runCatching { api.typing(TypingRequest(chatId = chatId, isTyping = isTyping)) }
    }

    fun setActiveChat(chatId: String?) {
        activeChatState.value = chatId
        if (chatId != null) {
            typingState.value = typingState.value - chatId
        }
    }

    fun setAppForeground(isForeground: Boolean) {
        appForegroundState.value = isForeground
    }

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        secureStore.saveSettings(transform(settingsFlow.value))
    }

    fun startRealtime(scope: CoroutineScope) {
        if (realtimeJob?.isActive == true) return
        realtimeJob = scope.launch(Dispatchers.IO) {
            var backoffMs = 1000L
            while (isActive) {
                val session = secureStore.currentSession ?: break
                val closed = MutableStateFlow(false)
                val request = Request.Builder()
                    .url(BuildConfig.WEBSOCKET_URL)
                    .header("Authorization", "Bearer ${session.token}")
                    .build()
                realtimeSocket = realtimeClient.newWebSocket(request, object : WebSocketListener() {
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        scope.launch(Dispatchers.IO) {
                            runCatching { processRealtime(text) }
                        }
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        closed.value = true
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        closed.value = true
                    }
                })
                while (isActive && !closed.value) {
                    delay(500L)
                }
                realtimeSocket = null
                delay(backoffMs)
                backoffMs = (backoffMs * 2).coerceAtMost(15_000L)
            }
        }
    }

    fun stopRealtime() {
        realtimeJob?.cancel()
        realtimeJob = null
        realtimeSocket?.close(1000, "app stopped")
        realtimeSocket = null
    }

    suspend fun processRealtime(raw: String): IncomingAlert? {
        val envelope = json.decodeFromString<ApiRealtimeEnvelope>(raw)
        return when (envelope.type) {
            "message.created" -> {
                val message = json.decodeFromJsonElement<ApiMessage>(envelope.data)
                messages.upsert(message.toEntity(deviceCrypto))
                val session = secureStore.currentSession
                val activeChatVisible = appForegroundState.value && activeChatState.value == message.chatId
                if (activeChatVisible && message.senderId != session?.userId && session?.lastSeenVisibility != "ghost") {
                    runCatching { api.markRead(message.chatId) }
                    chats.markReadLocally(message.chatId)
                    return null
                }
                updateChatPreviewLocally(message)
                if (session != null && message.senderId != session.userId && !activeChatVisible) {
                    val chat = chats.getById(message.chatId)
                    IncomingAlert(
                        messageId = message.id,
                        chatId = message.chatId,
                        title = resolveNotificationTitle(chat, message),
                        body = message.text.ifBlank { if (message.media != null) "Image" else "" },
                        senderName = message.sender?.nickname ?: message.sender?.username ?: resolveNotificationTitle(chat, message),
                        senderAvatarUrl = message.sender?.avatarThumbUrl?.ifBlank { message.sender?.avatarUrl }.orEmpty(),
                        conversationAvatarUrl = chat?.peerAvatarThumbUrl?.ifBlank { chat.peerAvatarUrl }.orEmpty(),
                        isGroupConversation = chat?.type != "direct",
                    ).also { incomingAlertEvents.tryEmit(it) }
                } else {
                    null
                }
            }
            "message.updated" -> {
                val message = json.decodeFromJsonElement<ApiMessage>(envelope.data)
                messages.upsert(message.toEntity(deviceCrypto))
                refreshChats()
                null
            }
            "message.deleted" -> {
                val payload = json.decodeFromJsonElement<DeleteMessageEvent>(envelope.data)
                messages.getById(payload.id)?.let { existing ->
                    messages.upsert(existing.copy(bodyCiphertext = "", deletedAt = System.currentTimeMillis() / 1000))
                }
                refreshChats()
                null
            }
            "chat.cleared" -> {
                val payload = json.decodeFromJsonElement<ChatClearedEvent>(envelope.data)
                messages.deleteForChat(payload.chatId)
                refreshChats()
                null
            }
            "presence" -> {
                val payload = json.decodeFromJsonElement<PresenceEvent>(envelope.data)
                contacts.updatePresence(payload.userId, payload.isOnline, payload.lastSeenAt)
                chats.updatePresence(payload.userId, payload.isOnline, payload.lastSeenAt)
                null
            }
            "typing" -> {
                val payload = json.decodeFromJsonElement<TypingEvent>(envelope.data)
                val currentUserId = secureStore.currentSession?.userId
                if (payload.userId == currentUserId) {
                    return null
                }
                typingState.value = if (payload.isTyping) {
                    typingState.value + (payload.chatId to payload.userId)
                } else {
                    typingState.value - payload.chatId
                }
                null
            }
            "chat.read" -> {
                val payload = json.decodeFromJsonElement<ChatReadEvent>(envelope.data)
                val currentUserId = secureStore.currentSession?.userId.orEmpty()
                if (payload.chatId.isBlank()) {
                    refreshChats()
                } else if (payload.userId == currentUserId || payload.userId.isBlank()) {
                    chats.markReadLocally(payload.chatId)
                } else if (currentUserId.isNotBlank()) {
                    messages.markMessagesReadBySender(payload.chatId, currentUserId)
                    chats.markLastMessageRead(payload.chatId, currentUserId)
                }
                null
            }
            else -> null
        }
    }

    suspend fun buildNotificationConversation(chatId: String, limit: Int = 6): NotificationConversation? {
        val chat = chats.getById(chatId) ?: return null
        val currentUserId = secureStore.currentSession?.userId.orEmpty()
        val recentMessages = messages.latestByChat(chatId, limit).asReversed().map { entity ->
            NotificationMessage(
                id = entity.id,
                senderName = entity.senderName,
                body = deviceCrypto.decrypt(entity.bodyCiphertext).ifBlank {
                    if (entity.mediaUrl.isNotBlank()) "[Image]" else ""
                },
                sentAtMillis = entity.createdAt * 1000L,
                isOwn = entity.senderId == currentUserId,
            )
        }
        return NotificationConversation(
            chatId = chatId,
            title = chat.title,
            conversationAvatarUrl = chat.peerAvatarThumbUrl?.ifBlank { chat.peerAvatarUrl }.orEmpty(),
            senderAvatarUrl = chat.peerAvatarThumbUrl?.ifBlank { chat.peerAvatarUrl }.orEmpty(),
            isGroupConversation = chat.type != "direct",
            messages = recentMessages,
        )
    }

    private suspend fun applyBootstrap(bootstrap: ApiBootstrap, token: String) {
        val deviceName = bootstrap.session.deviceName.ifBlank { "Android device" }
        secureStore.saveSession(
                StoredSession(
                    token = token,
                    userId = bootstrap.user.id,
                    username = bootstrap.user.username,
                    nickname = bootstrap.user.nickname,
                    about = bootstrap.user.about,
                    avatarUrl = bootstrap.user.avatarUrl,
                    avatarThumbUrl = bootstrap.user.avatarThumbUrl,
                    lastSeenVisibility = bootstrap.user.lastSeenVisibility,
                    usernameDiscoverable = bootstrap.user.usernameDiscoverable,
                    language = bootstrap.user.language,
                    deviceName = deviceName,
                ),
            )
        secureStore.saveSettings(settingsFlow.value.copy(language = bootstrap.user.language))
        database.withTransaction {
            contacts.clear()
            chats.clear()
            feed.clear()
            messages.clear()
            contacts.upsertAll(bootstrap.contacts.map { it.toEntity() })
            chats.upsertAll(bootstrap.chats.map { it.toEntity(deviceCrypto) })
            feed.upsertAll(bootstrap.feed.map { FeedPostEntity(it.id, it.title, it.body, it.icon, it.accent, it.createdAt) })
        }
    }

    private suspend fun clearLocalData() {
        database.withTransaction {
            contacts.clear()
            chats.clear()
            feed.clear()
            messages.clear()
        }
    }

    private suspend fun updateChatPreviewLocally(message: ApiMessage) {
        val existing = chats.getById(message.chatId) ?: run {
            refreshChats()
            return
        }
        val currentUserId = secureStore.currentSession?.userId
        val isOwn = message.senderId == currentUserId
        val activeChat = activeChatState.value == message.chatId
        val preview = message.text.ifBlank { if (message.media != null) "[Image]" else "" }
        chats.upsert(
            existing.copy(
                lastMessageId = message.id,
                lastMessagePreviewCiphertext = deviceCrypto.encrypt(preview),
                lastMessageCreatedAt = message.createdAt,
                lastMessageSenderId = message.senderId,
                lastMessageStatus = message.status,
                updatedAt = message.createdAt,
                unreadCount = if (activeChat || isOwn) 0 else (existing.unreadCount + 1),
                isMarkedUnread = if (activeChat || isOwn) false else existing.isMarkedUnread,
                pinnedSort = if (existing.isPinned) message.createdAt else 0L,
            ),
        )
    }

    suspend fun setUserNicknameOverride(userId: String, displayName: String) {
        database.userNicknameOverrideDao().upsert(UserNicknameOverrideEntity(userId, displayName))
    }

    suspend fun clearUserNicknameOverride(userId: String) {
        database.userNicknameOverrideDao().deleteByUserId(userId)
    }

    private suspend fun applyNicknameOverrides(items: List<MessageModel>): List<MessageModel> {
        val overrideMap = database.userNicknameOverrideDao().getAll().associateBy { it.userId }
        return items.map { message ->
            val overrideName = overrideMap[message.senderId]?.displayName
            if (overrideName.isNullOrBlank()) {
                message
            } else {
                message.copy(senderName = overrideName)
            }
        }
    }

    private suspend fun resolveNotificationTitle(chat: ChatEntity?, message: ApiMessage): String {
        val peerId = chat?.peerId
        val senderId = message.senderId
        val directOverride = peerId?.let { database.userNicknameOverrideDao().getByUserId(it)?.displayName }
        if (!directOverride.isNullOrBlank()) return directOverride
        val senderOverride = database.userNicknameOverrideDao().getByUserId(senderId)?.displayName
        if (!senderOverride.isNullOrBlank()) return senderOverride
        return chat?.title ?: message.sender?.nickname ?: message.sender?.username ?: "Skytale"
    }
}

@kotlinx.serialization.Serializable
private data class ChatClearedEvent(
    val chatId: String,
)

private fun ApiContact.toEntity(): ContactEntity = ContactEntity(
    id = user.id,
    username = user.username,
    nickname = user.nickname,
    about = user.about,
    language = user.language,
    avatarUrl = user.avatarUrl,
    avatarThumbUrl = user.avatarThumbUrl,
    createdAt = createdAt,
    lastSeenAt = user.lastSeenAt,
    isOnline = user.isOnline,
    alias = alias,
    isBlocked = isBlocked,
    isFavorite = isFavorite,
)

private fun ApiChatSummary.toEntity(deviceCrypto: DeviceCrypto): ChatEntity = ChatEntity(
    id = id,
    type = type,
    title = title,
    peerId = peer?.id,
    peerUsername = peer?.username,
    peerNickname = peer?.nickname,
    peerAbout = peer?.about,
    peerLanguage = peer?.language,
    peerAvatarUrl = peer?.avatarUrl,
    peerAvatarThumbUrl = peer?.avatarThumbUrl,
    peerCreatedAt = peer?.createdAt,
    peerLastSeenAt = peer?.lastSeenAt,
    peerIsOnline = peer?.isOnline ?: false,
    lastMessageId = lastMessage?.id,
    lastMessagePreviewCiphertext = deviceCrypto.encrypt(
        lastMessage?.text?.ifBlank {
            if (lastMessage.media != null) "[Image]" else ""
        }.orEmpty(),
    ),
    lastMessageCreatedAt = lastMessage?.createdAt ?: updatedAt,
    lastMessageSenderId = lastMessage?.senderId,
    lastMessageStatus = lastMessage?.status.orEmpty(),
    updatedAt = updatedAt,
    unreadCount = unreadCount,
    isPinned = isPinned,
    isArchived = isArchived,
    isMuted = isMuted,
    isMarkedUnread = isMarkedUnread,
    pinnedSort = if (isPinned) updatedAt else 0L,
)

private fun ChatEntity.toModel(deviceCrypto: DeviceCrypto): ChatModel = ChatModel(
    id = id,
    type = type,
    title = title,
    peer = peerId?.let {
        UserModel(
            id = it,
            username = peerUsername.orEmpty(),
            nickname = peerNickname.orEmpty(),
            about = peerAbout.orEmpty(),
            language = AppLanguage.fromCode(peerLanguage),
            avatarUrl = peerAvatarUrl.orEmpty(),
            avatarThumbUrl = peerAvatarThumbUrl.orEmpty(),
            createdAt = peerCreatedAt ?: 0L,
            lastSeenAt = peerLastSeenAt ?: 0L,
            isOnline = peerIsOnline,
            lastSeenVisibility = "contacts",
            usernameDiscoverable = true,
        )
    },
    lastMessagePreview = deviceCrypto.decrypt(lastMessagePreviewCiphertext),
    lastMessageCreatedAt = lastMessageCreatedAt,
    lastMessageSenderId = lastMessageSenderId,
    lastMessageStatus = lastMessageStatus,
    updatedAt = updatedAt,
    unreadCount = unreadCount,
    isPinned = isPinned,
    isArchived = isArchived,
    isMuted = isMuted,
    isMarkedUnread = isMarkedUnread,
)

private fun ApiMessage.toModel(deviceCrypto: DeviceCrypto, currentUserId: String): MessageModel {
    val entity = toEntity(deviceCrypto)
    return MessageModel(
        id = entity.id,
        chatId = entity.chatId,
        senderId = entity.senderId,
        senderName = entity.senderName,
        text = deviceCrypto.decrypt(entity.bodyCiphertext),
        media = entity.toMediaModel(),
        replyToId = entity.replyToId,
        createdAt = entity.createdAt,
        editedAt = entity.editedAt,
        deletedAt = entity.deletedAt,
        status = entity.status,
        isOwn = entity.senderId == currentUserId,
    )
}

private fun ApiMessage.toEntity(deviceCrypto: DeviceCrypto): MessageEntity = MessageEntity(
    id = id,
    chatId = chatId,
    senderId = senderId,
    senderName = sender?.nickname ?: sender?.username ?: "Unknown",
    bodyCiphertext = deviceCrypto.encrypt(text),
    mediaUrl = media?.url.orEmpty(),
    mediaPreviewUrl = media?.previewUrl.orEmpty(),
    mediaThumbUrl = media?.thumbUrl.orEmpty(),
    mediaMimeType = media?.mimeType.orEmpty(),
    mediaFileName = media?.fileName.orEmpty(),
    mediaWidth = media?.width ?: 0,
    mediaHeight = media?.height ?: 0,
    mediaSizeBytes = media?.sizeBytes ?: 0L,
    replyToId = replyToId,
    createdAt = createdAt,
    editedAt = editedAt,
    deletedAt = deletedAt,
    status = status,
)

private fun ApiMedia.toModel(): MediaModel = MediaModel(
    kind = kind,
    url = url,
    previewUrl = previewUrl,
    thumbUrl = thumbUrl,
    mimeType = mimeType,
    fileName = fileName,
    width = width,
    height = height,
    sizeBytes = sizeBytes,
)

private fun MediaModel.toApi(): ApiMedia = ApiMedia(
    kind = kind,
    url = url,
    previewUrl = previewUrl,
    thumbUrl = thumbUrl,
    mimeType = mimeType,
    fileName = fileName,
    width = width,
    height = height,
    sizeBytes = sizeBytes,
)

private fun MessageEntity.toMediaModel(): MediaModel? {
    if (mediaUrl.isBlank()) return null
    return MediaModel(
        kind = "image",
        url = mediaUrl,
        previewUrl = mediaPreviewUrl,
        thumbUrl = mediaThumbUrl,
        mimeType = mediaMimeType,
        fileName = mediaFileName,
        width = mediaWidth,
        height = mediaHeight,
        sizeBytes = mediaSizeBytes,
    )
}

private class ProgressRequestBody(
    private val bytes: ByteArray,
    private val mediaType: MediaType,
    private val onProgress: (Float) -> Unit,
) : RequestBody() {
    override fun contentType(): MediaType = mediaType

    override fun contentLength(): Long = bytes.size.toLong()

    override fun writeTo(sink: okio.BufferedSink) {
        val total = bytes.size.toLong().coerceAtLeast(1L)
        val chunkSize = 16 * 1024
        var offset = 0
        while (offset < bytes.size) {
            val count = minOf(chunkSize, bytes.size - offset)
            sink.write(bytes, offset, count)
            offset += count
            onProgress(offset.toFloat() / total.toFloat())
        }
    }
}
