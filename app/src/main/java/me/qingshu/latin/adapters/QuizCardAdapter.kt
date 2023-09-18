package me.qingshu.latin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.data.QuizCard
import me.qingshu.latin.databinding.QuizCardBinding

class QuizCardAdapter(
    private val click:(QuizCard)->Unit
): ListAdapter<QuizCard, QuizCardAdapter.QuizCardViewHolder>(
object : DiffUtil.ItemCallback<QuizCard>(){
    override fun areItemsTheSame(oldItem: QuizCard, newItem: QuizCard): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: QuizCard, newItem: QuizCard): Boolean {
        return oldItem.title==newItem.title && oldItem.subtitle == newItem.subtitle
    }
}
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizCardViewHolder {
        return QuizCardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: QuizCardViewHolder, position: Int) {
        holder.bind(getItem(position),click)
    }

    class QuizCardViewHolder private constructor(
        private val binding: QuizCardBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(card: QuizCard, click:(QuizCard)->Unit){
            binding.cap = card
            binding.executePendingBindings()
            binding.icon.setImageResource(card.icon)
            binding.click.setOnClickListener {
                click.invoke(card)
            }
        }

        companion object{
            fun from(parent: ViewGroup):QuizCardViewHolder{
                return QuizCardViewHolder(
                    QuizCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                )
            }
        }
    }
}