package com.example.stockmarketcomposeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity
data class IntradayInfoEntity(
    val date: LocalDateTime,
    val close: Double,
    val symbol: String,
    @PrimaryKey val id: Int? = null
)
