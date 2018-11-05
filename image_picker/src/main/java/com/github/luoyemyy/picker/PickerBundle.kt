package com.github.luoyemyy.picker

import android.os.Parcel
import android.os.Parcelable

class PickerBundle() : Parcelable {

    var maxSelect: Int = 1
    var pickerType: Int = 1 // 1 camera and album(default); 2 camera; 3 album
    var fileProvider: String? = null
    var crop: Boolean = false
    var aspectX: Int = 1
    var aspectY: Int = 1
    var portrait:Boolean = true

    constructor(parcel: Parcel) : this() {
        maxSelect = parcel.readInt()
        pickerType = parcel.readInt()
        fileProvider = parcel.readString()
        crop = parcel.readByte() != 0.toByte()
        aspectX = parcel.readInt()
        aspectY = parcel.readInt()
        portrait = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(maxSelect)
        parcel.writeInt(pickerType)
        parcel.writeString(fileProvider)
        parcel.writeByte(if (crop) 1 else 0)
        parcel.writeInt(aspectX)
        parcel.writeInt(aspectY)
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