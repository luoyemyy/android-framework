package com.github.luoyemyy.framework.bus

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

class BusMsg(var group: Int = 0,
             var code: Long = 0,
             var mInt: Int = 0,
             var mLong: Long = 0,
             var mBoolean: Boolean = false,
             var mString: String? = null,
             var mBundle: Bundle? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readLong(),
            source.readInt(),
            source.readLong(),
            1 == source.readInt(),
            source.readString(),
            source.readParcelable<Bundle>(Bundle::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(group)
        writeLong(code)
        writeInt(mInt)
        writeLong(mLong)
        writeInt((if (mBoolean) 1 else 0))
        writeString(mString)
        writeParcelable(mBundle, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BusMsg> = object : Parcelable.Creator<BusMsg> {
            override fun createFromParcel(source: Parcel): BusMsg = BusMsg(source)
            override fun newArray(size: Int): Array<BusMsg?> = arrayOfNulls(size)
        }
    }
}
