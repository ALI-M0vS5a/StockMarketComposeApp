package com.example.stockmarketcomposeapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.stockmarketcomposeapp.data.local.IntradayInfoEntity
import com.example.stockmarketcomposeapp.data.remote.dto.IntradayInfoDto
import com.example.stockmarketcomposeapp.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp,formatter)
    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}

fun IntradayInfoEntity.toIntradayInfo(): IntradayInfo {
    return IntradayInfo(
        date = date,
        close = close
    )
}

fun IntradayInfo.toIntradayInfosEntity(symbol: String): IntradayInfoEntity {
    return IntradayInfoEntity(
        date = date,
        close = close,
        symbol = symbol
    )
}