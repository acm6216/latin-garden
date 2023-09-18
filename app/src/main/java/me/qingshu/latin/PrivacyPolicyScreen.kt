package me.qingshu.latin

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.navigation.fragment.findNavController
import me.qingshu.latin.databinding.AboutScreenBinding
import me.qingshu.latin.extensions.edit

class PrivacyPolicyScreen:BaseFragment<AboutScreenBinding>() {

    override fun layout() = AboutScreenBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.resources?.assets?.open("privacy_policy.txt")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?.also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.text.text = Html.fromHtml(it,Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
                }else binding.text.text = Html.fromHtml(it)
            }

        sharedPreference.getBoolean(getString(R.string.privacy_policy_status_key),false).also {
            binding.notReminder.isChecked = it
        }
    }

    override fun onClicks() {
        binding.toolbar.setNavigationOnClickListener {
            sharedPreference.getBoolean(it.context.getString(R.string.privacy_policy_status_key),false).also { enable ->
                if(enable) findNavController().navigateUp()
            }
        }
        binding.close.setOnClickListener {
            sharedPreference.edit {
                putBoolean(it.context.getString(R.string.privacy_policy_status_key),binding.notReminder.isChecked)
            }.also {
                findNavController().navigateUp()
            }
        }
        binding.finish.setOnClickListener {
            requireActivity().finish()
        }
    }

}