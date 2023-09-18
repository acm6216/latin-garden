package me.qingshu.latin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.data.CourseCard
import me.qingshu.latin.databinding.CourseCardBinding

class CourseCardAdapter(
    private val click:(CourseCard)->Unit,
    private val groupClick:(CourseCard)->Unit
):ListAdapter<CourseCard,CourseCardAdapter.CourseCardViewHolder>(
    object :DiffUtil.ItemCallback<CourseCard>(){
        override fun areItemsTheSame(oldItem: CourseCard, newItem: CourseCard): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: CourseCard, newItem: CourseCard): Boolean {
            return oldItem.progress==newItem.progress && oldItem.max == newItem.max
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseCardViewHolder {
        return CourseCardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CourseCardViewHolder, position: Int) {
        holder.bind(getItem(position),click,groupClick)
    }

    class CourseCardViewHolder private constructor(
        private val binding:CourseCardBinding
    ):RecyclerView.ViewHolder(binding.root){

        fun bind(card: CourseCard,click:(CourseCard)->Unit,groupClick:(CourseCard)->Unit){
            binding.card = card
            binding.label.text = card.label()
            binding.executePendingBindings()
            binding.fab.apply {
                setOnClickListener {
                    groupClick.invoke(card)
                }
            }
            binding.icon.setImageResource(card.icon)
            binding.click.setOnClickListener {
                click.invoke(card)
            }
        }

        companion object{
            fun from(parent:ViewGroup):CourseCardViewHolder{
                return CourseCardViewHolder(
                    CourseCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                )
            }
        }
    }
}