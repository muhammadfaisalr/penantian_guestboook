package id.muhammadfaisal.penantianqrscanner.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.LocaleList
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.adapter.SpinnerAdapter
import id.muhammadfaisal.penantianqrscanner.data.Data
import id.muhammadfaisal.penantianqrscanner.databinding.ActivityLoginBinding
import id.muhammadfaisal.penantianqrscanner.databinding.ItemLanguageBinding
import java.net.URLEncoder
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private lateinit var binding: ActivityLoginBinding
    private var isChecked = false
    private lateinit var pref: SharedPreferences

    private var isDetect: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityLoginBinding.inflate(this.layoutInflater)
        this.supportActionBar?.hide()
        super.setContentView(this.binding.root)

        this.init()
        this.detect()
        this.setupPermission()
    }

    private fun detect() {
        this.isDetect = this.intent.getBooleanExtra("IS_DETECT", true)

        if (this.isDetect) {
            val isLogIn = this.pref.getBoolean("IS_LOG_IN", false)

            if (isLogIn) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return
            }
        }
    }

    private fun setupPermission() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permission2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permission3 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED) {
            this.makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1001
        )
    }


    private fun init() {

        this.pref = getPreferences(MODE_PRIVATE)

        this.binding.spinner.adapter = SpinnerAdapter(this, Data.language())
        this.binding.spinner.setSelection(0, false)

        val binding = ItemLanguageBinding.bind(this.binding.spinner.selectedView)
        binding.text.apply {
            setTextColor(Color.WHITE)
        }



        this.binding.buttonStart.setOnClickListener(this)
        this.binding.textContact.setOnClickListener(this)
        this.binding.spinner.onItemSelectedListener = this
        this.binding.spinner.setOnTouchListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.buttonStart) {
            this.validate()
        }
        if (v == this.binding.textContact) {
            this.contact()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun contact() {
        val i = Intent(Intent.ACTION_VIEW)
        val url =
            "https://api.whatsapp.com/send?phone=" + "6285155337700" + "&text=" + URLEncoder.encode(
                "Halo, saya ingin meminta kode akses untuk aplikasi Penantian QR",
                "UTF-8"
            )
        i.setPackage("com.whatsapp")
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun validate() {
        var isPassed = false
        this.binding.layoutStatus.visibility = View.INVISIBLE
        val pin = this.binding.inputCode.text.toString()

        if (pin.isEmpty()) {
            this.binding.layoutStatus.visibility = View.VISIBLE
            this.binding.imageStatus.setImageResource(R.drawable.remove)
            this.binding.textStatus.text = getString(R.string.access_denied)
            return
        }

        for (i in Data.pin()) {
            if (i == pin) {
                this.binding.layoutStatus.visibility = View.VISIBLE
                this.binding.imageStatus.setImageResource(R.drawable.check)
                this.binding.textStatus.text = getString(R.string.access_accepted)

                isPassed = true
                Handler(Looper.getMainLooper()).postDelayed({
                    requestSignIn()
                }, 1000L)

                break
            }
        }

        if (!isPassed) {
            this.binding.layoutStatus.visibility = View.VISIBLE
            this.binding.imageStatus.setImageResource(R.drawable.remove)
            this.binding.textStatus.text = getString(R.string.access_denied)
        }
    }

    private fun requestSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, signInOptions)
        startActivityForResult(client.signInIntent, 1)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val binding = ItemLanguageBinding.bind(view!!)

        binding.text.apply {
            setTextColor(Color.WHITE)
        }

        if (isChecked) {

            if (position == 0) {

                this.changeLanguage("id")
            } else {

                this.changeLanguage("en")
            }

        }

    }

    private fun changeLanguage(s: String) {

        val locale = Locale(s)
        Locale.setDefault(locale)

        val config = baseContext.resources.configuration
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        this.recreate()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        isChecked = true
        return false
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener {

                    with(pref.edit()) {
                        putBoolean("IS_LOG_IN", true)
                        apply()
                    }

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Log.d(LoginActivity::class.java.simpleName, it.message!!)
                    it.printStackTrace()
                }
        }
    }
}