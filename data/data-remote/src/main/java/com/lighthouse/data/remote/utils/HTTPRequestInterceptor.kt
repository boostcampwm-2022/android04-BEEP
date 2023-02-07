package com.lighthouse.data.remote.utils

import com.lighthouse.data.remote.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

internal class HTTPRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val origin = chain.request()
        val request = origin.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "KakaoAK ${BuildConfig.kakaoSearchId}")
            .build()
        return chain.proceed(request)
    }
}
