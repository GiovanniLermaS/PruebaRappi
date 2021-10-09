package com.example.pruebarappi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pruebarappi.db.model.ResponseService
import com.example.pruebarappi.repository.IHomeActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(private val homeActivityRepository: IHomeActivityRepository) :
    ViewModel() {

    fun getResponseV(
        url: String,
        page: Int,
        response: (ResponseService?) -> Unit,
        error: (String?) -> Unit
    ) {
        homeActivityRepository.getResponseR(url, page, { response(it) }, { error(it) })
    }
}