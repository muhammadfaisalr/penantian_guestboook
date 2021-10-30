package id.muhammadfaisal.penantianqrscanner.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.databinding.ActivityWebviewBinding
import id.muhammadfaisal.penantianqrscanner.helper.DBHelper
import id.muhammadfaisal.penantianqrscanner.room.AppDatabase
import id.muhammadfaisal.penantianqrscanner.room.dao.DaoSpreadSheetConfig

class WebviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityWebviewBinding

    private lateinit var database : AppDatabase
    private lateinit var daoSpreadSheetConfig: DaoSpreadSheetConfig

    private lateinit var swipe : SwipeRefreshLayout
    private lateinit var webView : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityWebviewBinding.inflate(this.layoutInflater)
        this.supportActionBar?.hide()
        super.setContentView(this.binding.root)

        this.init()
        this.load()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun load() {
        this.webView.loadUrl("https://docs.google.com/spreadsheets/d/${daoSpreadSheetConfig.getConfig()[0].sheetId}")
        this.webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                this@WebviewActivity.swipe.isRefreshing = true

                if(view!!.url!!.endsWith("htmlview")){
                    this@WebviewActivity.swipe.isRefreshing = false
                    startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(view.url)))
                    return true
                }

                return super.shouldOverrideUrlLoading(view, request)
            }


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                this@WebviewActivity.swipe.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                this@WebviewActivity.swipe.isRefreshing = false
            }
        }

        this.webView.settings.apply {
            javaScriptEnabled = true
        }
    }

    override fun onBackPressed() {
        if (this.webView.canGoBack()){
            this.webView.goBack()
            return
        }

        super.onBackPressed()
    }

    private fun init(){
        this.database = DBHelper.connect(this)
        this.daoSpreadSheetConfig = this.database.daoSpreadSheetConfig()

        this.swipe = this.binding.swipeRefreshLayout
        this.webView = this.binding.webView

        this.swipe.isRefreshing = true
        this.swipe.setOnRefreshListener {
            this.webView.reload()
        }
    }
}