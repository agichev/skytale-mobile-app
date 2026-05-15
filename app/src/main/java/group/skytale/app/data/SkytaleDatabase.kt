package group.skytale.app.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val username: String,
    val nickname: String,
    val about: String,
    val language: String,
    val avatarUrl: String,
    val avatarThumbUrl: String,
    val createdAt: Long,
    val lastSeenAt: Long,
    val isOnline: Boolean,
    val alias: String,
    val isBlocked: Boolean,
    val isFavorite: Boolean,
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val peerId: String?,
    val peerUsername: String?,
    val peerNickname: String?,
    val peerAbout: String?,
    val peerLanguage: String?,
    val peerAvatarUrl: String?,
    val peerAvatarThumbUrl: String?,
    val peerCreatedAt: Long?,
    val peerLastSeenAt: Long?,
    val peerIsOnline: Boolean,
    val lastMessageId: String?,
    val lastMessagePreviewCiphertext: String,
    val lastMessageCreatedAt: Long,
    val lastMessageSenderId: String?,
    val lastMessageStatus: String,
    val updatedAt: Long,
    val unreadCount: Int,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val isMuted: Boolean,
    val isMarkedUnread: Boolean,
    val pinnedSort: Long,
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val bodyCiphertext: String,
    val mediaUrl: String,
    val mediaPreviewUrl: String,
    val mediaThumbUrl: String,
    val mediaMimeType: String,
    val mediaFileName: String,
    val mediaWidth: Int,
    val mediaHeight: Int,
    val mediaSizeBytes: Long,
    val replyToId: String?,
    val createdAt: Long,
    val editedAt: Long,
    val deletedAt: Long,
    val status: String,
)

@Entity(tableName = "feed_posts")
data class FeedPostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val icon: String,
    val accent: String,
    val createdAt: Long,
)

@Entity(tableName = "user_nickname_overrides", indices = [Index(value = ["userId"])])
data class UserNicknameOverrideEntity(
    @PrimaryKey val userId: String,
    val displayName: String,
)

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY lower(nickname), lower(username)")
    fun observeAll(): Flow<List<ContactEntity>>

    @Query("DELETE FROM contacts")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ContactEntity>)

    @Query("UPDATE contacts SET isOnline = :isOnline, lastSeenAt = :lastSeenAt WHERE id = :userId")
    suspend fun updatePresence(userId: String, isOnline: Boolean, lastSeenAt: Long)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY CASE WHEN isPinned THEN 0 ELSE 1 END, pinnedSort DESC, updatedAt DESC")
    fun observeAll(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    suspend fun getById(chatId: String): ChatEntity?

    @Query("DELETE FROM chats")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ChatEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ChatEntity)

    @Query("UPDATE chats SET peerIsOnline = :isOnline, peerLastSeenAt = :lastSeenAt WHERE peerId = :userId")
    suspend fun updatePresence(userId: String, isOnline: Boolean, lastSeenAt: Long)

    @Query("UPDATE chats SET unreadCount = 0, isMarkedUnread = 0 WHERE id = :chatId")
    suspend fun markReadLocally(chatId: String)

    @Query("UPDATE chats SET lastMessageStatus = 'read' WHERE id = :chatId AND lastMessageSenderId = :senderId")
    suspend fun markLastMessageRead(chatId: String, senderId: String)

    @Query("UPDATE chats SET isPinned = :enabled, pinnedSort = :pinnedSort WHERE id = :chatId")
    suspend fun updatePinned(chatId: String, enabled: Boolean, pinnedSort: Long)

    @Query("UPDATE chats SET isMarkedUnread = :enabled WHERE id = :chatId")
    suspend fun updateMarkedUnread(chatId: String, enabled: Boolean)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC")
    fun observeByChat(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun latestByChat(chatId: String, limit: Int): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE id = :messageId LIMIT 1")
    suspend fun getById(messageId: String): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: MessageEntity)

    @Query("DELETE FROM messages")
    suspend fun clear()

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteById(messageId: String)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteForChat(chatId: String)

    @Query("UPDATE messages SET status = 'read' WHERE chatId = :chatId AND senderId = :senderId AND status != 'read'")
    suspend fun markMessagesReadBySender(chatId: String, senderId: String)
}

@Dao
interface FeedDao {
    @Query("SELECT * FROM feed_posts ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<FeedPostEntity>>

    @Query("DELETE FROM feed_posts")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<FeedPostEntity>)
}

@Dao
interface UserNicknameOverrideDao {
    @Query("SELECT * FROM user_nickname_overrides WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): UserNicknameOverrideEntity?

    @Query("SELECT * FROM user_nickname_overrides")
    suspend fun getAll(): List<UserNicknameOverrideEntity>

    @Query("SELECT * FROM user_nickname_overrides")
    fun observeAllOverrides(): Flow<List<UserNicknameOverrideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: UserNicknameOverrideEntity)

    @Query("DELETE FROM user_nickname_overrides WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)
}

@Database(
    entities = [ContactEntity::class, ChatEntity::class, MessageEntity::class, FeedPostEntity::class, UserNicknameOverrideEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class SkytaleDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun feedDao(): FeedDao
    abstract fun userNicknameOverrideDao(): UserNicknameOverrideDao
}
