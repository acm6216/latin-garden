package me.qingshu.latin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.data.ListItem
import me.qingshu.latin.databinding.PlantCardItemBinding

class PlantCardAdapter(
    private val remember:(ListItem)->Unit,
    private val notRemember:(ListItem)->Unit,
    private val soundClick:(ListItem)->Unit,
    private val favorite:(ListItem)->Unit
):ListAdapter<ListItem, PlantCardAdapter.PlantCardViewHolder>(
    object : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.data.id == newItem.data.id
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean{
            return oldItem.data.chinese==newItem.data.chinese
                    && oldItem.data.latin==newItem.data.latin
                    && oldItem.data.completed==newItem.data.completed
                    && oldItem.data.isFavorite==newItem.data.isFavorite
        }
    }
) {

    override fun onBindViewHolder(holder: PlantCardViewHolder, position: Int) {
        holder.bind(getItem(position), soundClick,remember,notRemember,favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantCardViewHolder {
        return PlantCardViewHolder.from(parent)
    }

    class PlantCardViewHolder(
        val binding:PlantCardItemBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: ListItem,
            soundClick: (ListItem) -> Unit,
            rememberClick: (ListItem) -> Unit,
            notRememberClick: (ListItem) -> Unit,
            favoriteClick:(ListItem)->Unit
        ) {
            binding.item = item
            binding.executePendingBindings()
            binding.rotate.end()
            execute(arrayListOf(binding.sound,binding.sound1)){
                it.setOnClickListener {
                    soundClick.invoke(item)
                }
            }
            execute(arrayListOf(binding.remember,binding.remember1)){
                it.setOnClickListener {
                    rememberClick.invoke(item)
                }
            }
            execute(arrayListOf(binding.favorite,binding.favorite1)){ icon ->
                icon.isActivated = item.data.isFavorite
                icon.setOnClickListener {
                    it.isActivated = !item.data.isFavorite
                    favoriteClick.invoke(item)
                }
            }
            execute(arrayListOf(binding.notRemember,binding.notRemember1)){
                it.setOnClickListener { notRememberClick.invoke(item) }
            }

            binding.rotate.setOnClickListener {
                binding.rotate.rotate3D()
            }
        }

        private fun <T:View> execute(views: List<T>, call:(T)->Unit){
            views.forEach {
                call.invoke(it)
            }
        }

        val data: ListItem? get() = binding.item

        companion object{
            fun from(parent:ViewGroup):PlantCardViewHolder{
                return PlantCardViewHolder(
                    PlantCardItemBinding.inflate(
                        LayoutInflater.from(parent.context),parent,false
                    )
                )
            }
        }
    }
}