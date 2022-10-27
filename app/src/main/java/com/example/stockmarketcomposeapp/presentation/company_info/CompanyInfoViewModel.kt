package com.example.stockmarketcomposeapp.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketcomposeapp.domain.repository.StockRepository
import com.example.stockmarketcomposeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val repository: StockRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("symbol")?.let { symbol ->
                getCompanyInfos(symbol)
                getIntradayInfos(symbol)
                refreshEvery30SecondsIfTheUserIsInCompanyInfoScreen(symbol)
            }
        }
    }

    private fun getCompanyInfos(
        symbol: String,
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            repository
                .getCompanyInfo(fetchFromRemote, symbol)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        is Resource.Success -> {
                            result.data?.let { infos ->
                                state = state.copy(
                                    company = infos
                                )
                            }
                        }
                    }
                }
        }
    }

    suspend fun getIntradayInfos(
        symbol: String,
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            repository
                .getIntraDayInfo(fetchFromRemote, symbol)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { IntraInfos ->
                                state = state.copy(
                                    stockInfos = IntraInfos
                                )
                            }
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
        }
    }

    private fun refreshEvery30SecondsIfTheUserIsInCompanyInfoScreen(symbol: String) {
        val t = Timer()
        t.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    viewModelScope.launch {
                        getCompanyInfos(symbol)
                        getIntradayInfos(symbol)
                    }
                }
            },
            30000,
            30000
        )
    }
}