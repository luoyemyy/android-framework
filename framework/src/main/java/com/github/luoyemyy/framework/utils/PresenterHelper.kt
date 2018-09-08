package com.github.luoyemyy.framework.utils

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

inline fun <reified T : AndroidViewModel> getPresenter(fragment: Fragment): T = ViewModelProviders.of(fragment).get(T::class.java)

inline fun <reified T : AndroidViewModel> getPresenter(activity: FragmentActivity): T = ViewModelProviders.of(activity).get(T::class.java)
