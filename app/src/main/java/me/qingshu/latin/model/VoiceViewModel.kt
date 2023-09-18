package me.qingshu.latin.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.qingshu.latin.data.AutoPlayVoice
import me.qingshu.latin.data.CourseCard
import me.qingshu.latin.data.ListItem
import me.qingshu.latin.data.PlayController

class VoiceViewModel:ViewModel() {

    private val playController = MutableStateFlow(PlayController(CourseCard.EMPTY, emptyList()))
    fun playDataSource(play: PlayController){
        viewModelScope.launch {
            playController.emit(play)
        }
    }
    private val voicePosition = MutableStateFlow(0)

    fun playPosition(position:Int){
        viewModelScope.launch {
            voicePosition.emit(position)
        }
    }

    val playDisplay = combine(playController,voicePosition){ pc, position ->
        CourseCard(
            icon = pc.card.icon,
            title = pc.card.title,
            max = pc.resource.size,
            progress = position+1,
            type = pc.card.type,
            contentDescription = pc.card.contentDescription
        )
    }

    fun pause(play:Boolean){
        _pause.trySend(play)
    }
    private val _pause = Channel<Boolean>(capacity = Channel.CONFLATED)
    val pause = _pause.receiveAsFlow()

    private val autoPlayVoice = MutableStateFlow<ListItem?>(null)
    private val isAutoPlay = MutableStateFlow(false)

    val tryAutoPlay = combine(autoPlayVoice,isAutoPlay){ voice,auto ->
        autoPlayVoice.emit(null)
        AutoPlayVoice(auto,voice)
    }

    fun isAutoPlay(auto:Boolean){
        viewModelScope.launch {
            isAutoPlay.emit(auto)
        }
    }
    fun autoPlay(listItem: ListItem){
        viewModelScope.launch {
            autoPlayVoice.emit(listItem)
        }
    }

    private val _play = Channel<ListItem>(capacity = Channel.CONFLATED)
    val play = _play.receiveAsFlow()
    fun play(listItem: ListItem){
        viewModelScope.launch {
            _play.trySend(listItem)
        }
    }
}