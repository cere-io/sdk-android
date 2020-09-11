package io.cere.cere_sdk

import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.webview_activity.*


class WebviewActivity : AppCompatActivity() {
    var webview: WebView? = CereModule.getInstance()?.webview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)
        this.setFinishOnTouchOutside(true)
        attachBridgeView()
    }

    private fun attachBridgeView() {
        if (webview?.getParent() == null) {
            root.addView(webview)
            val params = webview?.getLayoutParams() as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            params.width = MATCH_PARENT
            params.height = MATCH_PARENT
            webview?.setLayoutParams(params)
        }
    }

    private fun detachBridgeView() {
        if (webview != null && webview?.getParent() != null) {
            (webview?.getParent() as ViewGroup).removeAllViews()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detachBridgeView()
    }
}