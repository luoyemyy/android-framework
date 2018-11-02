package com.github.luoyemyy.picker.album

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.luoyemyy.mvp.recycler.AbstractSingleRecyclerAdapter
import com.github.luoyemyy.mvp.recycler.VH
import com.github.luoyemyy.picker.databinding.ImagePickerAlbumRecyclerBinding
import com.github.luoyemyy.picker.entity.Image

class AlbumAdapter(private var albumFragment: AlbumFragment, private val mPresenter: AlbumPresenter, private val recyclerView: RecyclerView) : AbstractSingleRecyclerAdapter<Image, ImagePickerAlbumRecyclerBinding>(recyclerView) {

    override fun enableLoadMore(): Boolean {
        return false
    }

    override fun enableEmpty(): Boolean {
        return false
    }

    override fun createContentView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ImagePickerAlbumRecyclerBinding {
        return ImagePickerAlbumRecyclerBinding.inflate(inflater, parent, false).apply {
            root.layoutParams.width = mPresenter.getSize()
            root.layoutParams.height = mPresenter.getSize()
        }
    }

    override fun bindContentViewHolder(binding: ImagePickerAlbumRecyclerBinding, content: Image, position: Int) {
        binding.image = content
        binding.executePendingBindings()
    }

    override fun onItemClickListener(vh: VH<ImagePickerAlbumRecyclerBinding>, view: View?) {
        if (view == null) return
        val image = getItem(vh.adapterPosition) ?: return
        val imageView = vh.binding?.imageView ?: return
        ViewCompat.setTransitionName(imageView, "${vh.adapterPosition}_share")
        val shareName = ViewCompat.getTransitionName(imageView) ?: return
        val images = mPresenter.getDataSet().dataList()
        val position = images.indexOf(image)

//        val previewFragment = PreviewFragment().apply {
//            arguments = Bundle().apply {
//                putInt("position", position)
//                putString("images", images.toJsonString())
//                putString("shareName", shareName)
//                putString("image", image.path)
//            }
//        }
//        albumFragment.requireFragmentManager().beginTransaction()
//                .replace(R.id.container, previewFragment)
////                .hide(albumFragment)
//                .addSharedElement(imageView, shareName)
//                .addToBackStack(null)
//                .commit()
    }

    override fun afterLoadRefresh(list: List<Image>?) {
        if (list != null && list.isNotEmpty()) {
            recyclerView.scrollToPosition(0)
        }
    }
}