package me.qingshu.latin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.R
import me.qingshu.latin.data.ListItem
import me.qingshu.latin.databinding.PlantItemBinding
import me.qingshu.latin.extensions.isGone

class PlantAdapter(
    private val plantClick:(ListItem) -> Unit,
    private val completed:(ListItem)->Unit,
    private val soundClick:(ListItem)->Unit,
    private val favorite:(ListItem)->Unit
):ListAdapter<ListItem, PlantAdapter.PlantViewHolder>(
    object : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.data.id == newItem.data.id
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean{
            return oldItem.data.chinese==newItem.data.chinese && oldItem.data.latin==newItem.data.latin
                    && oldItem.data.isFavorite == newItem.data.isFavorite && oldItem.data.completed == newItem.data.completed
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        return PlantViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(getItem(position),plantClick,completed,soundClick,favorite,position)
    }

    class PlantViewHolder(
        private val binding: PlantItemBinding
    ):RecyclerView.ViewHolder(binding.root){

        fun bind(
            listItem: ListItem,
            click:(ListItem) -> Unit,
            completed:(ListItem)->Unit,
            soundClick:(ListItem)->Unit,
            favoriteClick:(ListItem)->Unit,
            position: Int
        ){
            binding.favorite.apply {
                isActivated = listItem.data.isFavorite
                setOnClickListener {
                    isActivated = !listItem.data.isFavorite
                    favoriteClick.invoke(listItem)
                }
            }
            binding.plant = listItem.data
            binding.executePendingBindings()
            binding.name.text = binding.root.context.getString(R.string.number_title,position+1,listItem.data.chinese)
            binding.inner.setOnClickListener {
                click.invoke(listItem)
            }
            binding.sound.setOnClickListener {
                soundClick.invoke(listItem)
            }
            binding.card.isChecked = listItem.data.completed
            binding.inner.setOnLongClickListener {
                completed.invoke(listItem)
                true
            }
            binding.definition.isGone(listItem.data.definition.trim().isEmpty())
        }

        companion object{
            fun from(parent:ViewGroup): PlantViewHolder {
                return PlantViewHolder(
                    PlantItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                )
            }
        }
    }

    /*override fun getPopupText(position: Int): String {
        val target = getItem(position)
        return when(target.sortBy){
            SortBy.CHINESE -> target.data.chinese
            SortBy.LATIN -> target.data.latin
            SortBy.FAMILY -> target.data.family
            else -> target.data.category
        }.trim().first().toString()
    }*/
}
