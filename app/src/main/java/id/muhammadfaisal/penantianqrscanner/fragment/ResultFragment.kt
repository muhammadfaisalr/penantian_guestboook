package id.muhammadfaisal.penantianqrscanner.fragment

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import com.google.api.client.util.DateTime
import id.muhammadfaisal.penantianqrscanner.R
import id.muhammadfaisal.penantianqrscanner.activity.WebviewActivity
import id.muhammadfaisal.penantianqrscanner.adapter.ResultAdapter
import id.muhammadfaisal.penantianqrscanner.databinding.FragmentResultBinding
import id.muhammadfaisal.penantianqrscanner.databinding.PdfResultBinding
import id.muhammadfaisal.penantianqrscanner.helper.DBHelper
import id.muhammadfaisal.penantianqrscanner.room.AppDatabase
import id.muhammadfaisal.penantianqrscanner.room.dao.DaoSpreadSheet
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ResultFragment : Fragment(), View.OnClickListener{

    private lateinit var binding: FragmentResultBinding
    private lateinit var bindingPdf: PdfResultBinding

    private lateinit var recyclerData: RecyclerView

    private lateinit var database: AppDatabase
    private lateinit var daoSpreadSheet: DaoSpreadSheet

    val pageWidth = 792;
    val pageHeight = 1120;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentResultBinding.inflate(this.layoutInflater)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.init()
        this.include()
        this.setup()
    }

    private fun include() {
        this.bindingPdf = this.binding.pdf
        this.bindingPdf.recyclerData.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        this.bindingPdf.recyclerData.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        this.bindingPdf.recyclerData.adapter = ResultAdapter(requireContext(), this.daoSpreadSheet.getAll(), true)

    }

    private fun setup() {
        this.recyclerData.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        this.recyclerData.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        this.recyclerData.adapter = ResultAdapter(requireContext(), this.daoSpreadSheet.getAll(), false)

    }

    private fun init() {
        this.database = DBHelper.connect(requireContext())
        this.daoSpreadSheet = this.database.daoSpreadSheet()
        this.recyclerData = this.binding.recyclerData

        if (this.database.daoSpreadSheetConfig().getConfig().isEmpty()){
            this.binding.buttonOpenSpreadsheet.visibility = View.GONE
            this.binding.buttonExport.visibility = View.GONE
        }

        this.binding.buttonOpenSpreadsheet.setOnClickListener(this)
        this.binding.buttonExport.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.buttonOpenSpreadsheet) {
            startActivity(Intent(requireContext(), WebviewActivity::class.java))
        }else if (v == this.binding.buttonExport){
            this.generatePdf()
        }
    }

    private fun generatePdf() {
        PdfGenerator
            .getBuilder()
            .setContext(requireContext())
            .fromViewSource()
            .fromView(this.bindingPdf.parent)
            .setPageSize(PdfGenerator.PageSize.A4)
            .setFileName("PenantianGuestbook ${Date().time}")
            .openPDFafterGeneration(true)
            .build(object : PdfGeneratorListener(){
                override fun onSuccess(response: SuccessResponse?) {
                    super.onSuccess(response)
                }

                override fun showLog(log: String?) {
                    super.showLog(log)
                    Log.d(ResultFragment::class.java.simpleName, "[LOG] $log")
                }

                override fun onFailure(failureResponse: FailureResponse?) {
                    super.onFailure(failureResponse)
                }

                override fun onStartPDFGeneration() {
                    Log.d(ResultFragment::class.java.simpleName, "[START] Start Generate PDF")
                }

                override fun onFinishPDFGeneration() {
                    Log.d(ResultFragment::class.java.simpleName, "[FINISH] Finish Generate PDF")
                }

            })
    }
}