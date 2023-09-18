package me.qingshu.latin.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import me.qingshu.latin.R

data class QuizCard(
    @DrawableRes val icon:Int,
    @StringRes val title:Int,
    @StringRes val subtitle:Int,
    val type:QuizType
){
    companion object{
        val target = arrayListOf(
            QuizCard(
                icon = R.drawable.ic_radio,
                R.string.learn_test_radio,
                R.string.learn_test_radio_hint,
                type = QuizType.SINGLE
            ),
            QuizCard(
                icon = R.drawable.ic_blank,
                R.string.learn_test_completion,
                R.string.learn_test_completion_hint,
                type = QuizType.COMPLETION
            )/*,
            QuizCard(
                icon = R.drawable.ic_history,
                R.string.learn_history_title,
                R.string.learn_history_zero,
                type = QuizType.HISTORY
            )*/
        )
    }
}