package me.qingshu.latin

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.fragment.app.FragmentManager
import me.qingshu.latin.data.CourseCard
import me.qingshu.latin.data.CourseType
import me.qingshu.latin.database.Gold
import me.qingshu.latin.views.MarqueeToolbar
import me.qingshu.latin.views.PlayPauseDrawable

abstract class ToolbarBinding<V:Any>(
    open val toolbar: Toolbar
){
    abstract fun load(data:V,childFragmentManager: FragmentManager):Any
}

class SentenceToolbar(
    override val toolbar: MarqueeToolbar,
    private val callback:(MenuItem)->Unit,
    private val titleClick:(Gold)->Unit
):ToolbarBinding<Gold>(toolbar){

    override fun load(data: Gold,childFragmentManager: FragmentManager) = toolbar.apply {
        setNavigationIcon(R.drawable.ic_account)
        setNavigationOnClickListener {
            DevelopScreen().show(childFragmentManager,javaClass.simpleName)
        }
        menu.forEach {
            it.isVisible = it.itemId != R.id.action_close
        }
        title = data.sentence
        subtitle = data.subtitle
        setOnMenuItemClickListener {
            callback.invoke(it)
            true
        }
        subtitleClick{ titleClick.invoke(data) }
        titleClick{ titleClick.invoke(data) }
    }

}

class PlayBarToolbar(
    override val toolbar: Toolbar,
    private val closeClick:()->Unit,
    private val pauseClick:(Boolean)->Unit
):ToolbarBinding<CourseCard>(toolbar){

    private var isPlaying = false
    private val playPause = PlayPauseDrawable(toolbar.context)

    override fun load(data:CourseCard,childFragmentManager: FragmentManager) = toolbar.apply {
        if(data.type==CourseType.UN_DEFINE) return this
        isPlaying = true
        menu.forEach {
            it.isVisible = it.itemId == R.id.action_close
        }
        navigationIcon = playPause
        setNavigationOnClickListener {
            if(isPlaying) playPause.setPlay(true)
            else playPause.setPause(true)
            isPlaying = !isPlaying
            pauseClick.invoke(isPlaying)
        }
        playPause.setPause(true)
        setTitle(data.title)
        subtitle = data.label()
        setOnMenuItemClickListener {
            closeClick.invoke()
            true
        }
    }

}
