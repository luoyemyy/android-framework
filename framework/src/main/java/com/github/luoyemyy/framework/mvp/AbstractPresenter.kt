package com.github.luoyemyy.framework.mvp

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
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
 *      class Presenter(app: Application) : AbstractPresenter<String>(app) {
 *
 *          override fun load(bundle: Bundle?) {
 *              data.value = "123"
 *          }
 *      }
 *  }
 *
 */
abstract class AbstractPresenter<T>(app: Application) : AndroidViewModel(app) {

    val data = MutableLiveData<T>()

    abstract fun load(bundle: Bundle? = null)

}