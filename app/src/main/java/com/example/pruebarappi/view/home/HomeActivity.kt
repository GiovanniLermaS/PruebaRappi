package com.example.pruebarappi.view.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebarappi.databinding.ActivityHomeBinding
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.utils.*
import com.example.pruebarappi.view.detail.DetailActivity
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import com.example.pruebarappi.viewmodel.HomeActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), MoviesTvShowInterface {

    private var sheetBehavior: BottomSheetBehavior<View>? = null

    private var resultService: ResultService? = null

    private var binding: ActivityHomeBinding? = null

    @Inject
    lateinit var appDatabase: AppDatabase

    private val homeActivityViewModel by viewModels<HomeActivityViewModel>()

    private fun consumeMoviesNowPlaying() {
        homeActivityViewModel.getResponseV(
            MOVIES_NOW_PLAYING,
            1,
            { listMoviesNowPlaying ->
                consumeMoviesPopular(listMoviesNowPlaying?.results)
            },
            { error ->
                Snackbar.make(binding?.coordinatorLayout!!, "$error", Snackbar.LENGTH_SHORT).show()
            })
    }

    private fun consumeMoviesPopular(moviesNowPlaying: ArrayList<ResultService>?) {
        homeActivityViewModel.getResponseV(
            MOVIES_POPULAR,
            1,
            { listMoviesPopular ->
                consumeTvAiringToday(moviesNowPlaying, listMoviesPopular?.results)
            },
            { error ->
                Snackbar.make(binding?.coordinatorLayout!!, "$error", Snackbar.LENGTH_SHORT).show()
            })
    }

    private fun consumeTvAiringToday(
        moviesNowPlaying: ArrayList<ResultService>?,
        moviesPopular: ArrayList<ResultService>?
    ) {
        homeActivityViewModel.getResponseV(
            TV_AIRING_TODAY,
            1,
            { listTvAiringToday ->
                consumeTvPopular(moviesNowPlaying, moviesPopular, listTvAiringToday?.results)
            },
            { error ->
                Snackbar.make(binding?.coordinatorLayout!!, "$error", Snackbar.LENGTH_SHORT).show()
            })
    }

    private fun consumeTvPopular(
        moviesNowPlaying: ArrayList<ResultService>?,
        moviesPopular: ArrayList<ResultService>?,
        tvAiringToday: ArrayList<ResultService>?
    ) {
        homeActivityViewModel.getResponseV(
            TV_POPULAR,
            1,
            { listTvPopular ->
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
            },
            { error ->
                Snackbar.make(binding?.coordinatorLayout!!, "$error", Snackbar.LENGTH_SHORT).show()
            })
    }

    fun setDataResultService(resultService: ResultService?) {
        binding?.clBottomSheet?.ivImageBottom?.setImageURI(Uri.parse(BASE_URL_IMAGE + resultService?.poster_path))
        binding?.clBottomSheet?.tvTitleBottom?.text = resultService?.title
        binding?.clBottomSheet?.tvDescriptionBottom?.text = resultService?.overview
    }

    fun onClick(v: View?) {
        when (v) {
            binding?.clToolbar?.tvShows -> homeActivityViewModel.showByType(
                binding?.clToolbar?.tvShows,
                binding?.clToolbar?.tvMovies,
                binding?.clToolbar?.tvMyList,
                isMovie = false,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            binding?.clToolbar?.tvMovies -> homeActivityViewModel.showByType(
                binding?.clToolbar?.tvMovies,
                binding?.clToolbar?.tvShows,
                binding?.clToolbar?.tvMyList,
                isMovie = true,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            binding?.clToolbar?.tvMyList -> homeActivityViewModel.showByType(
                binding?.clToolbar?.tvMyList,
                binding?.clToolbar?.tvMovies,
                binding?.clToolbar?.tvShows,
                isMovie = false,
                isMyList = true,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            binding?.clBottomSheet?.ivCloseBottom -> sheetBehavior?.state =
                BottomSheetBehavior.STATE_HIDDEN
            binding?.clBottomSheet?.clDetailBottom -> {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
        consumeMoviesNowPlaying()
        sheetBehavior = BottomSheetBehavior.from(binding?.clBottomSheet?.clDetailBottom!!)
        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        binding?.clToolbar?.tvShows?.setOnClickListener { onClick(binding?.clToolbar?.tvShows) }
        binding?.clToolbar?.tvMovies?.setOnClickListener { onClick(binding?.clToolbar?.tvMovies) }
        binding?.clToolbar?.tvMyList?.setOnClickListener { onClick(binding?.clToolbar?.tvMyList) }
        binding?.clBottomSheet?.ivCloseBottom?.setOnClickListener { onClick(binding?.clBottomSheet?.ivCloseBottom) }
        binding?.clBottomSheet?.clDetailBottom?.setOnClickListener { onClick(binding?.clBottomSheet?.clDetailBottom) }
    }

    override fun clickMovieTvShow(resultService: ResultService?) {
        this.resultService = resultService
        homeActivityViewModel.clickMovieTvShow(sheetBehavior, resultService, this)
    }

    override fun onBackPressed() {
        when {
            binding?.clToolbar?.tvShows?.visibility == View.VISIBLE -> homeActivityViewModel.showByType(
                binding?.clToolbar?.tvShows,
                binding?.clToolbar?.tvMovies,
                binding?.clToolbar?.tvMyList,
                isMovie = false,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            binding?.clToolbar?.tvMovies?.visibility == View.VISIBLE -> homeActivityViewModel.showByType(
                binding?.clToolbar?.tvMovies,
                binding?.clToolbar?.tvShows,
                binding?.clToolbar?.tvMyList,
                isMovie = false,
                isMyList = false,
                supportFragmentManager.beginTransaction(),
                appDatabase,
                this
            )
            binding?.clToolbar?.tvMyList?.visibility == View.VISIBLE -> homeActivityViewModel.showByType(
                binding?.clToolbar?.tvMyList,
                binding?.clToolbar?.tvShows,
                binding?.clToolbar?.tvMovies,
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