package com.github.luoyemyy.picker.album

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.luoyemyy.framework.mvp.recycler.VH
import com.github.luoyemyy.framework.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.picker.databinding.ImagePickerBucketRecyclerBinding
import com.github.luoyemyy.picker.entity.Bucket

class BucketAdapter(private val mPresenter: BucketPresenter, recyclerView: RecyclerView) : AbstractSingleRecyclerAdapter<Bucket, ImagePickerBucketRecyclerBinding>(recyclerView) {

    override fun enableEmpty(): Boolean {
        return false
    }

    override fun enableLoadMore(): Boolean {
        return false
    }

    override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerBucketRecyclerBinding {
        return ImagePickerBucketRecyclerBinding.inflate(inflater, parent, false)
    }

    override fun bindContentViewHolder(binding: ImagePickerBucketRecyclerBinding, content: Bucket, position: Int) {
        binding.bucket = content
        binding.executePendingBindings()
    }

    override fun onItemClickListener(vh: VH<ImagePickerBucketRecyclerBinding>, view: View?) {
        val item = mPresenter.getDataSet().item(vh.adapterPosition) ?: return
        mPresenter.selectBucket(item.id)
    }
}