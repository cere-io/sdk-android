package io.cere.cere_sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

enum class InitStatus {
    Uninitialised, Initialising, Initialised
}

class CereModule(private val context: Context) {

    private class MyWebViewClient : WebViewClient() {
        private val TAG = this::class.java.simpleName
        override fun onPageFinished(view: WebView?, url: String?) {
            Log.i(TAG, "page finished 2")
        }
    }

    companion object Factory {
        @Volatile
        private var instance: CereModule? = null
        @JvmStatic private fun make(context: Context): CereModule {
            val module = CereModule(context).configureWebView()
            instance = module
            return module
        }
        @JvmStatic fun getInstance(application: Application): CereModule {
            val inst = this.instance
            if (inst != null) {
                return inst
            } else {
                return make(application.applicationContext)
            }
        }
    }

    private val TAG = "CereModule"

    var webview: WebView? = null
    var appId: String? = null
    var integrationPartnerUserId: String? = null
    private var initStatus: InitStatus = InitStatus.Uninitialised
    fun getInitStatus(): InitStatus {
        return this.initStatus
    }
    private val baseUrl: String = "https://5448d01cf48d.ngrok.io/native.html"


    fun init(appId: String, integrationPartnerUserId: String) {
        this.appId = appId
        this.integrationPartnerUserId = integrationPartnerUserId
        val url = "${baseUrl}?appId=${appId}&integrationPartnerUserId=${integrationPartnerUserId}&platform=android&version=v1.0.0"
        Log.i(TAG, "load url ${url}")
        this.initStatus = InitStatus.Initialising
        this.webview?.loadUrl(url)
    }

    private fun configureWebView(): CereModule {
        val webview = WebView(context)
        webview.settings?.javaScriptEnabled = true
        webview.settings?.domStorageEnabled = true
        webview.settings?.databaseEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)

        webview.webViewClient = MyWebViewClient()
        webview.addJavascriptInterface(this, "Android")
        this.webview = webview
        return this
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
        context.startActivity(intent)
    }

    @JavascriptInterface
    fun sdkInitialized() {
        Log.i(TAG, "sdk initialised")
        this.initStatus = InitStatus.Initialised
    }
}