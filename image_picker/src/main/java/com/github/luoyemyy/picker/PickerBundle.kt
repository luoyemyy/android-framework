package com.github.luoyemyy.picker

import android.os.Parcel
import android.os.Parcelable

class PickerBundle() : Parcelable {

    var maxSelect: Int = 1
    var pickerType: Int = 1 // 1 camera and album(default); 2 camera; 3 album
    var fileProvider: String? = null
    var cropType: Int = 0           // 0 不裁剪 1 按尺寸 2 按比例
    var size: Int = 0               //dp 相对于imageView的最小边
    var percent: Float = 0f         //相对于imageView的最小边的比例
    var ratio: Float = 0f           //x:y 比例
    var portrait: Boolean = true    //true 锁定垂直布局 false 锁定横向布局

    constructor(parcel: Parcel) : this() {
        maxSelect = parcel.readInt()
        pickerType = parcel.readInt()
        fileProvider = parcel.readString()
        cropType = parcel.readInt()
        size = parcel.readInt()
        percent = parcel.readFloat()
        ratio = parcel.readFloat()
        portrait = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(maxSelect)
        parcel.writeInt(pickerType)
        parcel.writeString(fileProvider)
        parcel.writeInt(cropType)
        parcel.writeInt(size)
        parcel.writeFloat(percent)
        parcel.writeFloat(ratio)
        parcel.writeByte(if (portrait) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PickerBundle> {
        override fun createFromParcel(parcel: Parcel): PickerBundle {
            return PickerBundle(parcel)
        }

        override fun newArray(size: Int): Array<PickerBundle?> {
            return arrayOfNulls(size)
        }
    }


}