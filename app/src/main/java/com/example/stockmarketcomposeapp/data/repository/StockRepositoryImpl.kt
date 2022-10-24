package com.example.stockmarketcomposeapp.data.repository

import com.example.stockmarketcomposeapp.R
import com.example.stockmarketcomposeapp.data.csv.CSVParser
import com.example.stockmarketcomposeapp.data.local.StockDatabase
import com.example.stockmarketcomposeapp.data.mapper.toCompanyListing
import com.example.stockmarketcomposeapp.data.mapper.toCompanyListingEntity
import com.example.stockmarketcomposeapp.data.remote.StockApi
import com.example.stockmarketcomposeapp.domain.model.CompanyListing
import com.example.stockmarketcomposeapp.domain.repository.StockRepository
import com.example.stockmarketcomposeapp.util.Resource
import com.example.stockmarketcomposeapp.util.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>

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
            if(shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            }  catch (e: IOException) {
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
}