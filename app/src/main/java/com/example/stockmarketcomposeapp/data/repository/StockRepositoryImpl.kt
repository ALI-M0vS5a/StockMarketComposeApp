package com.example.stockmarketcomposeapp.data.repository

import com.example.stockmarketcomposeapp.R
import com.example.stockmarketcomposeapp.data.csv.CSVParser
import com.example.stockmarketcomposeapp.data.local.StockDatabase
import com.example.stockmarketcomposeapp.data.mapper.*
import com.example.stockmarketcomposeapp.data.remote.StockApi
import com.example.stockmarketcomposeapp.domain.model.CompanyInfo
import com.example.stockmarketcomposeapp.domain.model.CompanyListing
import com.example.stockmarketcomposeapp.domain.model.IntradayInfo
import com.example.stockmarketcomposeapp.domain.repository.StockRepository
import com.example.stockmarketcomposeapp.util.Resource
import com.example.stockmarketcomposeapp.util.UiText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@DelicateCoroutinesApi
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>

) : StockRepository {
    private val dao = db.dao
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                emit(
                    Resource.Error(
                        message = UiText.StringResource(
                            resId = R.string.please_check_your_connection
                        )
                    )
                )
                null
            } catch (e: HttpException) {
                emit(
                    Resource.Error(
                        message = (e.localizedMessage ?: UiText.StringResource(
                            resId = R.string.Oops_something_went_wrong
                        )) as UiText
                    )
                )
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntraDayInfo(
        fetchFromRemote: Boolean,
        symbol: String
    ): Flow<Resource<List<IntradayInfo>>> {
        return flow {
            emit(Resource.Loading(true))
            val localIntraInfos = dao.getIntradayInfo(symbol)
            emit(
                Resource.Success(
                    data = localIntraInfos.map { it.toIntradayInfo() }
                )
            )
            val isDbEmpty = localIntraInfos.isEmpty()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteIntraInfo = try {
                val response = api.getIntradayInfo(symbol)
                intradayInfoParser.parse(response.byteStream())
            } catch (e: IOException) {
                emit(
                    Resource.Error(
                        message = UiText.StringResource(
                            resId = R.string.please_check_your_connection
                        )
                    )
                )
                null
            } catch (e: HttpException) {
                emit(
                    Resource.Error(
                        message = (e.localizedMessage ?: UiText.StringResource(
                            resId = R.string.Oops_something_went_wrong
                        )) as UiText
                    )
                )
                null
            }
            remoteIntraInfo?.let { IntraInfos ->
                dao.clearIntradayInfos(symbol)
                dao.insertIntradayInfo(
                    IntraInfos.map { it.toIntradayInfosEntity(symbol) }
                )
                emit(
                    Resource.Success(
                        data = dao.getIntradayInfo(symbol).map { it.toIntradayInfo() }
                    )
                )
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getCompanyInfo(
        fetchFromRemote: Boolean,
        symbol: String
    ): Flow<Resource<CompanyInfo>> {
        return flow {
            emit(Resource.Loading(true))
            val localInfos = dao.getCompanyInfos(symbol)
            val isDbEmpty = dao.getAnyInfo(symbol)
            val shouldJustLoadFromCache = isDbEmpty != null && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                emit(
                    Resource.Success(
                        data = localInfos.toCompanyInfo()
                    )
                )
                return@flow
            }
            val remoteInfos = try {
                api.getCompanyInfo(symbol)
            } catch (e: IOException) {
                emit(
                    Resource.Error(
                        message = UiText.StringResource(
                            resId = R.string.please_check_your_connection
                        )
                    )
                )
                null
            } catch (e: HttpException) {
                emit(
                    Resource.Error(
                        message = (e.localizedMessage ?: UiText.StringResource(
                            resId = R.string.Oops_something_went_wrong
                        )) as UiText
                    )
                )
                null
            }
            remoteInfos?.let { infos ->
                dao.clearCompanyInfos(symbol)
                dao.insertCompanyInfos(
                    infos.toCompanyInfoEntity()
                )
                emit(
                    Resource.Success(
                        data = infos.toCompanyInfo()
                    )
                )
                emit(Resource.Loading(false))
            }
        }
    }
}
