package me.qingshu.latin

import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.exoplayer.ExoPlayer
import me.qingshu.latin.data.ListItem
import me.qingshu.latin.data.PlantType
import java.io.File

class Sound{

    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private lateinit var exoPlayer:ExoPlayer

    private var created = false

    private fun createName(id:String,type:PlantType):String{
        return when(type){
            PlantType.TREE -> {
                val result = StringBuilder()
                repeat(4-id.length){
                    result.append("0")
                }
                "a${result}$id.mp3"
            }
            PlantType.FLOWER -> {
                val pre = when{
                    id.length < 2 -> "00$id"
                    else -> "0$id"
                }
                "$pre.m4a"
            }
            else -> throw IllegalArgumentException("非法参数")
        }
    }

    fun pause(){
        if(exoPlayer.isPlaying) exoPlayer.pause()
        else exoPlayer.play()
    }

    fun setPlaybackSpeed(speed:Float){
        exoPlayer.setPlaybackSpeed(speed)
    }

    fun setRepeatMode(mode:Int){
        exoPlayer.repeatMode = mode
    }

    fun playGroup(list:List<ListItem>){
        list.map {
            fileName(it.data.resId,it.data.type).mediaItem()
        }.also {
            exoPlayer.setMediaItems(it, 0, 0L)
            exoPlayer.playWhenReady = true
            exoPlayer.prepare()
        }
    }

    private fun fileName(id: String,type: PlantType) = when(type){
        PlantType.TREE -> "voice${File.separator}vol1${File.separator}${createName(id,type)}"
        PlantType.FLOWER -> "voice${File.separator}vol2${File.separator}${createName(id,type)}"
        else -> throw IllegalArgumentException("非法参数")
    }

    private fun String.mediaItem() = MediaItem.fromUri(Uri.parse("asset:///$this"))

    fun play(id: String,type: PlantType){
        val fileName = fileName(id,type)
        val mediaItem = fileName.mediaItem()
        exoPlayer.setMediaItems(listOf(mediaItem), 0, 0L)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    fun create(
        playStatus:((Int)->Unit)?=null,
        playPosition:((Int)->Unit)?=null
    ):Sound{
        exoPlayer = ExoPlayer.Builder(application).build()
        exoPlayer.repeatMode = REPEAT_MODE_OFF
        exoPlayer.setAudioAttributes(audioAttributes, true)
        exoPlayer.addListener(object :Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                playStatus?.invoke(playbackState)
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                playPosition?.invoke(newPosition.mediaItemIndex)
            }
        })
        created = true
        return this
    }

    fun release(){
        exoPlayer.release()
        created = false
    }
}