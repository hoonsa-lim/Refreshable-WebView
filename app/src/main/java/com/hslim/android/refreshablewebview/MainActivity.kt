package com.hslim.android.refreshablewebview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity(), RefreshListener {
    companion object{
        private const val TAG = "MainActivity"
    }

    private lateinit var webView: RefreshableWebView

    @SuppressLint("SetJavaScriptEnabled", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initWebView()
        initWebViewRefresh()
    }

    @SuppressLint("InflateParams")
    private fun initWebViewRefresh() {
        val indicator = layoutInflater.inflate(R.layout.view_indicator, null)
        indicator.findViewById<ImageView>(R.id.image_view).apply {
            if (background is AnimationDrawable) (background as AnimationDrawable).start()
        }

        val setting = RefreshSetting
            .Builder(this)
            .setLoadingIndicatorView(indicator)
            .setOnRefreshListener(this)
            .setRefreshThresholdDp(200)
            .setRefreshingTime(1.seconds)
            .setRefreshVibrate(true)
            .build()

        webView.setRefreshSetting(setting)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView = findViewById(R.id.web_view)

        webView.apply {
            settings.javaScriptEnabled = true
            loadUrl("https://m.naver.com")
        }
        webView.setRefreshEnabled(true)
    }

    override fun onRefresh(view: View) {
        webView.reload()
    }
}