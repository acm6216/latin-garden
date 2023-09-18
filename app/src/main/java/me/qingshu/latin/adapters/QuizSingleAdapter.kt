package me.qingshu.latin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.R
import me.qingshu.latin.data.Answer
import me.qingshu.latin.database.Problem
import me.qingshu.latin.databinding.QuizSingleItemBinding

class QuizSingleAdapter(
    private val commitAnswer:(String,Problem)->Unit
): ListAdapter<Answer, QuizSingleAdapter.QuizViewHolder>(
    object :DiffUtil.ItemCallback<Answer>(){
        override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.problem == newItem.problem && oldItem.isTesting == newItem.isTesting
        }

        override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.answer == newItem.answer
                    && oldItem.problem==newItem.problem
                    && oldItem.isTesting == newItem.isTesting
        }
    }
) {

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position),commitAnswer,position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        return QuizViewHolder.from(parent)
    }

    class QuizViewHolder private constructor(
        private val binding:QuizSingleItemBinding
    ):RecyclerView.ViewHolder(binding.root){

        private val context get() = binding.root.context

        fun bind(answer: Answer,commit:(String,Problem)->Unit,position:Int){
            binding.problem = answer.problem
            binding.executePendingBindings()
            binding.detail.text = binding.root.context.getString(R.string.number_title,position+1,answer.problem.problemDetail)
            val arr = arrayOf(binding.a,binding.b,binding.c,binding.d)
            val ans = arrayOf("A","B","C","D")
            arr.forEach {
                it.isChecked = false
            }
            if(answer.answer.isNotEmpty()){
                ans.indexOf(answer.answer).also {
                    arr[it].isChecked = true
                }
            }
            arr.forEachIndexed { index, radioButton ->
                radioButton.setOnClickListener {
                    if(answer.isTesting) {
                        commit.invoke(ans[index], answer.problem)
                    }else radioButton.isChecked = index == ans.indexOf(answer.answer)
                }
            }
            binding.answer.visibility = if(answer.isTesting) View.GONE
            else View.VISIBLE
            if(!answer.isTesting && answer.answer==answer.problem.answer.trim())
                binding.answer.setTextColor(ContextCompat.getColor(context,R.color.pass))
        }

        companion object{
            fun from(parent:ViewGroup):QuizViewHolder{
                return QuizViewHolder(
                    QuizSingleItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                )
            }
        }

    }
}