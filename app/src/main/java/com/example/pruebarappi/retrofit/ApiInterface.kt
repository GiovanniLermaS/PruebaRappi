package com.example.pruebarappi.retrofit

import com.example.pruebarappi.db.model.ResponseService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiInterface {

    @GET
    fun getNowPlayingMovies(@Url url: String, @Query("page") page: Int): Call<ResponseService>
}