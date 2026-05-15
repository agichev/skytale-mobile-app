package group.skytale.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Part
import retrofit2.http.Query

@Serializable
data class ApiUser(
    val id: String,
    val username: String,
    val nickname: String,
    val about: String = "",
    val language: String = "en",
    val avatarUrl: String = "",
    val avatarThumbUrl: String = "",
    val createdAt: Long = 0L,
    val lastSeenAt: Long = 0L,
    val isOnline: Boolean = false,
    val lastSeenVisibility: String = "contacts",
    val usernameDiscoverable: Boolean = true,
)

@Serializable
data class ApiMedia(
    val kind: String = "image",
    val url: String,
    val previewUrl: String = "",
    val thumbUrl: String = "",
    val mimeType: String = "",
    val fileName: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val sizeBytes: Long = 0L,
)

@Serializable
data class ApiSession(
    val id: String,
    val userId: String,
    val deviceName: String,
    val createdAt: Long,
    val lastSeenAt: Long,
    val expiresAt: Long,
)

@Serializable
data class ApiContact(
    val user: ApiUser,
    val alias: String = "",
    val createdAt: Long = 0L,
    val isBlocked: Boolean = false,
    val isFavorite: Boolean = false,
)

@Serializable
data class ApiMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String = "",
    val media: ApiMedia? = null,
    val replyToId: String? = null,
    val createdAt: Long,
    val editedAt: Long = 0L,
    val deletedAt: Long = 0L,
    val status: String = "sent",
    val sender: ApiUser? = null,
)

@Serializable
data class ApiChatSummary(
    val id: String,
    val type: String,
    val title: String,
    val peer: ApiUser? = null,
    val lastMessage: ApiMessage? = null,
    val updatedAt: Long,
    val unreadCount: Int,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val isMarkedUnread: Boolean = false,
)

@Serializable
data class ApiFeedPost(
    val id: String,
    val title: String,
    val body: String,
    val icon: String,
    val accent: String,
    val createdAt: Long,
)

@Serializable
data class ApiBootstrap(
    val user: ApiUser,
    val session: ApiSession,
    val contacts: List<ApiContact> = emptyList(),
    val chats: List<ApiChatSummary> = emptyList(),
    val feed: List<ApiFeedPost> = emptyList(),
    val sessions: List<ApiSession> = emptyList(),
)

@Serializable
data class ApiAuthResponse(
    val token: String,
    val bootstrap: ApiBootstrap,
)

@Serializable
data class ApiRealtimeEnvelope(
    val type: String,
    val data: JsonElement,
)

@Serializable
data class DeleteMessageEvent(
    val id: String,
    val chatId: String,
)

@Serializable
data class PresenceEvent(
    val userId: String,
    val isOnline: Boolean,
    val lastSeenAt: Long,
)

@Serializable
data class TypingEvent(
    val chatId: String,
    val userId: String,
    val isTyping: Boolean,
)

@Serializable
data class ChatReadEvent(
    val chatId: String = "",
    val userId: String = "",
    val readAt: Long = 0L,
)

interface SkytaleApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiAuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiAuthResponse

    @GET("bootstrap")
    suspend fun bootstrap(): ApiBootstrap

    @POST("auth/logout")
    suspend fun logout(): Map<String, Boolean>

    @POST("auth/logout-others")
    suspend fun logoutOthers(): Map<String, Boolean>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Map<String, Boolean>

    @GET("auth/sessions")
    suspend fun sessions(): List<ApiSession>

    @PATCH("profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiUser

    @Multipart
    @POST("media/images")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("purpose") purpose: RequestBody,
    ): ApiMedia

    @DELETE("account")
    suspend fun deleteAccount(): Map<String, Boolean>

    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): List<ApiUser>

    @POST("contacts")
    suspend fun addContact(@Body request: AddContactRequest): DirectChatResponse

    @GET("contacts")
    suspend fun contacts(): List<ApiContact>

    @DELETE("contacts/{userId}")
    suspend fun removeContact(@Path("userId") userId: String): Map<String, Boolean>

    @POST("contacts/{userId}/block")
    suspend fun blockContact(@Path("userId") userId: String, @Body request: BlockContactRequest): Map<String, Boolean>

    @POST("chats/direct")
    suspend fun ensureDirectChat(@Body request: EnsureDirectChatRequest): DirectChatResponse

    @GET("chats")
    suspend fun chats(
        @Query("q") query: String? = null,
        @Query("archived") archived: Boolean? = null,
    ): List<ApiChatSummary>

    @POST("chats/{chatId}/archive")
    suspend fun archiveChat(@Path("chatId") chatId: String, @Body request: EnabledRequest): Map<String, Boolean>

    @POST("chats/{chatId}/pin")
    suspend fun pinChat(@Path("chatId") chatId: String, @Body request: EnabledRequest): Map<String, Boolean>

    @POST("chats/{chatId}/mute")
    suspend fun muteChat(@Path("chatId") chatId: String, @Body request: EnabledRequest): Map<String, Boolean>

    @POST("chats/{chatId}/mark-unread")
    suspend fun markUnread(@Path("chatId") chatId: String, @Body request: EnabledRequest): Map<String, Boolean>

    @GET("chats/{chatId}/messages")
    suspend fun messages(
        @Path("chatId") chatId: String,
        @Query("before") before: Long? = null,
        @Query("limit") limit: Int? = null,
    ): List<ApiMessage>

    @POST("chats/{chatId}/messages")
    suspend fun sendMessage(@Path("chatId") chatId: String, @Body request: SendMessageRequest): ApiMessage

    @POST("chats/{chatId}/read")
    suspend fun markRead(@Path("chatId") chatId: String): Map<String, Boolean>

    @GET("chats/{chatId}/search")
    suspend fun searchMessages(@Path("chatId") chatId: String, @Query("q") query: String): List<ApiMessage>

    @PATCH("messages/{messageId}")
    suspend fun updateMessage(@Path("messageId") messageId: String, @Body request: UpdateMessageRequest): ApiMessage

    @DELETE("messages/{messageId}")
    suspend fun deleteMessage(@Path("messageId") messageId: String): Map<String, Boolean>

    @DELETE("chats/{chatId}/messages")
    suspend fun clearChat(@Path("chatId") chatId: String): Map<String, Boolean>

    @GET("feed")
    suspend fun feed(): List<ApiFeedPost>

    @POST("realtime/typing")
    suspend fun typing(@Body request: TypingRequest): Map<String, Boolean>
}

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val language: String,
    val nickname: String,
    val deviceName: String,
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val deviceName: String,
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)

@Serializable
data class UpdateProfileRequest(
    val username: String,
    val nickname: String,
    val about: String,
    val language: String,
    val avatarUrl: String = "",
    val avatarThumbUrl: String = "",
    val lastSeenVisibility: String,
    val usernameDiscoverable: Boolean,
)

@Serializable
data class AddContactRequest(
    val userId: String? = null,
    val username: String? = null,
)

@Serializable
data class EnsureDirectChatRequest(
    val userId: String,
)

@Serializable
data class SendMessageRequest(
    val text: String,
    val media: ApiMedia? = null,
    @SerialName("replyToId") val replyToId: String? = null,
)

@Serializable
data class UpdateMessageRequest(
    val text: String,
)

@Serializable
data class EnabledRequest(
    val enabled: Boolean,
)

@Serializable
data class BlockContactRequest(
    val blocked: Boolean,
)

@Serializable
data class DirectChatResponse(
    val chatId: String,
)

@Serializable
data class TypingRequest(
    val chatId: String,
    val isTyping: Boolean,
)
