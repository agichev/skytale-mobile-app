package group.skytale.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import group.skytale.app.ui.LocalSkytaleStrings
import group.skytale.app.ui.SkytaleRoot
import group.skytale.app.ui.SkytaleTheme
import group.skytale.app.ui.stringsFor

class MainActivity : FragmentActivity() {
    private val viewModel by viewModels<SkytaleViewModel>()
    private var suppressNextRelock = false

    private val requestNotifications = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        routeIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            val state by viewModel.uiState.collectAsState()
            val strings = stringsFor(state.language)
            CompositionLocalProvider(LocalSkytaleStrings provides strings) {
                SkytaleTheme(settings = state.settings) {
                    var locked by rememberSaveable { mutableStateOf(false) }
                    var lockCycle by rememberSaveable { mutableIntStateOf(0) }
                    var pendingRelock by rememberSaveable { mutableStateOf(false) }
                    var relockOnForeground by rememberSaveable { mutableStateOf(false) }
                    var unlockSatisfied by rememberSaveable { mutableStateOf(false) }
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val activity = LocalContext.current as FragmentActivity
                    val shouldRequireUnlock = state.session != null && state.settings.appLockEnabled

                    DisposableEffect(lifecycleOwner, shouldRequireUnlock) {
                        val observer = LifecycleEventObserver { _, event ->
                            when (event) {
                                Lifecycle.Event.ON_START -> {
                                    viewModel.setAppForeground(true)
                                    suppressNextRelock = false
                                    if (shouldRequireUnlock && locked && relockOnForeground) {
                                        lockCycle += 1
                                        pendingRelock = false
                                        relockOnForeground = false
                                    } else if (shouldRequireUnlock && !locked && !unlockSatisfied) {
                                        locked = true
                                        lockCycle += 1
                                    }
                                }

                                Lifecycle.Event.ON_STOP -> {
                                    viewModel.setAppForeground(false)
                                    if (shouldRequireUnlock && !activity.isChangingConfigurations && !suppressNextRelock) {
                                        locked = true
                                        pendingRelock = true
                                        relockOnForeground = true
                                        unlockSatisfied = false
                                    }
                                    suppressNextRelock = false
                                }

                                else -> Unit
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                    }

                    LaunchedEffect(shouldRequireUnlock) {
                        if (!shouldRequireUnlock) {
                            locked = false
                            pendingRelock = false
                            relockOnForeground = false
                            unlockSatisfied = false
                        } else if (!unlockSatisfied && !locked) {
                            locked = true
                            lockCycle += 1
                        }
                    }

                    if (shouldRequireUnlock && locked) {
                        AppLockGate(
                            lockCycle = lockCycle,
                            title = strings.unlockPrompt,
                            retryLabel = strings.retry,
                            onUnlocked = {
                                locked = false
                                unlockSatisfied = true
                                relockOnForeground = false
                            },
                        )
                    } else {
                        SkytaleRoot(
                            state = state,
                            onContinue = viewModel::nextAuthStep,
                            onBack = viewModel::previousAuthStep,
                            onAuthModeSelected = viewModel::setAuthMode,
                            onLanguageSelected = viewModel::setLanguage,
                            onUsernameChanged = viewModel::updateUsername,
                            onPasswordChanged = viewModel::updatePassword,
                            onNicknameChanged = viewModel::updateNickname,
                            onAboutChanged = viewModel::updateAbout,
                            onSelectComposerMedia = viewModel::selectComposerMedia,
                            onClearComposerMedia = viewModel::clearComposerMedia,
                            onUpdateComposerMediaOptions = viewModel::updateComposerMediaOptions,
                            onCancelPendingUpload = viewModel::cancelPendingUpload,
                            onSelectProfileAvatar = viewModel::selectProfileAvatar,
                            onSelectChannelAvatar = viewModel::updateChannelAvatar,
                            onClearProfileAvatar = viewModel::clearProfileAvatarSelection,
                            onGeneratePassword = viewModel::generatePassword,
                            onSubmitAuth = viewModel::submitAuth,
                            onSelectTab = viewModel::selectTab,
                            onOpenChat = viewModel::openChat,
                            onOpenContactChat = viewModel::openContactChat,
                            onSaveContactNickname = viewModel::saveContactNicknameOverride,
                            onCloseChat = viewModel::closeChat,
                            onSendMessage = viewModel::sendMessage,
                            onEditMessage = viewModel::editMessage,
                            onDeleteMessage = viewModel::deleteMessage,
                            onReplyMessage = viewModel::startReply,
                            onCancelReply = viewModel::clearReply,
                            onSearchPeopleQueryChanged = viewModel::updateAddContactQuery,
                            onChatListQueryChanged = viewModel::updateChatListQuery,
                            onRefreshChats = viewModel::restore,
                            onSearchPeople = viewModel::searchPeople,
                            onSearchDirectory = viewModel::searchDirectory,
                            onAddContact = viewModel::addContactAndOpen,
                            onOpenDirectoryEntry = viewModel::openDirectoryEntry,
                            onCreateChannel = { title, username, description, commentsEnabled ->
                                viewModel.createChannel(
                                    title = title,
                                    username = username,
                                    description = description,
                                    commentsEnabled = commentsEnabled,
                                )
                            },
                            onOpenContactPreview = viewModel::openContactPreview,
                            onCloseContactPreview = viewModel::closeContactPreview,
                            onOpenPreviewChat = viewModel::openPreviewChat,
                            onMarkPreviewChatUnread = viewModel::markPreviewChatUnread,
                            onToggleArchive = viewModel::toggleArchive,
                            onTogglePin = viewModel::togglePin,
                            onToggleMute = viewModel::toggleMute,
                            onToggleUnread = viewModel::toggleUnread,
                            onRemoveCurrentContact = viewModel::removeCurrentContact,
                            onClearCurrentChat = viewModel::clearCurrentChat,
                            onToggleCurrentBlocked = viewModel::toggleCurrentContactBlocked,
                            onUpdateNotifications = viewModel::updateNotifications,
                            onUpdatePreviews = viewModel::updatePreviews,
                            onUpdateSound = viewModel::updateSound,
                            onUpdateInstantSync = viewModel::updateInstantSync,
                            onUpdateThemeMode = viewModel::updateThemeMode,
                            onSettingsLanguageSelected = viewModel::setLanguage,
                            onUpdateCompact = viewModel::updateCompact,
                            onUpdateHaptics = viewModel::updateHaptics,
                            onUpdateArchivedChats = viewModel::updateArchivedChats,
                            onUpdateAppLock = viewModel::updateAppLock,
                            onUpdateGhostMode = viewModel::updateGhostMode,
                            onLastSeenVisibilityChanged = viewModel::updateLastSeenVisibility,
                            onUsernameDiscoverableChanged = viewModel::updateUsernameDiscoverable,
                            onSaveProfile = viewModel::saveProfile,
                            onChangePassword = viewModel::changePassword,
                            onDeleteAccount = viewModel::deleteAccount,
                            onLogout = viewModel::logout,
                            onLogoutOthers = viewModel::logoutOthers,
                            onChatSearchChanged = viewModel::updateChatSearchQuery,
                            onSearchMessages = viewModel::searchMessages,
                            onEnsureMessageLoaded = viewModel::ensureMessageLoaded,
                            onLoadOlderMessages = viewModel::loadOlderMessages,
                            onTypingChanged = viewModel::setTyping,
                            onLoadChannelMembers = viewModel::loadChannelMembers,
                            onUpdateChannelRole = viewModel::updateChannelRole,
                            onUpdateChannel = { chatId, title, username, description, postingPolicy, commentsEnabled ->
                                viewModel.updateChannel(
                                    chatId = chatId,
                                    title = title,
                                    username = username,
                                    description = description,
                                    postingPolicy = postingPolicy,
                                    commentsEnabled = commentsEnabled,
                                )
                            },
                            onLeaveChannel = viewModel::leaveChannel,
                            onDeleteChannel = viewModel::deleteChannel,
                            onForwardChannelPost = viewModel::forwardMessageToChat,
                            onPinChannelPost = viewModel::pinChannelPost,
                            onLoadChannelComments = viewModel::loadChannelComments,
                            onCloseChannelComments = viewModel::closeChannelComments,
                            onSendChannelComment = viewModel::sendChannelComment,
                            onDismissErrorDialogs = viewModel::dismissErrorDialogs,
                            onConsumeSoundEvent = viewModel::consumeSoundEvent,
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        routeIntent(intent)
    }

    private fun routeIntent(intent: Intent?) {
        viewModel.handleNotificationChatIntent(intent?.getStringExtra(EXTRA_CHAT_ID))
        val data = intent?.data
        val username = when {
            data == null -> null
            data.scheme.equals("skytale", ignoreCase = true) -> data.lastPathSegment
            data.host.equals("use.skytale.dpdns.org", ignoreCase = true) -> data.pathSegments.firstOrNull()
            else -> null
        }
        viewModel.handlePublicLink(username)
    }

    fun suppressRelockOnce() {
        suppressNextRelock = true
    }

    companion object {
        const val EXTRA_CHAT_ID = "chatId"
    }
}

@Composable
private fun AppLockGate(
    lockCycle: Int,
    title: String,
    retryLabel: String,
    onUnlocked: () -> Unit,
) {
    val activity = LocalContext.current as FragmentActivity
    val onUnlockedState = rememberUpdatedState(onUnlocked)
    var promptVersion by remember(lockCycle) { mutableIntStateOf(lockCycle) }
    var promptRunning by remember(lockCycle) { mutableStateOf(false) }
    var failed by remember(lockCycle) { mutableStateOf(false) }

    fun launchPrompt() {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        if (BiometricManager.from(activity).canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            promptRunning = false
            failed = true
            return
        }
        promptRunning = true
        failed = false
        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    promptRunning = false
                    failed = false
                    onUnlockedState.value.invoke()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    promptRunning = false
                    failed = true
                }

                override fun onAuthenticationFailed() {
                    failed = true
                }
            },
        )
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setAllowedAuthenticators(authenticators)
                .build(),
        )
    }

    LaunchedEffect(promptVersion) {
        launchPrompt()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_mark),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.height(18.dp))
                Text(title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(14.dp))
                if (!promptRunning && failed) {
                    OutlinedButton(onClick = {
                        promptVersion += 1
                    }) {
                        Text(retryLabel)
                    }
                }
            }
        }
    }
}
