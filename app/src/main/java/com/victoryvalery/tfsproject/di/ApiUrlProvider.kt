package com.victoryvalery.tfsproject.di

import com.victoryvalery.tfsproject.data.apiStorage.BASE_URL
import javax.inject.Inject

interface ApiUrlProvider {
    var url: String

    class ApiUrlProviderImpl @Inject constructor(): ApiUrlProvider {
        override var url: String = BASE_URL
    }
}
