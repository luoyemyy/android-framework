//package com.github.luoyemyy.audio
//
//import android.app.Service
//import android.content.Intent
//import android.media.MediaPlayer
//import android.os.Handler
//import android.os.IBinder
//import android.util.Log
//import com.github.luoyemyy.audio.AudioMsg.Companion.EMPTY_MSG
//import com.github.luoyemyy.audio.AudioMsg.Companion.LEFT_SECOND
//import com.github.luoyemyy.audio.AudioMsg.Companion.PATH
//import com.github.luoyemyy.audio.AudioMsg.Companion.PLAY
//import com.github.luoyemyy.audio.AudioMsg.Companion.PLAYING
//import com.github.luoyemyy.audio.AudioMsg.Companion.PREPARE
//import com.github.luoyemyy.audio.AudioMsg.Companion.RAW
//import com.github.luoyemyy.audio.AudioMsg.Companion.STOP
//import com.github.luoyemyy.audio.AudioMsg.Companion.URI
//import java.util.*
//
//class AudioService : Service(), MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
//
//    private lateinit var mMediaPlayer: MediaPlayer
//    private lateinit var mHandler: Handler
//    private lateinit var mDestroyRunnable: Runnable
//    private lateinit var mLeftTimeRunnable: Runnable
//    private lateinit var mMsgQueue: LinkedList<AudioMsg>
//    private var mLeftSecond: Int = 0
//    private var mAutoClose: Boolean = false
//    private var mCurrMsg: AudioMsg = EMPTY_MSG
//    private var mNextMsg: AudioMsg = EMPTY_MSG
//
//    override fun onBind(intent: Intent): IBinder? = null
//
//    override fun onCreate() {
//        super.onCreate()
//
//        mMediaPlayer = MediaPlayer()
//        mMediaPlayer.setOnErrorListener(this)
//        mMediaPlayer.setOnPreparedListener(this)
//        mMediaPlayer.setOnCompletionListener(this)
//
//        mMsgQueue = LinkedList()
//
//        mHandler = Handler()
//        mDestroyRunnable = Runnable { stopSelf() }
//        mLeftTimeRunnable = Runnable {
//            timerBus(mCurrMsg)
//            if (mLeftSecond > 0) {
//                mHandler.postDelayed(mLeftTimeRunnable, 1000)
//                mLeftSecond--
//            }
//        }
//    }
//
//    override fun onCompletion(mp: MediaPlayer) {
//        if (mCurrMsg.isValid()) {
//            var repeat = mCurrMsg.repeat
//            if (repeat > 0) {
//                repeat--
//                mCurrMsg.repeat = repeat
//                mCurrMsg.repeatState = true
//                mCurrMsg.state = AudioMsg.INIT
//                tryPlay()
//            } else {
//                stop(0)
//            }
//        }
//    }
//
//    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
//        if (mCurrMsg.isValid()) {
//            stop(1)
//        }
//        return true//已经处理，不在调用 onCompletion
//    }
//
//    override fun onPrepared(mp: MediaPlayer) {
//        if (mCurrMsg.isValid() && mCurrMsg.state == AudioMsg.PREPARE) {
//            mLeftSecond = mMediaPlayer.duration
//            if (mLeftSecond != -1 && !mMediaPlayer.isLooping) {
//                mLeftSecond /= 1000
//                mHandler.post(mLeftTimeRunnable)
//            }
//
//            mMediaPlayer.start()
//            mCurrMsg.state = AudioMsg.PLAYING
//            if (!mCurrMsg.repeatState) {
//                playBus(mCurrMsg)
//            }
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        if (intent != null) {
//            val msg = intent.getParcelableExtra<AudioMsg>(AudioMsg.TAG)
//            if (msg != null) {
//                if (msg.op == AudioMsg.PLAY) {
//                    if (!mCurrMsg.isValid()) {
//                        mCurrMsg = msg
//                        tryPlay()
//                    } else {
//                        mNextMsg = msg
//                        stop(if (msg.id == mCurrMsg.id) 3 else 4)
//                    }
//                } else if (msg.op == AudioMsg.STOP) {
//                    if (mCurrMsg.isValid()) {
//                        stop(2)
//                    }
//                } else if (msg.op == AudioMsg.DESTROY) {
//                    if (mCurrMsg.isValid()) {
//                        stop(2)
//                    }
//                    stopSelf()
//                }
//            } else {
//                //一般不会执行到这里
//                setAutoClose()
//            }
//        } else {
//            //服务重建时会执行到这里
//            setAutoClose()
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    /**
//     * @param stop 0 播放结束； 1 播放错误； 2 直接暂停； 3 播放相同（停止） 4 播放其他
//     */
//    private fun stop(stop: Int) {
//        //取消播放计时
//        mHandler.removeCallbacks(mLeftTimeRunnable)
//
//        if (stop == 0 || (mCurrMsg.state == PLAYING && (stop == 1 || stop == 2))) {
//            mMediaPlayer.stop()
//            stopBus(mCurrMsg)
//        } else if (stop == 3) {
//            if (mCurrMsg.state == PLAYING) {
//                mMediaPlayer.stop()
//                stopBus(mCurrMsg)
//            }
//            mNextMsg = EMPTY_MSG
//        } else if (stop == 4) {
//            if (mCurrMsg.state == PLAYING) {
//                mMediaPlayer.stop()
//                stopBus(mCurrMsg)
//            }
//            mCurrMsg = mNextMsg
//            mNextMsg = EMPTY_MSG
//            tryPlay()
//            return
//        }
//        mCurrMsg = EMPTY_MSG
//        setAutoClose()
//    }
//
//    private fun setAutoClose() {
//        if (!mAutoClose) {
//            mAutoClose = mHandler.postDelayed(mDestroyRunnable, 60000)
//            if (mAutoClose) {
//                Log.i("AudioService", "60秒后自动销毁服务")
//            }
//        }
//    }
//
//    private fun clearAutoClose() {
//        if (mAutoClose) {
//            mHandler.removeCallbacks(mDestroyRunnable)
//            mAutoClose = false
//            Log.i("AudioService", "取消自动销毁服务")
//        }
//    }
//
//    private fun tryPlay() {
//        if (play()) {
//            clearAutoClose()
//        } else {
//            setAutoClose()
//        }
//    }
//
//    private fun play(): Boolean {
//        try {
//            mMediaPlayer.reset()
//            //判断资源类型
//            when {
//                mCurrMsg.sourceType == PATH -> mMediaPlayer.setDataSource(mCurrMsg.path)
//                mCurrMsg.sourceType == URI && mCurrMsg.uri != null -> mMediaPlayer.setDataSource(this, mCurrMsg.uri!!)
//                mCurrMsg.sourceType == RAW -> {
//                    val afd = resources.openRawResourceFd(mCurrMsg.rawId)
//                    mMediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
//                    //mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + msg.getRawId()));
//                }
//            }
//            if (mCurrMsg.repeat < 0) {
//                mMediaPlayer.isLooping = true
//            }
//            mCurrMsg.state = PREPARE
//            mMediaPlayer.prepareAsync()
//            return true
//        } catch (e: Exception) {
//            Log.e("AudioService", "play", e)
//            return false
//        }
//
//    }
//
//    private fun playBus(msg: AudioMsg) {
//        AudioManager.bus(op = PLAY, id = msg.id)
//        Log.i("AudioService", "playBus:" + msg.id)
//    }
//
//    private fun stopBus(msg: AudioMsg) {
//        AudioManager.bus(op = STOP, id = msg.id)
//        Log.i("AudioService", "stopBus:" + msg.id)
//    }
//
//    private fun timerBus(msg: AudioMsg) {
//        AudioManager.bus(op = LEFT_SECOND, id = msg.id, leftSecond = mLeftSecond)
//        Log.i("AudioService", "timerBus:" + msg.id + "，leftSecond:" + mLeftSecond)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mMediaPlayer.release()
//        mMsgQueue.clear()
//        Log.i("AudioService", "音频服务已销毁")
//    }
//}
