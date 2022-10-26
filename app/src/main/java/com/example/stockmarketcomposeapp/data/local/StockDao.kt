package com.example.stockmarketcomposeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    )
    @Query("DELETE FROM companylistingentity")
    suspend fun clearCompanyListings()

    @Query(
        """
            SELECT *
            FROM companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || "%" OR
            UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListing(query: String): List<CompanyListingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyInfos(
        companyInfosEntities: CompanyInfoEntity
    )

    @Query("SELECT * FROM companyinfoentity WHERE :symbol == symbol")
    suspend fun getCompanyInfos(symbol: String): CompanyInfoEntity

    @Query("DELETE FROM companyinfoentity WHERE :symbol == symbol")
    suspend fun clearCompanyInfos(symbol: String)


    @Query("SELECT * FROM companyinfoentity  WHERE :symbol == symbol")
    fun getAnyInfo(symbol: String): CompanyInfoEntity?

 ///////////////////////////////////////////////////////////////////////////////////////////////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntradayInfo(
        intradayInfoEntities: List<IntradayInfoEntity>
    )
    @Query("SELECT * FROM intradayinfoentity")
    suspend fun getIntradayInfo(): List<IntradayInfoEntity>

    @Query("DELETE FROM intradayinfoentity")
    suspend fun clearIntradayInfos()

}