package me.qingshu.latin

import android.os.Bundle
import android.view.View
import me.qingshu.latin.adapters.PlantAdapter
import me.qingshu.latin.databinding.SearchScreenBinding

class SearchScreen : BaseFragment<SearchScreenBinding>() {

    override fun layout() = SearchScreenBinding.inflate(layoutInflater)

    private val plantAdapter = PlantAdapter(
        plantClick = {

        },
        plantLongClick = {

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

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        repeatWithViewLifecycle {
            viewModel.searchItems.collect {
                plantAdapter.submitList(it)
            }
        }
        binding.recyclerView.apply {
            //addItemDecoration(DetailItemDecoration(8))
            adapter = plantAdapter
        }
        binding.clearText.setOnClickListener{
            viewModel.clearText()
        }
    }

}