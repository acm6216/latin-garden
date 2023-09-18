package me.qingshu.latin.preference

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import me.qingshu.latin.R
import me.qingshu.latin.model.DatabaseViewModel

class SettingsScreen : BasePreferenceFragment() {

    override fun isShowIcon(): Boolean {
        return true
    }

    override fun windowsInset(): Boolean {
        return true
    }

    private val databaseViewModel: DatabaseViewModel by activityViewModels()
    private val documentTree =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.also {
                databaseViewModel.export(it,requireContext())
            }
        }
    
    private val completionFile =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.also {
                databaseViewModel.exportCompletion(it,requireContext())
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        R.string.setting_data_export_word_table_key.click {
            documentTree.launch(null)
        }

        R.string.setting_data_export_quiz_completion_key.click {
            completionFile.launch(null)
        }
    }
}