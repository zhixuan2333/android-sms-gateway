package me.capcom.smsgateway.ui.settings

import android.os.Bundle
import android.text.InputType
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.capcom.smsgateway.R
import me.capcom.smsgateway.modules.line.LineMessagingService
import org.koin.android.ext.android.inject

class LineSettingsFragment : BasePreferenceFragment() {
    private val lineMessagingService: LineMessagingService by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.line_preferences, rootKey)

        findPreference<Preference>("line.send_test_message")?.setOnPreferenceClickListener {
            lifecycleScope.launch {
                val error = withContext(Dispatchers.IO) {
                    lineMessagingService.sendTestMessage()
                }
                if (error == null) {
                    showToast(R.string.line_test_sent)
                } else {
                    showToast(getString(R.string.line_test_failed, error))
                }
            }
            true
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference.key == "line.channel_access_token") {
            (preference as EditTextPreference).setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                it.setSelectAllOnFocus(true)
            }
        }
        super.onDisplayPreferenceDialog(preference)
    }
}
