package group.skytale.app.sync

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import group.skytale.app.BuildConfig
import group.skytale.app.MainActivity
import group.skytale.app.R
import group.skytale.app.SkytaleApp
import group.skytale.app.data.IncomingAlert
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SkytaleSyncService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val reconnectJob = Job()

    private val repository by lazy { (application as SkytaleApp).graph.repository }
    private val secureStore by lazy { (application as SkytaleApp).graph.secureStore }
    private val client by lazy {
        OkHttpClient.Builder()
            .pingInterval(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Volatile
    private var webSocket: WebSocket? = null

    override fun onCreate() {
        super.onCreate()
        createChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(ONGOING_NOTIFICATION_ID, ongoingNotification())
        serviceScope.launch { connectLoop() }
        return START_STICKY
    }

    override fun onDestroy() {
        webSocket?.close(1000, "service stopped")
        reconnectJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private suspend fun connectLoop() {
        var backoffMs = 1000L
        while (serviceScope.isActive) {
            val session = secureStore.currentSession
            val settings = secureStore.settingsFlow.value
            if (session == null || !settings.instantSyncEnabled) {
                stopSelf()
                return
            }
            val request = Request.Builder()
                .url(BuildConfig.WEBSOCKET_URL)
                .header("Authorization", "Bearer ${session.token}")
                .build()
            val socket = client.newWebSocket(request, socketListener())
            webSocket = socket
            delay(backoffMs)
            backoffMs = (backoffMs * 2).coerceAtMost(30_000L)
        }
    }

    private fun socketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            serviceScope.launch {
                val alert = runCatching { repository.processRealtime(text) }.getOrNull()
                if (alert != null && secureStore.settingsFlow.value.notificationsEnabled) {
                    showIncomingNotification(alert)
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
        }
    }

    private fun ongoingNotification(): Notification {
        val openApp = PendingIntent.getActivity(
            this,
            100,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        return NotificationCompat.Builder(this, CHANNEL_BACKGROUND_SYNC)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Skytale")
            .setContentText("Waiting message")
            .setContentIntent(openApp)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun showIncomingNotification(alert: IncomingAlert) {
        val intent = Intent(this, MainActivity::class.java).putExtra("chatId", alert.chatId)
        val pendingIntent = PendingIntent.getActivity(
            this,
            alert.chatId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_MESSAGES)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle(alert.title)
            .setContentText(
                if (secureStore.settingsFlow.value.previewsEnabled) alert.body else "New message",
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setOnlyAlertOnce(true)
        if (secureStore.settingsFlow.value.soundEnabled) {
            builder.setSound(android.net.Uri.parse("android.resource://$packageName/${R.raw.notification_sound_fx}"))
        }
        NotificationManagerCompat.from(this).notify(alert.chatId.hashCode(), builder.build())
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CHANNEL_BACKGROUND_SYNC,
                    "Background sync",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "Persistent sync channel for instant delivery"
                    setShowBadge(false)
                },
                NotificationChannel(
                    CHANNEL_MESSAGES,
                    "Messages",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Incoming Skytale messages"
                },
            ),
        )
    }

    companion object {
        private const val CHANNEL_BACKGROUND_SYNC = "skytale_background_sync"
        private const val CHANNEL_MESSAGES = "skytale_messages"
        private const val ONGOING_NOTIFICATION_ID = 11
    }
}
