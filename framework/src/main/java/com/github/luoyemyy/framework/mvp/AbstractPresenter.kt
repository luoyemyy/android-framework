package com.github.luoyemyy.framework.mvp

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Bundle

/**
 *
 *  example:
 *
 *  class MainActivity : AppCompatActivity() {
 *
 *      private lateinit var mBinding: ActivityMainBinding
 *
 *      override fun onCreate(savedInstanceState: Bundle?) {
 *          super.onCreate(savedInstanceState)
 *          mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
 *          mBinding.also {
 *              it.setLifecycleOwner(this)
 *              it.presenter = getPresenter(this)
 *          }
 *
 *          mBinding.presenter?.load()
 *      }
 *
 *      class Presenter(app: Application) : AbstractPresenter(app) {
 *
 *          val liveData = MutableLiveData<String>()
 *
 *          override fun load(bundle: Bundle?) {
 *              liveData.value = "123"
 *          }
 *      }
 *  }
 *
 */
abstract class AbstractPresenter(app: Application) : AndroidViewModel(app) {
    abstract fun load(bundle: Bundle? = null)
}