package com.example.stockmarketcomposeapp.domain.repository

import com.example.stockmarketcomposeapp.domain.model.CompanyInfo
import com.example.stockmarketcomposeapp.domain.model.CompanyListing
import com.example.stockmarketcomposeapp.domain.model.IntradayInfo
import com.example.stockmarketcomposeapp.util.Resource
import kotlinx.coroutines.flow.Flow


interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    suspend fun getIntraDayInfo(
        fetchFromRemote: Boolean,
        symbol: String
    ):Flow<Resource<List<IntradayInfo>>>

    suspend fun getCompanyInfo(
        fetchFromRemote: Boolean,
        symbol: String
    ):Flow<Resource<CompanyInfo>>
}