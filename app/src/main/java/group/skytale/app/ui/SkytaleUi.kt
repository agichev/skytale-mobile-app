package group.skytale.app.ui

import android.app.Activity
import android.Manifest
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Forward
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.MarkChatUnread
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import group.skytale.app.BuildConfig
import group.skytale.app.R
import group.skytale.app.SkytaleApp
import group.skytale.app.SkytaleUiState
import group.skytale.app.data.AppLanguage
import group.skytale.app.data.AuthMode
import group.skytale.app.data.ChatModel
import group.skytale.app.data.ContactModel
import group.skytale.app.data.DirectoryEntryModel
import group.skytale.app.data.HomeTab
import group.skytale.app.data.MessageModel
import group.skytale.app.data.MediaModel
import group.skytale.app.data.SearchUserModel
import group.skytale.app.SoundEffect
import group.skytale.app.data.ThemeMode
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

@Composable
fun SkytaleRoot(
    state: SkytaleUiState,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    onAuthModeSelected: (AuthMode) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onNicknameChanged: (String) -> Unit,
    onAboutChanged: (String) -> Unit,
    onSelectComposerMedia: (String, String, String) -> Unit,
    onClearComposerMedia: () -> Unit,
    onUpdateComposerMediaOptions: (Boolean, Boolean) -> Unit,
    onCancelPendingUpload: (String) -> Unit,
    onSelectProfileAvatar: (String, String, String) -> Unit,
    onSelectChannelAvatar: (String, String, String, String) -> Unit,
    onClearProfileAvatar: () -> Unit,
    onGeneratePassword: () -> Unit,
    onSubmitAuth: () -> Unit,
    onSelectTab: (HomeTab) -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenContactChat: (String) -> Unit,
    onSaveContactNickname: (String, String) -> Unit,
    onCloseChat: () -> Unit,
    onSendMessage: (String) -> Unit,
    onEditMessage: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onReplyMessage: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSearchPeopleQueryChanged: (String) -> Unit,
    onChatListQueryChanged: (String) -> Unit,
    onRefreshChats: () -> Unit,
    onSearchPeople: () -> Unit,
    onSearchDirectory: () -> Unit,
    onAddContact: (String) -> Unit,
    onOpenDirectoryEntry: (DirectoryEntryModel) -> Unit,
    onCreateChannel: (String, String, String, Boolean) -> Unit,
    onOpenContactPreview: (String) -> Unit,
    onCloseContactPreview: () -> Unit,
    onOpenPreviewChat: () -> Unit,
    onMarkPreviewChatUnread: () -> Unit,
    onToggleArchive: (String, Boolean) -> Unit,
    onTogglePin: (String, Boolean) -> Unit,
    onToggleMute: (String, Boolean) -> Unit,
    onToggleUnread: (String, Boolean) -> Unit,
    onRemoveCurrentContact: () -> Unit,
    onClearCurrentChat: () -> Unit,
    onToggleCurrentBlocked: () -> Unit,
    onUpdateNotifications: (Boolean) -> Unit,
    onUpdatePreviews: (Boolean) -> Unit,
    onUpdateSound: (Boolean) -> Unit,
    onUpdateInstantSync: (Boolean) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onSettingsLanguageSelected: (AppLanguage) -> Unit,
    onUpdateCompact: (Boolean) -> Unit,
    onUpdateHaptics: (Boolean) -> Unit,
    onUpdateArchivedChats: (Boolean) -> Unit,
    onUpdateAppLock: (Boolean) -> Unit,
    onUpdateGhostMode: (Boolean) -> Unit,
    onLastSeenVisibilityChanged: (String) -> Unit,
    onUsernameDiscoverableChanged: (Boolean) -> Unit,
    onSaveProfile: () -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    onLogoutOthers: () -> Unit,
    onChatSearchChanged: (String) -> Unit,
    onSearchMessages: () -> Unit,
    onEnsureMessageLoaded: (String, Long) -> Unit,
    onLoadOlderMessages: () -> Unit,
    onTypingChanged: (Boolean) -> Unit,
    onLoadChannelMembers: (String) -> Unit,
    onUpdateChannelRole: (String, String, String) -> Unit,
    onUpdateChannel: (String, String, String, String, String, Boolean) -> Unit,
    onLeaveChannel: (String) -> Unit,
    onDeleteChannel: (String) -> Unit,
    onForwardChannelPost: (String, String) -> Unit,
    onPinChannelPost: (String, String?) -> Unit,
    onLoadChannelComments: (String, String) -> Unit,
    onCloseChannelComments: () -> Unit,
    onSendChannelComment: (String, String, String) -> Unit,
    onDismissErrorDialogs: () -> Unit,
    onConsumeSoundEvent: () -> Unit,
) {
    val strings = LocalSkytaleStrings.current
    val context = LocalContext.current
    var legalDocumentDialog by remember { mutableStateOf<LegalDocumentType?>(null) }
    BackHandler(enabled = state.session != null && state.openedChatId != null) {
        onCloseChat()
    }
    BackHandler(enabled = state.session == null && state.authStep > 0) {
        onBack()
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(state.pendingSoundEvent?.nonce) {
        val event = state.pendingSoundEvent ?: return@LaunchedEffect
        val soundRes = when (event.type) {
            SoundEffect.SENT -> R.raw.enter_sound
            SoundEffect.INCOMING -> R.raw.notification_sound_fx
        }
        runCatching {
            val player = MediaPlayer.create(context, soundRes)
            player?.setOnCompletionListener { completed -> completed.release() }
            player?.setOnErrorListener { failed, _, _ ->
                failed.release()
                true
            }
            player?.start()
        }
        onConsumeSoundEvent()
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(targetState = state.session != null) { authenticated ->
            if (!authenticated) {
                AuthFlow(
                    state = state,
                    strings = strings,
                    onContinue = onContinue,
                    onBack = onBack,
                    onAuthModeSelected = onAuthModeSelected,
                    onLanguageSelected = onLanguageSelected,
                    onUsernameChanged = onUsernameChanged,
                    onPasswordChanged = onPasswordChanged,
                    onNicknameChanged = onNicknameChanged,
                    onGeneratePassword = onGeneratePassword,
                    onSubmitAuth = onSubmitAuth,
                    onOpenPrivacyPolicy = { legalDocumentDialog = LegalDocumentType.PRIVACY_POLICY },
                    onOpenTerms = { legalDocumentDialog = LegalDocumentType.TERMS_OF_USE },
                )
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomeShell(
                        state = state,
                        strings = strings,
                        onSelectTab = onSelectTab,
                        onOpenChat = onOpenChat,
                        onOpenContactChat = onOpenContactChat,
                        onSaveContactNickname = onSaveContactNickname,
                        onSearchPeopleQueryChanged = onSearchPeopleQueryChanged,
                        onChatListQueryChanged = onChatListQueryChanged,
                        onRefreshChats = onRefreshChats,
                        onSearchPeople = onSearchPeople,
                        onSearchDirectory = onSearchDirectory,
                        onAddContact = onAddContact,
                        onOpenDirectoryEntry = onOpenDirectoryEntry,
                        onCreateChannel = onCreateChannel,
                        onOpenContactPreview = onOpenContactPreview,
                        onCloseContactPreview = onCloseContactPreview,
                        onOpenPreviewChat = onOpenPreviewChat,
                        onMarkPreviewChatUnread = onMarkPreviewChatUnread,
                        onToggleArchive = onToggleArchive,
                        onTogglePin = onTogglePin,
                        onToggleMute = onToggleMute,
                        onToggleUnread = onToggleUnread,
                        onUpdateNotifications = onUpdateNotifications,
                        onUpdatePreviews = onUpdatePreviews,
                        onUpdateSound = onUpdateSound,
                        onUpdateInstantSync = onUpdateInstantSync,
                        onUpdateThemeMode = onUpdateThemeMode,
                        onSettingsLanguageSelected = onLanguageSelected,
                        onUpdateCompact = onUpdateCompact,
                        onUpdateHaptics = onUpdateHaptics,
                        onUpdateArchivedChats = onUpdateArchivedChats,
                        onUpdateAppLock = onUpdateAppLock,
                        onUpdateGhostMode = onUpdateGhostMode,
                        onLastSeenVisibilityChanged = onLastSeenVisibilityChanged,
                        onUsernameDiscoverableChanged = onUsernameDiscoverableChanged,
                        onUsernameChanged = onUsernameChanged,
                        onNicknameChanged = onNicknameChanged,
                        onAboutChanged = onAboutChanged,
                        onSelectProfileAvatar = onSelectProfileAvatar,
                        onClearProfileAvatar = onClearProfileAvatar,
                        onSaveProfile = onSaveProfile,
                        onChangePassword = onChangePassword,
                        onDeleteAccount = onDeleteAccount,
                        onLogout = onLogout,
                        onLogoutOthers = onLogoutOthers,
                        onOpenPrivacyPolicy = { legalDocumentDialog = LegalDocumentType.PRIVACY_POLICY },
                        onOpenTerms = { legalDocumentDialog = LegalDocumentType.TERMS_OF_USE },
                    )
                    var overlayChatId by remember { mutableStateOf<String?>(null) }
                    var overlayVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(state.openedChatId) {
                        if (state.openedChatId != null) {
                            overlayChatId = state.openedChatId
                            overlayVisible = true
                        } else {
                            overlayVisible = false
                            delay(220)
                            overlayChatId = null
                        }
                    }
                    AnimatedVisibility(
                        visible = overlayVisible,
                        enter = slideInHorizontally(initialOffsetX = { it / 6 }, animationSpec = tween(260, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(220)),
                        exit = slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(220, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(180)),
                    ) {
                        if (overlayChatId != null) {
                            val overlayState = if (state.openedChatId == null) state.copy(openedChatId = overlayChatId) else state
                            val openedChat = overlayState.chats.firstOrNull { it.id == overlayChatId }
                            if (openedChat?.type == "channel") {
                                ChannelScreen(
                                    state = overlayState,
                                    strings = strings,
                                    onBack = onCloseChat,
                                    onSendPost = onSendMessage,
                                    onToggleMute = {
                                        overlayChatId?.let { chatId ->
                                            openedChat.let { chat -> onToggleMute(chatId, !chat.isMuted) }
                                        }
                                    },
                                    onSelectComposerMedia = onSelectComposerMedia,
                                    onClearComposerMedia = onClearComposerMedia,
                                    onUpdateComposerMediaOptions = onUpdateComposerMediaOptions,
                                    onCancelPendingUpload = onCancelPendingUpload,
                                    onSelectChannelAvatar = onSelectChannelAvatar,
                                    onLoadMembers = onLoadChannelMembers,
                                    onUpdateMemberRole = onUpdateChannelRole,
                                    onUpdateChannel = onUpdateChannel,
                                    onLeaveChannel = onLeaveChannel,
                                    onDeleteChannel = onDeleteChannel,
                                    onDeletePost = onDeleteMessage,
                                    onForwardPost = onForwardChannelPost,
                                    onPinPost = onPinChannelPost,
                                    onOpenComments = onLoadChannelComments,
                                    onCloseComments = onCloseChannelComments,
                                    onSendComment = onSendChannelComment,
                                )
                            } else {
                                ChatScreen(
                                    state = overlayState,
                                    strings = strings,
                                    onBack = onCloseChat,
                                    onSendMessage = onSendMessage,
                                    onSelectComposerMedia = onSelectComposerMedia,
                                    onClearComposerMedia = onClearComposerMedia,
                                    onUpdateComposerMediaOptions = onUpdateComposerMediaOptions,
                                    onCancelPendingUpload = onCancelPendingUpload,
                                    onSaveContactNickname = onSaveContactNickname,
                                    onEditMessage = onEditMessage,
                                    onDeleteMessage = onDeleteMessage,
                                    onReplyMessage = onReplyMessage,
                                    onCancelReply = onCancelReply,
                                    onChatSearchChanged = onChatSearchChanged,
                                    onSearchMessages = onSearchMessages,
                                    onEnsureMessageLoaded = onEnsureMessageLoaded,
                                    onLoadOlderMessages = onLoadOlderMessages,
                                    onTypingChanged = onTypingChanged,
                                    onRemoveContact = onRemoveCurrentContact,
                                    onClearChat = onClearCurrentChat,
                                    onToggleBlocked = onToggleCurrentBlocked,
                                    onToggleMute = {
                                        state.openedChatId?.let { chatId ->
                                            state.chats.firstOrNull { it.id == chatId }?.let { chat ->
                                                onToggleMute(chatId, !chat.isMuted)
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
        if (state.offlineMessage != null) {
            AlertDialog(
                onDismissRequest = onDismissErrorDialogs,
                title = { Text(strings.appName) },
                text = { Text(state.offlineMessage) },
                confirmButton = { TextButton(onClick = onDismissErrorDialogs) { Text(strings.retry) } },
            )
        }
        AnimatedVisibility(
            visible = state.busy,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        legalDocumentDialog?.let { document ->
            LegalDocumentDialog(
                type = document,
                onDismiss = { legalDocumentDialog = null },
            )
        }
    }
}

private fun formatClockTime(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp * 1000))
}

private fun formatChatPreview(preview: String, strings: SkytaleStrings): String = when (preview.trim().lowercase(Locale.getDefault())) {
    "[image]" -> strings.photoAttachment
    else -> preview
}

private fun formatPresenceTime(timestamp: Long, nowMillis: Long = System.currentTimeMillis()): String {
    if (timestamp <= 0L) return ""
    val target = Date(timestamp * 1000)
    val now = Date(nowMillis)
    val dayKey = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val pattern = if (dayKey.format(target) == dayKey.format(now)) "HH:mm" else "dd.MM.yyyy"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(target)
}

private fun performPlatformVibration(view: android.view.View, durationMs: Long) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val manager = view.context.getSystemService(android.os.VibratorManager::class.java)
        val vibrator = manager?.defaultVibrator
        if (vibrator?.hasVibrator() == true) {
            vibrator.vibrate(
                android.os.VibrationEffect.createOneShot(durationMs, android.os.VibrationEffect.DEFAULT_AMPLITUDE),
            )
        }
    } else {
        @Suppress("DEPRECATION")
        val vibrator = view.context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
        if (vibrator?.hasVibrator() == true) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    android.os.VibrationEffect.createOneShot(durationMs, android.os.VibrationEffect.DEFAULT_AMPLITUDE),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        }
    }
}

private fun performLongPressHaptic(view: android.view.View, enabled: Boolean = true) {
    if (!enabled) return
    val performed = view.performHapticFeedback(
        HapticFeedbackConstants.LONG_PRESS,
        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING,
    )
    if (!performed) {
        performPlatformVibration(view, 18L)
    }
}

private fun performGestureThresholdHaptic(view: android.view.View, enabled: Boolean = true) {
    if (!enabled) return
    val constant = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        HapticFeedbackConstants.GESTURE_THRESHOLD_ACTIVATE
    } else {
        HapticFeedbackConstants.KEYBOARD_TAP
    }
    val performed = view.performHapticFeedback(
        constant,
        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING,
    )
    if (!performed) {
        performPlatformVibration(view, 14L)
    }
}

private fun Context.resolveDisplayName(uri: Uri): String {
    contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (index >= 0) {
                return cursor.getString(index).orEmpty().ifBlank { "image.jpg" }
            }
        }
    }
    return "image.jpg"
}

private fun Context.createCameraImageUri(): Uri {
    val baseDir = externalCacheDir ?: cacheDir
    val cameraDir = File(baseDir, "camera").apply { mkdirs() }
    val imageFile = File.createTempFile("skytale_camera_", ".jpg", cameraDir)
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
}

@Composable
private fun rememberNetworkAvailable(): Boolean {
    val context = LocalContext.current
    val connectivityManager = remember(context) {
        context.getSystemService(ConnectivityManager::class.java)
    }
    var isAvailable by remember(connectivityManager) {
        mutableStateOf(connectivityManager?.isNetworkAvailable() ?: true)
    }
    DisposableEffect(connectivityManager) {
        if (connectivityManager == null) {
            isAvailable = true
            return@DisposableEffect onDispose {}
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isAvailable = true
            }

            override fun onLost(network: Network) {
                isAvailable = connectivityManager.isNetworkAvailable()
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                isAvailable = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }

            override fun onUnavailable() {
                isAvailable = false
            }
        }
        isAvailable = connectivityManager.isNetworkAvailable()
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
        onDispose {
            runCatching { connectivityManager.unregisterNetworkCallback(callback) }
        }
    }
    return isAvailable
}

private fun ConnectivityManager.isNetworkAvailable(): Boolean {
    val capabilities = getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

private suspend fun Context.saveRemoteMedia(sourceUrl: String, targetUri: Uri) {
    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val app = applicationContext as SkytaleApp
        val token = app.graph.secureStore.currentSession?.token
        val request = Request.Builder()
            .url(sourceUrl)
            .apply {
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }
            }
            .build()
        OkHttpClient().newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Download failed")
            }
            val body = response.body ?: throw IllegalStateException("Download failed")
            body.byteStream().use { input ->
                contentResolver.openOutputStream(targetUri, "w")?.use { output ->
                    input.copyTo(output)
                    output.flush()
                } ?: throw IllegalStateException("Unable to save image")
            }
        }
    }
}

@Composable
private fun AuthFlow(
    state: SkytaleUiState,
    strings: SkytaleStrings,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    onAuthModeSelected: (AuthMode) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onNicknameChanged: (String) -> Unit,
    onGeneratePassword: () -> Unit,
    onSubmitAuth: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    val gradient = Brush.linearGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp),
    ) {
        AnimatedContent(targetState = state.authMode to state.authStep) { (mode, step) ->
            when {
                step == 0 -> LanguageStep(
                    strings = strings,
                    state = state,
                    showBack = false,
                    onBack = onBack,
                    onLanguageSelected = onLanguageSelected,
                    onContinue = onContinue,
                    onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                    onOpenTerms = onOpenTerms,
                )
                step == 1 -> ModeStep(
                    strings = strings,
                    showBack = true,
                    onBack = onBack,
                    onSelectMode = {
                        onAuthModeSelected(it)
                        onContinue()
                    },
                )
                step == 2 -> UsernameStep(
                    strings = strings,
                    state = state,
                    onBack = onBack,
                    onContinue = onContinue,
                    onUsernameChanged = onUsernameChanged,
                )
                mode == AuthMode.REGISTER && step == 3 -> PasswordStep(
                    strings = strings,
                    state = state,
                    registerMode = true,
                    onBack = onBack,
                    onPasswordChanged = onPasswordChanged,
                    onGeneratePassword = onGeneratePassword,
                    onContinue = onContinue,
                )
                mode == AuthMode.LOGIN && step == 3 -> PasswordStep(
                    strings = strings,
                    state = state,
                    registerMode = false,
                    onBack = onBack,
                    onPasswordChanged = onPasswordChanged,
                    onGeneratePassword = onGeneratePassword,
                    onContinue = onContinue,
                )
                else -> NicknameStep(
                    strings = strings,
                    state = state,
                    onBack = onBack,
                    onNicknameChanged = onNicknameChanged,
                    onContinue = onContinue,
                )
            }
        }
    }
}

@Composable
private fun ModeStep(
    strings: SkytaleStrings,
    showBack: Boolean,
    onBack: () -> Unit,
    onSelectMode: (AuthMode) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            if (showBack) {
                TextButton(onClick = onBack) { Text(strings.back) }
                Spacer(Modifier.height(18.dp))
            } else {
                Spacer(Modifier.height(24.dp))
            }
            Image(
                painter = painterResource(R.drawable.logo_mark),
                contentDescription = strings.appName,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(28.dp))
            Text(strings.chooseActionTitle, style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(12.dp))
            Text(strings.chooseActionSubtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            ActionCard(title = strings.createAccount, subtitle = strings.createAccountHint, onClick = { onSelectMode(AuthMode.REGISTER) })
            Spacer(Modifier.height(14.dp))
            ActionCard(title = strings.signIn, subtitle = strings.signInHint, onClick = { onSelectMode(AuthMode.LOGIN) })
        }
        Spacer(Modifier.height(1.dp))
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LanguageStep(
    strings: SkytaleStrings,
    state: SkytaleUiState,
    showBack: Boolean = true,
    onBack: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onContinue: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            if (showBack) {
                TextButton(onClick = onBack) { Text(strings.back) }
                Spacer(Modifier.height(18.dp))
            } else {
                Spacer(Modifier.height(24.dp))
            }
            Text(strings.chooseLanguageTitle, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(strings.chooseLanguageSubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LanguageCard(modifier = Modifier.weight(1f), label = "English", selected = state.language == AppLanguage.EN) { onLanguageSelected(AppLanguage.EN) }
                LanguageCard(modifier = Modifier.weight(1f), label = "Русский", selected = state.language == AppLanguage.RU) { onLanguageSelected(AppLanguage.RU) }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LegalConsentText(
                isRussian = strings.back == "Назад",
                onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                onOpenTerms = onOpenTerms,
            )
            StepButton(strings = strings, onClick = onContinue)
        }
    }
}

@Composable
private fun UsernameStep(
    strings: SkytaleStrings,
    state: SkytaleUiState,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onUsernameChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            TextButton(onClick = onBack) { Text(strings.back) }
            Spacer(Modifier.height(18.dp))
            Text(strings.usernameTitle, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(strings.usernameSubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = state.username,
                onValueChange = onUsernameChanged,
                label = { Text(strings.username) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            AuthErrorText(state.authError)
        }
        StepButton(strings = strings, onClick = onContinue)
    }
}

@Composable
private fun PasswordStep(
    strings: SkytaleStrings,
    state: SkytaleUiState,
    registerMode: Boolean,
    onBack: () -> Unit,
    onPasswordChanged: (String) -> Unit,
    onGeneratePassword: () -> Unit,
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            TextButton(onClick = onBack) { Text(strings.back) }
            Spacer(Modifier.height(18.dp))
            Text(strings.passwordTitle, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(strings.passwordSubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                label = { Text(strings.password) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            if (registerMode) {
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onGeneratePassword) { Text(strings.generatePassword) }
                Text(strings.passwordAdvice, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            AuthErrorText(state.authError)
        }
        StepButton(strings = strings, onClick = onContinue)
    }
}

@Composable
private fun ColumnScope.StepButton(
    strings: SkytaleStrings,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.End)
            .navigationBarsPadding(),
    ) {
        Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = strings.continueLabel)
    }
}

@Composable
private fun AuthErrorText(error: String?) {
    if (error.isNullOrBlank()) return
    Spacer(Modifier.height(12.dp))
    Text(error, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun WelcomeStep(
    strings: SkytaleStrings,
    state: SkytaleUiState,
    onContinue: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = strings.appName,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(28.dp))
            Text(strings.chooseActionTitle, style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(12.dp))
            Text(strings.chooseActionSubtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Text(strings.chooseLanguageTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LanguageCard(modifier = Modifier.weight(1f), label = "English", selected = state.language == AppLanguage.EN) { onLanguageSelected(AppLanguage.EN) }
                LanguageCard(modifier = Modifier.weight(1f), label = "Русский", selected = state.language == AppLanguage.RU) { onLanguageSelected(AppLanguage.RU) }
            }
        }
        FilledIconButton(
            onClick = onContinue,
            modifier = Modifier
                .align(Alignment.End)
                .navigationBarsPadding(),
        ) {
            Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = strings.continueLabel)
        }
    }
}

@Composable
private fun LanguageCard(modifier: Modifier = Modifier, label: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceContainer,
        ),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(label, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            Text(if (selected) "Selected" else "Tap to choose", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CredentialsStep(
    strings: SkytaleStrings,
    state: SkytaleUiState,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onAuthModeSelected: (AuthMode) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onGeneratePassword: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            TextButton(onClick = onBack) { Text(strings.back) }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AssistChip(
                    onClick = { onAuthModeSelected(AuthMode.REGISTER) },
                    label = { Text(strings.createAccount) },
                )
                AssistChip(
                    onClick = { onAuthModeSelected(AuthMode.LOGIN) },
                    label = { Text(strings.signIn) },
                )
            }
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = state.username,
                onValueChange = onUsernameChanged,
                label = { Text(strings.username) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                label = { Text(strings.password) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.authMode == AuthMode.REGISTER) {
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onGeneratePassword) { Text(strings.generatePassword) }
                Text(strings.passwordAdvice, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        FilledIconButton(onClick = onContinue, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = strings.continueLabel)
        }
    }
}

@Composable
private fun NicknameStep(
    strings: SkytaleStrings,
    state: SkytaleUiState,
    onBack: () -> Unit,
    onNicknameChanged: (String) -> Unit,
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            TextButton(onClick = onBack) { Text(strings.back) }
            Spacer(Modifier.height(16.dp))
            Text(strings.nicknameTitle, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(strings.nicknameSubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = state.nickname,
                onValueChange = onNicknameChanged,
                label = { Text(strings.nickname) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            AuthErrorText(state.authError)
        }
        FilledIconButton(onClick = onContinue, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = strings.continueLabel)
        }
    }
}

@Composable
private fun HomeShell(
    state: SkytaleUiState,
    strings: SkytaleStrings,
    onSelectTab: (HomeTab) -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenContactChat: (String) -> Unit,
    onSaveContactNickname: (String, String) -> Unit,
    onSearchPeopleQueryChanged: (String) -> Unit,
    onChatListQueryChanged: (String) -> Unit,
    onRefreshChats: () -> Unit,
    onSearchPeople: () -> Unit,
    onSearchDirectory: () -> Unit,
    onAddContact: (String) -> Unit,
    onOpenDirectoryEntry: (DirectoryEntryModel) -> Unit,
    onCreateChannel: (String, String, String, Boolean) -> Unit,
    onOpenContactPreview: (String) -> Unit,
    onCloseContactPreview: () -> Unit,
    onOpenPreviewChat: () -> Unit,
    onMarkPreviewChatUnread: () -> Unit,
    onToggleArchive: (String, Boolean) -> Unit,
    onTogglePin: (String, Boolean) -> Unit,
    onToggleMute: (String, Boolean) -> Unit,
    onToggleUnread: (String, Boolean) -> Unit,
    onUpdateNotifications: (Boolean) -> Unit,
    onUpdatePreviews: (Boolean) -> Unit,
    onUpdateSound: (Boolean) -> Unit,
    onUpdateInstantSync: (Boolean) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onSettingsLanguageSelected: (AppLanguage) -> Unit,
    onUpdateCompact: (Boolean) -> Unit,
    onUpdateHaptics: (Boolean) -> Unit,
    onUpdateArchivedChats: (Boolean) -> Unit,
    onUpdateAppLock: (Boolean) -> Unit,
    onUpdateGhostMode: (Boolean) -> Unit,
    onLastSeenVisibilityChanged: (String) -> Unit,
    onUsernameDiscoverableChanged: (Boolean) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onNicknameChanged: (String) -> Unit,
    onAboutChanged: (String) -> Unit,
    onSelectProfileAvatar: (String, String, String) -> Unit,
    onClearProfileAvatar: () -> Unit,
    onSaveProfile: () -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    onLogoutOthers: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    val view = LocalView.current
    val isNetworkAvailable = rememberNetworkAvailable()
    var actionMenuVisible by remember { mutableStateOf(false) }
    var addDialog by remember { mutableStateOf(false) }
    var createChannelDialog by remember { mutableStateOf(false) }
    var findChannelDialog by remember { mutableStateOf(false) }
    var topMenuExpanded by remember { mutableStateOf(false) }
    var chatsSearchVisible by remember { mutableStateOf(false) }
    var settingsAtRoot by remember { mutableStateOf(true) }
    val tabHistory = remember { mutableListOf<HomeTab>() }
    val density = LocalDensity.current
    val homeSwipeThreshold = with(density) { 72.dp.toPx() }
    var homeDragDistance by remember(state.selectedTab) { mutableStateOf(0f) }
    var homeSwipeHapticTriggered by remember(state.selectedTab) { mutableStateOf(false) }
    val navigateTab: (HomeTab) -> Unit = { target ->
        if (target != state.selectedTab) {
            if (tabHistory.lastOrNull() != state.selectedTab) {
                tabHistory.add(state.selectedTab)
            }
            onSelectTab(target)
        }
    }
    val navigateTabBack: () -> Unit = {
        val previous = if (tabHistory.isNotEmpty()) tabHistory.removeAt(tabHistory.lastIndex) else HomeTab.CHATS
        onSelectTab(previous)
    }
    val headerTitle = when (state.selectedTab) {
        HomeTab.CHATS -> strings.appName
        HomeTab.FEED -> strings.feed
        HomeTab.SETTINGS -> strings.settings
        HomeTab.PROFILE -> strings.profile
    }
    BackHandler(enabled = state.selectedTab != HomeTab.CHATS && (state.selectedTab != HomeTab.SETTINGS || settingsAtRoot)) {
        navigateTabBack()
    }
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(headerTitle, style = MaterialTheme.typography.headlineMedium)
                            if (state.selectedTab == HomeTab.CHATS) {
                                Text(
                                    when {
                                        !isNetworkAvailable -> if (Locale.getDefault().language == "ru") "Оффлайн" else "Offline"
                                        state.lastSeenVisibility == "ghost" -> strings.ghostMode
                                        else -> strings.online
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        if (state.selectedTab == HomeTab.CHATS) {
                            IconButton(onClick = { actionMenuVisible = true }) {
                                Icon(Icons.Outlined.Add, contentDescription = strings.addContact)
                            }
                            Box {
                                IconButton(onClick = { topMenuExpanded = true }) {
                                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                                }
                                DropdownMenu(expanded = topMenuExpanded, onDismissRequest = { topMenuExpanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text(strings.searchChats) },
                                        leadingIcon = { Icon(Icons.Outlined.Search, null) },
                                        onClick = {
                                            chatsSearchVisible = !chatsSearchVisible
                                            if (!chatsSearchVisible) {
                                                onChatListQueryChanged("")
                                            }
                                            topMenuExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = state.selectedTab == HomeTab.CHATS && chatsSearchVisible) {
                        OutlinedTextField(
                            value = state.chatListQuery,
                            onValueChange = onChatListQueryChanged,
                            label = { Text(strings.searchChats) },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    chatsSearchVisible = false
                                    onChatListQueryChanged("")
                                }) {
                                    Icon(Icons.Outlined.Close, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (state.selectedTab == HomeTab.CHATS) {
                FloatingActionButton(onClick = { actionMenuVisible = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = strings.addContact)
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 12.dp, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)) {
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = Color.Transparent,
                ) {
                    NavigationBarItem(selected = state.selectedTab == HomeTab.CHATS, onClick = { navigateTab(HomeTab.CHATS) }, icon = { Icon(Icons.Outlined.ChatBubbleOutline, null) }, label = { Text(strings.chats) })
                    NavigationBarItem(selected = state.selectedTab == HomeTab.FEED, onClick = { navigateTab(HomeTab.FEED) }, icon = { Icon(Icons.Outlined.Info, null) }, label = { Text(strings.feed) })
                    NavigationBarItem(selected = state.selectedTab == HomeTab.SETTINGS, onClick = { navigateTab(HomeTab.SETTINGS) }, icon = { Icon(Icons.Outlined.Settings, null) }, label = { Text(strings.settings) })
                    NavigationBarItem(selected = state.selectedTab == HomeTab.PROFILE, onClick = { navigateTab(HomeTab.PROFILE) }, icon = { Icon(Icons.Outlined.Person, null) }, label = { Text(strings.profile) })
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .let { base ->
                    if (state.selectedTab != HomeTab.SETTINGS || settingsAtRoot) {
                        base.pointerInput(state.selectedTab) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    homeDragDistance = 0f
                                    homeSwipeHapticTriggered = false
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    if (state.selectedTab != HomeTab.CHATS && dragAmount > 0) {
                                        homeDragDistance += dragAmount
                                        if (!homeSwipeHapticTriggered && homeDragDistance >= homeSwipeThreshold) {
                                            performGestureThresholdHaptic(view, state.settings.hapticsEnabled)
                                            homeSwipeHapticTriggered = true
                                        }
                                        if (homeDragDistance >= homeSwipeThreshold) {
                                            homeDragDistance = 0f
                                            homeSwipeHapticTriggered = false
                                            navigateTabBack()
                                        }
                                    }
                                },
                                onDragEnd = {
                                    homeDragDistance = 0f
                                    homeSwipeHapticTriggered = false
                                },
                                onDragCancel = {
                                    homeDragDistance = 0f
                                    homeSwipeHapticTriggered = false
                                },
                            )
                        }
                    } else {
                        base
                    }
                },
        ) {
            AnimatedContent(
                targetState = state.selectedTab,
                transitionSpec = {
                    val forward = targetState.ordinal > initialState.ordinal
                    (slideInHorizontally { if (forward) it / 5 else -it / 5 } + fadeIn()) togetherWith
                        (slideOutHorizontally { if (forward) -it / 6 else it / 6 } + fadeOut())
                },
                label = "bottom-tab-transition",
            ) { selectedTab ->
                when (selectedTab) {
                    HomeTab.CHATS -> ChatsTab(
                        state = state,
                        strings = strings,
                        showSearch = chatsSearchVisible,
                        onOpenChat = onOpenChat,
                        onOpenContactChat = onOpenContactChat,
                        onOpenContactPreview = onOpenContactPreview,
                        onChatListQueryChanged = onChatListQueryChanged,
                        onRefreshChats = onRefreshChats,
                        onToggleArchive = onToggleArchive,
                        onTogglePin = onTogglePin,
                        onToggleMute = onToggleMute,
                        onToggleUnread = onToggleUnread,
                    )
                    HomeTab.FEED -> InfoTab(strings = strings)
                    HomeTab.SETTINGS -> SettingsTab(
                        state = state,
                        strings = strings,
                        onUpdateNotifications = onUpdateNotifications,
                        onUpdatePreviews = onUpdatePreviews,
                        onUpdateSound = onUpdateSound,
                        onUpdateInstantSync = onUpdateInstantSync,
                        onUpdateThemeMode = onUpdateThemeMode,
                        onSettingsLanguageSelected = onSettingsLanguageSelected,
                        onUpdateCompact = onUpdateCompact,
                        onUpdateHaptics = onUpdateHaptics,
                        onUpdateArchivedChats = onUpdateArchivedChats,
                        onUpdateAppLock = onUpdateAppLock,
                        onUpdateGhostMode = onUpdateGhostMode,
                        onLastSeenVisibilityChanged = onLastSeenVisibilityChanged,
                        onUsernameDiscoverableChanged = onUsernameDiscoverableChanged,
                        onSaveProfile = onSaveProfile,
                        onChangePassword = onChangePassword,
                        onDeleteAccount = onDeleteAccount,
                        onLogout = onLogout,
                        onLogoutOthers = onLogoutOthers,
                        onRootStateChanged = { settingsAtRoot = it },
                        onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                        onOpenTerms = onOpenTerms,
                    )
        HomeTab.PROFILE -> ProfileTab(
            state = state,
            strings = strings,
            onUsernameChanged = onUsernameChanged,
            onNicknameChanged = onNicknameChanged,
            onAboutChanged = onAboutChanged,
            onSelectProfileAvatar = onSelectProfileAvatar,
            onClearProfileAvatar = onClearProfileAvatar,
            onSaveProfile = onSaveProfile,
        )
                }
            }
        }
    }
    if (actionMenuVisible) {
        CreateMenuDialog(
            onDismiss = { actionMenuVisible = false },
            onAddContact = {
                actionMenuVisible = false
                addDialog = true
            },
            onCreateChannel = {
                actionMenuVisible = false
                createChannelDialog = true
            },
            onFindChannel = {
                actionMenuVisible = false
                findChannelDialog = true
            },
        )
    }
    if (addDialog) {
        AddContactDialog(
            strings = strings,
            query = state.addContactQuery,
            results = state.searchUsers,
            onQueryChanged = onSearchPeopleQueryChanged,
            onSearch = onSearchPeople,
            onDismiss = { addDialog = false },
            onAdd = {
                onAddContact(it)
                addDialog = false
            },
        )
    }
    if (createChannelDialog) {
        CreateChannelDialog(
            onDismiss = { createChannelDialog = false },
            onCreate = { title, username, description, commentsEnabled ->
                onCreateChannel(title, username, description, commentsEnabled)
                createChannelDialog = false
            },
        )
    }
    if (findChannelDialog) {
        DirectorySearchDialog(
            title = if (Locale.getDefault().language == "ru") "Найти канал" else "Find channel",
            query = state.addContactQuery,
            results = state.directoryResults.filter { it.kind == "channel" },
            onQueryChanged = onSearchPeopleQueryChanged,
            onSearch = onSearchDirectory,
            onDismiss = { findChannelDialog = false },
            onOpen = {
                onOpenDirectoryEntry(it)
                findChannelDialog = false
            },
        )
    }
    state.contactPreview?.let { contact ->
        ContactPreviewDialog(
            strings = strings,
            contact = contact,
            messages = state.contactPreviewMessages,
            onDismiss = onCloseContactPreview,
            onOpenChat = onOpenPreviewChat,
            onMarkUnread = onMarkPreviewChatUnread,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ChatsTab(
    state: SkytaleUiState,
    strings: SkytaleStrings,
    showSearch: Boolean,
    onOpenChat: (String) -> Unit,
    onOpenContactChat: (String) -> Unit,
    onOpenContactPreview: (String) -> Unit,
    onChatListQueryChanged: (String) -> Unit,
    onRefreshChats: () -> Unit,
    onToggleArchive: (String, Boolean) -> Unit,
    onTogglePin: (String, Boolean) -> Unit,
    onToggleMute: (String, Boolean) -> Unit,
    onToggleUnread: (String, Boolean) -> Unit,
) {
    val query = state.chatListQuery.trim().lowercase()
    val filteredChats = state.chats.filter {
        query.isBlank() || it.title.lowercase().contains(query) || it.lastMessagePreview.lowercase().contains(query)
    }
    if (state.chats.isEmpty()) {
        EmptyState(
            icon = Icons.Outlined.ChatBubbleOutline,
            title = strings.noChatsTitle,
            body = strings.noChatsBody,
        )
        return
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.busy,
        onRefresh = onRefreshChats,
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (showSearch) {
                item {
                    Text(
                        strings.searchChats,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (state.contacts.isNotEmpty()) {
                item {
                    Text(
                        strings.contacts,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.contacts, key = { it.user.id }) { contact ->
                            ContactShortcut(
                                contact = contact,
                                compact = state.settings.compactMode,
                                hapticsEnabled = state.settings.hapticsEnabled,
                                onClick = { onOpenContactChat(contact.user.id) },
                                onLongPress = { onOpenContactPreview(contact.user.id) },
                            )
                        }
                    }
                }
            }
            items(filteredChats, key = { it.id }) { chat ->
                ChatRow(
                    chat = chat,
                    swipesEnabled = state.openedChatId == null,
                    compact = state.settings.compactMode,
                    hapticsEnabled = state.settings.hapticsEnabled,
                    onClick = { onOpenChat(chat.id) },
                    onToggleArchive = { onToggleArchive(chat.id, !chat.isArchived) },
                    onTogglePin = { onTogglePin(chat.id, !chat.isPinned) },
                    onToggleMute = { onToggleMute(chat.id, !chat.isMuted) },
                    onToggleUnread = { onToggleUnread(chat.id, !chat.isMarkedUnread) },
                )
            }
        }
        PullRefreshIndicator(
            refreshing = state.busy,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ChatRow(
    chat: ChatModel,
    swipesEnabled: Boolean,
    compact: Boolean,
    hapticsEnabled: Boolean,
    onClick: () -> Unit,
    onToggleArchive: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleUnread: () -> Unit,
) {
    val view = LocalView.current
    val strings = LocalSkytaleStrings.current
    androidx.compose.runtime.key(chat.id, chat.isPinned, chat.isArchived) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (!swipesEnabled) {
                    return@rememberSwipeToDismissBoxState false
                }
                when (it) {
                    SwipeToDismissBoxValue.StartToEnd -> onTogglePin()
                    SwipeToDismissBoxValue.EndToStart -> onToggleArchive()
                    SwipeToDismissBoxValue.Settled -> Unit
                }
                false
            },
        )
        SwipeToDismissBox(
            state = dismissState,
            gesturesEnabled = swipesEnabled,
            backgroundContent = {
                val aligned = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 18.dp),
                    contentAlignment = aligned,
                ) {
                    Icon(
                        imageVector = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Icons.Outlined.PushPin else Icons.Outlined.Archive,
                        contentDescription = null,
                    )
                }
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = {
                            performLongPressHaptic(view, hapticsEnabled)
                            onToggleUnread()
                        },
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = if (compact) 12.dp else 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserAvatar(
                        model = chat.peer?.avatarThumbUrl?.ifBlank { chat.peer?.avatarUrl.orEmpty() }
                            ?: chat.avatarThumbUrl.ifBlank { chat.avatarUrl },
                        displayName = chat.title,
                        size = if (compact) 46.dp else 52.dp,
                        textSize = MaterialTheme.typography.labelLarge,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(chat.title, style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.width(8.dp))
                            if (chat.isPinned) Icon(Icons.Outlined.PushPin, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                            if (chat.isMuted) Icon(Icons.AutoMirrored.Outlined.VolumeOff, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatChatPreview(chat.lastMessagePreview, strings).ifBlank { " " },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatClockTime(chat.updatedAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        if (chat.unreadCount > 0) {
                            Badge { Text(chat.unreadCount.toString()) }
                        }
                        if (chat.isMuted) {
                            IconButton(onClick = onToggleMute) { Icon(Icons.AutoMirrored.Outlined.VolumeOff, null, modifier = Modifier.size(18.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(
    state: SkytaleUiState,
    strings: SkytaleStrings,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSelectComposerMedia: (String, String, String) -> Unit,
    onClearComposerMedia: () -> Unit,
    onUpdateComposerMediaOptions: (Boolean, Boolean) -> Unit,
    onCancelPendingUpload: (String) -> Unit,
    onSaveContactNickname: (String, String) -> Unit,
    onEditMessage: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onReplyMessage: (String) -> Unit,
    onCancelReply: () -> Unit,
    onChatSearchChanged: (String) -> Unit,
    onSearchMessages: () -> Unit,
    onEnsureMessageLoaded: (String, Long) -> Unit,
    onLoadOlderMessages: () -> Unit,
    onTypingChanged: (Boolean) -> Unit,
    onRemoveContact: () -> Unit,
    onClearChat: () -> Unit,
    onToggleBlocked: () -> Unit,
    onToggleMute: () -> Unit,
) {
    val chat = state.chats.firstOrNull { it.id == state.openedChatId }
    val messages = state.messages
    val view = LocalView.current
    var draft by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    var searchVisible by remember { mutableStateOf(false) }
    var profileVisible by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val typingUserId = state.typingUserIdByChat[state.openedChatId]
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val keyboard = LocalSoftwareKeyboardController.current
    val compact = state.settings.compactMode
    val swipeThreshold = with(density) { 32.dp.toPx() }
    var dragDistance by remember(state.openedChatId) { mutableStateOf(0f) }
    var initialScrollDone by remember(state.openedChatId) { mutableStateOf(false) }
    var focusComposerOnReply by remember(state.openedChatId) { mutableStateOf(false) }
    var highlightedMessageId by remember(state.openedChatId) { mutableStateOf<String?>(null) }
    var showSearchResultsDialog by remember(state.openedChatId) { mutableStateOf(false) }
    var composerMediaOptionsVisible by remember(state.openedChatId) { mutableStateOf(false) }
    var pendingPrependAnchorId by remember(state.openedChatId) { mutableStateOf<String?>(null) }
    var pendingPrependAnchorOffset by remember(state.openedChatId) { mutableIntStateOf(0) }
    val presenceTick = rememberRelativeTimeTicker()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val replyPreviewMap = remember(messages) { messages.associateBy { it.id } }
    val latestMessages by rememberUpdatedState(messages)
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val imeVisible = imeBottomPx > 0
    val animatedImeBottom by animateDpAsState(
        targetValue = with(density) { imeBottomPx.toDp() },
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "chat-ime-bottom",
    )
    val replyingMessage = messages.firstOrNull { it.id == state.replyToMessageId }
    val currentContact = chat?.peer?.id?.let { peerId -> state.contacts.firstOrNull { it.user.id == peerId } }
    val showSenderName = chat?.type != "direct"
    var previousImeBottomPx by remember(state.openedChatId) { mutableIntStateOf(imeBottomPx) }
    var wasNearBottomBeforeImeOpen by remember(state.openedChatId) { mutableStateOf(true) }
    var composerFocused by remember(state.openedChatId) { mutableStateOf(false) }
    var backSwipeHapticTriggered by remember(state.openedChatId) { mutableStateOf(false) }
    var attachmentMenuExpanded by remember(state.openedChatId) { mutableStateOf(false) }
    var pendingCameraUri by remember(state.openedChatId) { mutableStateOf<Uri?>(null) }
    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
        uris.forEach { uri ->
            onSelectComposerMedia(
                uri.toString(),
                context.resolveDisplayName(uri),
                context.contentResolver.getType(uri).orEmpty(),
            )
        }
    }
    val localeIsRussian = remember { Locale.getDefault().language == "ru" }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val cameraUri = pendingCameraUri
        if (cameraUri != null) {
            context.revokeUriPermission(cameraUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        pendingCameraUri = null
        if (result.resultCode == Activity.RESULT_OK && cameraUri != null) {
            onSelectComposerMedia(
                cameraUri.toString(),
                context.resolveDisplayName(cameraUri),
                context.contentResolver.getType(cameraUri).orEmpty().ifBlank { "image/jpeg" },
            )
        }
    }
    fun openSystemCamera() {
        val cameraUri = runCatching { context.createCameraImageUri() }.getOrElse {
            Toast.makeText(
                context,
                if (localeIsRussian) "Не удалось подготовить фото" else "Unable to prepare photo",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(android.provider.MediaStore.EXTRA_OUTPUT, cameraUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            clipData = ClipData.newUri(context.contentResolver, "Skytale camera", cameraUri)
        }
        val resolvedActivities = context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY,
        )
        if (resolvedActivities.isEmpty()) {
            Toast.makeText(
                context,
                if (localeIsRussian) "Камера недоступна" else "Camera unavailable",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        pendingCameraUri = cameraUri
        resolvedActivities.mapNotNull { it.activityInfo?.packageName }.distinct().forEach { packageName ->
            context.grantUriPermission(
                packageName,
                cameraUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
        }
        cameraLauncher.launch(intent)
    }
    val requestCameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
            openSystemCamera()
        } else {
            Toast.makeText(
                context,
                if (localeIsRussian) "Нет доступа к камере" else "Camera permission denied",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
    var openedMedia by remember(state.openedChatId) { mutableStateOf<MediaModel?>(null) }
    val startReplyWithFocus: (String) -> Unit = {
        focusComposerOnReply = true
        onReplyMessage(it)
    }

    BackHandler(enabled = profileVisible) { profileVisible = false }
    BackHandler(enabled = searchVisible) {
        searchVisible = false
        onChatSearchChanged("")
        onSearchMessages()
    }
    BackHandler(enabled = composerMediaOptionsVisible) { composerMediaOptionsVisible = false }
    var isNearBottom by remember(state.openedChatId) { mutableStateOf(true) }
    val performBackAction: () -> Unit = {
        dragDistance = 0f
        when {
            profileVisible -> profileVisible = false
            showSearchResultsDialog -> showSearchResultsDialog = false
            searchVisible -> {
                searchVisible = false
                onChatSearchChanged("")
                onSearchMessages()
            }
            else -> onBack()
        }
    }
    val updateBackDrag: (Float) -> Unit = { delta ->
        dragDistance = (dragDistance + delta).coerceIn(0f, swipeThreshold * 1.35f)
        if (!backSwipeHapticTriggered && dragDistance >= swipeThreshold) {
            performGestureThresholdHaptic(view, state.settings.hapticsEnabled)
            backSwipeHapticTriggered = true
        }
        if (dragDistance >= swipeThreshold) {
            performBackAction()
        }
    }
    val cancelBackDrag: () -> Unit = {
        dragDistance = 0f
        backSwipeHapticTriggered = false
    }
    val animateBackDragReset: () -> Unit = {
        scope.launch {
            animate(
                initialValue = dragDistance,
                targetValue = 0f,
                animationSpec = tween(180, easing = FastOutSlowInEasing),
            ) { value, _ ->
                dragDistance = value
            }
        }
    }
    val finishBackDrag: () -> Unit = {
        val shouldGoBack = dragDistance >= swipeThreshold
        backSwipeHapticTriggered = false
        if (shouldGoBack) {
            performBackAction()
        } else {
            animateBackDragReset()
        }
    }

    LaunchedEffect(messages.lastOrNull()?.id) {
        if (messages.isEmpty()) {
            initialScrollDone = false
            return@LaunchedEffect
        }
        if (!initialScrollDone) {
            listState.scrollToItem(messages.lastIndex)
            initialScrollDone = true
            return@LaunchedEffect
        }
        val latestMessage = messages.last()
        val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        val bottomIndex = (messages.size + state.pendingUploads.size - 1).coerceAtLeast(0)
        val nearBottom = lastVisibleIndex >= (bottomIndex - 1).coerceAtLeast(0)
        if (nearBottom || latestMessage.isOwn) {
            listState.scrollToItem(bottomIndex)
        }
    }
    LaunchedEffect(state.selectedComposerMedia.isEmpty()) {
        if (state.selectedComposerMedia.isEmpty()) {
            composerMediaOptionsVisible = false
        }
    }
    LaunchedEffect(state.replyToMessageId, focusComposerOnReply) {
        if (state.replyToMessageId != null && focusComposerOnReply) {
            val replyIndex = messages.indexOfFirst { it.id == state.replyToMessageId }
            if (replyIndex >= 0) {
                listState.scrollToItem(replyIndex, 0)
            }
            delay(90)
            focusRequester.requestFocus()
            keyboard?.show()
            delay(120)
            if (replyIndex >= 0) {
                listState.scrollToItem(replyIndex, 0)
            }
            focusComposerOnReply = false
        }
    }
    LaunchedEffect(listState, state.hasMoreMessages, state.loadingOlderMessages, state.openedChatId, messages.size) {
        snapshotFlow {
            Triple(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset,
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1,
            )
        }.collect { (firstIndex, firstOffset, lastVisibleIndex) ->
            val bottomIndex = (messages.size + state.pendingUploads.size - 1).coerceAtLeast(0)
            isNearBottom = (messages.isEmpty() && state.pendingUploads.isEmpty()) || lastVisibleIndex >= bottomIndex
            if (initialScrollDone && firstIndex == 0 && firstOffset < 24 && state.hasMoreMessages && !state.loadingOlderMessages) {
                pendingPrependAnchorId = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.key as? String
                pendingPrependAnchorOffset = listState.firstVisibleItemScrollOffset
                onLoadOlderMessages()
            }
        }
    }
    LaunchedEffect(messages.size, state.loadingOlderMessages, pendingPrependAnchorId) {
        val anchorId = pendingPrependAnchorId ?: return@LaunchedEffect
        if (state.loadingOlderMessages) return@LaunchedEffect
        val targetIndex = messages.indexOfFirst { it.id == anchorId }
        if (targetIndex >= 0) {
            listState.scrollToItem(targetIndex, pendingPrependAnchorOffset)
        }
        pendingPrependAnchorId = null
        pendingPrependAnchorOffset = 0
    }
    LaunchedEffect(imeVisible, isNearBottom) {
        if (!imeVisible) {
            wasNearBottomBeforeImeOpen = isNearBottom
        }
    }
    LaunchedEffect(imeBottomPx, state.replyToMessageId, messages.lastOrNull()?.id, isNearBottom, initialScrollDone, state.pendingUploads.size) {
        val delta = imeBottomPx - previousImeBottomPx
        previousImeBottomPx = imeBottomPx
        if (!initialScrollDone || messages.isEmpty() || delta <= 0) return@LaunchedEffect
        delay(48)
        if (state.replyToMessageId == null) {
            if (wasNearBottomBeforeImeOpen || isNearBottom) {
                val bottomIndex = (messages.size + state.pendingUploads.size - 1).coerceAtLeast(0)
                listState.animateScrollToItem(bottomIndex)
            }
        } else {
            val replyIndex = messages.indexOfFirst { it.id == state.replyToMessageId }
            if (replyIndex >= 0) {
                listState.animateScrollToItem(replyIndex, 0)
            }
        }
    }
    LaunchedEffect(imeVisible, composerFocused, state.replyToMessageId, messages.lastOrNull()?.id, state.pendingUploads.size, wasNearBottomBeforeImeOpen, isNearBottom) {
        if (!imeVisible || !composerFocused || messages.isEmpty()) return@LaunchedEffect
        delay(72)
        if (state.replyToMessageId == null) {
            if (wasNearBottomBeforeImeOpen || isNearBottom) {
                val bottomIndex = (messages.size + state.pendingUploads.size - 1).coerceAtLeast(0)
                listState.animateScrollToItem(bottomIndex)
            }
        } else {
            val replyIndex = messages.indexOfFirst { it.id == state.replyToMessageId }
            if (replyIndex >= 0) {
                listState.animateScrollToItem(replyIndex, 0)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)))
            .pointerInput(state.openedChatId, searchVisible, profileVisible, showSearchResultsDialog, composerMediaOptionsVisible) {
                detectHorizontalDragGestures(
                    onDragStart = { cancelBackDrag() },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount > 0f) {
                            updateBackDrag(dragAmount)
                        }
                    },
                    onDragEnd = finishBackDrag,
                    onDragCancel = cancelBackDrag,
                )
            },
    ) {
        if (dragDistance > 0.5f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f))
                    .alpha((dragDistance / swipeThreshold).coerceIn(0f, 1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = animatedImeBottom),
        ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 10.dp,
            shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = strings.back)
                    }
                    chat?.peer?.let { peer ->
                        UserAvatar(
                            model = peer.avatarThumbUrl.ifBlank { peer.avatarUrl },
                            displayName = peer.nickname,
                            size = 38.dp,
                            textSize = MaterialTheme.typography.labelLarge,
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = chat?.peer != null) { profileVisible = true },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                chat?.title.orEmpty(),
                                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            if (currentContact?.isBlocked == true) {
                                Icon(
                                    Icons.Outlined.Shield,
                                    contentDescription = strings.block,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                        Text(
                            when {
                                typingUserId != null && typingUserId == chat?.peer?.id -> strings.typing
                                chat?.peer?.isOnline == true -> strings.online
                                else -> "${strings.lastSeen} ${formatPresenceTime(chat?.peer?.lastSeenAt ?: 0L, presenceTick)}"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Outlined.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text(strings.messageSearch) },
                                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                                onClick = {
                                    searchVisible = !searchVisible
                                    if (!searchVisible) {
                                        onChatSearchChanged("")
                                        onSearchMessages()
                                    }
                                    menuExpanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(if (chat?.isMuted == true) strings.unmute else strings.mute) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.VolumeOff, null) },
                                onClick = {
                                    onToggleMute()
                                    menuExpanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(if (currentContact?.isBlocked == true) strings.unblock else strings.block) },
                                leadingIcon = { Icon(Icons.Outlined.Shield, null) },
                                onClick = {
                                    onToggleBlocked()
                                    menuExpanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(strings.removeContact) },
                                leadingIcon = { Icon(Icons.Outlined.Person, null) },
                                onClick = {
                                    onRemoveContact()
                                    menuExpanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(strings.clearChat) },
                                leadingIcon = { Icon(Icons.Outlined.DeleteOutline, null) },
                                onClick = {
                                    onClearChat()
                                    menuExpanded = false
                                },
                            )
                        }
                    }
                }
                AnimatedVisibility(visible = searchVisible) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.chatSearchQuery,
                            onValueChange = { onChatSearchChanged(it) },
                            label = { Text(strings.messageSearch) },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (state.chatSearchQuery.isBlank()) {
                                        searchVisible = false
                                    }
                                    onChatSearchChanged("")
                                    onSearchMessages()
                                }) {
                                    Icon(Icons.Outlined.Close, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                        )
                        OutlinedButton(
                            onClick = {
                                onSearchMessages()
                                showSearchResultsDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.chatSearchQuery.isNotBlank(),
                        ) {
                            Icon(Icons.Outlined.Search, null)
                            Spacer(Modifier.width(8.dp))
                            Text(strings.messageSearch)
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = if (messages.isEmpty() || initialScrollDone) 1f else 0f },
                state = listState,
                contentPadding = PaddingValues(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(messages, key = { it.id }, contentType = { "message" }) { message ->
                    MessageBubble(
                        message = message,
                        replyPreview = message.replyToId?.let(replyPreviewMap::get),
                        compact = compact,
                        showSenderName = showSenderName,
                        hapticsEnabled = state.settings.hapticsEnabled,
                        highlighted = highlightedMessageId == message.id,
                        onBackDragStart = cancelBackDrag,
                        onBackDrag = updateBackDrag,
                        onBackDragEnd = finishBackDrag,
                        onBackDragCancel = cancelBackDrag,
                        onEditMessage = onEditMessage,
                        onDeleteMessage = onDeleteMessage,
                        onReplyMessage = startReplyWithFocus,
                        onOpenReplyTarget = { targetId ->
                            val targetIndex = messages.indexOfFirst { it.id == targetId }
                            if (targetIndex >= 0) {
                                scope.launch {
                                        highlightedMessageId = targetId
                                        listState.animateScrollToItem(targetIndex)
                                        delay(1400)
                                    if (highlightedMessageId == targetId) {
                                        highlightedMessageId = null
                                    }
                                }
                            }
                        },
                        onCopy = {
                            clipboard.setText(AnnotatedString(message.text))
                            Toast.makeText(context, strings.messageCopied, Toast.LENGTH_SHORT).show()
                        },
                        onOpenMedia = { media -> openedMedia = media },
                    )
                }
                items(state.pendingUploads, key = { it.localId }, contentType = { "pending-upload" }) { pending ->
                    PendingImageBubble(
                        localUri = pending.localUri,
                        progress = pending.progress,
                        onCancel = { onCancelPendingUpload(pending.localId) },
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = state.loadingOlderMessages,
                enter = fadeIn(animationSpec = tween(160)) + expandVertically(animationSpec = tween(180)),
                exit = fadeOut(animationSpec = tween(140)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    tonalElevation = 4.dp,
                    shadowElevation = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(128.dp)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                    )
                }
            }
        }
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnimatedVisibility(visible = state.selectedComposerMedia.isNotEmpty()) {
                        SelectedMediaComposer(
                            selections = state.selectedComposerMedia,
                            hapticsEnabled = state.settings.hapticsEnabled,
                            onRemove = onClearComposerMedia,
                            onLongPress = { composerMediaOptionsVisible = true },
                        )
                    }
                    AnimatedVisibility(visible = replyingMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(strings.reply, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                    Text(
                                        replyingMessage?.text.orEmpty().ifBlank { strings.messageDeleted },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                IconButton(onClick = onCancelReply) {
                                    Icon(Icons.Outlined.Close, contentDescription = strings.cancel)
                                }
                            }
                        }
                    }
                    TextField(
                        value = draft,
                        onValueChange = {
                            draft = it
                            onTypingChanged(it.isNotBlank())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { composerFocused = it.isFocused }
                            .focusRequester(focusRequester),
                        placeholder = { Text(strings.sendMessage) },
                        maxLines = 5,
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ),
                        leadingIcon = {
                            Box {
                                IconButton(onClick = {
                                    attachmentMenuExpanded = true
                                }) {
                                    Icon(Icons.Outlined.Add, contentDescription = strings.add)
                                }
                                DropdownMenu(
                                    expanded = attachmentMenuExpanded,
                                    onDismissRequest = { attachmentMenuExpanded = false },
                                ) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
                                        },
                                        text = { Text("Сделать фото") },
                                        onClick = {
                                            attachmentMenuExpanded = false
                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                                (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
                                                openSystemCamera()
                                            } else {
                                                requestCameraPermission.launch(Manifest.permission.CAMERA)
                                            }
                                        },
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_attach_gallery),
                                                contentDescription = null,
                                            )
                                        },
                                        text = { Text("Выбрать фото") },
                                        onClick = {
                                            attachmentMenuExpanded = false
                                            (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
                                            mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        },
                                    )
                                }
                            }
                        },
                    )
                }
                Spacer(Modifier.width(12.dp))
                FilledIconButton(
                    onClick = {
                        if (draft.isNotBlank() || state.selectedComposerMedia.isNotEmpty()) {
                            onSendMessage(draft.trim())
                            draft = ""
                            onTypingChanged(false)
                        }
                    },
                    modifier = Modifier.size(58.dp),
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = strings.sendMessage)
                }
            }
        }
        }
    }
    if (showSearchResultsDialog) {
        AlertDialog(
            onDismissRequest = { showSearchResultsDialog = false },
            title = { Text(strings.foundMessages) },
            text = {
                if (state.chatSearchResults.isEmpty()) {
                    Text(strings.noResults, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(modifier = Modifier.height(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.chatSearchResults, key = { it.id }) { result ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showSearchResultsDialog = false
                                        searchVisible = false
                                        scope.launch {
                                            var targetIndex = latestMessages.indexOfFirst { it.id == result.id }
                                            if (targetIndex < 0) {
                                                onEnsureMessageLoaded(result.id, result.createdAt)
                                                repeat(12) {
                                                    delay(140L)
                                                    targetIndex = latestMessages.indexOfFirst { it.id == result.id }
                                                    if (targetIndex >= 0) return@repeat
                                                }
                                            }
                                            if (targetIndex >= 0) {
                                                highlightedMessageId = result.id
                                                listState.animateScrollToItem(targetIndex)
                                                delay(1400)
                                                if (highlightedMessageId == result.id) highlightedMessageId = null
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(result.senderName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                    Text(result.text.ifBlank { strings.messageDeleted }, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text(formatPresenceTime(result.createdAt, presenceTick), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showSearchResultsDialog = false }) { Text(strings.cancel) } },
        )
    }
    AnimatedVisibility(
        visible = profileVisible && chat?.peer != null,
        enter = slideInHorizontally(initialOffsetX = { it / 5 }, animationSpec = tween(240, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(220)),
        exit = slideOutHorizontally(targetOffsetX = { it / 5 }, animationSpec = tween(200)) + fadeOut(animationSpec = tween(180)),
    ) {
        if (chat?.peer != null) {
            ContactProfileScreen(
                strings = strings,
                user = chat.peer,
                onSaveNickname = { onSaveContactNickname(chat.peer.id, it) },
                onClose = { profileVisible = false },
            )
        }
    }
    if (composerMediaOptionsVisible && state.selectedComposerMedia.isNotEmpty()) {
        AttachmentOptionsDialog(
            strings = strings,
            selection = state.selectedComposerMedia.first(),
            selectedCount = state.selectedComposerMedia.size,
            onDismiss = { composerMediaOptionsVisible = false },
            onOptionsChanged = onUpdateComposerMediaOptions,
        )
    }
    if (openedMedia != null) {
        FullscreenImageViewer(
            media = openedMedia!!,
            onClose = { openedMedia = null },
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun MessageBubble(
    message: MessageModel,
    replyPreview: MessageModel?,
    compact: Boolean,
    showSenderName: Boolean,
    hapticsEnabled: Boolean,
    highlighted: Boolean,
    onBackDragStart: () -> Unit,
    onBackDrag: (Float) -> Unit,
    onBackDragEnd: () -> Unit,
    onBackDragCancel: () -> Unit,
    onEditMessage: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onReplyMessage: (String) -> Unit,
    onOpenReplyTarget: (String) -> Unit,
    onCopy: () -> Unit,
    onOpenMedia: (MediaModel) -> Unit,
) {
    val strings = LocalSkytaleStrings.current
    val context = LocalContext.current
    val view = LocalView.current
    var menuExpanded by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(message.text) }
    val density = LocalDensity.current
    val replyThreshold = with(density) { 72.dp.toPx() }
    val maxReplyOffset = with(density) { 88.dp.toPx() }
    var replyOffset by remember(message.id) { mutableStateOf(0f) }
    var replyTriggered by remember(message.id) { mutableStateOf(false) }
    val animatedReplyOffset by animateFloatAsState(
        targetValue = replyOffset,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "reply-offset",
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    performLongPressHaptic(view, hapticsEnabled)
                    menuExpanded = true
                })
            },
        horizontalAlignment = if (message.isOwn) Alignment.End else Alignment.Start,
    ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(message.id) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                onBackDragStart()
                                replyOffset = 0f
                                replyTriggered = false
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                if (dragAmount > 0f) {
                                    onBackDrag(dragAmount)
                                } else if (dragAmount < 0) {
                                    replyOffset = (replyOffset + (-dragAmount * 0.78f)).coerceAtMost(maxReplyOffset)
                                    if (!replyTriggered && replyOffset >= replyThreshold) {
                                        replyTriggered = true
                                        performLongPressHaptic(view, hapticsEnabled)
                                        onReplyMessage(message.id)
                                    }
                                } else if (replyOffset > 0f) {
                                    replyOffset = (replyOffset - dragAmount).coerceAtLeast(0f)
                                }
                            },
                            onDragEnd = {
                                onBackDragEnd()
                                replyOffset = 0f
                                replyTriggered = false
                            },
                            onDragCancel = {
                                onBackDragCancel()
                                replyOffset = 0f
                                replyTriggered = false
                            },
                        )
                    },
            ) {
                val bubbleAlignment = if (message.isOwn) Alignment.CenterEnd else Alignment.CenterStart
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Reply,
                        contentDescription = null,
                        tint = if (animatedReplyOffset >= replyThreshold * 0.55f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                        modifier = Modifier
                            .size(18.dp)
                            .alpha((animatedReplyOffset / maxReplyOffset).coerceIn(0f, 1f)),
                    )
                }
                Box(
                    modifier = Modifier
                        .align(bubbleAlignment)
                        .offset { IntOffset(-animatedReplyOffset.roundToInt(), 0) },
                ) {
                    Box {
                        Surface(
                            color = when {
                                message.isOwn && highlighted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                message.isOwn -> MaterialTheme.colorScheme.primary
                                highlighted -> MaterialTheme.colorScheme.surfaceContainerHighest
                                else -> MaterialTheme.colorScheme.surfaceContainer
                            },
                            contentColor = if (message.isOwn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(
                                topStart = 22.dp,
                                topEnd = 22.dp,
                                bottomStart = if (message.isOwn) 22.dp else 8.dp,
                                bottomEnd = if (message.isOwn) 8.dp else 22.dp,
                            ),
                            modifier = Modifier
                                .wrapContentWidth()
                                .widthIn(max = if (compact) 216.dp else 252.dp),
                        ) {
                            Column(modifier = Modifier.padding(horizontal = if (compact) 9.dp else 11.dp, vertical = if (compact) 7.dp else 9.dp)) {
                                if (showSenderName) {
                                    Text(message.senderName, style = MaterialTheme.typography.labelMedium, modifier = Modifier.alpha(0.72f))
                                    Spacer(Modifier.height(4.dp))
                                }
                                if (replyPreview != null && message.deletedAt == 0L && !editing) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.Black.copy(alpha = if (message.isOwn) 0.12f else 0.08f),
                                        modifier = Modifier.clickable { onOpenReplyTarget(replyPreview.id) },
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                                            if (showSenderName) {
                                                Text(replyPreview.senderName, style = MaterialTheme.typography.labelSmall, modifier = Modifier.alpha(0.75f))
                                                Spacer(Modifier.height(2.dp))
                                            }
                                            Text(
                                                if (replyPreview.deletedAt > 0L) {
                                                    strings.messageDeleted
                                                } else {
                                                    replyPreview.text.ifBlank { if (replyPreview.media != null) strings.photoAttachment else "" }
                                                },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                }
                                if (message.forwardedFromUsername.isNotBlank() && message.deletedAt == 0L && !editing) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.Black.copy(alpha = if (message.isOwn) 0.12f else 0.08f),
                                        modifier = Modifier.clickable {
                                            openExternalUrl(context, "skytale://open/${message.forwardedFromUsername}")
                                        },
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                                            Text(
                                                "@${message.forwardedFromUsername}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (message.isOwn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                            )
                                            if (message.forwardedFromTitle.isNotBlank()) {
                                                Spacer(Modifier.height(2.dp))
                                                Text(
                                                    message.forwardedFromTitle,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                }
                                if (editing) {
                                    OutlinedTextField(value = editText, onValueChange = { editText = it }, modifier = Modifier.fillMaxWidth())
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(
                                            onClick = { editing = false },
                                            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                                contentColor = if (message.isOwn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                            ),
                                        ) { Text(strings.cancel) }
                                        TextButton(onClick = {
                                            onEditMessage(message.id, editText)
                                            editing = false
                                        }, colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                            contentColor = if (message.isOwn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                        )) { Text(strings.save) }
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (message.deletedAt > 0L) {
                                            Text(strings.messageDeleted, style = MaterialTheme.typography.bodyMedium)
                                        } else {
                                            if (message.media != null) {
                                                ChatImageAttachment(media = message.media, onOpen = { onOpenMedia(message.media) })
                                            }
                                            if (message.text.isNotBlank()) {
                                                MessageText(
                                                    text = message.text,
                                                    ownMessage = message.isOwn,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.align(Alignment.End),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        formatClockTime(message.createdAt),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (message.isOwn) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    if (message.isOwn) {
                                        Spacer(Modifier.width(5.dp))
                                        Icon(
                                            imageVector = if (message.status == "read") Icons.Rounded.DoneAll else Icons.Rounded.Done,
                                            contentDescription = message.status,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (message.status == "read") 1f else 0.84f),
                                        )
                                    }
                                }
                            }
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(text = { Text(strings.copy) }, leadingIcon = { Icon(Icons.Outlined.ContentCopy, null) }, onClick = {
                                onCopy()
                                menuExpanded = false
                            })
                            DropdownMenuItem(text = { Text(strings.reply) }, leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Reply, null) }, onClick = {
                                onReplyMessage(message.id)
                                menuExpanded = false
                            })
                            if (message.isOwn && message.deletedAt == 0L) {
                                DropdownMenuItem(text = { Text(strings.edit) }, leadingIcon = { Icon(Icons.Outlined.Edit, null) }, onClick = {
                                    editing = true
                                    menuExpanded = false
                                })
                                DropdownMenuItem(text = { Text(strings.delete) }, leadingIcon = { Icon(Icons.Outlined.DeleteOutline, null) }, onClick = {
                                    onDeleteMessage(message.id)
                                    menuExpanded = false
                                })
                            }
                        }
                    }
                }
            }
    }
}

@Composable
private fun InfoTab(strings: SkytaleStrings) {
    val context = LocalContext.current
    val isRussian = strings.back == "Назад"
    val infoCards = remember(isRussian) {
        if (isRussian) {
            listOf(
                InfoCardContent(
                    icon = Icons.Outlined.Info,
                    title = "Что это за мессенджер",
                    body = "Skytale - это мессенджер с нормальным фокусом на приватность и удобство. Проект сейчас в активной разработке: приложение регулярно обновляется, функции постепенно доезжают, а сама платформа ещё собирается в цельный продукт.",
                ),
                InfoCardContent(
                    icon = Icons.Outlined.Shield,
                    title = "Что с безопасностью",
                    body = "Соединение с сервером идёт только по защищённому каналу через официальный домен. В приложении отключён незашифрованный трафик, а локальные сессии и настройки на устройстве тоже не хранятся в открытом виде.",
                ),
                InfoCardContent(
                    icon = Icons.Outlined.LockOpen,
                    title = "Какое шифрование мы используем",
                    body = "Мы используем защищённое соединение и шифрование хранения сообщений. Проще говоря: переписка идёт до сервера по защищённому каналу, а сами сообщения не лежат в базе в открытом виде, а держатся в зашифрованном виде на сервере.",
                ),
                InfoCardContent(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "Как мы защищаем вашу приватность",
                    body = "Мы стараемся не превращать приложение в витрину слежки. Уже сейчас есть настройки видимости, а сам подход простой: собирать меньше лишнего, хранить аккуратнее и давать пользователю понятный контроль там, где он реально важен.",
                ),
            )
        } else {
            listOf(
                InfoCardContent(
                    icon = Icons.Outlined.Info,
                    title = "What Skytale is",
                    body = "Skytale is a messenger being built with a real focus on privacy and usability. The project is in active development, updates ship often, and a lot of the missing functionality is still being added step by step.",
                ),
                InfoCardContent(
                    icon = Icons.Outlined.Shield,
                    title = "What security looks like today",
                    body = "The app uses protected transport over the official domain, blocks cleartext traffic, and keeps session data encrypted on the device instead of leaving it in plain storage.",
                ),
                InfoCardContent(
                    icon = Icons.Outlined.LockOpen,
                    title = "What encryption we use",
                    body = "Skytale uses protected transport together with encrypted message storage. In practice that means messages travel to the server over a secured channel and are not stored in the database as plain readable text. It is a solid security baseline for the current stage of the product, and the security architecture is still evolving.",
                ),
                InfoCardContent(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "How we protect privacy",
                    body = "The goal is to avoid collecting more than the product actually needs, keep storage and transport protected, and give users clear visibility controls instead of vague promises. That part of the product is still evolving with the rest of the app.",
                ),
            )
        }
    }
    val supportTitle = if (isRussian) "Поддержка проекта" else "Support the project"
    val supportBody = if (isRussian) {
        "Skytale делает один человек, без команды и без нормального бюджета. Такой проект сложно тащить в одиночку, особенно если хочется сделать его действительно удобным и безопасным, а не просто \"ещё одним мессенджером\". Поддержка здесь - это не формальность, а реальная помощь, которая даёт проекту время и возможность расти дальше."
    } else {
        "Skytale is being built by one person without a real budget or a full team behind it. Building a messenger that is both comfortable and genuinely secure takes a lot of time, and support here is not symbolic - it directly helps the project keep moving."
    }
    val supportButton = if (isRussian) "Поддержать" else "Support"
    val changelogTitle = if (isRussian) "Changelog установленной версии" else "Installed version changelog"
    val changelogBody = if (isRussian) {
        "Версия ${BuildConfig.VERSION_NAME}\n• Профили и каналы дочищены: у своего профиля убран бессмысленный переход в публичную ссылку, username теперь копирует публичную ссылку по удержанию, а у каналов убраны дубли кнопок редактирования и смены аватарки.\n• Посты в каналах стали ровнее и спокойнее: нижняя строка больше не разъезжается, комментарии/просмотры/время стоят подряд, закреп больше не висит отдельной плашкой, а сами обновления канала больше не должны дёргать экран при редактировании и удалении.\n• Публичные страницы use.skytale.dpdns.org упрощены и приближены к основному сайту: убраны лишние рамки, свечение, пустые плашки про несуществующие публичные посты, а backend и SQLite сохранены с предыдущими оптимизациями производительности."
    } else {
        "Version ${BuildConfig.VERSION_NAME}\n• Profiles and channels were cleaned up further: your own profile no longer tries to reopen the public link, usernames now copy the public URL on long press, and channel settings no longer duplicate edit or avatar actions.\n• Channel posts now look calmer and update more safely: the footer stays in one line, comments/views/time sit in sequence, the pinned badge no longer hangs over posts, and channel updates should stop causing jumpy redraws on edits or deletes.\n• Public pages on use.skytale.dpdns.org were flattened to match the main site more closely, with the noisy framing, glow, and empty \"no public posts\" panel removed while keeping the earlier backend and SQLite performance work."
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(infoCards) { card ->
            InfoSectionCard(content = card)
        }
        item {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.FavoriteBorder, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(supportTitle, style = MaterialTheme.typography.titleMedium)
                    }
                    Text(supportBody, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(onClick = { openExternalUrl(context, "https://dalink.to/agichev") }) {
                        Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(supportButton)
                    }
                }
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.Update, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(changelogTitle, style = MaterialTheme.typography.titleMedium)
                    }
                    Text(changelogBody, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private data class InfoCardContent(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val body: String,
)

@Composable
private fun InfoSectionCard(content: InfoCardContent) {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(content.icon, null, tint = MaterialTheme.colorScheme.primary)
                }
                Text(content.title, style = MaterialTheme.typography.titleMedium)
            }
            Text(content.body, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private enum class LegalDocumentType {
    PRIVACY_POLICY,
    TERMS_OF_USE,
}

@Composable
private fun LegalConsentText(
    isRussian: Boolean,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    val linkColor = MaterialTheme.colorScheme.primary
    val bodyColor = MaterialTheme.colorScheme.onSurfaceVariant
    val annotated = remember(isRussian, linkColor) {
        buildAnnotatedString {
            if (isRussian) {
                append("Используя сервис, вы соглашаетесь с ")
                pushStringAnnotation("LEGAL", "privacy")
                pushStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Medium, textDecoration = TextDecoration.Underline))
                append("политикой конфиденциальности")
                pop()
                pop()
                append(" и ")
                pushStringAnnotation("LEGAL", "terms")
                pushStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Medium, textDecoration = TextDecoration.Underline))
                append("условиями использования")
                pop()
                pop()
                append(".")
            } else {
                append("By using the service, you agree to the ")
                pushStringAnnotation("LEGAL", "privacy")
                pushStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Medium, textDecoration = TextDecoration.Underline))
                append("Privacy Policy")
                pop()
                pop()
                append(" and ")
                pushStringAnnotation("LEGAL", "terms")
                pushStyle(SpanStyle(color = linkColor, fontWeight = FontWeight.Medium, textDecoration = TextDecoration.Underline))
                append("Terms of Use")
                pop()
                pop()
                append(".")
            }
        }
    }
    ClickableText(
        text = annotated,
        style = MaterialTheme.typography.bodySmall.copy(color = bodyColor),
        modifier = Modifier.fillMaxWidth(),
        onClick = { offset ->
            when (annotated.getStringAnnotations("LEGAL", offset, offset).firstOrNull()?.item) {
                "privacy" -> onOpenPrivacyPolicy()
                "terms" -> onOpenTerms()
            }
        },
    )
}

@Composable
private fun LegalDocumentDialog(
    type: LegalDocumentType,
    onDismiss: () -> Unit,
) {
    val strings = LocalSkytaleStrings.current
    val isRussian = strings.back == "Назад"
    val title = when (type) {
        LegalDocumentType.PRIVACY_POLICY -> if (isRussian) "Политика конфиденциальности" else "Privacy Policy"
        LegalDocumentType.TERMS_OF_USE -> if (isRussian) "Условия использования" else "Terms of Use"
    }
    val content = when (type) {
        LegalDocumentType.PRIVACY_POLICY -> if (isRussian) privacyPolicyRu else privacyPolicyEn
        LegalDocumentType.TERMS_OF_USE -> if (isRussian) termsOfUseRu else termsOfUseEn
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(if (isRussian) "Закрыть" else "Close")
                }
            }
        }
    }
}

private val privacyPolicyRu = """
Редакция от 13 мая 2026 года

1. Общие положения

Настоящая Политика конфиденциальности описывает, как в сервисе Skytale обрабатываются персональные данные пользователей. Оператором сервиса является физическое лицо, действующее под именем Agichev и организующее разработку и работу сервиса Skytale.

Политика подготовлена с учетом требований законодательства Российской Федерации о персональных данных, включая Федеральный закон от 27 июля 2006 года № 152-ФЗ «О персональных данных». Если к обработке данных применяются иные обязательные требования закона, сервис исходит из необходимости их соблюдения.

2. Какие данные мы обрабатываем

В зависимости от того, как вы используете сервис, могут обрабатываться:

- регистрационные данные: имя пользователя, пароль в защищённом виде, язык интерфейса, никнейм;
- данные профиля: описание профиля, аватар, настройки видимости;
- сервисные данные: идентификатор аккаунта, идентификаторы сессий, название устройства, время входов и активности;
- данные переписки: текст сообщений, вложения, сведения о доставке, прочтении, ответах и удалении сообщений;
- технические данные, необходимые для работы сервиса и защиты от злоупотреблений: сетевые и служебные сведения из запросов, данные о работе приложения, сведения об ошибках и сбоях;
- локальные данные на устройстве: кэш сообщений, настройки и данные сессии в пределах, необходимых для работы приложения.

3. Для чего мы обрабатываем данные

Персональные данные обрабатываются для следующих целей:

- регистрации и входа в аккаунт;
- доставки сообщений, вложений и служебных событий;
- работы списка контактов, чатов, поиска и настроек;
- обеспечения безопасности аккаунта, сессий и сервиса;
- исправления ошибок, поддержки пользователей и развития продукта;
- исполнения требований законодательства Российской Федерации и законных запросов уполномоченных органов в случаях, прямо предусмотренных законом.

4. Правовые основания обработки

Обработка данных осуществляется, в зависимости от ситуации:

- с вашего согласия;
- для заключения и исполнения пользовательского соглашения, стороной которого вы являетесь;
- для соблюдения обязанностей, установленных законодательством Российской Федерации;
- для осуществления законных интересов оператора в части обеспечения работоспособности, безопасности и устойчивости сервиса, если такие интересы не нарушают ваши права и свободы.

5. Как именно хранятся и защищаются данные

Skytale использует технические и организационные меры защиты данных. Передача данных между приложением и сервером осуществляется по защищённым каналам связи. Сообщения и часть служебных данных хранятся в зашифрованном виде. На устройстве пользователя чувствительные локальные данные также защищаются средствами платформы.

При этом ни одна система, подключённая к сети Интернет, не может считаться абсолютно неуязвимой. Оператор принимает разумные меры для снижения рисков несанкционированного доступа, утраты, изменения или неправомерного распространения данных.

6. Передача данных третьим лицам

Оператор не продаёт персональные данные пользователей.

Данные могут передаваться третьим лицам только в объёме, необходимом для работы сервиса, например:

- хостинг- и инфраструктурным провайдерам;
- поставщикам сетевых, серверных и защитных технологий;
- провайдерам, через которых обрабатываются и доставляются медиафайлы, если это требуется текущей архитектурой сервиса.

На момент настоящей редакции отдельные изображения, отправляемые через сервис, могут обрабатываться с привлечением внешнего медиа-провайдера. Это делается исключительно для загрузки, хранения и доставки вложений.

Отдельно данные могут быть предоставлены государственным органам и иным лицам в случаях и в порядке, прямо предусмотренных законодательством Российской Федерации.

7. Локализация и срок хранения

При сборе персональных данных граждан Российской Федерации сервис исходит из необходимости их записи, систематизации, накопления, хранения, уточнения и извлечения с использованием баз данных, находящихся на территории Российской Федерации, если иное не допускается законом.

Данные хранятся не дольше, чем это необходимо для целей обработки, если иной срок не вытекает из закона, обязательств по договору, особенностей работы сервиса или необходимости защиты сервиса от злоупотреблений и споров.

8. Ваши права

Вы вправе:

- получать сведения об обработке ваших персональных данных;
- требовать уточнения, обновления, блокирования или удаления данных, если они неполные, устаревшие, неточные, незаконно получены или не нужны для заявленной цели;
- отозвать согласие на обработку данных в случаях, когда обработка основана на согласии;
- прекратить использование сервиса и потребовать удаление аккаунта, если иное хранение не требуется по закону или для защиты законных интересов оператора;
- обжаловать действия оператора в уполномоченный орган или в суд.

9. Удаление аккаунта и переписки

Вы можете удалить аккаунт средствами сервиса, если такая функция доступна в текущей версии приложения. Удаление аккаунта не всегда означает мгновенное физическое уничтожение всех технических следов в резервных копиях, журналах безопасности и иных системах, где сохранение допускается законом или необходимо для восстановления, устойчивости и защиты от злоупотреблений.

10. Несовершеннолетние

Сервис не предназначен для использования лицами, не обладающими необходимой дееспособностью для принятия условий использования сервиса, если иное не допускается законом и не осуществляется с участием законных представителей.

11. Изменение Политики

Политика может обновляться по мере развития сервиса, изменения архитектуры, функциональности или требований закона. Актуальная редакция доводится до пользователей через приложение или иные официальные каналы сервиса.

12. Контакты

По вопросам обработки персональных данных и реализации ваших прав вы можете обратиться к оператору через официальные каналы проекта Skytale / Agichev, доступные пользователю в рамках сервиса или на официальных страницах проекта.
""".trimIndent()

private val termsOfUseRu = """
Редакция от 13 мая 2026 года

1. Общие положения

Настоящие Условия использования регулируют доступ к сервису Skytale и его использование. Начиная использовать сервис, вы подтверждаете, что ознакомились с настоящими условиями и принимаете их.

Сервис Skytale предоставляется физическим лицом, действующим под именем Agichev. Сервис находится в активной стадии разработки, а отдельные функции могут меняться, добавляться, ограничиваться или удаляться без предварительного уведомления, если это требуется для развития, безопасности или стабильности продукта.

2. Назначение сервиса

Skytale предназначен для личного обмена сообщениями, медиафайлами и связанными с ними данными в рамках доступной функциональности приложения.

3. Регистрация и доступ

Для использования основных функций сервиса вам может потребоваться регистрация аккаунта. Вы обязаны:

- предоставлять данные, которые не вводят сервис в заблуждение;
- не передавать свой аккаунт третьим лицам;
- самостоятельно обеспечивать конфиденциальность пароля и безопасность устройства;
- незамедлительно прекратить использование сервиса и сменить пароль при подозрении на компрометацию аккаунта.

Вы несёте ответственность за действия, совершённые через ваш аккаунт, если иное не будет доказано.

4. Допустимое использование

Запрещается использовать сервис для:

- нарушения законодательства Российской Федерации;
- распространения вредоносного кода, спама, мошеннических сообщений, угроз или материалов, нарушающих права третьих лиц;
- попыток несанкционированного доступа к аккаунтам, переписке, серверам, исходному коду, базе данных или инфраструктуре сервиса;
- вмешательства в работу сервиса, обхода ограничений, перегрузки API, перебора учётных данных или эксплуатации уязвимостей;
- использования сервиса способом, который может причинить вред пользователям, оператору или третьим лицам.

5. Контент пользователя

Права на содержимое ваших сообщений и материалов сохраняются за вами или иным правообладателем. При этом вы предоставляете оператору ограниченное право использовать такой контент в объёме, необходимом для работы сервиса: передачи, хранения, отображения в интерфейсе, обработки вложений, обеспечения доставки, безопасности и резервирования.

Вы подтверждаете, что имеете право отправлять размещаемый вами контент и что такой контент не нарушает закон и права третьих лиц.

6. Безопасность и технические ограничения

Оператор принимает разумные меры для защиты сервиса, однако не гарантирует абсолютную безошибочность, непрерывную доступность и полную неуязвимость системы. Сервис предоставляется по модели «как есть» и «по мере доступности».

Оператор вправе:

- ограничивать отдельные функции;
- временно приостанавливать работу сервиса;
- блокировать отдельные запросы, устройства, аккаунты или действия при подозрении на злоупотребление или угрозу безопасности;
- удалять контент или ограничивать доступ к нему, если это требуется законом, безопасностью или защитой сервиса.

7. Конфиденциальность и персональные данные

Обработка персональных данных регулируется отдельной Политикой конфиденциальности, которая является неотъемлемо связанной с использованием сервиса. Использование сервиса означает также ознакомление с этой политикой.

8. Вложения и внешние провайдеры

Некоторые вложения и медиафайлы могут обрабатываться с участием внешних технологических провайдеров, если это необходимо для загрузки, хранения, преобразования или доставки контента. Используя функции отправки медиа, вы соглашаетесь с такой технически необходимой обработкой.

9. Прекращение доступа

Вы можете прекратить использование сервиса в любой момент. Оператор также вправе ограничить или прекратить доступ к сервису полностью или частично, если:

- пользователь нарушает настоящие условия;
- дальнейшее использование создаёт риски для безопасности;
- этого требует закон или обязательное предписание уполномоченного органа;
- сервис меняет модель работы, закрывается или переводится в иной режим эксплуатации.

10. Ответственность

В пределах, допускаемых законодательством Российской Федерации, оператор не несёт ответственности за:

- косвенные убытки, упущенную выгоду, потерю данных или репутационные потери;
- действия третьих лиц, включая неправомерный доступ, если оператор принял разумные меры защиты;
- временную недоступность сервиса, перебои связи, сбои оборудования, программного обеспечения или внешних поставщиков;
- содержание сообщений и материалов, размещаемых пользователями.

Ничто в настоящих условиях не исключает ответственность, которую нельзя исключить или ограничить в силу закона.

11. Применимое право

К настоящим условиям подлежит применению законодательство Российской Федерации. Споры подлежат разрешению в порядке, установленном действующим законодательством Российской Федерации.

12. Изменение условий

Настоящие условия могут изменяться по мере развития сервиса и изменения правовых или технических требований. Продолжение использования сервиса после публикации новой редакции означает согласие с обновлёнными условиями, если иное не предусмотрено законом.
""".trimIndent()

private val privacyPolicyEn = """
Version dated May 13, 2026

1. General

This Privacy Policy explains how Skytale processes personal data. The service is operated by an individual acting under the name Agichev.

2. Data we may process

Depending on how you use the service, Skytale may process account details, profile data, session and device information, message and media data, delivery and read status, service logs, crash data, and local app data required for the app to function.

3. Why we process data

We process data to register accounts, authenticate users, deliver messages and media, run chats and contacts, protect accounts and infrastructure, improve the product, provide support, and comply with legal obligations.

4. Legal basis

Processing may rely on your consent, on performance of the service agreement with you, on legal obligations, and on legitimate interests related to security, stability, and abuse prevention where applicable.

5. Security

Skytale uses protected transport, encrypted local storage, and encrypted message storage on the server side. Reasonable technical and organizational measures are used to reduce the risk of unauthorized access, alteration, disclosure, or loss. No Internet-connected system can be guaranteed to be perfectly secure.

6. Third parties

We do not sell personal data. Data may be processed by hosting, infrastructure, network, and media providers to the extent necessary to operate the service. Some image attachments may currently be handled through an external media provider for upload, storage, or delivery.

7. Storage and localization

Data is stored only for as long as needed for the purposes of processing, unless a longer period is required by law, contract, dispute resolution, or security needs.

8. Your rights

You may request information about processing, ask for correction or deletion where legally appropriate, withdraw consent where consent is the basis for processing, and stop using the service or request account deletion subject to legal and technical retention limits.

9. Updates

This policy may change as the service evolves or legal requirements change. The current version is made available through the service.
""".trimIndent()

private val termsOfUseEn = """
Version dated May 13, 2026

1. General

These Terms of Use govern access to and use of Skytale. By using the service, you agree to these terms.

2. Nature of the service

Skytale is an actively developing messaging product. Features may change, be limited, or be removed as the product evolves.

3. Account responsibility

You are responsible for keeping your credentials and device secure, for providing non-misleading account data, and for activity performed through your account unless proven otherwise.

4. Prohibited conduct

You may not use the service for unlawful activity, spam, fraud, malware, unauthorized access, credential attacks, infrastructure abuse, or interference with the service or other users.

5. User content

You retain rights to your content, but grant the operator the limited rights required to transmit, store, process, display, secure, and deliver that content as part of the service.

6. Availability and security

The service is provided on an “as is” and “as available” basis. Reasonable measures are taken to protect the service, but uninterrupted operation and absolute security cannot be guaranteed.

7. Privacy

Use of the service is also subject to the Privacy Policy.

8. Third-party providers

Some media handling functions may involve third-party technical providers where required for upload, storage, transformation, or delivery.

9. Suspension and termination

The operator may suspend or terminate access where required by law, necessary for security, or justified by a violation of these terms.

10. Governing law

These terms are governed by the laws applicable to the operator and the service. For users in the Russian Federation, mandatory rules of Russian law may apply.
""".trimIndent()

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun SettingsCategoryCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val view = LocalView.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { performLongPressHaptic(view) },
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsRootContent(
    strings: SkytaleStrings,
    onOpenMessages: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAccount: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { SettingsSectionHeader(title = strings.settingsOverview) }
        item {
            SettingsCategoryCard(
                title = strings.messageSettings,
                subtitle = strings.notifications,
                icon = Icons.Outlined.Notifications,
                onClick = onOpenMessages,
            )
        }
        item {
            SettingsCategoryCard(
                title = strings.appearanceSettings,
                subtitle = strings.compactMode,
                icon = Icons.Outlined.Bolt,
                onClick = onOpenAppearance,
            )
        }
        item {
            SettingsCategoryCard(
                title = strings.privacySettings,
                subtitle = strings.onlineVisibility,
                icon = Icons.Outlined.Shield,
                onClick = onOpenPrivacy,
            )
        }
        item {
            SettingsCategoryCard(
                title = strings.accountSettings,
                subtitle = strings.security,
                icon = Icons.Outlined.LockOpen,
                onClick = onOpenAccount,
            )
        }
    }
}

private enum class SettingsSection {
    ROOT,
    MESSAGES,
    APPEARANCE,
    PRIVACY,
    ACCOUNT,
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SettingsTab(
    state: SkytaleUiState,
    strings: SkytaleStrings,
    onUpdateNotifications: (Boolean) -> Unit,
    onUpdatePreviews: (Boolean) -> Unit,
    onUpdateSound: (Boolean) -> Unit,
    onUpdateInstantSync: (Boolean) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onSettingsLanguageSelected: (AppLanguage) -> Unit,
    onUpdateCompact: (Boolean) -> Unit,
    onUpdateHaptics: (Boolean) -> Unit,
    onUpdateArchivedChats: (Boolean) -> Unit,
    onUpdateAppLock: (Boolean) -> Unit,
    onUpdateGhostMode: (Boolean) -> Unit,
    onLastSeenVisibilityChanged: (String) -> Unit,
    onUsernameDiscoverableChanged: (Boolean) -> Unit,
    onSaveProfile: () -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    onLogoutOthers: () -> Unit,
    onRootStateChanged: (Boolean) -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    val view = LocalView.current
    var section by remember { mutableStateOf(SettingsSection.ROOT) }
    var passwordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var deleteDialog by remember { mutableStateOf(false) }
    val ghostEnabled = state.lastSeenVisibility == "ghost"
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 42.dp.toPx() }
    var dragOffsetPx by remember(section) { mutableStateOf(0f) }
    var swipeHapticTriggered by remember(section) { mutableStateOf(false) }

    BackHandler(enabled = section != SettingsSection.ROOT) {
        section = SettingsSection.ROOT
    }

    LaunchedEffect(section) {
        onRootStateChanged(section == SettingsSection.ROOT)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(section) {
                if (section != SettingsSection.ROOT) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            dragOffsetPx = 0f
                            swipeHapticTriggered = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (dragAmount > 0) {
                                dragOffsetPx = (dragOffsetPx + dragAmount).coerceIn(0f, swipeThreshold * 1.35f)
                                if (!swipeHapticTriggered && dragOffsetPx >= swipeThreshold) {
                                    performGestureThresholdHaptic(view, state.settings.hapticsEnabled)
                                    swipeHapticTriggered = true
                                }
                            }
                        },
                        onDragEnd = {
                            if (dragOffsetPx >= swipeThreshold && section != SettingsSection.ROOT) {
                                section = SettingsSection.ROOT
                            } else {
                                dragOffsetPx = 0f
                            }
                            swipeHapticTriggered = false
                        },
                        onDragCancel = {
                            dragOffsetPx = 0f
                            swipeHapticTriggered = false
                        },
                    )
                }
            },
    ) {
        if (dragOffsetPx > 0.5f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f))
                    .alpha((dragOffsetPx / swipeThreshold).coerceIn(0f, 1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        AnimatedContent(
            targetState = section,
            transitionSpec = {
                val forward = targetState.ordinal > initialState.ordinal
                (slideInHorizontally { if (forward) it / 5 else -it / 5 } + fadeIn(animationSpec = tween(220))) togetherWith
                    (slideOutHorizontally { if (forward) -it / 6 else it / 6 } + fadeOut(animationSpec = tween(180)))
            },
            label = "settings-section-transition",
            modifier = Modifier.fillMaxSize(),
        ) { currentSection ->
            if (currentSection == SettingsSection.ROOT) {
                SettingsRootContent(
                    strings = strings,
                    onOpenMessages = { section = SettingsSection.MESSAGES },
                    onOpenAppearance = { section = SettingsSection.APPEARANCE },
                    onOpenPrivacy = { section = SettingsSection.PRIVACY },
                    onOpenAccount = { section = SettingsSection.ACCOUNT },
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    when (currentSection) {
            SettingsSection.MESSAGES -> {
                item { SettingsSectionHeader(title = strings.messageSettings, onBack = { section = SettingsSection.ROOT }) }
                item { SettingToggle(strings.notifications, state.settings.notificationsEnabled, onUpdateNotifications) }
                item { SettingToggle(strings.sound, state.settings.soundEnabled, onUpdateSound) }
                item { SettingToggle(strings.instantSync, state.settings.instantSyncEnabled, onUpdateInstantSync) }
            }

            SettingsSection.APPEARANCE -> {
                item { SettingsSectionHeader(title = strings.appearanceSettings, onBack = { section = SettingsSection.ROOT }) }
                item {
                    Card(modifier = Modifier.height(0.dp), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(strings.language, style = MaterialTheme.typography.titleMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(
                                    onClick = { onSettingsLanguageSelected(AppLanguage.EN) },
                                    label = { Text("English") },
                                    leadingIcon = if (state.language == AppLanguage.EN) ({ Icon(Icons.Outlined.Bolt, null, modifier = Modifier.size(16.dp)) }) else null,
                                )
                                AssistChip(
                                    onClick = { onSettingsLanguageSelected(AppLanguage.RU) },
                                    label = { Text("Русский") },
                                    leadingIcon = if (state.language == AppLanguage.RU) ({ Icon(Icons.Outlined.Bolt, null, modifier = Modifier.size(16.dp)) }) else null,
                                )
                            }
                        }
                    }
                }
                item {
                    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(strings.themeModeLabel, style = MaterialTheme.typography.titleMedium)
                            ThemeOptionRow(
                                current = state.settings.themeMode,
                                strings = strings,
                                onSelect = onUpdateThemeMode,
                            )
                        }
                    }
                }
                item { SettingToggle(strings.compactMode, state.settings.compactMode, onUpdateCompact) }
                item { SettingToggle(strings.haptics, state.settings.hapticsEnabled, onUpdateHaptics) }
                item { SettingToggle(strings.archivedChats, state.settings.showArchivedChats, onUpdateArchivedChats) }
                item { SettingToggle(strings.appLock, state.settings.appLockEnabled, onUpdateAppLock) }
                item {
                    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(strings.language, style = MaterialTheme.typography.titleMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SelectionChip(
                                    label = "English",
                                    selected = state.language == AppLanguage.EN,
                                    onClick = { onSettingsLanguageSelected(AppLanguage.EN) },
                                )
                                SelectionChip(
                                    label = "Русский",
                                    selected = state.language == AppLanguage.RU,
                                    onClick = { onSettingsLanguageSelected(AppLanguage.RU) },
                                )
                            }
                        }
                    }
                }
            }

            SettingsSection.PRIVACY -> {
                item { SettingsSectionHeader(title = strings.privacySettings, onBack = { section = SettingsSection.ROOT }) }
                item {
                    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(strings.onlineVisibility, style = MaterialTheme.typography.titleMedium)
                            PrivacyOptionRow(
                                selected = state.lastSeenVisibility,
                                everyone = strings.everyone,
                                contactsOnly = strings.contactsOnly,
                                nobody = strings.nobody,
                                onSelect = onLastSeenVisibilityChanged,
                            )
                            HorizontalDivider()
                            SettingToggleInline(
                                title = strings.ghostMode,
                                checked = ghostEnabled,
                                onCheckedChange = onUpdateGhostMode,
                            )
                            Text(strings.ghostModeHint, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            HorizontalDivider()
                            Text(strings.usernameDiscoverabilityLabel, style = MaterialTheme.typography.titleMedium)
                            SettingToggleInline(
                                title = strings.searchPeople,
                                checked = state.usernameDiscoverable,
                                onCheckedChange = onUsernameDiscoverableChanged,
                            )
                            OutlinedButton(onClick = onSaveProfile, modifier = Modifier.fillMaxWidth()) {
                                Text(strings.applyChanges)
                            }
                        }
                    }
                }
            }

            SettingsSection.ACCOUNT -> {
                item { SettingsSectionHeader(title = strings.accountSettings, onBack = { section = SettingsSection.ROOT }) }
                item {
                    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(strings.security, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(10.dp))
                            OutlinedButton(onClick = { passwordDialog = true }, modifier = Modifier.fillMaxWidth()) { Text(strings.changePassword) }
                            Spacer(Modifier.height(10.dp))
                            OutlinedButton(onClick = onLogoutOthers, modifier = Modifier.fillMaxWidth()) { Text(strings.logoutOthers) }
                            Spacer(Modifier.height(10.dp))
                            OutlinedButton(onClick = { deleteDialog = true }, modifier = Modifier.fillMaxWidth()) { Text(strings.deleteAccount) }
                            Spacer(Modifier.height(10.dp))
                            TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text(strings.logout, color = MaterialTheme.colorScheme.primary) }
                            Spacer(Modifier.height(18.dp))
                            LegalConsentText(
                                isRussian = strings.back == "Назад",
                                onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                                onOpenTerms = onOpenTerms,
                            )
                        }
                    }
                }
            }
            SettingsSection.ROOT -> Unit
        }
                }
            }
        }
    }
    if (passwordDialog) {
        AlertDialog(
            onDismissRequest = { passwordDialog = false },
            title = { Text(strings.changePassword) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text(strings.currentPassword) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(strings.newPassword) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onChangePassword(currentPassword, newPassword)
                    currentPassword = ""
                    newPassword = ""
                    passwordDialog = false
                }) { Text(strings.save) }
            },
            dismissButton = {
                TextButton(onClick = {
                    currentPassword = ""
                    newPassword = ""
                    passwordDialog = false
                }) { Text(strings.cancel) }
            },
        )
    }
    if (deleteDialog) {
        AlertDialog(
            onDismissRequest = { deleteDialog = false },
            title = { Text(strings.deleteAccount) },
            text = { Text(strings.deleteAccountWarning) },
            confirmButton = {
                TextButton(onClick = {
                    deleteDialog = false
                    onDeleteAccount()
                }) { Text(strings.delete, color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = { TextButton(onClick = { deleteDialog = false }) { Text(strings.cancel) } },
        )
    }
}

@Composable
private fun SettingToggle(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean = true) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                title,
                modifier = Modifier.weight(1f),
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Switch(checked = checked, onCheckedChange = if (enabled) onCheckedChange else null, enabled = enabled)
        }
    }
}

@Composable
private fun SettingToggleInline(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun PrivacyOptionRow(
    selected: String,
    everyone: String,
    contactsOnly: String,
    nobody: String,
    onSelect: (String) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            "everyone" to everyone,
            "contacts" to contactsOnly,
            "nobody" to nobody,
        ).forEach { (value, label) ->
            SelectionChip(
                label = label,
                selected = selected == value,
                onClick = { onSelect(value) },
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ThemeOptionRow(
    current: ThemeMode,
    strings: SkytaleStrings,
    onSelect: (ThemeMode) -> Unit,
) {
    val normalizedCurrent = if (current == ThemeMode.LIGHT_CLASSIC) ThemeMode.LIGHT else current
    val labels = listOf(
        ThemeMode.DARK to strings.themeScarletDark,
        ThemeMode.LIGHT to strings.themeSoftLight,
        ThemeMode.MONO_DARK to strings.themeMonoDark,
        ThemeMode.TELEGRAM_DARK to strings.themeTelegramDark,
        ThemeMode.SYSTEM to strings.themeSystem,
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEach { (mode, label) ->
            SelectionChip(
                label = label,
                selected = mode == normalizedCurrent,
                onClick = { onSelect(mode) },
            )
        }
    }
}

@Composable
private fun SelectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
        border = BorderStroke(
            width = if (selected) 1.8.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.72f),
        ),
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String, onBack: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ProfileTab(
    state: SkytaleUiState,
    strings: SkytaleStrings,
    onUsernameChanged: (String) -> Unit,
    onNicknameChanged: (String) -> Unit,
    onAboutChanged: (String) -> Unit,
    onSelectProfileAvatar: (String, String, String) -> Unit,
    onClearProfileAvatar: () -> Unit,
    onSaveProfile: () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val isRussian = remember { Locale.getDefault().language == "ru" }
    var pendingAvatarCrop by remember { mutableStateOf<group.skytale.app.DraftMediaSelection?>(null) }
    var editing by remember { mutableStateOf(false) }
    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            pendingAvatarCrop = group.skytale.app.DraftMediaSelection(
                uri = it.toString(),
                fileName = context.resolveDisplayName(it),
                mimeType = context.contentResolver.getType(it).orEmpty(),
            )
        }
    }
    val avatarModel = when {
        state.removeProfileAvatar -> ""
        state.selectedProfileAvatar != null -> state.selectedProfileAvatar.uri
        else -> state.session?.avatarThumbUrl?.ifBlank { state.session?.avatarUrl }.orEmpty()
    }
    var avatarMenuExpanded by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Card(shape = RoundedCornerShape(34.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (!editing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                UserAvatar(
                                    model = avatarModel,
                                    displayName = state.nickname.ifBlank { state.session?.nickname.orEmpty() },
                                    size = 92.dp,
                                    textSize = MaterialTheme.typography.titleLarge,
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(state.nickname.ifBlank { state.session?.nickname.orEmpty() }, style = MaterialTheme.typography.headlineMedium)
                                if (state.session?.username?.isNotBlank() == true) {
                                    ProfileUsernameLink(
                                        username = state.session.username,
                                        enableOpen = false,
                                    )
                                }
                                Text(
                                    if (state.session == null) "" else strings.online,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            FilledIconButton(onClick = { editing = true }, modifier = Modifier.size(38.dp)) {
                                Icon(Icons.Outlined.Edit, contentDescription = strings.edit, modifier = Modifier.size(18.dp))
                            }
                        }
                        if (state.about.isNotBlank()) {
                            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    MessageText(
                                        text = state.about,
                                        ownMessage = false,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), CircleShape)
                                    .combinedClickable(
                                        onClick = {
                                            (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
                                            avatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        },
                                        onLongClick = {
                                            if (avatarModel.isNotBlank()) {
                                                performLongPressHaptic(view)
                                                avatarMenuExpanded = true
                                            }
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                UserAvatar(
                                    model = avatarModel,
                                    displayName = state.nickname.ifBlank { state.session?.nickname.orEmpty() },
                                    size = 92.dp,
                                    textSize = MaterialTheme.typography.titleLarge,
                                )
                                DropdownMenu(expanded = avatarMenuExpanded, onDismissRequest = { avatarMenuExpanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text(strings.delete) },
                                        leadingIcon = { Icon(Icons.Outlined.DeleteOutline, null) },
                                        onClick = {
                                            avatarMenuExpanded = false
                                            onClearProfileAvatar()
                                        },
                                    )
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(state.nickname.ifBlank { state.session?.nickname.orEmpty() }, style = MaterialTheme.typography.headlineMedium)
                                Text(state.session?.username.orEmpty(), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    if (state.session == null) "" else strings.online,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            TextButton(onClick = { editing = false }) {
                                Text(strings.cancel)
                            }
                        }
                        if (state.selectedProfileAvatar != null) {
                            TextButton(onClick = onClearProfileAvatar, modifier = Modifier.align(Alignment.End)) {
                                Text(strings.cancel)
                            }
                        }
                        OutlinedTextField(value = state.username, onValueChange = onUsernameChanged, label = { Text(strings.username) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = state.nickname, onValueChange = onNicknameChanged, label = { Text(strings.nickname) }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = state.about, onValueChange = onAboutChanged, label = { Text(strings.about) }, modifier = Modifier.fillMaxWidth(), minLines = 4)
                        FilledIconButton(
                            onClick = {
                                onSaveProfile()
                                editing = false
                            },
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Icon(Icons.Rounded.Done, contentDescription = strings.save)
                        }
                    }
                }
            }
        }
    }
    pendingAvatarCrop?.let { selection ->
        AvatarCropDialog(
            selection = selection,
            onDismiss = { pendingAvatarCrop = null },
            onConfirm = { croppedUri, fileName, mimeType ->
                pendingAvatarCrop = null
                onSelectProfileAvatar(croppedUri, fileName, mimeType)
            },
        )
    }
}

@Composable
private fun AvatarCropDialog(
    selection: group.skytale.app.DraftMediaSelection,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
) {
    val context = LocalContext.current
    val strings = LocalSkytaleStrings.current
    val density = LocalDensity.current
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = selection.uri) {
        value = runCatching { context.loadBitmapForCropping(Uri.parse(selection.uri)) }.getOrNull()
    }
    var zoom by remember(selection.uri) { mutableStateOf(1f) }
    var offsetX by remember(selection.uri) { mutableStateOf(0f) }
    var offsetY by remember(selection.uri) { mutableStateOf(0f) }
    val cropSize = 280.dp
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Обрезка аватарки", style = MaterialTheme.typography.titleLarge)
                if (bitmap == null) {
                    Box(modifier = Modifier.fillMaxWidth().height(cropSize), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val sourceBitmap = bitmap!!
                    val cropSizePx = with(density) { cropSize.toPx() }
                    val baseScale = maxOf(
                        cropSizePx / sourceBitmap.width.toFloat(),
                        cropSizePx / sourceBitmap.height.toFloat(),
                    )
                    val displayWidth = sourceBitmap.width * baseScale
                    val displayHeight = sourceBitmap.height * baseScale
                    val scaledWidth = displayWidth * zoom
                    val scaledHeight = displayHeight * zoom
                    val maxOffsetX = ((scaledWidth - cropSizePx) / 2f).coerceAtLeast(0f)
                    val maxOffsetY = ((scaledHeight - cropSizePx) / 2f).coerceAtLeast(0f)
                    offsetX = offsetX.coerceIn(-maxOffsetX, maxOffsetX)
                    offsetY = offsetY.coerceIn(-maxOffsetY, maxOffsetY)
                    Box(
                        modifier = Modifier
                            .size(cropSize)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.Black.copy(alpha = 0.9f))
                            .pointerInput(selection.uri) {
                                detectTransformGestures { _, pan, gestureZoom, _ ->
                                    val updatedZoom = (zoom * gestureZoom).coerceIn(1f, 4f)
                                    val updatedScale = baseScale * updatedZoom
                                    val updatedWidth = sourceBitmap.width * updatedScale
                                    val updatedHeight = sourceBitmap.height * updatedScale
                                    val updatedMaxOffsetX = ((updatedWidth - cropSizePx) / 2f).coerceAtLeast(0f)
                                    val updatedMaxOffsetY = ((updatedHeight - cropSizePx) / 2f).coerceAtLeast(0f)
                                    zoom = updatedZoom
                                    offsetX = (offsetX + pan.x).coerceIn(-updatedMaxOffsetX, updatedMaxOffsetX)
                                    offsetY = (offsetY + pan.y).coerceIn(-updatedMaxOffsetY, updatedMaxOffsetY)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            bitmap = sourceBitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(
                                    with(density) { displayWidth.toDp() },
                                    with(density) { displayHeight.toDp() },
                                )
                                .graphicsLayer {
                                    scaleX = zoom
                                    scaleY = zoom
                                    translationX = offsetX
                                    translationY = offsetY
                                },
                            contentScale = ContentScale.FillBounds,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(2.dp, Color.White.copy(alpha = 0.92f), RoundedCornerShape(28.dp)),
                        )
                    }
                    Text(
                        "Потяните и увеличьте изображение, чтобы выбрать область.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(strings.cancel) }
                    TextButton(
                        enabled = bitmap != null,
                        onClick = {
                            val sourceBitmap = bitmap ?: return@TextButton
                            val cropSizePx = with(density) { cropSize.toPx() }
                            val scale = maxOf(
                                cropSizePx / sourceBitmap.width.toFloat(),
                                cropSizePx / sourceBitmap.height.toFloat(),
                            ) * zoom
                            val cropped = cropSquareBitmap(sourceBitmap, cropSizePx, scale, offsetX, offsetY)
                            val saved = context.saveCroppedAvatar(selection.fileName, cropped)
                            cropped.recycle()
                            if (saved != null) {
                                onConfirm(saved.first.toString(), saved.second, saved.third)
                            } else {
                                Toast.makeText(context, "Не удалось подготовить аватарку", Toast.LENGTH_SHORT).show()
                            }
                        },
                    ) { Text(strings.save) }
                }
            }
        }
    }
}

private fun Context.loadBitmapForCropping(uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri)) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } else {
        contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
            ?: error("Unable to decode image")
    }
}

private fun cropSquareBitmap(
    bitmap: Bitmap,
    cropSizePx: Float,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
): Bitmap {
    val sourceSize = (cropSizePx / scale).coerceAtMost(minOf(bitmap.width, bitmap.height).toFloat())
    val left = (((bitmap.width - sourceSize) / 2f) - (offsetX / scale))
        .coerceIn(0f, (bitmap.width - sourceSize).coerceAtLeast(0f))
    val top = (((bitmap.height - sourceSize) / 2f) - (offsetY / scale))
        .coerceIn(0f, (bitmap.height - sourceSize).coerceAtLeast(0f))
    val size = sourceSize.roundToInt()
        .coerceAtLeast(1)
        .coerceAtMost(bitmap.width - left.roundToInt())
        .coerceAtMost(bitmap.height - top.roundToInt())
    return Bitmap.createBitmap(bitmap, left.roundToInt(), top.roundToInt(), size, size)
}

private fun Context.saveCroppedAvatar(
    originalFileName: String,
    bitmap: Bitmap,
): Triple<Uri, String, String>? {
    return runCatching {
        val directory = File(cacheDir, "camera").apply { mkdirs() }
        val mimeType = if (bitmap.hasAlpha()) "image/png" else "image/jpeg"
        val extension = if (bitmap.hasAlpha()) "png" else "jpg"
        val fileBase = originalFileName.substringBeforeLast('.').ifBlank { "avatar" }
        val outputFile = File(directory, "$fileBase-cropped-${System.currentTimeMillis()}.$extension")
        FileOutputStream(outputFile).use { stream ->
            bitmap.compress(if (bitmap.hasAlpha()) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG, 96, stream)
        }
        Triple(
            FileProvider.getUriForFile(this, "$packageName.fileprovider", outputFile),
            outputFile.name,
            mimeType,
        )
    }.getOrNull()
}

@Composable
private fun UserAvatar(
    model: String,
    displayName: String,
    size: androidx.compose.ui.unit.Dp,
    textSize: TextStyle,
) {
    if (model.isNotBlank()) {
        AsyncImage(
            model = model,
            contentDescription = displayName,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            val initials = displayName.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.ifBlank { "S" }
            Text(initials.uppercase(Locale.getDefault()), style = textSize, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ChatImageAttachment(
    media: MediaModel,
    onOpen: () -> Unit,
) {
    val aspect = remember(media.width, media.height) {
        when {
            media.width > 0 && media.height > 0 -> (media.width.toFloat() / media.height.toFloat()).coerceIn(0.72f, 1.45f)
            else -> 1f
        }
    }
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.Black.copy(alpha = 0.06f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
    ) {
        AsyncImage(
            model = media.previewUrl.ifBlank { media.thumbUrl.ifBlank { media.url } },
            contentDescription = media.fileName.ifBlank { "image" },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspect),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun SelectedMediaComposer(
    selections: List<group.skytale.app.DraftMediaSelection>,
    hapticsEnabled: Boolean,
    onRemove: () -> Unit,
    onLongPress: () -> Unit,
) {
    val view = LocalView.current
    val strings = LocalSkytaleStrings.current
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        performLongPressHaptic(view, hapticsEnabled)
                        onLongPress()
                    },
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                AsyncImage(
                    model = selections.firstOrNull()?.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop,
                )
                if (selections.size > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.66f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            "+${selections.size - 1}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                if (selections.size == 1) strings.imageReady else "${strings.imageReady} (${selections.size})",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Outlined.Close, contentDescription = null)
            }
        }
    }
}

@Composable
private fun AttachmentOptionsDialog(
    strings: SkytaleStrings,
    selection: group.skytale.app.DraftMediaSelection,
    selectedCount: Int,
    onDismiss: () -> Unit,
    onOptionsChanged: (Boolean, Boolean) -> Unit,
) {
    var compress by remember(selection) { mutableStateOf(selection.compressImage) }
    var stripMetadata by remember(selection) { mutableStateOf(selection.stripMetadata) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.mediaOptions) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (selectedCount > 1) {
                    Text(
                        "Настройки будут применены ко всем $selectedCount изображениям",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                SettingToggle(
                    title = strings.sendWithoutCompression,
                    checked = !compress,
                    onCheckedChange = {
                        compress = !it
                        if (compress) {
                            stripMetadata = true
                        }
                    },
                )
                SettingToggle(
                    title = strings.deleteMetadata,
                    checked = stripMetadata,
                    enabled = !compress,
                    onCheckedChange = { stripMetadata = it },
                )
                Text(
                    if (compress) strings.optimizedUploadHint else strings.originalUploadHint,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onOptionsChanged(compress, stripMetadata)
                onDismiss()
            }) { Text(strings.save) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        },
    )
}

@Composable
private fun PendingImageBubble(
    localUri: String,
    progress: Float,
    onCancel: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.widthIn(max = 240.dp),
        ) {
            Box(modifier = Modifier.padding(8.dp)) {
                AsyncImage(
                    model = localUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.24f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(progress = { progress.coerceIn(0.05f, 0.98f) }, modifier = Modifier.size(54.dp), strokeWidth = 4.dp)
                        IconButton(onClick = onCancel) {
                            Icon(Icons.Outlined.Close, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullscreenImageViewer(
    media: MediaModel,
    onClose: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    val dismissViewer: () -> Unit = {
        visible = false
    }
    BackHandler(onBack = dismissViewer)
    val context = LocalContext.current
    val localeIsRussian = remember { Locale.getDefault().language == "ru" }
    val scope = rememberCoroutineScope()
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offsetX += panChange.x
        offsetY += panChange.y
    }
    val saver = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(media.mimeType.ifBlank { "image/*" })) { uri ->
        if (uri != null) {
            scope.launch {
                runCatching { context.saveRemoteMedia(media.url, uri) }
                    .onSuccess {
                        Toast.makeText(context, if (localeIsRussian) "Сохранено" else "Saved", Toast.LENGTH_SHORT).show()
                    }
                    .onFailure {
                        Toast.makeText(context, it.message ?: if (localeIsRussian) "Не удалось сохранить" else "Save failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    LaunchedEffect(Unit) {
        visible = true
    }
    LaunchedEffect(visible) {
        if (!visible) {
            delay(180)
            onClose()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.96f),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(180)) + scaleIn(animationSpec = tween(220), initialScale = 0.96f),
                exit = fadeOut(animationSpec = tween(160)) + scaleOut(animationSpec = tween(180), targetScale = 0.98f),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = media.url,
                        contentDescription = media.fileName.ifBlank { "image" },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offsetX
                                translationY = offsetY
                            }
                            .transformable(transformableState),
                        contentScale = ContentScale.Fit,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = dismissViewer) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                        IconButton(onClick = {
                            saver.launch(media.fileName.ifBlank { "skytale-image.jpg" })
                        }) {
                            Icon(Icons.Outlined.Download, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    strings: SkytaleStrings,
    query: String,
    results: List<SearchUserModel>,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
) {
    val readyToSearch = query.trim().length >= 3
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addContact) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = query, onValueChange = onQueryChanged, label = { Text(strings.searchPeople) }, modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = onSearch, modifier = Modifier.fillMaxWidth(), enabled = readyToSearch) {
                    Icon(Icons.Outlined.Search, null)
                    Spacer(Modifier.width(8.dp))
                    Text(strings.searchPeople)
                }
                if (results.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.height(220.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(results, key = { it.id }) { user ->
                            Card(modifier = Modifier.fillMaxWidth().clickable { onAdd(user.id) }) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons.Outlined.Person, null)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(user.nickname)
                                        Text(user.username, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (user.isOnline) Badge {}
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } },
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ContactShortcut(
    contact: ContactModel,
    compact: Boolean,
    hapticsEnabled: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
) {
    val view = LocalView.current
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = {
                performLongPressHaptic(view, hapticsEnabled)
                onLongPress()
            },
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = if (compact) 10.dp else 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserAvatar(
                model = contact.user.avatarThumbUrl.ifBlank { contact.user.avatarUrl },
                displayName = contact.user.nickname,
                size = if (compact) 42.dp else 48.dp,
                textSize = MaterialTheme.typography.labelLarge,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(contact.user.nickname, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelLarge)
                if (contact.isBlocked) {
                    Icon(Icons.Outlined.Shield, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun ContactPreviewDialog(
    strings: SkytaleStrings,
    contact: ContactModel,
    messages: List<MessageModel>,
    onDismiss: () -> Unit,
    onOpenChat: () -> Unit,
    onMarkUnread: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(contact.user.nickname)
                Text(contact.user.username, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (messages.isEmpty()) {
                    Text(strings.noChatsBody, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(modifier = Modifier.height(220.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(messages.takeLast(8), key = { it.id }) { message ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (message.isOwn) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceContainerHigh,
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                    Text(message.senderName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        if (message.deletedAt > 0L) strings.messageDeleted else message.text,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onOpenChat) { Text(strings.openChat) }
        },
        dismissButton = {
            Row {
                TextButton(onClick = {
                    onMarkUnread()
                    onDismiss()
                }) { Text(strings.undoLastRead) }
                TextButton(onClick = onDismiss) { Text(strings.cancel) }
            }
        },
    )
}

@Composable
private fun ContactProfileScreen(
    strings: SkytaleStrings,
    user: group.skytale.app.data.UserModel,
    onSaveNickname: (String) -> Unit,
    onClose: () -> Unit,
) {
    val presenceTick = rememberRelativeTimeTicker()
    val view = LocalView.current
    val isRussian = remember { Locale.getDefault().language == "ru" }
    val density = LocalDensity.current
    val threshold = with(density) { 42.dp.toPx() }
    val scope = rememberCoroutineScope()
    var dragDistance by remember { mutableStateOf(0f) }
    var swipeHapticTriggered by remember(user.id) { mutableStateOf(false) }
    var nicknameDialogVisible by remember(user.id) { mutableStateOf(false) }
    var nicknameDraft by remember(user.id) { mutableStateOf(user.nickname) }
    val nicknameDialogTitle = if (isRussian) "Локальный никнейм" else "Local nickname"
    val nicknameDialogHint = if (isRussian) {
        "Имя меняется только для вас. Оставьте поле пустым, чтобы вернуть исходный ник."
    } else {
        "This name changes only for you. Leave the field empty to restore the original nickname."
    }
    val canSaveNickname = nicknameDraft.trim().isBlank() || nicknameDraft.trim().length >= 2
    BackHandler(onBack = onClose)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(user.id) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        dragDistance = 0f
                        swipeHapticTriggered = false
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount > 0f) {
                            dragDistance = (dragDistance + dragAmount).coerceIn(0f, threshold * 1.35f)
                            if (!swipeHapticTriggered && dragDistance >= threshold) {
                                performGestureThresholdHaptic(view)
                                swipeHapticTriggered = true
                            }
                        }
                    },
                    onDragEnd = {
                        swipeHapticTriggered = false
                        if (dragDistance >= threshold) {
                            onClose()
                        } else {
                            scope.launch {
                                animate(
                                    initialValue = dragDistance,
                                    targetValue = 0f,
                                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                                ) { value, _ ->
                                    dragDistance = value
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        dragDistance = 0f
                        swipeHapticTriggered = false
                    },
                )
            },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f))
                .alpha((dragDistance / threshold).coerceIn(0f, 1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.985f),
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = strings.back)
                }
                Text(strings.profile, style = MaterialTheme.typography.titleLarge)
            }
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        UserAvatar(
                            model = user.avatarThumbUrl.ifBlank { user.avatarUrl },
                            displayName = user.nickname,
                            size = 96.dp,
                            textSize = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(user.nickname, style = MaterialTheme.typography.headlineMedium)
                            FilledIconButton(
                                onClick = {
                                    nicknameDraft = user.nickname
                                    nicknameDialogVisible = true
                                },
                                modifier = Modifier.size(36.dp),
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = nicknameDialogTitle, modifier = Modifier.size(18.dp))
                            }
                        }
                        ProfileUsernameLink(username = user.username)
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            if (user.about.isNotBlank()) {
                                Text(
                                    user.about,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                HorizontalDivider()
                            }
                            Text(
                                if (user.isOnline) strings.online else "${strings.lastSeen} ${formatPresenceTime(user.lastSeenAt, presenceTick)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
            if (nicknameDialogVisible) {
                AlertDialog(
                    onDismissRequest = { nicknameDialogVisible = false },
                    title = { Text(nicknameDialogTitle) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = nicknameDraft,
                                onValueChange = { nicknameDraft = it },
                                label = { Text(strings.nickname) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                nicknameDialogHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onSaveNickname(nicknameDraft)
                                nicknameDialogVisible = false
                            },
                            enabled = canSaveNickname,
                        ) {
                            Text(strings.save)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { nicknameDialogVisible = false }) {
                            Text(strings.cancel)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(18.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChannelProfileScreen(
    strings: SkytaleStrings,
    chat: ChatModel,
    onClose: () -> Unit,
    onChangeAvatar: (String, String, String, String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val isRussian = remember { Locale.getDefault().language == "ru" }
    val view = LocalView.current
    val context = LocalContext.current
    val density = LocalDensity.current
    val threshold = with(density) { 42.dp.toPx() }
    val scope = rememberCoroutineScope()
    var dragDistance by remember { mutableStateOf(0f) }
    var swipeHapticTriggered by remember(chat.id) { mutableStateOf(false) }
    var pendingAvatarCrop by remember(chat.id) { mutableStateOf<group.skytale.app.DraftMediaSelection?>(null) }
    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        pendingAvatarCrop = group.skytale.app.DraftMediaSelection(
            uri = uri.toString(),
            fileName = context.resolveDisplayName(uri),
            mimeType = context.contentResolver.getType(uri).orEmpty().ifBlank { "image/jpeg" },
        )
    }
    BackHandler(onBack = onClose)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(chat.id) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        dragDistance = 0f
                        swipeHapticTriggered = false
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount > 0f) {
                            dragDistance = (dragDistance + dragAmount).coerceIn(0f, threshold * 1.35f)
                            if (!swipeHapticTriggered && dragDistance >= threshold) {
                                performGestureThresholdHaptic(view)
                                swipeHapticTriggered = true
                            }
                        }
                    },
                    onDragEnd = {
                        swipeHapticTriggered = false
                        if (dragDistance >= threshold) {
                            onClose()
                        } else {
                            scope.launch {
                                animate(
                                    initialValue = dragDistance,
                                    targetValue = 0f,
                                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                                ) { value, _ ->
                                    dragDistance = value
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        dragDistance = 0f
                        swipeHapticTriggered = false
                    },
                )
            },
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.985f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = strings.back)
                    }
                    Text(if (isRussian) "Профиль канала" else "Channel profile", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    if (chat.canManage) {
                        FilledIconButton(onClick = onOpenSettings, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    if (chat.canManage) {
                                        (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
                                        avatarPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                        )
                                    }
                                },
                                onLongClick = {
                                    if (chat.canManage) {
                                        performLongPressHaptic(view)
                                        (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
                                        avatarPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                        )
                                    }
                                },
                            ),
                        ) {
                            UserAvatar(
                                model = chat.avatarThumbUrl.ifBlank { chat.avatarUrl },
                                displayName = chat.title,
                                size = 96.dp,
                                textSize = MaterialTheme.typography.headlineSmall,
                            )
                        }
                        Text(chat.title, style = MaterialTheme.typography.headlineMedium)
                        if (chat.username.isNotBlank()) {
                            ProfileUsernameLink(
                                username = chat.username,
                                onClick = { openExternalUrl(context, "skytale://open/${chat.username.trim().removePrefix("@")}") },
                            )
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(
                                    "${chat.memberCount} ${if (isRussian) "подписчиков" else "subscribers"}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                if (chat.description.isNotBlank()) {
                                    HorizontalDivider()
                                    MessageText(
                                        text = chat.description,
                                        ownMessage = false,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    pendingAvatarCrop?.let { selection ->
        AvatarCropDialog(
            selection = selection,
            onDismiss = { pendingAvatarCrop = null },
            onConfirm = { croppedUri, fileName, mimeType ->
                pendingAvatarCrop = null
                onChangeAvatar(chat.id, croppedUri, fileName, mimeType)
            },
        )
    }
}

@Composable
private fun MessageText(
    text: String,
    ownMessage: Boolean,
    style: TextStyle,
) {
    val context = LocalContext.current
    val linkColor = if (ownMessage) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }
    val annotated = remember(text, linkColor) { buildLinkedMessage(text, linkColor) }
    val hasLinks = remember(text) { urlRegex.containsMatchIn(text) || mentionRegex.containsMatchIn(text) }
    if (!hasLinks) {
        Text(text = text, style = style)
        return
    }
    ClickableText(
        text = annotated,
        style = style,
        onClick = { offset ->
            annotated
                .getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()
                ?.let { openExternalUrl(context, it.item) }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProfileUsernameLink(
    username: String,
    enableOpen: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val view = LocalView.current
    val isRussian = Locale.getDefault().language == "ru"
    val normalized = username.trim().removePrefix("@")
    val url = "https://use.skytale.dpdns.org/$normalized"
    val openAction = onClick ?: if (enableOpen) {
        { openExternalUrl(context, "skytale://open/$normalized") }
    } else {
        null
    }
    Text(
        text = "@$normalized",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.combinedClickable(
            onClick = { openAction?.invoke() },
            onLongClick = {
                performLongPressHaptic(view)
                clipboard.setText(AnnotatedString(url))
                Toast.makeText(
                    context,
                    if (isRussian) "Ссылка скопирована" else "Link copied",
                    Toast.LENGTH_SHORT,
                ).show()
            },
        ),
    )
}

@Composable
private fun ChannelPostActionsDialog(
    chat: ChatModel,
    post: MessageModel,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onForward: () -> Unit,
    onDelete: () -> Unit,
) {
    val isRussian = Locale.getDefault().language == "ru"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isRussian) "Действия с постом" else "Post actions") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MenuActionRow(icon = Icons.Outlined.ContentCopy, title = if (isRussian) "Копировать" else "Copy", onClick = onCopy)
                MenuActionRow(icon = Icons.AutoMirrored.Outlined.Forward, title = if (isRussian) "Переслать" else "Forward", onClick = onForward)
                if (chat.canManage || post.isOwn) {
                    MenuActionRow(icon = Icons.Outlined.DeleteOutline, title = if (isRussian) "Удалить" else "Delete", onClick = onDelete)
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(if (isRussian) "Закрыть" else "Close") } },
    )
}

@Composable
private fun ForwardChannelPostDialog(
    chats: List<ChatModel>,
    onDismiss: () -> Unit,
    onForward: (String) -> Unit,
) {
    val isRussian = Locale.getDefault().language == "ru"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isRussian) "Переслать пост" else "Forward post") },
        text = {
            if (chats.isEmpty()) {
                Text(if (isRussian) "Нет доступных чатов" else "No chats available", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(chats, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onForward(item.id) },
                            shape = RoundedCornerShape(20.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                UserAvatar(
                                    model = item.peer?.avatarThumbUrl?.ifBlank { item.peer?.avatarUrl.orEmpty() } ?: item.avatarThumbUrl.ifBlank { item.avatarUrl },
                                    displayName = item.title,
                                    size = 42.dp,
                                    textSize = MaterialTheme.typography.labelLarge,
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    val subtitle = if (item.type == "channel" && item.username.isNotBlank()) "@${item.username}" else item.lastMessagePreview
                                    if (subtitle.isNotBlank()) {
                                        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(if (isRussian) "Отмена" else "Cancel") } },
    )
}

@Composable
private fun CreateMenuDialog(
    onDismiss: () -> Unit,
    onAddContact: () -> Unit,
    onCreateChannel: () -> Unit,
    onFindChannel: () -> Unit,
) {
    val isRussian = Locale.getDefault().language == "ru"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isRussian) "Создать" else "Create") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MenuActionRow(icon = Icons.Outlined.Person, title = if (isRussian) "Добавить контакт" else "Add contact", onClick = onAddContact)
                MenuActionRow(icon = Icons.Outlined.ChatBubbleOutline, title = if (isRussian) "Создать канал" else "Create channel", onClick = onCreateChannel)
                MenuActionRow(icon = Icons.Outlined.Search, title = if (isRussian) "Найти канал" else "Find channel", onClick = onFindChannel)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(if (isRussian) "Закрыть" else "Close") } },
    )
}

@Composable
private fun MenuActionRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun CreateChannelDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, Boolean) -> Unit,
) {
    val isRussian = Locale.getDefault().language == "ru"
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var commentsEnabled by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isRussian) "Создать канал" else "Create channel") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isRussian) "Название" else "Title") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(if (isRussian) "Username" else "Username") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (isRussian) "Описание" else "Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(if (isRussian) "Комментарии" else "Comments")
                    Switch(checked = commentsEnabled, onCheckedChange = { commentsEnabled = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(title.trim(), username.trim(), description.trim(), commentsEnabled) },
                enabled = title.trim().length >= 2,
            ) { Text(if (isRussian) "Создать" else "Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(if (isRussian) "Отмена" else "Cancel") } },
    )
}

@Composable
private fun DirectorySearchDialog(
    title: String,
    query: String,
    results: List<DirectoryEntryModel>,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onDismiss: () -> Unit,
    onOpen: (DirectoryEntryModel) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = query, onValueChange = onQueryChanged, label = { Text(title) }, modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = onSearch, modifier = Modifier.fillMaxWidth(), enabled = query.trim().length >= 3) {
                    Icon(Icons.Outlined.Search, null)
                    Spacer(Modifier.width(8.dp))
                    Text(title)
                }
                if (results.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(results, key = { "${it.kind}:${it.id}" }) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpen(item) },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    UserAvatar(
                                        model = item.avatarThumbUrl.ifBlank { item.avatarUrl },
                                        displayName = item.title,
                                        size = 42.dp,
                                        textSize = MaterialTheme.typography.labelLarge,
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("@${item.username}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        if (item.description.isNotBlank()) {
                                            Text(item.description, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    if (item.kind == "channel") {
                                        Text(item.memberCount.toString(), color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(if (Locale.getDefault().language == "ru") "Закрыть" else "Close") } },
    )
}

@Composable
private fun ChannelScreen(
    state: SkytaleUiState,
    strings: SkytaleStrings,
    onBack: () -> Unit,
    onSendPost: (String) -> Unit,
    onToggleMute: () -> Unit,
    onSelectComposerMedia: (String, String, String) -> Unit,
    onClearComposerMedia: () -> Unit,
    onUpdateComposerMediaOptions: (Boolean, Boolean) -> Unit,
    onCancelPendingUpload: (String) -> Unit,
    onSelectChannelAvatar: (String, String, String, String) -> Unit,
    onLoadMembers: (String) -> Unit,
    onUpdateMemberRole: (String, String, String) -> Unit,
    onUpdateChannel: (String, String, String, String, String, Boolean) -> Unit,
    onLeaveChannel: (String) -> Unit,
    onDeleteChannel: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onForwardPost: (String, String) -> Unit,
    onPinPost: (String, String?) -> Unit,
    onOpenComments: (String, String) -> Unit,
    onCloseComments: () -> Unit,
    onSendComment: (String, String, String) -> Unit,
) {
    val chat = state.chats.firstOrNull { it.id == state.openedChatId } ?: return
    val view = LocalView.current
    val isRussian = Locale.getDefault().language == "ru"
    val posts by remember(chat.id, state.messages) {
        derivedStateOf { state.messages.sortedBy { it.createdAt } }
    }
    val pinnedPost by remember(posts) {
        derivedStateOf { posts.firstOrNull { it.isPinned } }
    }
    val pendingUploads by remember(chat.id, state.pendingUploads) {
        derivedStateOf { state.pendingUploads.filter { it.chatId == chat.id } }
    }
    val context = LocalContext.current
    val density = LocalDensity.current
    val compact = state.settings.compactMode
    val swipeThreshold = with(density) { 32.dp.toPx() }
    val scope = rememberCoroutineScope()
    var draft by remember(chat.id) { mutableStateOf("") }
    var menuExpanded by remember(chat.id) { mutableStateOf(false) }
    var settingsVisible by remember(chat.id) { mutableStateOf(false) }
    var membersVisible by remember(chat.id) { mutableStateOf(false) }
    var profileVisible by remember(chat.id) { mutableStateOf(false) }
    var initialScrollDone by remember(chat.id) { mutableStateOf(false) }
    var openedMedia by remember(chat.id) { mutableStateOf<MediaModel?>(null) }
    var composerMediaOptionsVisible by remember(chat.id) { mutableStateOf(false) }
    var forwardingPost by remember(chat.id) { mutableStateOf<MessageModel?>(null) }
    var attachmentMenuExpanded by remember(chat.id) { mutableStateOf(false) }
    var dragDistance by remember(chat.id) { mutableStateOf(0f) }
    var backSwipeHapticTriggered by remember(chat.id) { mutableStateOf(false) }
    var pendingCameraUri by remember(chat.id) { mutableStateOf<Uri?>(null) }
    var previousPostCount by remember(chat.id) { mutableIntStateOf(0) }
    var previousLastPostId by remember(chat.id) { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val animatedImeBottom by animateDpAsState(
        targetValue = with(density) { imeBottomPx.toDp() },
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "channel-ime-bottom",
    )
    val localeIsRussian = remember { Locale.getDefault().language == "ru" }
    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
        uris.forEach { uri ->
            onSelectComposerMedia(
                uri.toString(),
                context.resolveDisplayName(uri),
                context.contentResolver.getType(uri).orEmpty().ifBlank { "image/jpeg" },
            )
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val cameraUri = pendingCameraUri
        if (cameraUri != null) {
            context.revokeUriPermission(cameraUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        pendingCameraUri = null
        if (result.resultCode == Activity.RESULT_OK && cameraUri != null) {
            onSelectComposerMedia(
                cameraUri.toString(),
                context.resolveDisplayName(cameraUri),
                context.contentResolver.getType(cameraUri).orEmpty().ifBlank { "image/jpeg" },
            )
        }
    }
    fun openSystemCamera() {
        val cameraUri = runCatching { context.createCameraImageUri() }.getOrElse {
            Toast.makeText(
                context,
                if (localeIsRussian) "Не удалось подготовить фото" else "Unable to prepare photo",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(android.provider.MediaStore.EXTRA_OUTPUT, cameraUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            clipData = ClipData.newUri(context.contentResolver, "Skytale camera", cameraUri)
        }
        val resolvedActivities = context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY,
        )
        if (resolvedActivities.isEmpty()) {
            Toast.makeText(
                context,
                if (localeIsRussian) "Камера недоступна" else "Camera unavailable",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        pendingCameraUri = cameraUri
        resolvedActivities.mapNotNull { it.activityInfo?.packageName }.distinct().forEach { packageName ->
            context.grantUriPermission(
                packageName,
                cameraUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
        }
        cameraLauncher.launch(intent)
    }
    val requestCameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
            openSystemCamera()
        } else {
            Toast.makeText(
                context,
                if (localeIsRussian) "Нет доступа к камере" else "Camera permission denied",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
    BackHandler(onBack = onBack)
    BackHandler(enabled = profileVisible) { profileVisible = false }
    LaunchedEffect(membersVisible, chat.id) {
        if (membersVisible) {
            onLoadMembers(chat.id)
        }
    }
    LaunchedEffect(posts.size, posts.lastOrNull()?.id) {
        if (posts.isEmpty()) {
            initialScrollDone = false
            previousPostCount = 0
            previousLastPostId = null
            return@LaunchedEffect
        }
        if (!initialScrollDone) {
            listState.scrollToItem(posts.lastIndex)
            initialScrollDone = true
            previousPostCount = posts.size
            previousLastPostId = posts.lastOrNull()?.id
            return@LaunchedEffect
        }
        val currentLastPostId = posts.lastOrNull()?.id
        val userNearBottom = (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1) >= (posts.lastIndex - 2)
        val appendedPost = posts.size > previousPostCount && previousLastPostId != currentLastPostId
        if (appendedPost && userNearBottom) {
            listState.animateScrollToItem(posts.lastIndex)
        }
        previousPostCount = posts.size
        previousLastPostId = currentLastPostId
    }
    val performBackAction: () -> Unit = {
        dragDistance = 0f
        if (profileVisible) {
            profileVisible = false
        } else {
            onBack()
        }
    }
    val updateBackDrag: (Float) -> Unit = { delta ->
        dragDistance = (dragDistance + delta).coerceIn(0f, swipeThreshold * 1.35f)
        if (!backSwipeHapticTriggered && dragDistance >= swipeThreshold) {
            performGestureThresholdHaptic(view, state.settings.hapticsEnabled)
            backSwipeHapticTriggered = true
        }
        if (dragDistance >= swipeThreshold) {
            performBackAction()
        }
    }
    val cancelBackDrag: () -> Unit = {
        dragDistance = 0f
        backSwipeHapticTriggered = false
    }
    val animateBackDragReset: () -> Unit = {
        scope.launch {
            animate(
                initialValue = dragDistance,
                targetValue = 0f,
                animationSpec = tween(180, easing = FastOutSlowInEasing),
            ) { value, _ ->
                dragDistance = value
            }
        }
    }
    val finishBackDrag: () -> Unit = {
        val shouldGoBack = dragDistance >= swipeThreshold
        backSwipeHapticTriggered = false
        if (shouldGoBack) {
            performBackAction()
        } else {
            animateBackDragReset()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)))
            .pointerInput(chat.id, profileVisible) {
                detectHorizontalDragGestures(
                    onDragStart = { cancelBackDrag() },
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount > 0f) {
                            updateBackDrag(dragAmount)
                        }
                    },
                    onDragEnd = finishBackDrag,
                    onDragCancel = cancelBackDrag,
                )
            },
    ) {
        if (dragDistance > 0.5f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f))
                    .alpha((dragDistance / swipeThreshold).coerceIn(0f, 1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = animatedImeBottom),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = strings.back)
                        }
                        UserAvatar(
                            model = chat.avatarThumbUrl.ifBlank { chat.avatarUrl },
                            displayName = chat.title,
                            size = 38.dp,
                            textSize = MaterialTheme.typography.labelLarge,
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { profileVisible = true },
                        ) {
                            Text(
                                chat.title,
                                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                if (chat.memberCount > 0) {
                                    "${chat.memberCount} ${if (isRussian) "подписчиков" else "subscribers"}"
                                } else {
                                    if (isRussian) "Канал" else "Channel"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Outlined.MoreVert, contentDescription = null)
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                DropdownMenuItem(
                                    text = { Text(if (chat.isMuted) strings.unmute else strings.mute) },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.VolumeOff, null) },
                                    onClick = {
                                        onToggleMute()
                                        menuExpanded = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(if (isRussian) "Профиль канала" else "Channel profile") },
                                    leadingIcon = { Icon(Icons.Outlined.Info, null) },
                                    onClick = {
                                        profileVisible = true
                                        menuExpanded = false
                                    },
                                )
                                if (chat.canManage) {
                                    DropdownMenuItem(
                                        text = { Text(if (isRussian) "Настройки канала" else "Channel settings") },
                                        leadingIcon = { Icon(Icons.Outlined.Settings, null) },
                                        onClick = {
                                            settingsVisible = true
                                            menuExpanded = false
                                        },
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (isRussian) "Администраторы" else "Admins") },
                                        leadingIcon = { Icon(Icons.Outlined.Person, null) },
                                        onClick = {
                                            membersVisible = true
                                            menuExpanded = false
                                        },
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text(if (isRussian) "Покинуть канал" else "Leave channel") },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, null) },
                                    onClick = {
                                        onLeaveChannel(chat.id)
                                        menuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    AnimatedVisibility(visible = pinnedPost != null) {
                        val post = pinnedPost ?: return@AnimatedVisibility
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Outlined.PushPin, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Text(if (isRussian) "Закреплённый пост" else "Pinned post", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                }
                                Text(
                                    post.text.ifBlank {
                                        if (post.media != null) {
                                            if (isRussian) "Медиа-вложение" else "Media attachment"
                                        } else {
                                            strings.messageDeleted
                                        }
                                    },
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = if (posts.isEmpty() || initialScrollDone) 1f else 0f },
                state = listState,
                contentPadding = PaddingValues(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(posts, key = { it.id }) { post ->
                    ChannelPostBubble(
                        post = post,
                        compact = compact,
                        isRussian = isRussian,
                        hapticsEnabled = state.settings.hapticsEnabled,
                        commentsEnabled = chat.commentsEnabled,
                        canManage = chat.canManage,
                        onPinPost = { onPinPost(chat.id, if (post.isPinned) null else post.id) },
                        onOpenComments = { onOpenComments(chat.id, post.id) },
                        onForward = { forwardingPost = post },
                        onDelete = { onDeletePost(post.id) },
                        onOpenMedia = { media -> openedMedia = media },
                    )
                }
                items(pendingUploads, key = { it.localId }, contentType = { "pending-upload" }) { pending ->
                    PendingImageBubble(
                        localUri = pending.localUri,
                        progress = pending.progress,
                        onCancel = { onCancelPendingUpload(pending.localId) },
                    )
                }
            }
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            ) {
                if (chat.canPost) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AnimatedVisibility(visible = state.selectedComposerMedia.isNotEmpty()) {
                            SelectedMediaComposer(
                                selections = state.selectedComposerMedia,
                                hapticsEnabled = state.settings.hapticsEnabled,
                                onRemove = onClearComposerMedia,
                                onLongPress = { composerMediaOptionsVisible = true },
                            )
                        }
                        Row(verticalAlignment = Alignment.Bottom) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextField(
                                    value = draft,
                                    onValueChange = { draft = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(if (isRussian) "Новый пост" else "New post") },
                                    maxLines = 5,
                                    shape = RoundedCornerShape(30.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    ),
                                    leadingIcon = {
                                        Box {
                                            IconButton(onClick = { attachmentMenuExpanded = true }) {
                                                Icon(Icons.Outlined.Add, contentDescription = strings.add)
                                            }
                                            DropdownMenu(
                                                expanded = attachmentMenuExpanded,
                                                onDismissRequest = { attachmentMenuExpanded = false },
                                            ) {
                                                DropdownMenuItem(
                                                    leadingIcon = { Icon(Icons.Outlined.PhotoCamera, contentDescription = null) },
                                                    text = { Text("Сделать фото") },
                                                    onClick = {
                                                        attachmentMenuExpanded = false
                                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                                            (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
                                                            openSystemCamera()
                                                        } else {
                                                            requestCameraPermission.launch(Manifest.permission.CAMERA)
                                                        }
                                                    },
                                                )
                                                DropdownMenuItem(
                                                    leadingIcon = {
                                                        Icon(
                                                            painter = painterResource(R.drawable.ic_attach_gallery),
                                                            contentDescription = null,
                                                        )
                                                    },
                                                    text = { Text("Выбрать фото") },
                                                    onClick = {
                                                        attachmentMenuExpanded = false
                                                        (context as? group.skytale.app.MainActivity)?.suppressRelockOnce()
                                                        mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                                    },
                                                )
                                            }
                                        }
                                    },
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            FilledIconButton(
                                onClick = {
                                    if (draft.isNotBlank() || state.selectedComposerMedia.isNotEmpty()) {
                                        onSendPost(draft.trim())
                                        draft = ""
                                    }
                                },
                                modifier = Modifier.size(52.dp),
                            ) {
                                Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null)
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        FilledTonalButton(
                            onClick = onToggleMute,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.VolumeOff, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (chat.isMuted) strings.unmute else strings.mute)
                        }
                        if (!chat.commentsEnabled) {
                            Text(
                                if (isRussian) "Комментарии в этом канале отключены." else "Comments are disabled in this channel.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
    state.openedChannelCommentsPostId?.let { postId ->
        ChannelCommentsDialog(
            comments = state.channelComments,
            onDismiss = onCloseComments,
            onSend = { text -> onSendComment(chat.id, postId, text) },
        )
    }
    if (settingsVisible) {
        ChannelSettingsDialog(
            chat = chat,
            onDismiss = { settingsVisible = false },
            onSave = { title, username, description, postingPolicy, commentsEnabled ->
                onUpdateChannel(chat.id, title, username, description, postingPolicy, commentsEnabled)
                settingsVisible = false
            },
            onDelete = {
                settingsVisible = false
                onDeleteChannel(chat.id)
            }
        )
    }
    if (membersVisible) {
        ChannelMembersDialog(
            chat = chat,
            members = state.channelMembers,
            onDismiss = { membersVisible = false },
            onToggleRole = { userId, role -> onUpdateMemberRole(chat.id, userId, role) },
        )
    }
    if (openedMedia != null) {
        FullscreenImageViewer(
            media = openedMedia!!,
            onClose = { openedMedia = null },
        )
    }
    if (composerMediaOptionsVisible && state.selectedComposerMedia.isNotEmpty()) {
        AttachmentOptionsDialog(
            strings = strings,
            selection = state.selectedComposerMedia.first(),
            selectedCount = state.selectedComposerMedia.size,
            onDismiss = { composerMediaOptionsVisible = false },
            onOptionsChanged = onUpdateComposerMediaOptions,
        )
    }
    AnimatedVisibility(
        visible = profileVisible,
        enter = slideInHorizontally(initialOffsetX = { it / 5 }, animationSpec = tween(240, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(220)),
        exit = slideOutHorizontally(targetOffsetX = { it / 5 }, animationSpec = tween(200)) + fadeOut(animationSpec = tween(180)),
    ) {
        ChannelProfileScreen(
            strings = strings,
            chat = chat,
            onClose = { profileVisible = false },
            onChangeAvatar = onSelectChannelAvatar,
            onOpenSettings = {
                profileVisible = false
                settingsVisible = true
            },
        )
    }
    forwardingPost?.let { post ->
        ForwardChannelPostDialog(
            chats = state.chats.filter { it.id != chat.id },
            onDismiss = { forwardingPost = null },
            onForward = { targetChatId ->
                onForwardPost(post.id, targetChatId)
                forwardingPost = null
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChannelPostBubble(
    post: MessageModel,
    compact: Boolean,
    isRussian: Boolean,
    hapticsEnabled: Boolean,
    commentsEnabled: Boolean,
    canManage: Boolean,
    onPinPost: () -> Unit,
    onOpenComments: () -> Unit,
    onForward: () -> Unit,
    onDelete: () -> Unit,
    onOpenMedia: (MediaModel) -> Unit,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val view = LocalView.current
    var menuExpanded by remember(post.id) { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(post.id) {
                detectTapGestures(onLongPress = {
                    performLongPressHaptic(view, hapticsEnabled)
                    menuExpanded = true
                })
            },
        horizontalAlignment = Alignment.Start,
    ) {
        Box {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(
                    topStart = 22.dp,
                    topEnd = 22.dp,
                    bottomStart = 8.dp,
                    bottomEnd = 22.dp,
                ),
                modifier = Modifier
                    .wrapContentWidth()
                    .widthIn(max = if (compact) 228.dp else 276.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = if (compact) 10.dp else 12.dp, vertical = if (compact) 8.dp else 10.dp)) {
                    if (post.forwardedFromUsername.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Black.copy(alpha = 0.08f),
                            modifier = Modifier.clickable {
                                openExternalUrl(context, "skytale://open/${post.forwardedFromUsername}")
                            },
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                                Text(
                                    "@${post.forwardedFromUsername}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                if (post.forwardedFromTitle.isNotBlank()) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        post.forwardedFromTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                    if (post.media != null) {
                        ChatImageAttachment(media = post.media, onOpen = { onOpenMedia(post.media) })
                    }
                    if (post.text.isNotBlank()) {
                        if (post.media != null) Spacer(Modifier.height(8.dp))
                        MessageText(
                            text = post.text,
                            ownMessage = false,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (commentsEnabled) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable(onClick = onOpenComments)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(17.dp),
                                )
                                Text(
                                    post.commentCount.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(Icons.Outlined.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Text(post.viewCount.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            formatClockTime(post.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(if (isRussian) "Копировать" else "Copy") },
                    leadingIcon = { Icon(Icons.Outlined.ContentCopy, null) },
                    onClick = {
                        clipboard.setText(AnnotatedString(post.text))
                        menuExpanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(if (isRussian) "Переслать" else "Forward") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Forward, null) },
                    onClick = {
                        onForward()
                        menuExpanded = false
                    },
                )
                if (canManage) {
                    DropdownMenuItem(
                        text = { Text(if (post.isPinned) if (isRussian) "Открепить" else "Unpin" else if (isRussian) "Закрепить" else "Pin") },
                        leadingIcon = { Icon(Icons.Outlined.PushPin, null) },
                        onClick = {
                            onPinPost()
                            menuExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(if (isRussian) "Удалить" else "Delete") },
                        leadingIcon = { Icon(Icons.Outlined.DeleteOutline, null) },
                        onClick = {
                            onDelete()
                            menuExpanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelCommentsDialog(
    comments: List<MessageModel>,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
) {
    val isRussian = Locale.getDefault().language == "ru"
    var draft by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isRussian) "Комментарии" else "Comments", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                    }
                }
                if (comments.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                    ) {
                        Text(
                            if (isRussian) "Пока без комментариев" else "No comments yet",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(320.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(comments, key = { it.id }) { comment ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(comment.senderName, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                                    Text(comment.text.ifBlank { if (isRussian) "Сообщение удалено" else "Message deleted" })
                                }
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    TextField(
                        value = draft,
                        onValueChange = { draft = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(if (isRussian) "Комментарий" else "Comment") },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ),
                    )
                    Spacer(Modifier.width(10.dp))
                    FilledIconButton(
                        onClick = {
                            if (draft.isNotBlank()) {
                                onSend(draft.trim())
                                draft = ""
                            }
                        },
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelSettingsDialog(
    chat: ChatModel,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val isRussian = Locale.getDefault().language == "ru"
    var title by remember(chat.id) { mutableStateOf(chat.title) }
    var username by remember(chat.id) { mutableStateOf(chat.username) }
    var description by remember(chat.id) { mutableStateOf(chat.description) }
    var postingPolicy by remember(chat.id) { mutableStateOf(chat.postingPolicy) }
    var commentsEnabled by remember(chat.id) { mutableStateOf(chat.commentsEnabled) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isRussian) "Профиль канала" else "Channel profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(if (isRussian) "Название" else "Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(if (isRussian) "Описание" else "Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(if (isRussian) "Писать могут все" else "Everyone can post")
                    Switch(
                        checked = postingPolicy == "everyone",
                        onCheckedChange = { postingPolicy = if (it) "everyone" else "admins" },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(if (isRussian) "Разрешить комментарии" else "Enable comments")
                    Switch(checked = commentsEnabled, onCheckedChange = { commentsEnabled = it })
                }
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (isRussian) "Удалить канал" else "Delete channel", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(title.trim(), username.trim(), description.trim(), postingPolicy, commentsEnabled) }) { Text(stringsFor(if (isRussian) AppLanguage.RU else AppLanguage.EN).save) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(if (isRussian) "Отмена" else "Cancel") } },
    )
}

@Composable
private fun ChannelMembersDialog(
    chat: ChatModel,
    members: List<group.skytale.app.data.ChannelMemberModel>,
    onDismiss: () -> Unit,
    onToggleRole: (String, String) -> Unit,
) {
    val isRussian = Locale.getDefault().language == "ru"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isRussian) "Администраторы канала" else "Channel admins") },
        text = {
            if (members.isEmpty()) {
                Text(if (isRussian) "Загрузка..." else "Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(members, key = { it.user.id }) { member ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                UserAvatar(
                                    model = member.user.avatarThumbUrl.ifBlank { member.user.avatarUrl },
                                    displayName = member.user.nickname,
                                    size = 40.dp,
                                    textSize = MaterialTheme.typography.labelLarge,
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(member.user.nickname)
                                    Text("@${member.user.username}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (member.role != "owner" && chat.canManage) {
                                    TextButton(onClick = {
                                        onToggleRole(member.user.id, if (member.role == "admin") "member" else "admin")
                                    }) {
                                        Text(if (member.role == "admin") {
                                            if (isRussian) "Снять" else "Remove"
                                        } else {
                                            if (isRussian) "Назначить" else "Make admin"
                                        })
                                    }
                                } else {
                                    Text(
                                        when (member.role) {
                                            "owner" -> if (isRussian) "Владелец" else "Owner"
                                            "admin" -> if (isRussian) "Админ" else "Admin"
                                            else -> if (isRussian) "Подписчик" else "Member"
                                        },
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(if (isRussian) "Закрыть" else "Close") } },
    )
}

private fun buildLinkedMessage(
    text: String,
    linkColor: Color,
): AnnotatedString = buildAnnotatedString {
    data class LinkedRange(val start: Int, val endInclusive: Int, val displayed: String, val destination: String)
    val ranges = mutableListOf<LinkedRange>()
    urlRegex.findAll(text).forEach { match ->
        val raw = match.value
        val displayUrl = raw.trimEnd('.', ',', '!', '?', ':', ';', ')', ']', '}')
        ranges += LinkedRange(
            start = match.range.first,
            endInclusive = match.range.first + displayUrl.length - 1,
            displayed = displayUrl,
            destination = if (displayUrl.startsWith("www.", ignoreCase = true)) "https://$displayUrl" else displayUrl,
        )
    }
    mentionRegex.findAll(text).forEach { match ->
        val mention = match.value
        val username = mention.removePrefix("@")
        ranges += LinkedRange(
            start = match.range.first,
            endInclusive = match.range.last,
            displayed = mention,
            destination = "skytale://open/$username",
        )
    }
    val sortedRanges = ranges.sortedBy { it.start }
    var cursor = 0
    sortedRanges.forEach { range ->
        if (range.start < cursor) {
            return@forEach
        }
        if (range.start > cursor) {
            append(text.substring(cursor, range.start))
        }
        pushStringAnnotation(tag = "URL", annotation = range.destination)
        pushStyle(
            SpanStyle(
                color = linkColor,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline,
            ),
        )
        append(range.displayed)
        pop()
        pop()
        cursor = range.endInclusive + 1
    }
    if (cursor < text.length) {
        append(text.substring(cursor))
    }
}

private fun openExternalUrl(context: Context, url: String) {
    runCatching {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    }.onFailure {
        Toast.makeText(
            context,
            if (Locale.getDefault().language == "ru") "Не удалось открыть ссылку" else "Unable to open link",
            Toast.LENGTH_SHORT,
        ).show()
    }
}

private val urlRegex = Regex("""((https?://|www\.)[^\s<]+)""", RegexOption.IGNORE_CASE)
private val mentionRegex = Regex("""(?<![\p{L}\p{N}_])@([\p{L}\p{N}._\-]{3,32})""")

@Composable
private fun rememberRelativeTimeTicker(periodMs: Long = 30_000L): Long {
    return produceState(initialValue = System.currentTimeMillis(), periodMs) {
        while (true) {
            delay(periodMs)
            value = System.currentTimeMillis()
        }
    }.value
}

private fun messageStatusGlyph(status: String): String = if (status == "read") "✓✓" else "✓"

private fun relativeTime(timestamp: Long, strings: SkytaleStrings? = null, nowMillis: Long = System.currentTimeMillis()): String {
    if (timestamp <= 0L) return ""
    val deltaSeconds = ((nowMillis / 1000) - timestamp).coerceAtLeast(0)
    if (deltaSeconds < 60) {
        return strings?.justNow ?: if (Locale.getDefault().language == "ru") "только что" else "just now"
    }
    if (deltaSeconds < 120) {
        return if (Locale.getDefault().language == "ru") "1 минуту назад" else "1 minute ago"
    }
    return DateUtils.getRelativeTimeSpanString(
        timestamp * 1000,
        nowMillis,
        DateUtils.MINUTE_IN_MILLIS,
    ).toString()
}
