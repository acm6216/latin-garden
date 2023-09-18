package me.qingshu.latin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import me.qingshu.latin.adapters.QuizCompletionAdapter
import me.qingshu.latin.adapters.QuizSingleAdapter
import me.qingshu.latin.data.QuizType
import me.qingshu.latin.databinding.QuizScreenBinding
import me.qingshu.latin.extensions.isGone
import me.qingshu.latin.model.QuizViewModel

class QuizScreen:BaseFragment<QuizScreenBinding>() {

    override fun layout() = QuizScreenBinding.inflate(layoutInflater)
    private val quizViewModel: QuizViewModel by activityViewModels()

    private val quizSingleAdapter = QuizSingleAdapter{ choice, problem ->
        quizViewModel.commitAnswer(choice,problem)
    }

    private val quizCompletionAdapter = QuizCompletionAdapter{
        quizViewModel.completionAnswer(it)
        findNavController().takeIf { nav->
            nav.currentDestination?.id != R.id.nav_quiz_answer
        }?.navigate(QuizScreenDirections.toAnswer())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.quiz = quizViewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        repeatWithViewLifecycle {
            launch {
                quizViewModel.displaySingleQuiz.collect{
                    quizSingleAdapter.submitList(it)
                }
            }
            launch {
                quizViewModel.displayCompletion.collect{
                    quizCompletionAdapter.submitList(it)
                }
            }
            launch {
                quizViewModel.quizType.collect{
                    binding.recyclerView.adapter = when(it){
                        QuizType.SINGLE -> quizSingleAdapter
                        else -> quizCompletionAdapter
                    }
                }
            }
        }

    }

    override fun onClicks() {
        binding.recyclerView.setItemViewCacheSize(10)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.renew.setOnClickListener {
            quizViewModel.renew()
            binding.empty.isGone(true)
        }

        binding.commit.setOnClickListener {
            quizViewModel.commitSingleQuiz()
        }
    }

}