package id.muhammadfaisal.penantianqrscanner.room

import androidx.room.Database
import androidx.room.RoomDatabase
import id.muhammadfaisal.penantianqrscanner.room.dao.DaoSpreadSheet
import id.muhammadfaisal.penantianqrscanner.room.dao.DaoSpreadSheetConfig
import id.muhammadfaisal.penantianqrscanner.room.entity.SpreadSheetConfigEntity
import id.muhammadfaisal.penantianqrscanner.room.entity.SpreadSheetEntity

@Database(entities = [SpreadSheetConfigEntity::class, SpreadSheetEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun daoSpreadSheetConfig() : DaoSpreadSheetConfig

    abstract fun daoSpreadSheet() : DaoSpreadSheet
}