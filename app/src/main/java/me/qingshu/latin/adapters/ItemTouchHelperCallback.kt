package me.qingshu.latin.adapters

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.data.ListItem
import kotlin.math.hypot

class ItemTouchHelperCallback(
    private val skipCall:(ListItem)->Unit
): ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        (viewHolder as PlantCardAdapter.PlantCardViewHolder).data?.also {
            skipCall.invoke(it)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val distance = hypot(dX.toDouble(), dY.toDouble()).toFloat()
        val maxDistance = recyclerView.width / 2f

        val fraction = (distance / maxDistance).let {
            if (it > 1) 1f
            else it
        }
        //为每个child执行动画
        val count = recyclerView.childCount

        for (i in 0 until count) {
            //获取的view从下层到上层
            val view = recyclerView.getChildAt(i)
            val level = CardConfig.SHOW_MAX_COUNT - i - 1
            //level范围（CardConfig.SHOW_MAX_COUNT-1）-0，每个child最大只移动一个CardConfig.TRANSLATION_Y和放大CardConfig.SCALE
            if (level == CardConfig.SHOW_MAX_COUNT - 1) { // 最下层的不动和最后第二层重叠
                view.translationY = CardConfig.TRANSLATION_Y * (level - 1)
                view.scaleX = 1 - CardConfig.SCALE * (level - 1)
                view.scaleY = 1 - CardConfig.SCALE * (level - 1)
            } else if (level > 0) {
                view.translationY =
                    level * CardConfig.TRANSLATION_Y - fraction * CardConfig.TRANSLATION_Y
                view.scaleX = 1 - level * CardConfig.SCALE + fraction * CardConfig.SCALE
                view.scaleY = 1 - level * CardConfig.SCALE + fraction * CardConfig.SCALE
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.3f
    }
}