package com.example.pruebarappi.repository

import com.example.pruebarappi.db.model.ResponseService

interface IHomeActivityRepository {

    fun getResponseR(
        url: String, page: Int, response: (ResponseService?) -> Unit, error: (String?) -> Unit
    )
}