package com.example.pruebarappi.view.home

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebarappi.R
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.utils.*
import com.example.pruebarappi.view.detail.DetailActivity
import com.example.pruebarappi.view.home.fragment.MoviesTvShowFragment
import com.example.pruebarappi.view.home.fragment.MyListFragment
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import com.example.pruebarappi.viewmodel.HomeActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_bottom_sheet_details.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), View.OnClickListener, MoviesTvShowInterface {

    private var sheetBehavior: BottomSheetBehavior<View>? = null

    private var temporalId: Int? = null

    private var resultService: ResultService? = null

    private var moviesNowPlaying: ArrayList<ResultService>? = null

    private var moviesPopular: ArrayList<ResultService>? = null

    private var tvAiringToday: ArrayList<ResultService>? = null

    private var tvPopular: ArrayList<ResultService>? = null

    @Inject
    lateinit var appDatabase: AppDatabase

    private val mainActivityViewModel by viewModels<HomeActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        mainActivityViewModel.getResponseV(MOVIES_NOW_PLAYING, 1, { listMoviesNowPlaying ->
            consumeMoviesPopular(listMoviesNowPlaying?.results)
        }, {

        })
        sheetBehavior = BottomSheetBehavior.from(clDetailBottom)
        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btPlay -> Toast.makeText(this, "Reproducir", Toast.LENGTH_LONG).show()
            R.id.tvShows -> showByType(
                tvShows, tvMovies, tvMyList,
                isMovie = false,
                isMyList = false
            )
            R.id.tvMovies -> showByType(
                tvMovies, tvShows, tvMyList,
                isMovie = true,
                isMyList = false
            )
            R.id.tvMyList -> showByType(
                tvMyList, tvMovies, tvShows,
                isMovie = false,
                isMyList = true
            )
            R.id.ivCloseBottom -> sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            R.id.clDetailBottom -> {
                if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED)
                    sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        val intent = Intent(this@HomeActivity, DetailActivity::class.java)
                        intent.putExtra(RESULT_SERVICE, resultService)
                        this@HomeActivity.startActivity(intent)
                    }
                }, 100)
            }
        }
    }

    override fun clickMovieTvShow(resultService: ResultService?) {
        this.resultService = resultService
        if (sheetBehavior?.state == BottomSheetBehavior.STATE_HIDDEN) {
            temporalId = resultService?.id
            setDataResultService(resultService)
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        } else if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED && temporalId == resultService?.id) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        } else if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED && temporalId != resultService?.id) {
            temporalId = resultService?.id
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            setDataResultService(resultService)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }, 500)
        }
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

    private fun consumeMoviesPopular(moviesNowPlaying: ArrayList<ResultService>?) {
        mainActivityViewModel.getResponseV(MOVIES_POPULAR, 1, { listMoviesPopular ->
            consumeTvAiringToday(moviesNowPlaying, listMoviesPopular?.results)
        }, {})
    }

    private fun consumeTvAiringToday(
        moviesNowPlaying: ArrayList<ResultService>?,
        moviesPopular: ArrayList<ResultService>?
    ) {
        mainActivityViewModel.getResponseV(TV_AIRING_TODAY, 1, { listTvAiringToday ->
            consumeTvPopular(moviesNowPlaying, moviesPopular, listTvAiringToday?.results)
        }, {})
    }

    private fun consumeTvPopular(
        moviesNowPlaying: ArrayList<ResultService>?,
        moviesPopular: ArrayList<ResultService>?,
        tvAiringToday: ArrayList<ResultService>?
    ) {
        this.moviesNowPlaying = moviesNowPlaying
        this.moviesPopular = moviesPopular
        this.tvAiringToday = tvAiringToday
        mainActivityViewModel.getResponseV(TV_POPULAR, 1, { listTvPopular ->
            this.tvPopular = listTvPopular?.results
            showFragment(
                moviesNowPlaying!![0],
                moviesNowPlaying,
                moviesPopular,
                tvAiringToday,
                listTvPopular?.results
            )
        }, {})
    }

    private fun setDataResultService(resultService: ResultService?) {
        ivImageBottom.setImageURI(Uri.parse(BASE_URL_IMAGE + resultService?.poster_path))
        tvTitleBottom.text = resultService?.title
        tvDescriptionBottom.text = resultService?.overview
    }

    private fun showByType(
        tvAnimation: TextView?,
        tvShowHide1: TextView?,
        tvShowHide2: TextView?,
        isMovie: Boolean?,
        isMyList: Boolean?
    ) {
        if (tvShowHide1?.visibility == View.VISIBLE && tvShowHide2?.visibility == View.VISIBLE) {
            tvShowHide1.visibility = View.INVISIBLE
            tvShowHide2.visibility = View.INVISIBLE
            animationTextView(tvAnimation!!, -tvAnimation.x + 250f)
            when {
                isMovie!! -> showFragment(
                    moviesNowPlaying!![0],
                    moviesNowPlaying,
                    moviesPopular,
                    null,
                    null
                )
                isMyList!! -> {
                    val ft = supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.fg1,
                        MyListFragment(this)
                    )
                    ft.commit()
                }
                else -> showFragment(tvAiringToday!![0], null, null, tvAiringToday, tvPopular)
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
                tvPopular
            )
        }
    }

    private fun showFragment(
        resultService: ResultService?,
        listMoviesNowPlaying: ArrayList<ResultService>?,
        listMoviesPopular: ArrayList<ResultService>?,
        listTvAiringToday: ArrayList<ResultService>?,
        listTvPopular: ArrayList<ResultService>?
    ) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(
            R.id.fg1,
            MoviesTvShowFragment(
                resultService,
                listMoviesNowPlaying,
                listMoviesPopular,
                listTvAiringToday,
                listTvPopular,
                this
            )
        )
        ft.commit()
    }

    override fun onBackPressed() {
        when {
            tvShows.visibility == View.VISIBLE -> showByType(
                tvShows, tvMovies, tvMyList,
                isMovie = false,
                isMyList = false
            )
            tvMovies.visibility == View.VISIBLE -> showByType(
                tvMovies, tvShows, tvMyList,
                isMovie = false,
                isMyList = false
            )
            tvMyList.visibility == View.VISIBLE -> showByType(
                tvMyList, tvShows, tvMovies,
                isMovie = false,
                isMyList = false
            )
            else -> super.onBackPressed()
        }
    }
}