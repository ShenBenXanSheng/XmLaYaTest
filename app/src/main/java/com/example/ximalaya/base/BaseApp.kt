package com.example.ximalaya.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault
import com.ximalaya.ting.android.opensdk.datatrasfer.IDeviceInfoProvider
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager


class BaseApp : Application() {

    private val oaid: String? = null
    companion object {
        @SuppressLint("StaticFieldLeak")
        var mBaseContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        val mXimalaya: CommonRequest = CommonRequest.getInstanse()
        if (DTransferConstants.isRelease) {
            val mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af"
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8")
            mXimalaya.setPackid("com.app.test.android")
            mXimalaya.init(this, mAppSecret, true, getDeviceInfoProvider(this))
        } else {
            val mAppSecret = "ff31ae8153185db13b5f5393cae962c4"
            mXimalaya.setAppkey("be022ee6e9f19df55c4a6eb836b7b0b9")
            mXimalaya.setPackid("android.test")
            mXimalaya.init(this, mAppSecret, getDeviceInfoProvider(this))

        }


        //初始始化播放器
        XmPlayerManager.getInstance(this).init()





        mBaseContext = baseContext
    }
    fun getDeviceInfoProvider(context: Context?): IDeviceInfoProvider? {
        return object : DeviceInfoProviderDefault(context) {
            override fun oaid(): String? {
                // 合作方要尽量优先回传用户真实的oaid，使用oaid可以关联并打通喜马拉雅主app中记录的用户画像数据，对后续个性化推荐接口推荐给用户内容的准确性会有极大的提升！
                return oaid
            }
        }
    }


}