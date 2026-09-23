package me.capcom.smsgateway.modules.line

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.capcom.smsgateway.modules.logs.LogsService
import me.capcom.smsgateway.modules.logs.db.LogEntry
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/** Sends a direct push message to the configured LINE user without using webhooks. */
class LineMessagingService(
    private val settings: LineSettings,
    private val logsService: LogsService,
) {
    private val client = OkHttpClient()

    fun notifyIncomingSms(sender: String, body: String) {
        if (!settings.isConfigured) return

        push("New SMS from $sender:\n$body")?.let { error ->
            logsService.insert(
                LogEntry.Priority.ERROR,
                MODULE_NAME,
                "LINE push notification failed",
                mapOf("error" to error),
            )
        }
    }

    /** Returns null after a successful push, otherwise a safe error description for the UI. */
    fun sendTestMessage(): String? {
        if (!settings.isConfigured) return "LINE is not configured"
        return push("SMSGate test message")
    }

    private fun push(message: String): String? {
        val json = JsonObject().apply {
            addProperty("to", settings.recipientUserId)
            add("messages", JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("type", "text")
                    addProperty("text", message.take(MAX_LINE_TEXT_LENGTH))
                })
            })
        }
        val request = Request.Builder()
            .url(PUSH_API_URL)
            .header("Authorization", "Bearer ${settings.channelAccessToken}")
            .post(json.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return "HTTP ${response.code}"
                }
            }
            return null
        } catch (e: Exception) {
            Log.e(MODULE_NAME, "LINE push notification failed", e)
            logsService.insert(
                LogEntry.Priority.ERROR,
                MODULE_NAME,
                "LINE push notification failed",
                mapOf("exception" to e.stackTraceToString()),
            )
            return e.javaClass.simpleName
        }
    }

    private companion object {
        const val MODULE_NAME = "line"
        const val PUSH_API_URL = "https://api.line.me/v2/bot/message/push"
        const val MAX_LINE_TEXT_LENGTH = 5_000
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
