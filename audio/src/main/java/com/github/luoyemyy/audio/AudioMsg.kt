package com.github.luoyemyy.audio

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntDef
import android.support.annotation.IntRange
import android.support.annotation.RawRes

internal class AudioMsg() : Parcelable {

    var op: Int = 0             //操作
    var id: String? = null      //消息id
    var sourceType: Int = 0     //1 path 2 uri 3 rawId
    var path: String? = null    //资源文件路径，文件绝对地址，或网络路径
    var uri: Uri? = null        //资源文件uri
    var rawId: Int = 0          //资源文件id
    var repeat: Int = 0         //-1 无限重复播放； 0 不重播； n>0 重播n次
    var state = INIT   //
    var repeatState: Boolean = false

    constructor(parcel: Parcel) : this() {
        op = parcel.readInt()
        id = parcel.readString()
        sourceType = parcel.readInt()
        path = parcel.readString()
        uri = parcel.readParcelable(Uri::class.java.classLoader)
        rawId = parcel.readInt()
        repeat = parcel.readInt()
        state = parcel.readInt()
        repeatState = parcel.readByte() != 0.toByte()
    }

    constructor(@Op op: Int,
                id: String? = null,
                @SourceType sourceType: Int = NONE,
                path: String? = null,
                uri: Uri? = null,
                @RawRes rawId: Int = 0,
                @Repeat repeat: Int = 0) : this() {
        this.op = op
        this.id = id
        this.sourceType = sourceType
        this.path = path
        this.uri = uri
        this.rawId = rawId
        this.repeat = repeat
    }

    constructor(state: Int) : this() {
        this.state = state
    }

    @IntDef(DESTROY, PLAY, STOP, LEFT_SECOND)
    internal annotation class Op

    @IntDef(PATH, URI, RAW, NONE)
    internal annotation class SourceType

    @IntRange(from = -1)
    internal annotation class Repeat

    fun isValid(): Boolean = state != EMPTY

    companion object {

        val TAG = "Msg"

        /**
         * 操作类型
         */
        const val DESTROY = 0
        const val PLAY = 1
        const val STOP = 2
        const val LEFT_SECOND = 3

        /**
         * 资源类型
         */
        const val NONE = 0
        const val PATH = 1
        const val URI = 2
        const val RAW = 3

        /**
         * 消息状态
         */

        const val EMPTY = 0
        const val INIT = 1
        const val PREPARE = 2
        const val PLAYING = 3
        const val END = 4

        @JvmField
        val CREATOR: Parcelable.Creator<AudioMsg> = object : Parcelable.Creator<AudioMsg> {
            override fun createFromParcel(parcel: Parcel): AudioMsg = AudioMsg(parcel)
            override fun newArray(size: Int): Array<AudioMsg?> = arrayOfNulls(size)
        }

        val EMPTY_MSG = AudioMsg(EMPTY)

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(op)
        parcel.writeString(id)
        parcel.writeInt(sourceType)
        parcel.writeString(path)
        parcel.writeParcelable(uri, flags)
        parcel.writeInt(rawId)
        parcel.writeInt(repeat)
        parcel.writeInt(state)
        parcel.writeByte(if (repeatState) 1 else 0)
    }

    override fun describeContents(): Int = 0

}
