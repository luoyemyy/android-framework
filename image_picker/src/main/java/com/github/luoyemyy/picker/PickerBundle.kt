package com.github.luoyemyy.picker

class PickerBundle {

    var pickerType: Int = 1 // 1 camera and album(default); 2 camera; 3 album
    var maxSelect: Int = 1
    var fileProvider: String? = null
    var portrait: Boolean = true    //true 锁定垂直布局 false 锁定横向布局
    var cropOption: CropOption = CropOption.create()
}