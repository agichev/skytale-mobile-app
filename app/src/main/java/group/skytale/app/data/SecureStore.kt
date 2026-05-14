package group.skytale.app.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SecureStore(
    context: Context,
    private val json: Json,
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        "skytale-secure-store",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val sessionState = MutableStateFlow(loadSession())
    private val settingsState = MutableStateFlow(loadSettings())

    val sessionFlow: StateFlow<StoredSession?> = sessionState
    val settingsFlow: StateFlow<AppSettings> = settingsState

    val currentSession: StoredSession?
        get() = sessionState.value

    fun saveSession(session: StoredSession) {
        preferences.edit().putString(KEY_SESSION, json.encodeToString(session)).apply()
        sessionState.value = session
    }

    fun clearSession() {
        preferences.edit().remove(KEY_SESSION).apply()
        sessionState.value = null
    }

    fun saveSettings(settings: AppSettings) {
        preferences.edit().putString(KEY_SETTINGS, json.encodeToString(settings)).apply()
        settingsState.value = settings
    }

    private fun loadSession(): StoredSession? {
        return preferences.getString(KEY_SESSION, null)?.let {
            runCatching { json.decodeFromString<StoredSession>(it) }.getOrNull()
        }
    }

    private fun loadSettings(): AppSettings {
        return preferences.getString(KEY_SETTINGS, null)?.let {
            runCatching { json.decodeFromString<AppSettings>(it) }.getOrNull()
        }?.let { loaded ->
            if (loaded.instantSyncConfigured) loaded else loaded.copy(instantSyncEnabled = false)
        } ?: AppSettings()
    }

    private companion object {
        const val KEY_SESSION = "session"
        const val KEY_SETTINGS = "settings"
    }
}

class DeviceCrypto {
    private val keyAlias = "skytale-local-message-key"
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    @Volatile
    private var cachedKey: SecretKey? = null

    fun encrypt(plaintext: String): String {
        if (plaintext.isBlank()) {
            return ""
        }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(iv + encrypted, Base64.NO_WRAP)
    }

    fun decrypt(ciphertext: String): String {
        if (ciphertext.isBlank()) {
            return ""
        }
        return runCatching {
            val data = Base64.decode(ciphertext, Base64.NO_WRAP)
            val iv = data.copyOfRange(0, IV_LENGTH)
            val body = data.copyOfRange(IV_LENGTH, data.size)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(body), StandardCharsets.UTF_8)
        }.getOrDefault("")
    }

    private fun getOrCreateKey(): SecretKey {
        cachedKey?.let { return it }
        synchronized(this) {
            cachedKey?.let { return it }
            (keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry)?.secretKey?.let {
                cachedKey = it
                return it
            }
        }
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build(),
        )
        return keyGenerator.generateKey().also { cachedKey = it }
    }

    private companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_LENGTH = 12
    }
}
