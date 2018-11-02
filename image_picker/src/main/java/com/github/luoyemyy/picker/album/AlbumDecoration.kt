package com.github.luoyemyy.picker.album

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class AlbumDecoration(private val span: Int, private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position < 0) {
            return
        }
        if (position >= span) {
            outRect.top = space
        }
        if (position % span > 0) {
            outRect.left = space
        }
    }
}