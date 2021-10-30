package id.muhammadfaisal.penantianqrscanner.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "s_spreadsheet_config")
data class SpreadSheetConfigEntity(
    @PrimaryKey(autoGenerate = false)
    public var id: Long?,
    @ColumnInfo(name = "sheet_name")
    public var sheetName: String?,
    @ColumnInfo(name = "sheet_id")
    public var sheetId: String?,
    @ColumnInfo(name = "column")
    public var column: String?,
    @ColumnInfo(name = "row")
    public var row: String?,
) {}