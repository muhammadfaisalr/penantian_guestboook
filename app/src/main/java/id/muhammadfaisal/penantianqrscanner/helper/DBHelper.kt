package id.muhammadfaisal.penantianqrscanner.helper

import android.content.Context
import androidx.room.Room
import id.muhammadfaisal.penantianqrscanner.room.AppDatabase

class DBHelper {
    companion object{
        const val DB_NAME = "db_penantian"

        fun connect(context : Context): AppDatabase{
            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).allowMainThreadQueries().build()
        }
    }
}