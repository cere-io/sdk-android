package io.cere.cere_sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView


const val baseUrl: String = "http://sdk-common.cere.io.s3-website-us-west-2.amazonaws.com/native.html"

/**
 * Interface used after `CereModule` init method.
 */
interface OnInitializationFinishedHandler {
    fun handle()
}

/**
 * This is the main class which incapsulates all logic (opening/closing activity etc) and
 * provides high-level methods to manipulate with.
 **
 * <p>All you need to start working with the class is to instantiate <tt>CereModule</tt> once and
 * initialize it with 2 params. Example:
 * </p>
 *
 * <p>
 *     <pre>
 *         {@code
 *              CereModule cereModule = CereModule.getInstance(context);
 *              cereModule.init("Your appId", "Your integrationPartnerUserId");
 *         }
 *     </pre>
 * </p>
 *
 * <p>That's enough for start loading {@code CereModule}, but note that {@code CereModule} still
 * remains hidden. Also, first load of {@code CereModule} takes a some time which depends on
 * network connection quality. That's why you need to init {@code CereModule} as soon as possible.
 * </p>
 *
 * <p>If you want to show {@code CereModule} right after it has initialized, you can add listener
 * {@see OnInitializationFinishedHandler} implementation which will invoke method <tt>sendEvent</tt> on
 * {@code CereModule} instance. Example:
 * </p>
 *
 * <p>
 *     <pre>
 *         {@code
 *              cereModule.onInitializationFinishedHandler(() -> {
 *                  cereModule.sendEvent("APP_LAUNCHED_TEST", "{}");
 *              });
 *         }
 *     </pre>
 * </p>
 *
 * @author  Rudolf Markulin
 */
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

    var onInitializationFinishedHandler: OnInitializationFinishedHandler = object: OnInitializationFinishedHandler {
        override fun handle() {

        }
    }

    lateinit var webview: WebView
    private lateinit var appId: String
    private lateinit var integrationPartnerUserId: String

    private var initStatus: InitStatus = InitStatus.Uninitialised

    /**
     * @return current sdk initialisation status instance of {@code InitStatus}
     */
    fun getInitStatus(): InitStatus {
        return this.initStatus
    }

    /**
     * Initializes and prepares the SDK for usage.
     * @param appId: identifier of the application from RXB.
     * @param integrationPartnerUserId: The user’s id in the system.
     */
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

    /**
     * Send event to RXB.
     * @param eventType: Type of event. For example `APP_LAUNCHED`.
     * @param payload: Optional parameter which can be passed with event. It should contain serialised json payload associated with eventType.
     */
    fun sendEvent(eventType: String, payload: String = "") {
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

            val handler = Handler(Looper.getMainLooper())

            handler.post{
                Log.e(TAG, "evaluate send event javascript")
                webview.evaluateJavascript(script)
                {
                    Log.i(TAG, "send event $eventType executed")
                }
            }
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
        onInitializationFinishedHandler.handle()
    }

    @JavascriptInterface
    fun sdkInitializedError(error: String) {
        Log.i(TAG, "sdk initialise error: $error")
        this.initStatus = InitStatus.InitialiseError(error)
    }
}
