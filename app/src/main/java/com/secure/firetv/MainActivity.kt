package com.secure.firetv

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {

    private lateinit var hiddenWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Setup the invisible WebView to solve Cloudflare dynamically
        hiddenWebView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
            layoutParams = ViewGroup.LayoutParams(1, 1)
        }
        addContentView(hiddenWebView, hiddenWebView.layoutParams)
        setupCloudflareBypass()

        // Load the visual Leanback UI
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, MainFragment())
                .commit()
        }
    }

    fun loadStream(movieId: String) {
        // In a real app, you would fetch the target URL from the backend first.
        // For testing, we send the WebView to a mock target.
        val targetScrapeUrl = "https://example-cloudflare-protected-site.com/watch?v=$movieId"
        hiddenWebView.loadUrl(targetScrapeUrl)
    }

    private fun setupCloudflareBypass() {
        hiddenWebView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                val url = request.url.toString()
                
                // When the local WebView catches the final streaming file:
                if (url.contains(".m3u8") || url.contains(".mp4")) {
                    val cookies = CookieManager.getInstance().getCookie(url)
                    val headers = request.requestHeaders ?: mutableMapOf()
                    if (cookies != null) headers["Cookie"] = cookies

                    Handler(Looper.getMainLooper()).post {
                        val intent = Intent(this@MainActivity, PlayerActivity::class.java).apply {
                            putExtra("STREAM_URL", url)
                            putExtra("HEADERS", HashMap(headers))
                        }
                        startActivity(intent)
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }
        }
    }
}
