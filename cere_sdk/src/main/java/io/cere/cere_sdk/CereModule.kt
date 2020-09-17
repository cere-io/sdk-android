package io.cere.cere_sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView

import java.util.concurrent.CountDownLatch

const val baseUrl: String = "https://5448d01cf48d.ngrok.io/native.html"

/**
 * Interface used after `CereModule` init method.
 */
interface OnInitializationHandler {
    fun handle()
}

class CereModule(private val context: Context) {

    companion object {
        const val TAG = "CereModule"
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

    var onInitializationHandler: OnInitializationHandler = object: OnInitializationHandler {
        override fun handle() {

        }
    }

    lateinit var webview: WebView
    private lateinit var appId: String
    private lateinit var integrationPartnerUserId: String

    private var initStatus: InitStatus = InitStatus.Uninitialised
    fun getInitStatus(): InitStatus {
        return this.initStatus
    }

    fun init(appId: String, integrationPartnerUserId: String) {
        this.appId = appId
        this.integrationPartnerUserId = integrationPartnerUserId
        val url = "${baseUrl}?appId=${appId}&integrationPartnerUserId=${integrationPartnerUserId}&platform=android&version=v1.0.0"
        Log.i(TAG, "load url ${url}")
        this.initStatus = InitStatus.Initialising
        this.webview.loadUrl(url)
    }

    private fun configureWebView(): CereModule {
        val webview = WebView(context)
        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true
        webview.settings.databaseEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)

        webview.addJavascriptInterface(this, "Android")
        this.webview = webview
        return this
    }

    fun sendEvent(eventType: String, payload: String) {
        if (this.initStatus == InitStatus.Initialised) {
            val script = """
                (async function() {
                    console.log('send event dialog');
                    return cereSDK.sendEvent('${eventType}', ${payload}).
                        then(() => {
                            console.log(`event ${eventType} sent`);
                        }).
                        catch(err => {
                            console.log(`${eventType} sending error` + err);
                        });
                })();""".trimIndent()
            Log.e(TAG, "Calling evaluate 2")
            val latch = CountDownLatch(1)
            webview.post{
                Log.e(TAG, "evaluate javascript 2")
                webview.evaluateJavascript(script)
                {
                    Log.i(TAG, "send event $eventType executed")
                }
                latch.countDown()
            }
            latch.await()
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
        onInitializationHandler.handle()
    }

    @JavascriptInterface
    fun sdkInitializedError(error: String) {
        Log.i(TAG, "sdk initialise error: $error")
        this.initStatus = InitStatus.InitialiseError(error)
    }
}
