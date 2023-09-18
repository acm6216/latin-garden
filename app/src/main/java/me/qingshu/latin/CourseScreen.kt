package me.qingshu.latin

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.qingshu.latin.adapters.QuizCardAdapter
import me.qingshu.latin.adapters.CourseCardAdapter
import me.qingshu.latin.data.QuizCard
import me.qingshu.latin.data.CourseCard
import me.qingshu.latin.data.CourseType
import me.qingshu.latin.data.QuizType
import me.qingshu.latin.database.Plant
import me.qingshu.latin.databinding.CourseScreenBinding
import me.qingshu.latin.model.DatabaseViewModel
import me.qingshu.latin.model.QuizViewModel

@AndroidEntryPoint
class CourseScreen : BaseFragment<CourseScreenBinding>() {

    override fun layout() = CourseScreenBinding.inflate(layoutInflater)

    private val quizViewModel: QuizViewModel by activityViewModels()
    private val databaseViewModel: DatabaseViewModel by activityViewModels()

    private val courseCardAdapter = CourseCardAdapter(
        click = {
            if(it.type==CourseType.COMPLETED && it.progress==0){
                viewModel.message(R.string.message_completed_empty)
            }
            else {
                if (it.max != 0) {
                    viewModel.setLearnDataSource(it.type)
                    viewModel.courseCard(it)
                    findNavController().navigate(CourseScreenDirections.actionToLearn(),navOptions)
                } else
                    when (it.type) {
                        CourseType.WRONG_QUESTION_BOOK -> R.string.message_wrong_question_book_empty
                        else -> R.string.message_review_empty
                    }.also { id -> viewModel.message(id) }
            }
        },
        groupClick = {
            if(it.type==CourseType.COMPLETED && it.progress==0){
                viewModel.message(R.string.message_completed_empty)
            }else {
                if (it.max != 0) viewModel.playGroup(it)
                else viewModel.message(R.string.message_group_empty)
            }
        }
    )

    private val itemClick:(MenuItem)->Unit = {
        when (it.itemId) {
            R.id.action_privacy_policy -> findNavController().navigate(
                CourseScreenDirections.actionToPrivacyPolicy(), navOptions
            )
            R.id.action_open_source -> findNavController().navigate(
                CourseScreenDirections.actionToOpenText(
                    "open_source.txt",R.string.action_open_source
                ),navOptions
            )
            R.id.action_update_log -> findNavController().navigate(
                CourseScreenDirections.actionToOpenText(
                    "update.txt",R.string.action_update_log
                ),navOptions
            )
            else -> {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sentenceToolbar = SentenceToolbar(
            binding.toolbar,
            callback = itemClick,
            titleClick = {
                MaterialAlertDialogBuilder(useContext).apply {
                    setTitle(it.sentence)
                    setMessage(it.subtitle)
                    setPositiveButton(android.R.string.ok,null)
                    show()
                }
            }
        )
        val playBarToolbar = PlayBarToolbar(
            binding.toolbar,
            closeClick = {
                viewModel.randomGold()
                viewModel.playGroup(CourseCard.EMPTY)
            },
            pauseClick = {
                voiceViewModel.pause(it)
            }
        )

        sharedPreference.getBoolean(Plant.CREATE,false).also {
            if(!it) databaseViewModel.load(useContext)
            else viewModel.randomGold()
        }

        binding.course.adapter = courseCardAdapter

        repeatWithViewLifecycle {
            launch {
                viewModel.gold.collect {
                    sentenceToolbar.load(it,childFragmentManager)
                }
            }
            launch {
                voiceViewModel.playDisplay.collect{
                    playBarToolbar.load(it,childFragmentManager)
                }
            }
            launch {
                viewModel.courseItems.collect {
                    courseCardAdapter.submitList(it)
                }
            }
        }
        binding.quiz.apply {
            adapter = QuizCardAdapter {
                quizViewModel.quizType(it)
                when(it.type){
                    QuizType.SINGLE -> quizViewModel.randomSingleQuiz()
                    QuizType.COMPLETION -> quizViewModel.randomCompletion()
                    else -> {}
                }
                if(it.type!=QuizType.HISTORY)
                    findNavController().navigate(CourseScreenDirections.actionToQuiz(),navOptions)
            }.also { it.submitList(QuizCard.target) }
        }

        binding.toolbar.apply {
            setNavigationOnClickListener {
            }
        }

        binding.viewmodel = viewModel
        binding.executePendingBindings()
    }

    override fun onDestroyView() {
        viewModel.setEmptyPlayDataSource()
        super.onDestroyView()
    }
}