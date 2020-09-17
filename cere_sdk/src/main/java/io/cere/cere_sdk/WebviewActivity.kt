package io.cere.cere_sdk

import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.webview_activity.*


class WebviewActivity : AppCompatActivity() {

    private lateinit var webview: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)
        this.setFinishOnTouchOutside(true)
        this.webview = CereModule.getInstance(this.application).webview
        attachBridgeView()
    }

    private fun attachBridgeView() {
        if (webview.parent == null) {
            root.addView(webview)
            val params = webview.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            params.width = MATCH_PARENT
            params.height = MATCH_PARENT
            webview.layoutParams = params
        }
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