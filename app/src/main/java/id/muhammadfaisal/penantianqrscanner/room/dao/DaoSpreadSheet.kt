package id.muhammadfaisal.penantianqrscanner.room.dao

import androidx.room.*
import id.muhammadfaisal.penantianqrscanner.room.entity.SpreadSheetEntity

@Dao
interface DaoSpreadSheet {
    @Insert
    fun insert(spreadSheetEntity: SpreadSheetEntity)

    @Update
    fun update(spreadSheetEntity: SpreadSheetEntity)

    @Query("SELECT * FROM m_spreadsheet")
    fun getAll() : List<SpreadSheetEntity>

    @Query("SELECT * FROM m_spreadsheet WHERE id = :id")
    fun get(id : String) : SpreadSheetEntity?

    @Delete
    fun delete(spreadSheetEntity: SpreadSheetEntity)
}