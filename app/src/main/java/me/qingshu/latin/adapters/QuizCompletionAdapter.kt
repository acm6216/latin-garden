package me.qingshu.latin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.R
import me.qingshu.latin.data.Completion
import me.qingshu.latin.databinding.QuizCompletionItemBinding

class QuizCompletionAdapter(
    private val completionClick:(Completion)->Unit
):ListAdapter<Completion,QuizCompletionAdapter.CompletionViewHolder>(
    object :DiffUtil.ItemCallback<Completion>(){
        override fun areItemsTheSame(oldItem: Completion, newItem: Completion): Boolean {
            return oldItem.detail == newItem.detail
                    && oldItem.resId == newItem.resId
                    && oldItem.isTesting == newItem.isTesting
                    && oldItem.useAnswer == newItem.useAnswer
        }

        override fun areContentsTheSame(oldItem: Completion, newItem: Completion): Boolean {
            return oldItem.detail == newItem.detail
                    && oldItem.answer == newItem.answer
                    && oldItem.isTesting == newItem.isTesting
                    && oldItem.useAnswer == newItem.useAnswer
        }

    }
) {

    override fun onBindViewHolder(holder: CompletionViewHolder, position: Int) {
        holder.bind(getItem(position),position,completionClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletionViewHolder {
        return CompletionViewHolder.from(parent)
    }

    class CompletionViewHolder private constructor(
        val binding:QuizCompletionItemBinding
    ):RecyclerView.ViewHolder(binding.root){

        fun bind(completion: Completion,position: Int,click:(Completion)->Unit){
            binding.completion = completion
            binding.executePendingBindings()
            binding.detail.text = binding.root.context.getString(R.string.number_title,position+1,completion.detail)
            binding.answer.visibility = if(completion.isTesting) View.GONE
            else View.VISIBLE
            binding.clickRoot.setOnClickListener { click.invoke(completion) }

            binding.useAnswer.visibility = if(completion.useAnswer.isEmpty()) View.GONE
            else View.VISIBLE
        }

        companion object{
            fun from(parent:ViewGroup):CompletionViewHolder{
                return CompletionViewHolder(
                    QuizCompletionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                )
            }
        }
    }
}