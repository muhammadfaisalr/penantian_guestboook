package id.muhammadfaisal.penantianqrscanner.helper

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import id.muhammadfaisal.penantianqrscanner.room.AppDatabase
import id.muhammadfaisal.penantianqrscanner.room.entity.SpreadSheetEntity

class SheetHelper {
    companion object {
        private val rows = mutableListOf<ValueRange>()

        fun setCellValue(context : Context, service: Sheets, spreadSheetEntity: SpreadSheetEntity) {
            val asyncTask = @SuppressLint("StaticFieldLeak")
            object : AsyncTask<Void, Void, String>() {
                override fun doInBackground(vararg params: Void?): String {
                    val body = ValueRange()
                        .setValues(
                            mutableListOf(listOf<Any>(spreadSheetEntity.value))
                        )

                    val range = spreadSheetEntity.row + spreadSheetEntity.column

                    val result =
                        service.spreadsheets()
                            .values()
                            .update(spreadSheetEntity.sheetId, range, body)
                            .setValueInputOption("RAW")
                            .execute()

                    Log.d(SheetHelper::class.java.simpleName, "Row Updated = ${result.updatedData}")

                    if (result != null){
                        val daoConfig = DBHelper.connect(context).daoSpreadSheetConfig()
                        val spreadSheetConfig = daoConfig.getConfig()[0]
                        spreadSheetConfig.column + (spreadSheetEntity.column!!.toInt() + 1).toString()

                        daoConfig.update(spreadSheetConfig)
                    }

                    return result.updatedCells.toString()

                }
            }
            asyncTask.execute()
        }
    }
}