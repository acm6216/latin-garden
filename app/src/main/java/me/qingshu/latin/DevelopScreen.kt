package me.qingshu.latin

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.qingshu.latin.databinding.DevelopScreenBinding

class DevelopScreen:DialogFragment() {

    private var _binding: DevelopScreenBinding? = null

    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.develop_title)
            DevelopScreenBinding.inflate(layoutInflater).also {
                _binding = it
                setView(it.root)
            }
        }.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}