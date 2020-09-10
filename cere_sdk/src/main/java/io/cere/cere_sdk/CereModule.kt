package io.cere.cere_sdk

import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class CereModule {

    private class MyWebViewClient : WebViewClient() {
        private val TAG = this::class.java.simpleName
        override fun onPageFinished(view: WebView?, url: String?) {
            Log.e(TAG, "page finished")
        }
    }

    companion object Factory {
        @JvmStatic fun init(context: Context, appId: String, externalUserId: String): CereModule {
            val module = CereModule()
            module.init2(context, appId, externalUserId)
            return module
        }
    }

    private val TAG = "CereModule"
    private var context: Context? = null

    var webview: WebView? = null
    var appId: String? = null
    var externalUserId: String? = null

    private fun init2(context: Context, appId: String, externalUserId: String) {
        this.context = context
        configureWebView()
        this.appId = appId
        this.externalUserId = externalUserId
        val url = "https://6527c6acb350.ngrok.io/?appId=${appId}&externalUserId=${externalUserId}"
        Log.e(TAG, "load url ${url}")
        this.webview?.loadUrl(url)
    }

    private fun configureWebView() {
        this.webview = WebView(this.context)
        this.webview?.settings?.javaScriptEnabled = true
        this.webview?.settings?.domStorageEnabled = true
        this.webview?.settings?.databaseEnabled = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webview?.settings?.setDatabasePath("/data/data/" + this.webview?.context?.packageName + "/databases/")
        }
        this.webview?.webViewClient = MyWebViewClient()

        val context = this.context
        if (context != null) {
            this.webview?.addJavascriptInterface(WebAppInterface(context), "Android")
        }
    }
}