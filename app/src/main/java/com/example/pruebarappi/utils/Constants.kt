package com.example.pruebarappi.utils

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.example.pruebarappi.db.model.ResultService

const val BASE_URL = "https://api.themoviedb.org/3/"
const val BASE_URL_IMAGE = "https://image.tmdb.org/t/p/original"
const val API_KEY = "a85162e941d8786f851eae457b3f6761"
const val LANGUAGE = "es-ES"
const val MOVIES_NOW_PLAYING = "movie/now_playing?api_key=$API_KEY&language=$LANGUAGE"
const val MOVIES_POPULAR = "movie/popular?api_key=$API_KEY&language=$LANGUAGE"
const val TV_AIRING_TODAY = "tv/airing_today?api_key=$API_KEY&language=$LANGUAGE\""
const val TV_POPULAR = "tv/popular?api_key=$API_KEY&language=$LANGUAGE\""
const val RESULT_SERVICE = "resultService"

fun hasNetwork(context: Context): Boolean? {
    var isConnected: Boolean? = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}

fun setImageAddOrRemoveMyList(
    resultService: ResultService?,
    ivAddMyList: ImageView,
    resources: Resources,
    image1: Int,
    image2: Int
): Boolean {
    if (resultService == null) {
        ivAddMyList.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                image1,
                null
            )
        )
        return true
    } else {
        ivAddMyList.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                image2,
                null
            )
        )
        return false
    }
}