package id.muhammadfaisal.penantianqrscanner.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.zxing.Result
import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.databinding.FragmentScanBinding
import id.muhammadfaisal.penantianqrscanner.helper.DBHelper
import id.muhammadfaisal.penantianqrscanner.helper.SheetHelper
import id.muhammadfaisal.penantianqrscanner.room.AppDatabase
import id.muhammadfaisal.penantianqrscanner.room.dao.DaoSpreadSheet
import id.muhammadfaisal.penantianqrscanner.room.dao.DaoSpreadSheetConfig
import id.muhammadfaisal.penantianqrscanner.room.entity.SpreadSheetEntity

class ScanFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentScanBinding

    private lateinit var codeScanner: CodeScanner

    private lateinit var database: AppDatabase
    private lateinit var daoSpreadSheet: DaoSpreadSheet
    private lateinit var daoSpreadSheetConfig: DaoSpreadSheetConfig

    //Const
    private var isTorchOn = false
    private var isBackCam = false
    private var isCanScan = true
    private var resultText = ""
    private val CAMERA_REQUEST: Int = 1002
    private val SCOPES = { SheetsScopes.SPREADSHEETS }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        this.binding = FragmentScanBinding.inflate(this.layoutInflater)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.init()
        this.setupScanner()
    }

    private fun setupScanner() {
        this.codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = isTorchOn

            decodeCallback = DecodeCallback {
                requireActivity().runOnUiThread {
                    this@ScanFragment.save(it)
                }
            }

            errorCallback = ErrorCallback {
                requireActivity().runOnUiThread {
                    Log.d(ScanFragment::class.simpleName, "Error ${it.message}")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        this.codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        this.codeScanner.releaseResources()
    }

    private fun init() {
        this.database = DBHelper.connect(requireContext())
        this.daoSpreadSheet = this.database.daoSpreadSheet()
        this.daoSpreadSheetConfig = this.database.daoSpreadSheetConfig()

        this.codeScanner = CodeScanner(requireContext(), this.binding.codeScanner)

        this.binding.imageFlash.setOnClickListener(this)
        this.binding.imageCamera.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.imageFlash) {
            this.torch()
        } else if (v == this.binding.imageCamera) {
            this.camera()
        }
    }

    private fun camera() {
        this.isBackCam = !isBackCam
        this.codeScanner.apply {
            this.camera = if (isBackCam) {
                CodeScanner.CAMERA_FRONT
            } else {
                CodeScanner.CAMERA_BACK
            }
        }

    }

    private fun torch() {
        this.isTorchOn = !this.isTorchOn
        this.codeScanner.isFlashEnabled = this.isTorchOn
        if (isTorchOn) {
            this.binding.imageFlash.setColorFilter(
                this.resources.getColor(
                    R.color.purple_200,
                    null
                )
            )
        } else {
            this.binding.imageFlash.setColorFilter(this.resources.getColor(R.color.black, null))

        }
    }

    private fun save(it: Result) {

        if (isCanScan){
            if (this.daoSpreadSheetConfig.getConfig().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.spreadsheet_not_yet_configured),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val configEntity = this.daoSpreadSheetConfig.getConfig()[0]
            val id = it.toString() + configEntity.row + configEntity.column
            val spreadSheet = this.daoSpreadSheet.get(id)

            if (spreadSheet == null) {
                this.daoSpreadSheet.insert(
                    SpreadSheetEntity(
                        id,
                        it.toString(),
                        configEntity.sheetName,
                        configEntity.sheetId,
                        configEntity.column,
                        configEntity.row,
                        System.currentTimeMillis()
                    )
                )

                this.binding.layoutStatus.visibility = View.VISIBLE
            } else {
                this.daoSpreadSheet.update(
                    SpreadSheetEntity(
                        id,
                        it.toString(),
                        configEntity.sheetName,
                        configEntity.sheetId,
                        configEntity.column,
                        configEntity.row,
                        System.currentTimeMillis()
                    )
                )

                this.binding.layoutStatus.visibility = View.VISIBLE
            }

            this.send(spreadSheet)
        }
    }

    private fun send(spreadSheet: SpreadSheetEntity?) {

        val lastSigned = GoogleSignIn.getLastSignedInAccount(requireContext())!!

        val scopes = listOf(SheetsScopes.SPREADSHEETS)
        val credential = GoogleAccountCredential.usingOAuth2(requireContext(), scopes)
        credential.selectedAccount = lastSigned.account

        val jsonFactory = JacksonFactory.getDefaultInstance()
        val httpTransport = AndroidHttp.newCompatibleTransport()

        val service = Sheets.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(this.resources.getString(R.string.app_name))
            .build()

        var exception: java.lang.Exception? = null

        try {
            SheetHelper.setCellValue(requireContext(), service, spreadSheet!!)
        } catch (e: Exception) {
            exception = e
            Log.e(ScanFragment::class.java.simpleName, "Err : ${e.printStackTrace()}")
        }

        if (exception == null) {

            isCanScan = false

            Handler(Looper.myLooper()!!).postDelayed({
                isCanScan = true
                this.binding.layoutStatus.visibility = View.INVISIBLE
            }, 3000L)

            val spreadSheetConfig = daoSpreadSheetConfig.getConfig()[0]
            spreadSheetConfig.column = (spreadSheet!!.column!!.toInt() + 1).toString()

            daoSpreadSheetConfig.update(spreadSheetConfig)
        }
    }
}
