package com.example.pruebarappi.viewmodel

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import com.example.pruebarappi.databinding.ActivityHomeBinding
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResponseService
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.repository.IHomeActivityRepository
import com.example.pruebarappi.view.home.HomeActivity
import com.example.pruebarappi.view.home.fragment.MoviesTvShowFragment
import com.example.pruebarappi.view.home.fragment.MyListFragment
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(private val homeActivityRepository: IHomeActivityRepository) :
    ViewModel() {

    private var moviesNowPlaying: ArrayList<ResultService>? = null

    private var moviesPopular: ArrayList<ResultService>? = null

    private var tvAiringToday: ArrayList<ResultService>? = null

    private var tvPopular: ArrayList<ResultService>? = null

    private var temporalId: Int? = null

    private var appDatabase: AppDatabase? = null

    private var binding: ActivityHomeBinding? = null

    private var moviesTvShowInterface: MoviesTvShowInterface? = null

    private var params: CoordinatorLayout.LayoutParams? = null

    fun setInitialParams(
        appDatabase: AppDatabase,
        binding: ActivityHomeBinding?,
        moviesTvShowInterface: MoviesTvShowInterface
    ) {
        this.appDatabase = appDatabase
        this.binding = binding
        this.moviesTvShowInterface = moviesTvShowInterface

        this.params = CoordinatorLayout.LayoutParams(
            this.binding?.fg1?.layoutParams?.width!!,
            this.binding?.fg1?.layoutParams?.height!!
        )

    }

    fun getResponseV(
        url: String,
        page: Int,
        response: (ResponseService?) -> Unit,
        error: (String?) -> Unit
    ) {
        homeActivityRepository.getResponseR(url, page, { response(it) }, { error(it) })
    }

    private fun animationTextView(
        view: View,
        translateX: Float
    ) {
        ObjectAnimator.ofFloat(view, "translationX", translateX).apply {
            duration = 700
            start()
        }
    }

    fun showFragment(
        resultService: ResultService?,
        listMoviesNowPlaying: ArrayList<ResultService>?,
        listMoviesPopular: ArrayList<ResultService>?,
        listTvAiringToday: ArrayList<ResultService>?,
        listTvPopular: ArrayList<ResultService>?,
        context: Context
    ) {
        if (listMoviesNowPlaying != null)
            this.moviesNowPlaying = listMoviesNowPlaying
        if (listMoviesPopular != null)
            this.moviesPopular = listMoviesPopular
        if (listTvAiringToday != null)
            this.tvAiringToday = listTvAiringToday
        if (listTvPopular != null)
            this.tvPopular = listTvPopular
        params?.setMargins(0, 0, 0, 100)
        binding?.fg1?.layoutParams = params
        val ft = (context as HomeActivity).supportFragmentManager.beginTransaction()
        ft.replace(
            binding?.fg1?.id!!,
            MoviesTvShowFragment(
                resultService,
                listMoviesNowPlaying,
                listMoviesPopular,
                listTvAiringToday,
                listTvPopular,
                appDatabase,
                moviesTvShowInterface
            )
        )
        ft.commit()
    }

    fun showByType(
        tvAnimation: TextView?,
        tvShowHide1: TextView?,
        tvShowHide2: TextView?,
        isMovie: Boolean,
        isMyList: Boolean,
        context: Context
    ) {
        if (tvShowHide1?.visibility == View.VISIBLE && tvShowHide2?.visibility == View.VISIBLE) {
            tvShowHide1.visibility = View.INVISIBLE
            tvShowHide2.visibility = View.INVISIBLE
            animationTextView(tvAnimation!!, -tvAnimation.x + 250f)
            when {
                isMovie -> {
                    showFragment(
                        moviesNowPlaying!![0],
                        moviesNowPlaying,
                        moviesPopular,
                        null,
                        null,
                        context
                    )
                }
                isMyList -> {
                    params?.setMargins(0, 300, 0, 0)
                    binding?.fg1?.layoutParams = params
                    val ft = (context as HomeActivity).supportFragmentManager.beginTransaction()
                    ft.replace(
                        binding?.fg1?.id!!,
                        MyListFragment(moviesTvShowInterface, appDatabase),
                    )
                    ft.commit()
                }
                else -> {
                    showFragment(
                        tvAiringToday!![0], null, null, tvAiringToday, tvPopular, context
                    )
                }
            }
        } else {
            animationTextView(tvAnimation!!, -tvAnimation.x + 250f)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    tvShowHide1?.visibility = View.VISIBLE
                    tvShowHide2?.visibility = View.VISIBLE
                }
            }, 1000)
            showFragment(
                moviesNowPlaying!![0],
                moviesNowPlaying,
                moviesPopular,
                tvAiringToday,
                tvPopular,
                context
            )
        }
    }

    fun clickMovieTvShow(
        sheetBehavior: BottomSheetBehavior<View>?,
        resultService: ResultService?,
        context: Context
    ) {
        if (sheetBehavior?.state == BottomSheetBehavior.STATE_HIDDEN) {
            temporalId = resultService?.id
            (context as HomeActivity).setDataResultService(resultService)
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED && temporalId == resultService?.id) {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED && temporalId != resultService?.id) {
            temporalId = resultService?.id
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            (context as HomeActivity).setDataResultService(resultService)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }, 500)
        }
    }
}