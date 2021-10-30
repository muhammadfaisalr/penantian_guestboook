package id.muhammadfaisal.penantianqrscanner.activity

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.databinding.ActivityMainBinding
import id.muhammadfaisal.penantianqrscanner.fragment.ResultFragment
import id.muhammadfaisal.penantianqrscanner.fragment.ScanFragment
import id.muhammadfaisal.penantianqrscanner.fragment.SettingFragment

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityMainBinding

    private lateinit var imageScan : ImageView
    private lateinit var imageSetting : ImageView
    private lateinit var imageDocument : ImageView

    private lateinit var pref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(this.layoutInflater)
        this.supportActionBar?.hide()
        super.setContentView(this.binding.root)

        this.saveState()
        this.init()
        this.scan()
    }

    private fun saveState() {
        this.pref = getPreferences(MODE_PRIVATE)


        if (!this.pref.getBoolean("IS_LOG_IN", false)){
            this.pref.edit().putBoolean("IS_LOG_IN", true).apply()
        }

    }

    private fun init(){
        this.imageScan = this.binding.imageScan
        this.imageSetting = this.binding.imageSetting
        this.imageDocument = this.binding.imageDocument

        this.binding.cardSetting.setOnClickListener(this)
        this.binding.cardScan.setOnClickListener(this)
        this.binding.cardResult.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.cardSetting){
            this.setting()
        }else if (v == this.binding.cardScan){
            this.scan()
        }else if (v == this.binding.cardResult){
            this.result()
        }
    }

    private fun result() {
        this.inflate(ResultFragment())
        this.imageScan.setColorFilter(this.resources.getColor(R.color.black, null))

        this.imageSetting.setColorFilter(this.resources.getColor(R.color.black, null))

        this.imageDocument.setColorFilter(this.resources.getColor(R.color.purple_200, null))
    }

    private fun scan() {
        this.inflate(ScanFragment())

        this.imageScan.setColorFilter(this.resources.getColor(R.color.purple_200, null))

        this.imageSetting.setColorFilter(this.resources.getColor(R.color.black, null))

        this.imageDocument.setColorFilter(this.resources.getColor(R.color.black, null))
    }

    private fun setting() {
        this.inflate(SettingFragment())

        this.imageScan.setColorFilter(this.resources.getColor(R.color.black, null))

        this.imageSetting.setColorFilter(this.resources.getColor(R.color.purple_200, null))

        this.imageDocument.setColorFilter(this.resources.getColor(R.color.black, null))
    }

    private fun inflate(fragment : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }
}