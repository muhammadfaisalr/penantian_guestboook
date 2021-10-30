package id.muhammadfaisal.penantianqrscanner.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.activity.LoginActivity
import id.muhammadfaisal.penantianqrscanner.databinding.FragmentSettingBinding
import id.muhammadfaisal.penantianqrscanner.helper.DBHelper
import id.muhammadfaisal.penantianqrscanner.room.AppDatabase
import id.muhammadfaisal.penantianqrscanner.room.dao.DaoSpreadSheetConfig
import id.muhammadfaisal.penantianqrscanner.room.entity.SpreadSheetConfigEntity
import java.net.URLEncoder


class SettingFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSettingBinding

    private lateinit var inputSheetName: EditText
    private lateinit var inputSheetId: EditText
    private lateinit var inputRow: EditText
    private lateinit var inputColumn: EditText

    private lateinit var database: AppDatabase
    private lateinit var daoSpreadSheetConfig: DaoSpreadSheetConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        this.binding = FragmentSettingBinding.inflate(this.layoutInflater)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.init()
    }

    private fun init() {
        this.database = DBHelper.connect(requireContext())
        this.daoSpreadSheetConfig = this.database.daoSpreadSheetConfig()

        this.inputSheetId = this.binding.inputSheetId
        this.inputSheetName = this.binding.inputSheetName
        this.inputColumn = this.binding.inputColumn
        this.inputRow = this.binding.inputRow

        if (this.daoSpreadSheetConfig.getConfig().isNotEmpty()) {
            val spreadsheet = this.daoSpreadSheetConfig.getConfig()[0]

            this.inputSheetName.setText(spreadsheet.sheetName)
            this.inputSheetId.setText(spreadsheet.sheetId)
            this.inputRow.setText(spreadsheet.row)
            this.inputColumn.setText(spreadsheet.column)
        }

        this.binding.buttonSave.setOnClickListener(this)
        this.binding.buttonHelp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.buttonSave) {
            this.save()
        }else if (v == this.binding.buttonHelp){
            this.help()
        }
    }

    private fun help(){
        val i = Intent(Intent.ACTION_VIEW)
        val url =
            "https://api.whatsapp.com/send?phone=" + "6285155337700" + "&text=" + URLEncoder.encode(
                "Halo, saya Memiliki Kendala Pada Aplikasi Guestbook Penantian",
                "UTF-8"
            )
        i.setPackage("com.whatsapp")
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun logout() {
        val pref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        pref.edit().clear().apply()

        if (this.daoSpreadSheetConfig.getConfig().isNotEmpty()){
            this.daoSpreadSheetConfig.delete(this.daoSpreadSheetConfig.getConfig()[0])
        }

        val daoSpreadSheet = this.database.daoSpreadSheet()
        if (daoSpreadSheet.getAll().isNotEmpty()){
            for (i in daoSpreadSheet.getAll()){
                daoSpreadSheet.delete(i)
            }
        }

        startActivity(Intent(requireContext(), LoginActivity::class.java).putExtra("IS_DETECT", false))
        requireActivity().finish()
    }

    private fun save() {

        this.validate(this.inputSheetName, this.inputSheetId, this.inputRow, this.inputColumn)

        val sheetName = this.inputSheetName.text.toString()
        val sheetId = this.inputSheetId.text.toString()
        val row = this.inputRow.text.toString()
        val column = this.inputColumn.text.toString()

        if (this.daoSpreadSheetConfig.getConfig().isEmpty()) {
            this.daoSpreadSheetConfig.insert(
                SpreadSheetConfigEntity(
                    0L,
                    sheetName,
                    sheetId,
                    column,
                    row
                )
            )

        } else {
            this.daoSpreadSheetConfig.update(
                SpreadSheetConfigEntity(
                    0L,
                    sheetName,
                    sheetId,
                    column,
                    row
                )
            )
        }

        this.binding.imageStatus.visibility = View.VISIBLE
        this.binding.buttonSave.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.purple_200)

        Handler(Looper.myLooper()!!).postDelayed({
            this.binding.imageStatus.visibility = View.INVISIBLE
            this.binding.buttonSave.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.black)
        },300L)
    }

    private fun validate(vararg views: EditText) {
        for (i in views) {
            if (i.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Anda Belum Memasukkan Data Dengan Benar!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }
    }
}