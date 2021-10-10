package com.example.pruebarappi.view.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebarappi.R
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.utils.*
import com.example.pruebarappi.view.detail.DetailActivity
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import com.example.pruebarappi.viewmodel.HomeActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_bottom_sheet_details.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), View.OnClickListener, MoviesTvShowInterface {

    private var sheetBehavior: BottomSheetBehavior<View>? = null

    private var resultService: ResultService? = null

    @Inject
    lateinit var appDatabase: AppDatabase

    private val homeActivityViewModel by viewModels<HomeActivityViewModel>()

    private fun consumeMoviesNowPlaying() {
        homeActivityViewModel.getResponseV(MOVIES_NOW_PLAYING, 1, { listMoviesNowPlaying ->
            consumeMoviesPopular(listMoviesNowPlaying?.results)
        }, { error -> Snackbar.make(coordinatorLayout, "$error", Snackbar.LENGTH_SHORT).show() })
    }

    private fun consumeMoviesPopular(moviesNowPlaying: ArrayList<ResultService>?) {
        homeActivityViewModel.getResponseV(MOVIES_POPULAR, 1, { listMoviesPopular ->
            consumeTvAiringToday(moviesNowPlaying, listMoviesPopular?.results)
        }, { error -> Snackbar.make(coordinatorLayout, "$error", Snackbar.LENGTH_SHORT).show() })
    }

    private fun consumeTvAiringToday(
        moviesNowPlaying: ArrayList<ResultService>?,
        moviesPopular: ArrayList<ResultService>?
    ) {
        homeActivityViewModel.getResponseV(TV_AIRING_TODAY, 1, { listTvAiringToday ->
            consumeTvPopular(moviesNowPlaying, moviesPopular, listTvAiringToday?.results)
        }, { error -> Snackbar.make(coordinatorLayout, "$error", Snackbar.LENGTH_SHORT).show() })
    }

    private fun consumeTvPopular(
        moviesNowPlaying: ArrayList<ResultService>?,
        moviesPopular: ArrayList<ResultService>?,
        tvAiringToday: ArrayList<ResultService>?
    ) {
        homeActivityViewModel.getResponseV(TV_POPULAR, 1, { listTvPopular ->
            homeActivityViewModel.showFragment(
                moviesNowPlaying!![0],
                moviesNowPlaying,
                moviesPopular,
                tvAiringToday,
                listTvPopular?.results,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
        }, { error -> Snackbar.make(coordinatorLayout, "$error", Snackbar.LENGTH_SHORT).show() })
    }

    fun setDataResultService(resultService: ResultService?) {
        ivImageBottom.setImageURI(Uri.parse(BASE_URL_IMAGE + resultService?.poster_path))
        tvTitleBottom.text = resultService?.title
        tvDescriptionBottom.text = resultService?.overview
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        consumeMoviesNowPlaying()
        sheetBehavior = BottomSheetBehavior.from(clDetailBottom)
        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btPlay -> Snackbar.make(coordinatorLayout, "Play any movie", Snackbar.LENGTH_SHORT)
                .show()
            R.id.tvShows -> homeActivityViewModel.showByType(
                tvShows, tvMovies, tvMyList,
                isMovie = false,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            R.id.tvMovies -> homeActivityViewModel.showByType(
                tvMovies, tvShows, tvMyList,
                isMovie = true,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            R.id.tvMyList -> homeActivityViewModel.showByType(
                tvMyList, tvMovies, tvShows,
                isMovie = false,
                isMyList = true,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
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
        homeActivityViewModel.clickMovieTvShow(sheetBehavior, resultService, this)
    }

    override fun onBackPressed() {
        when {
            tvShows.visibility == View.VISIBLE -> homeActivityViewModel.showByType(
                tvShows, tvMovies, tvMyList,
                isMovie = false,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            tvMovies.visibility == View.VISIBLE -> homeActivityViewModel.showByType(
                tvMovies, tvShows, tvMyList,
                isMovie = false,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            tvMyList.visibility == View.VISIBLE -> homeActivityViewModel.showByType(
                tvMyList, tvShows, tvMovies,
                isMovie = false,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            else -> super.onBackPressed()
        }
    }
}