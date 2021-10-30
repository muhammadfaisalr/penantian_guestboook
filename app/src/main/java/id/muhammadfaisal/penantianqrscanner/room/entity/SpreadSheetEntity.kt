package id.muhammadfaisal.penantianqrscanner.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "m_spreadsheet")
data class SpreadSheetEntity(
    @PrimaryKey
    public var id: String,
    @ColumnInfo(name = "value")
    public var value: String,
    @ColumnInfo(name = "sheet_name")
    public var sheetName: String?,
    @ColumnInfo(name = "sheet_id")
    public var sheetId: String?,
    @ColumnInfo(name = "column")
    public var column: String?,
    @ColumnInfo(name = "row")
    public var row: String?,
    @ColumnInfo(name = "time")
    public var time: Long?
) {}