package io.cere.cere_sdk

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

enum class InitStatus {
    Uninitialised, Initialising, Initialised
}

class CereModule {

    private class MyWebViewClient : WebViewClient() {
        private val TAG = this::class.java.simpleName
        override fun onPageFinished(view: WebView?, url: String?) {
            Log.i(TAG, "page finished")
        }
    }

    companion object Factory {
        @Volatile
        private var instance: CereModule? = null
        @JvmStatic fun init(context: Context, appId: String, externalUserId: String): CereModule {
            val module = CereModule()
            module.initialise(context, appId, externalUserId)
            instance = module
            return module
        }
        @JvmStatic fun getInstance(): CereModule? {
            return instance
        }
    }

    private val TAG = "CereModule"
    private var context: Context? = null

    var webview: WebView? = null
    var appId: String? = null
    var integrationPartnerUserId: String? = null
    private var initStatus: InitStatus = InitStatus.Uninitialised
    fun getInitStatus(): InitStatus {
        return this.initStatus
    }
    private val baseUrl: String = "https://5448d01cf48d.ngrok.io/native.html"


    private fun initialise(context: Context, appId: String, integrationPartnerUserId: String) {
        this.context = context
        configureWebView()
        this.appId = appId
        this.integrationPartnerUserId = integrationPartnerUserId
        val url = "${baseUrl}?appId=${appId}&integrationPartnerUserId=${integrationPartnerUserId}&platform=android&version=v1.0.0"
        Log.i(TAG, "load url ${url}")
        this.initStatus = InitStatus.Initialising
        this.webview?.loadUrl(url)
    }

    private fun configureWebView() {
        this.webview = WebView(this.context)
        this.webview?.settings?.javaScriptEnabled = true
        this.webview?.settings?.domStorageEnabled = true
        this.webview?.settings?.databaseEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)

        this.webview?.webViewClient = MyWebViewClient()

        val context = this.context
        if (context != null) {
            this.webview?.addJavascriptInterface(this, "Android")
        }
    }

    fun sendEvent(eventType: String, payload: String) {
        val script = "(async function() { console.log('send event dialog'); return cereSDK.sendEvent('${eventType}', ${payload}).then(() => {console.log(`event ${eventType} sent`);}).catch(err => {console.log(`${eventType} sending error` + err);});})();"
        webview?.evaluateJavascript(script)
        { _ ->
            Log.i(TAG, "send event $eventType executed")
        }
    }

    @JavascriptInterface
    fun engagementReceived() {
        Log.i(TAG, "engagement received on android")
        val intent = Intent(context, WebviewActivity::class.java)
        context?.startActivity(intent)
    }

    @JavascriptInterface
    fun sdkInitialized() {
        Log.i(TAG, "sdk initialised")
        this.initStatus = InitStatus.Initialised
    }
}