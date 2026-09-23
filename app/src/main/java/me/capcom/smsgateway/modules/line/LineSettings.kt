package me.capcom.smsgateway.modules.line

import me.capcom.smsgateway.modules.settings.KeyValueStorage
import me.capcom.smsgateway.modules.settings.get

/** Settings for direct LINE Messaging API notifications. */
class LineSettings(
    private val storage: KeyValueStorage,
) {
    var enabled: Boolean
        get() = storage.get<Boolean>(ENABLED) ?: false
        set(value) = storage.set(ENABLED, value)

    var channelAccessToken: String?
        get() = storage.get<String>(CHANNEL_ACCESS_TOKEN)?.takeIf { it.isNotBlank() }
        set(value) = storage.set(CHANNEL_ACCESS_TOKEN, value?.trim())

    /** A LINE user ID (for example, Uxxxxxxxx...). */
    var recipientUserId: String?
        get() = storage.get<String>(RECIPIENT_USER_ID)?.takeIf { it.isNotBlank() }
        set(value) = storage.set(RECIPIENT_USER_ID, value?.trim())

    val isConfigured: Boolean
        get() = enabled && channelAccessToken != null && recipientUserId != null

    private companion object {
        const val ENABLED = "enabled"
        const val CHANNEL_ACCESS_TOKEN = "channel_access_token"
        const val RECIPIENT_USER_ID = "recipient_user_id"
    }
}
