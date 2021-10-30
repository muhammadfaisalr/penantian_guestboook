package id.muhammadfaisal.penantianqrscanner.room.dao

import androidx.room.*
import id.muhammadfaisal.penantianqrscanner.room.entity.SpreadSheetConfigEntity

@Dao
interface DaoSpreadSheetConfig {

    @Query("SELECT * FROM s_spreadsheet_config")
    fun getConfig() : List<SpreadSheetConfigEntity>

    @Update
    fun update(spreadSheetConfigEntity: SpreadSheetConfigEntity)

    @Insert
    fun insert(spreadSheetConfigEntity: SpreadSheetConfigEntity)

    @Delete
    fun delete(spreadSheetConfigEntity: SpreadSheetConfigEntity)
}