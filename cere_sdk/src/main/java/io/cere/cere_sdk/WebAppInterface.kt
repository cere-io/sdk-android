package io.cere.cere_sdk

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface

class WebAppInterface(private val mContext: Context, private val module: CereModule) {

    private val TAG = this::class.java.simpleName

    @JavascriptInterface
    fun engagementReceived() {
        Log.i(TAG, "engagement received on android")
        val intent = Intent(mContext, WebviewActivity::class.java)
        mContext.startActivity(intent)
    }

    @JavascriptInterface
    fun sdkInitialized() {
        Log.i(TAG, "sdk initialised")

    }
}