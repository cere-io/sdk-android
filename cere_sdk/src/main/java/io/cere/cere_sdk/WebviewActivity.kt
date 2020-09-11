package io.cere.cere_sdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.webview_activity.*

class WebviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)
        this.setFinishOnTouchOutside(true)

        btnOk.setOnClickListener {
            finish()
        }
    }
}