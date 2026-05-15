package group.skytale.app.sync

import android.content.Context
import androidx.core.app.NotificationManagerCompat

object SkytaleNotificationManager {
    fun notificationIdForChat(chatId: String): Int = chatId.hashCode()

    fun cancelChatNotifications(context: Context, chatId: String) {
        NotificationManagerCompat.from(context).cancel(notificationIdForChat(chatId))
    }
}
