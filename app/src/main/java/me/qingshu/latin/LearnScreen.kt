package me.qingshu.latin

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.launch
import me.qingshu.latin.adapters.CardLayoutManager
import me.qingshu.latin.adapters.PlantCardAdapter
import me.qingshu.latin.adapters.ItemTouchHelperCallback
import me.qingshu.latin.databinding.LearnScreenBinding

class LearnScreen:BaseFragment<LearnScreenBinding>() {

    override fun layout() = LearnScreenBinding.inflate(layoutInflater)

    private val plantCardAdapter = PlantCardAdapter(
        remember = {
            viewModel.remember(it)
        },
        notRemember = {
            viewModel.notRemember(it)
        },
        soundClick = {
            voiceViewModel.play(it)
        },
        favorite = {
            viewModel.toggleFavorite(it)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = plantCardAdapter
        binding.recyclerView.layoutManager = CardLayoutManager()

        repeatWithViewLifecycle {
            launch {
                viewModel.displayLearnCard.collect {
                    plantCardAdapter.submitList(it)
                    if(it.isNotEmpty()) {
                        voiceViewModel.autoPlay(it.last())
                    }
                    binding.recyclerView.apply {
                        val itemTouchHelperCallback = ItemTouchHelperCallback { item ->
                            viewModel.skip(item)
                        }
                        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                        itemTouchHelper.attachToRecyclerView(this)
                    }
                }
            }
        }

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
    }

    override fun onClicks() {
        binding.finished.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tryAgain.setOnClickListener {
            viewModel.tryAgain()
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

}