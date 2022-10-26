package com.example.stockmarketcomposeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stockmarketcomposeapp.data.converter.LocalDateTimeConverter

@TypeConverters(LocalDateTimeConverter::class)
@Database(
    entities = [
        CompanyListingEntity::class,
        CompanyInfoEntity::class,
        IntradayInfoEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {
    abstract val dao: StockDao
}