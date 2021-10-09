package com.example.pruebarappi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pruebarappi.db.model.ResponseService
import com.example.pruebarappi.repository.IHomeActivityRepository
import com.example.pruebarappi.utils.MOVIES_NOW_PLAYING
import com.example.pruebarappi.utils.MOVIES_POPULAR
import com.example.pruebarappi.utils.TV_AIRING_TODAY
import com.example.pruebarappi.utils.TV_POPULAR
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(private val homeActivityRepository: IHomeActivityRepository) :
    ViewModel() {

    fun getResponseV(url:String, page: Int, response: (ResponseService?) -> Unit, error: (String?) -> Unit) {
        homeActivityRepository.getResponseR(MOVIES_NOW_PLAYING, page, {}, {})
    }

    fun getMoviesPopular(page: Int) {
        homeActivityRepository.getResponseR(MOVIES_POPULAR, page, {}, {})
    }

    fun getTvAiringToday(page: Int) {
        homeActivityRepository.getResponseR(TV_AIRING_TODAY, page, {}, {})
    }

    fun getTvPopular(page: Int) {
        homeActivityRepository.getResponseR(TV_POPULAR, page, {}, {})
    }
}