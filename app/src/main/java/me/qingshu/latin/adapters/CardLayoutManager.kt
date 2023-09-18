package me.qingshu.latin.adapters

import androidx.recyclerview.widget.RecyclerView

class CardLayoutManager: RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        detachAndScrapAttachedViews(recycler)
        val count = itemCount
        val initIndex = (count - CardConfig.SHOW_MAX_COUNT).let {
            if (it < 0) 0 else it
        }
        for (i in initIndex until count) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChild(view, 0, 0)
            val realWidth = getDecoratedMeasuredWidth(view)
            val realHeight = getDecoratedMeasuredHeight(view)
            val widthPadding = ((width - realWidth) / 2f).toInt()
            val heightPadding = ((height - realHeight) / 2f).toInt()
            layoutDecorated(
                view, widthPadding, heightPadding,
                widthPadding + realWidth, heightPadding + realHeight
            )
            val level = (count - i - 1).let {
                if (it == CardConfig.SHOW_MAX_COUNT - 1) it-1
                else it
            }
            view.translationY = level * CardConfig.TRANSLATION_Y
            view.scaleX = 1 - level * CardConfig.SCALE
            view.scaleY = 1 - level * CardConfig.SCALE
        }
    }
}