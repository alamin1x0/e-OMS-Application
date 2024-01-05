package com.alamin1x0.e_oms.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {

    fun getInstance(): Retrofit{
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()

                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level =HttpLoggingInterceptor.Level.BODY
                    }
                )
                .build()
            )
            .baseUrl("https://apex.oracle.com/pls/apex/order_management/oms/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
