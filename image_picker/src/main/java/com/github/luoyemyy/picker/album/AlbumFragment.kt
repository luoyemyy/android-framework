package com.github.luoyemyy.picker.album

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.picker.R
import com.github.luoyemyy.picker.databinding.ImagePickerAlbumBinding

class AlbumFragment : Fragment() {

    private lateinit var mBinding: ImagePickerAlbumBinding
    private lateinit var mAlbumPresenter: AlbumPresenter
    private lateinit var mBucketPresenter: BucketPresenter
    private lateinit var mBucketDialog: BottomSheetDialog
    private lateinit var mAdapter: AlbumAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ImagePickerAlbumBinding.inflate(inflater, container, false).apply {
            mBinding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAlbumPresenter = requireActivity().getPresenter()
        mBucketPresenter = requireActivity().getPresenter()
        mAdapter = AlbumAdapter(this, mAlbumPresenter, mBinding.recyclerView)
        mAlbumPresenter.setup(this, mAdapter)
        mAlbumPresenter.attachBucket(mBucketPresenter)
        mBucketPresenter.attachAlbum(mAlbumPresenter)

        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                activity?.finish()
            }
            inflateMenu(R.menu.image_picker_albun_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.sure -> mAlbumPresenter.clickSure()
                }
                true
            }
        }

        mBucketPresenter.liveDataCloseBucket.observe(this, Observer {
            mBucketDialog.dismiss()
        })

        //init view
        mBinding.recyclerView.apply {
            setGridManager(mAlbumPresenter.getSpan())//layoutManager = StaggeredGridLayoutManager(mAlbumPresenter.getSpan(), StaggeredGridLayoutManager.VERTICAL)
            addItemDecoration(AlbumDecoration(mAlbumPresenter.getSpan(), mAlbumPresenter.getSpace()))
        }
        mBinding.fab.setOnClickListener {
            openBucketDialog()
        }

        val recyclerView = createDialogView()
        mBucketPresenter.setup(this, BucketAdapter(mBucketPresenter, recyclerView))
        mBucketDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(recyclerView)
            setOnDismissListener {
                mBinding.fab.show()
            }
        }

        mAlbumPresenter.loadInit(requireActivity().intent.extras)
    }

    private fun createDialogView(): RecyclerView {
        val recyclerView = RecyclerView(requireContext())
        recyclerView.setLinearManager()
        recyclerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return recyclerView
    }

    private fun openBucketDialog() {
        mBinding.fab.hide()
        mBucketDialog.show()
    }
}