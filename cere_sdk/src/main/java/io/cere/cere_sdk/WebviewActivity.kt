package io.cere.cere_sdk

import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class WebviewActivity : AppCompatActivity() {

    private lateinit var webview: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setFinishOnTouchOutside(true)
        attachBridgeView(CereModule.getInstance(this.application).webview)
    }

    private fun attachBridgeView(wv: WebView) {
        val params = RelativeLayout.LayoutParams(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        params.width = MATCH_PARENT
        params.height = MATCH_PARENT
        webview = wv
        setContentView(wv, params)
    }

    private fun detachBridgeView() {
        if (webview.parent != null) {
            (webview.parent as ViewGroup).removeAllViews()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detachBridgeView()
    }
}