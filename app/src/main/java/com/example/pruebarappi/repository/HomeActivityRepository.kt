package com.example.pruebarappi.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.pruebarappi.db.model.ResponseService
import com.example.pruebarappi.retrofit.ApiInterface
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Call
import retrofit2.Callback

@ActivityRetainedScoped
class HomeActivityRepository(private val apiInterface: ApiInterface) : IHomeActivityRepository {
    override fun getResponseR(
        url: String,
        page: Int,
        response: (ResponseService?) -> Unit,
        error: (String?) -> Unit
    ) {
        val serviceResponse = MutableLiveData<ResponseService>()
        val call = apiInterface.getNowPlayingMovies(url, page)
        call.enqueue(object : Callback<ResponseService> {
            override fun onFailure(
                call: Call<ResponseService>,
                t: Throwable
            ) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ResponseService>,
                responseService: retrofit2.Response<ResponseService>
            ) {
                serviceResponse.value =
                    responseService.body() as ResponseService
            }
        })
    }
}