package me.qingshu.latin.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.qingshu.latin.data.Answer
import me.qingshu.latin.data.Completion
import me.qingshu.latin.data.QuizCard
import me.qingshu.latin.data.QuizType
import me.qingshu.latin.database.Problem
import me.qingshu.latin.database.UseCase
import me.qingshu.latin.extensions.chineseType
import me.qingshu.latin.extensions.chineseWithFamily
import me.qingshu.latin.extensions.familyType
import me.qingshu.latin.extensions.latinType
import me.qingshu.latin.extensions.latinWithFamily
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    useCase: UseCase
):ViewModel() {

    private val plants = useCase.queryPlants()

    private val problems = useCase.queryProblems()

    val isTesting = MutableStateFlow(true)

    private val quiz = combine(plants, problems) { plant, problem ->
        plant.filter { it.completed }.also {
            isEmpty.emit(it.isNotEmpty())
        }.let { p ->
            val c = p.map { "${it.resId}_${it.type}".trim() }
            problem.filter {
                val t = "${it.resId}_${it.type}".trim()
                t in c
            }
        }
    }

    private val randomQuiz = MutableStateFlow<MutableList<Answer>>(ArrayList())
    val displaySingleQuiz = combine(randomQuiz,isTesting){ user, test ->
        user.map {
            Answer(it.answer,it.problem,test)
        }
    }

    val quizType = MutableStateFlow(QuizType.SINGLE)
    val quizCard = MutableStateFlow(QuizCard.target[0])
    private val quizCount = MutableStateFlow(10)
    fun quizCount(value:Int){
        viewModelScope.launch {
            quizCount.emit(value)
        }
    }
    fun quizType(type: QuizCard){
        viewModelScope.launch {
            quizType.emit(type.type)
            quizCard.emit(type)
        }
    }

    val isEmpty = MutableStateFlow(true)

    val singleQuizStatus = MutableStateFlow("0/0")

    fun commitAnswer(userChoice: String, problem: Problem) {
        viewModelScope.launch {
            val target = randomQuiz.first().toMutableList()
            target.first {
                it.problem.resId == problem.resId && it.problem.type == problem.type
            }.also { ans ->
                val index = target.indexOf(ans)
                target.removeAt(index)
                target.add(index, Answer(userChoice, problem))
                randomQuiz.emit(target)
                singleQuizStatus.emit("${target.filter { it.answer.isNotEmpty() }.size}/${target.size}")
            }
        }
    }

    fun commitSingleQuiz(){
        viewModelScope.launch {
            isTesting.emit(false)
        }
    }

    fun renew(){
        viewModelScope.launch {
            when(quizType.first()){
                QuizType.SINGLE -> randomSingleQuiz()
                QuizType.COMPLETION -> randomCompletion()
                else -> {}
            }
        }
    }

    fun randomSingleQuiz() = viewModelScope.launch {
        isTesting.emit(true)
        randomQuiz.emit(emptyArray<Answer>().toMutableList())
        val count = quizCount.first()
        val target = quiz.first().toMutableList()
        target.shuffle()
        val result = if (target.size < count) target
        else target.subList(0, count)
        result.map {
            Answer("", it)
        }.toMutableList().also {
            randomQuiz.emit(it)
            singleQuizStatus.emit("0/${it.size}")
        }
    }

    ////

    private val completion = MutableStateFlow(emptyList<Completion>())
    val displayCompletion = combine(completion,isTesting){ c,test ->
        val com = c.filter { it.useAnswer.isNotEmpty() }.size
        singleQuizStatus.emit("$com/${c.size}")
        c.map {
            Completion(
                resId = it.resId,
                detail = it.detail,
                answer = it.answer,
                isTesting = test,
                useAnswer = it.useAnswer
            )
        }
    }

    fun randomCompletion() = viewModelScope.launch {
        isTesting.emit(true)
        singleQuizStatus.emit("0/0")
        completion.emit(emptyList())
        plants.first().filter {
            it.completed
        }.also { p ->
            isEmpty.emit(p.isNotEmpty())
            val count = quizCount.first()
            val temp = p.toMutableList()
            val random = Random.Default
            val result = if(temp.size<count) temp
            else temp.subList(0,count)
            result.shuffle()
            result.map {
                when(random.nextInt(5)){
                    0 -> it.chineseType()
                    1 -> it.latinType(random)
                    2 -> it.familyType(random)
                    3 -> it.chineseWithFamily(random)
                    else -> it.latinWithFamily(random)
                }
            }.also {
                completion.emit(it)
                singleQuizStatus.emit("0/${it.size}")
            }
        }
    }

    val completionAnswer = MutableStateFlow(Completion.empty())
    fun completionAnswer(completion: Completion){
        viewModelScope.launch {
            completionAnswer.emit(completion)
        }
    }

    fun commitCompletionQuiz(ans:String){
        if(!isTesting.value) return
        viewModelScope.launch {
            val current = completionAnswer.first()
            val target = Completion(
                resId = current.resId,
                detail = current.detail,
                answer = current.answer,
                useAnswer = ans
            )
            val modifier = completion.first().toMutableList()
            val index = modifier.indexOfFirst {
                it.resId == target.resId
            }
            modifier.removeAt(index)
            modifier.add(index,target)
            completion.emit(modifier)
        }
    }
}