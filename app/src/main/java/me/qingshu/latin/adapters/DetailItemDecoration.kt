package me.qingshu.latin.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.qingshu.latin.extensions.dp

class DetailItemDecoration(
    private val space:Int
): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space.dp
        outRect.right = space.dp
        outRect.bottom = space.dp
        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = space.dp
    }

}