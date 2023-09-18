package me.qingshu.latin

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import me.qingshu.latin.databinding.OpenTextBinding

class OpenText:BaseFragment<OpenTextBinding>() {

    override fun layout(): OpenTextBinding = OpenTextBinding.inflate(layoutInflater)
    private val args: OpenTextArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.resName.takeIf {
            it.isNotEmpty()
        }?.also { name ->
            context?.resources?.assets?.open(name)
                ?.bufferedReader()
                ?.use { it.readText() }
                ?.also {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.text.text = Html.fromHtml(it,Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
                    }else binding.text.text = Html.fromHtml(it)
                }
        }

        args.resTitle.takeIf {
            it!=0
        }?.also { binding.toolbar.setTitle(it) }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}