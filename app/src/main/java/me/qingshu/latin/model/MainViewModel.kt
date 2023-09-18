package me.qingshu.latin.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import me.qingshu.latin.R
import me.qingshu.latin.data.CourseCard
import me.qingshu.latin.data.CourseType
import me.qingshu.latin.data.ListItem
import me.qingshu.latin.data.PlantType
import me.qingshu.latin.data.PlayController
import me.qingshu.latin.data.SortBy
import me.qingshu.latin.database.Gold
import me.qingshu.latin.database.Plant
import me.qingshu.latin.database.UseCase
import me.qingshu.latin.extensions.intervalDay
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: UseCase
):ViewModel() {

    private val allPlants = useCase.queryPlants()

    val playbackSpeed = MutableStateFlow(1f)
    fun playbackSpeed(speed:Float){
        viewModelScope.launch {
            playbackSpeed.emit(speed)
        }
    }
    private val sortBy = MutableStateFlow(SortBy.CHINESE)
    fun sortBy(by: SortBy){
        viewModelScope.launch {
            sortBy.emit(by)
        }
    }
    private val plants = combine(allPlants,sortBy){ plant,sort ->
        val comparator = Collator.getInstance(Locale.CHINA)
        plant.toMutableList().sortedWith { p1, p2 ->
            when (sort) {
                SortBy.CHINESE -> comparator.compare(p1.chinese, p2.chinese)
                SortBy.LATIN -> comparator.compare(p1.latin, p2.latin)
                SortBy.FAMILY -> comparator.compare(p1.family, p2.family)
                else -> comparator.compare(p1.category, p2.category)
            }
        }
    }

    private val learnDataSource = MutableStateFlow(CourseType.TREE)

    val courseCard = MutableStateFlow(CourseCard.EMPTY)
    fun courseCard(card: CourseCard){
        viewModelScope.launch {
            courseCard.emit(card)
        }
    }

    fun setLearnDataSource(type:CourseType){
        viewModelScope.launch {
            playDataSource.emit(CourseCard.EMPTY)
            learnDataSource.emit(type)
            plantItemsCompleted.emit(emptyList())
        }
    }

    fun tryAgain(){
        setLearnDataSource(learnDataSource.value)
    }

    fun setEmptyPlayDataSource(){
        viewModelScope.launch {
            playDataSource.emit(CourseCard.EMPTY)
        }
    }

    private val golds = useCase.queryGolds()
    private val _gold = Channel<Gold>(capacity = Channel.CONFLATED)
    val gold = _gold.receiveAsFlow()

    fun randomGold(){
        viewModelScope.launch {
            golds.first().also { lg ->
                if(lg.isNotEmpty()) {
                    lg.random().also {
                        _gold.trySend(it)
                    }
                }
            }
        }
    }

    private val _message = Channel<Int>(capacity = Channel.CONFLATED)
    val message = _message.receiveAsFlow()

    fun message(resId:Int){
        viewModelScope.launch {
            _message.trySend(resId)
        }
    }

    private val plantItems = combine(plants,learnDataSource) { plant, source ->
        plant.filter {
            when(source){
                CourseType.TREE -> it.type==PlantType.TREE && !it.completed
                CourseType.FLOWER -> it.type==PlantType.FLOWER && !it.completed
                CourseType.WRONG_QUESTION_BOOK -> it.isFavorite
                CourseType.REVIEW -> it.lastCompetedTime.intervalDay() > 1
                else -> it.completed
            }
        }.map {
            ListItem(data = it)
        }
    }

    private val playDataSource = MutableStateFlow(CourseCard.EMPTY)
    fun playGroup(card: CourseCard){
        viewModelScope.launch {
            playDataSource.emit(card)
            _playControllerProcess.first()
        }
    }

    private val _playControllerProcess = combine(plants,playDataSource) { plant, source ->
        plant.filter {
            when(source.type){
                CourseType.TREE -> it.type==PlantType.TREE && !it.completed
                CourseType.FLOWER -> it.type==PlantType.FLOWER && !it.completed
                CourseType.WRONG_QUESTION_BOOK -> it.isFavorite
                CourseType.REVIEW -> it.lastCompetedTime.intervalDay() > 1
                else -> it.completed
            }
        }.map {
            ListItem(data = it)
        }.let {
            PlayController(source,it)
        }.also { _playController.trySend(it) }
    }

    private val _playController = Channel<PlayController>(capacity = Channel.CONFLATED)
    val playController = _playController.receiveAsFlow()

    private val plantItemsCompleted = MutableStateFlow(emptyList<ListItem>())

    val displayLearnCard = combine(plantItems,plantItemsCompleted){ plant,completed ->
        plant.filter {
            it !in completed
        }.reversed()
    }

    val learnStatus = displayLearnCard.transform { lc ->
        emit(lc.isNotEmpty())
    }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5_000),false)

    fun toggleFavorite(item: ListItem){
        val target = item.data
        viewModelScope.launch {
            Plant(
                id = target.id,
                chinese = target.chinese,
                latin = target.latin,
                family = target.family,
                category = target.category,
                definition = target.definition,
                resId = target.resId,
                type = target.type,
                completed = target.completed,
                isFavorite = !target.isFavorite,
                lastCompetedTime = target.lastCompetedTime
            ).also {
                useCase.updatePlant(it)
            }
        }
    }

    fun remember(item: ListItem){
        val target = item.data
        val isWrong = learnDataSource.value==CourseType.WRONG_QUESTION_BOOK
        viewModelScope.launch(Dispatchers.IO) {
            Plant(
                id = target.id,
                chinese = target.chinese,
                latin = target.latin,
                family = target.family,
                category = target.category,
                definition = target.definition,
                resId = target.resId,
                type = target.type,
                completed = true,
                isFavorite = target.isFavorite,
                lastCompetedTime = System.currentTimeMillis()
            ).also {
                useCase.updatePlant(it)
                if(isWrong) {
                    skip(ListItem(data = it))
                }
            }
        }
    }

    fun notRemember(item: ListItem){
        val target = item.data
        val isWrong = learnDataSource.value==CourseType.WRONG_QUESTION_BOOK
        val isReview = learnDataSource.value==CourseType.REVIEW
        viewModelScope.launch(Dispatchers.IO) {
            Plant(
                id = target.id,
                chinese = target.chinese,
                latin = target.latin,
                family = target.family,
                category = target.category,
                definition = target.definition,
                resId = target.resId,
                type = target.type,
                completed = if(isReview) target.completed else false,
                isFavorite = if(isWrong) target.isFavorite else true,
                lastCompetedTime = target.lastCompetedTime
            ).also {
                useCase.updatePlant(it)
                skip(ListItem(data = it))
            }
        }
    }

    fun skip(listItem: ListItem){
        viewModelScope.launch {
            val target = plantItemsCompleted.first().toMutableList()
            target.add(listItem)
            plantItemsCompleted.emit(target)
        }
    }

    val keyword = MutableStateFlow("")
    val searchItems = combine(plants,keyword,sortBy) { plant, key,sort ->
        plant.filter {
            it.chinese.trim().contains(key) ||
            it.latin.trim().contains(key) ||
            it.definition.trim().contains(key) ||
            it.family.trim().contains(key) ||
            it.category.trim().contains(key)
        }.filter {
            it.type == PlantType.TREE || it.type == PlantType.FLOWER
        }.map {
            ListItem(data = it, sortBy = sort)
        }
    }

    fun clearText(){
        viewModelScope.launch {
            keyword.emit("")
        }
    }

    val courseItems = plants.transform { pi ->
        val tree = pi.filter { it.type==PlantType.TREE }
        val flower = pi.filter { it.type==PlantType.FLOWER }
        val favorite = pi.filter { it.isFavorite }
        val review = pi.filter { it.lastCompetedTime.intervalDay() > 1 }
        val completed = pi.filter { it.completed }
        ArrayList<CourseCard>().apply {
            CourseCard(
                R.drawable.ic_tree,
                R.string.learn_tree_title,
                max = tree.size,
                progress = tree.filter { it.completed }.size,
                contentDescription = R.string.learn_tree_title,
                type = CourseType.TREE
            ).also { add(it) }
            CourseCard(
                R.drawable.ic_flower,
                R.string.learn_flower_title,
                max = flower.size,
                progress = flower.filter { it.completed }.size,
                contentDescription = R.string.learn_flower_title,
                type = CourseType.FLOWER
            ).also { add(it) }
            CourseCard(
                icon = R.drawable.ic_learn,
                title = R.string.learn_wrong_question_book,
                max = favorite.size,
                progress = favorite.filter { it.completed }.size,
                contentDescription = R.string.learn_wrong_question_book,
                type = CourseType.WRONG_QUESTION_BOOK
            ).also { add(it) }
            CourseCard(
                icon = R.drawable.ic_refresh,
                title = R.string.learn_review,
                max = review.size,
                progress = review.filter { it.completed }.size,
                contentDescription = R.string.learn_review,
                type = CourseType.REVIEW
            ).also { add(it) }
            CourseCard(
                icon = R.drawable.ic_check,
                title = R.string.learn_completed,
                max = plants.first().size,
                progress = completed.size,
                contentDescription = R.string.learn_completed,
                type = CourseType.COMPLETED
            ).also { add(it) }
            emit(this)
        }
    }

}