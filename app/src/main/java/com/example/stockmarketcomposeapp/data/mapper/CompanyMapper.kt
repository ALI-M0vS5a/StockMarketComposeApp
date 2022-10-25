package com.example.stockmarketcomposeapp.data.mapper

import com.example.stockmarketcomposeapp.data.local.CompanyListingEntity
import com.example.stockmarketcomposeapp.data.remote.dto.CompanyInfoDto
import com.example.stockmarketcomposeapp.domain.model.CompanyInfo
import com.example.stockmarketcomposeapp.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}