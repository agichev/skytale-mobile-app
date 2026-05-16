package group.skytale.app.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
enum class AppLanguage(val code: String) {
    EN("en"),
    RU("ru");

    companion object {
        fun fromCode(code: String?): AppLanguage = entries.firstOrNull { it.code == code } ?: EN
    }
}

@Immutable
enum class AuthMode {
    REGISTER,
    LOGIN,
}

@Immutable
enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT,
    MONO_DARK,
    LIGHT_CLASSIC,
    TELEGRAM_DARK,
}

@Immutable
enum class HomeTab {
    CHATS,
    FEED,
    SETTINGS,
    PROFILE,
}

@Serializable
@Immutable
data class StoredSession(
    val token: String,
    val userId: String,
    val username: String,
    val nickname: String,
    val about: String = "",
    val avatarUrl: String = "",
    val avatarThumbUrl: String = "",
    val lastSeenVisibility: String = "contacts",
    val usernameDiscoverable: Boolean = true,
    val language: String,
    val deviceName: String,
)

@Serializable
@Immutable
data class AppSettings(
    val language: String = AppLanguage.EN.code,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val notificationsEnabled: Boolean = true,
    val previewsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val instantSyncEnabled: Boolean = false,
    val instantSyncConfigured: Boolean = false,
    val appLockEnabled: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val compactMode: Boolean = false,
    val showArchivedChats: Boolean = false,
)

@Immutable
data class UserModel(
    val id: String,
    val username: String,
    val nickname: String,
    val about: String,
    val language: AppLanguage,
    val avatarUrl: String = "",
    val avatarThumbUrl: String = "",
    val createdAt: Long,
    val lastSeenAt: Long,
    val isOnline: Boolean,
    val lastSeenVisibility: String = "contacts",
    val usernameDiscoverable: Boolean = true,
)

@Immutable
data class MediaModel(
    val kind: String,
    val url: String,
    val previewUrl: String = "",
    val thumbUrl: String = "",
    val mimeType: String = "",
    val fileName: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val sizeBytes: Long = 0L,
)

@Immutable
data class SessionModel(
    val id: String,
    val userId: String,
    val deviceName: String,
    val createdAt: Long,
    val lastSeenAt: Long,
    val expiresAt: Long,
)

@Immutable
data class ContactModel(
    val user: UserModel,
    val alias: String,
    val createdAt: Long,
    val isBlocked: Boolean,
    val isFavorite: Boolean,
)

@Immutable
data class MessageModel(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val media: MediaModel? = null,
    val replyToId: String?,
    val createdAt: Long,
    val editedAt: Long,
    val deletedAt: Long,
    val status: String,
    val isOwn: Boolean,
    val commentCount: Int = 0,
    val viewCount: Int = 0,
    val isPinned: Boolean = false,
    val forwardedFromChatId: String = "",
    val forwardedFromUsername: String = "",
    val forwardedFromTitle: String = "",
)

@Immutable
data class ChatModel(
    val id: String,
    val type: String,
    val title: String,
    val peer: UserModel?,
    val username: String = "",
    val description: String = "",
    val avatarUrl: String = "",
    val avatarThumbUrl: String = "",
    val memberCount: Int = 0,
    val canPost: Boolean = true,
    val canManage: Boolean = false,
    val postingPolicy: String = "admins",
    val commentsEnabled: Boolean = true,
    val pinnedPostId: String = "",
    val lastMessagePreview: String,
    val lastMessageCreatedAt: Long,
    val lastMessageSenderId: String?,
    val lastMessageStatus: String,
    val updatedAt: Long,
    val unreadCount: Int,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val isMuted: Boolean,
    val isMarkedUnread: Boolean,
)

@Immutable
data class FeedPostModel(
    val id: String,
    val title: String,
    val body: String,
    val icon: String,
    val accent: String,
    val createdAt: Long,
)

@Immutable
data class SearchUserModel(
    val id: String,
    val username: String,
    val nickname: String,
    val avatarThumbUrl: String = "",
    val isOnline: Boolean,
)

@Immutable
data class DirectoryEntryModel(
    val kind: String,
    val id: String,
    val username: String,
    val title: String,
    val description: String,
    val avatarUrl: String = "",
    val avatarThumbUrl: String = "",
    val memberCount: Int = 0,
    val isOnline: Boolean = false,
)

@Immutable
data class ChannelMemberModel(
    val user: UserModel,
    val role: String,
    val joinedAt: Long,
    val isMuted: Boolean,
)

@Immutable
data class IncomingAlert(
    val messageId: String,
    val chatId: String,
    val title: String,
    val body: String,
    val senderName: String,
    val senderAvatarUrl: String = "",
    val conversationAvatarUrl: String = "",
    val isGroupConversation: Boolean = false,
)

@Immutable
data class NotificationMessage(
    val id: String,
    val senderName: String,
    val body: String,
    val sentAtMillis: Long,
    val isOwn: Boolean,
)

@Immutable
data class NotificationConversation(
    val chatId: String,
    val title: String,
    val conversationAvatarUrl: String = "",
    val senderAvatarUrl: String = "",
    val isGroupConversation: Boolean = false,
    val messages: List<NotificationMessage> = emptyList(),
)
