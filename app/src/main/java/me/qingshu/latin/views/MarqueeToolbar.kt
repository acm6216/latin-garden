package me.qingshu.latin.views

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.R
import androidx.appcompat.widget.Toolbar

class MarqueeToolbar @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr:Int = R.attr.toolbarStyle
): Toolbar(context,attr,defStyleAttr) {

    private var subtitleClick:(()->Unit)?=null
    private var titleClick:(()->Unit)?=null

    fun subtitleClick(click:(()->Unit)?){
        subtitleClick = click
        ensureSubtitleTarget()
    }
    fun titleClick(click:(()->Unit)?){
        titleClick = click
        ensureTitleTarget()
    }

    override fun setTitle(resId: Int) {
        ensureTitleTarget()
        super.setTitle(resId)
    }

    override fun setSubtitle(resId: Int) {
        ensureSubtitleTarget()
        super.setSubtitle(resId)
    }

    override fun setTitle(title: CharSequence?) {
        ensureTitleTarget()
        super.setTitle(title)
    }

    override fun setSubtitle(title: CharSequence?) {
        ensureSubtitleTarget()
        super.setSubtitle(title)
    }

    private fun ensureTitleTarget(){
        val titleTextView = try {
            Toolbar::class.java.getDeclaredField("mTitleTextView")
                .apply { isAccessible = true }
                .get(this) as TextView?
        }catch (e:Exception){
            e.printStackTrace()
            null
        }?:return
        titleTextView.ellipsize = TextUtils.TruncateAt.MARQUEE
        titleTextView.isSelected = true
        titleTextView.setOnClickListener {
            titleClick?.invoke()
        }
    }

    private fun ensureSubtitleTarget(){
        val subtitleTextView = try {
            Toolbar::class.java.getDeclaredField("mSubtitleTextView")
                .apply { isAccessible = true }
                .get(this) as TextView?
        }catch (e:Exception){
            e.printStackTrace()
            null
        }?:return
        subtitleTextView.ellipsize = TextUtils.TruncateAt.MARQUEE
        subtitleTextView.isSelected = true
        subtitleTextView.setOnClickListener {
            subtitleClick?.invoke()
        }
    }

}