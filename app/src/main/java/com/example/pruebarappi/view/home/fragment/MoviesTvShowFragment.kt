package com.example.pruebarappi.view.home.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebarappi.R
import com.example.pruebarappi.databinding.FragmentMoviesTvShowBinding
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.utils.BASE_URL_IMAGE
import com.example.pruebarappi.utils.setImageAddOrRemoveMyList
import com.example.pruebarappi.view.home.fragment.adapter.MoviesTvShowAdapter
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MoviesTvShowFragment(
    private val movieTvShow: ResultService?,
    private val listMoviesNowPlaying: ArrayList<ResultService>?,
    private val listMoviesPopular: ArrayList<ResultService>?,
    private val listTvAiringToday: ArrayList<ResultService>?,
    private val listTvPopular: ArrayList<ResultService>?,
    private val appDatabase: AppDatabase?,
    private val moviesTvShowInterface: MoviesTvShowInterface?
) : Fragment() {

    private var binding: FragmentMoviesTvShowBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoviesTvShowBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addOrRemoveMyList(
            binding?.clPrincipalImageFragment?.ivAdd,
            movieTvShow,
            false,
            R.drawable.ic_add,
            R.drawable.ic_check
        )

        //Principal image
        binding?.clPrincipalImageFragment?.clInfoPoster?.setOnClickListener {
            moviesTvShowInterface?.clickMovieTvShow(
                movieTvShow
            )
        }
        binding?.clPrincipalImageFragment?.ivPrincipalImage?.setImageURI(Uri.parse(BASE_URL_IMAGE + movieTvShow?.backdrop_path))
        binding?.clPrincipalImageFragment?.tvNameMovie?.text = movieTvShow?.title
        binding?.clPrincipalImageFragment?.clAddMyList?.setOnClickListener {
            addOrRemoveMyList(
                binding?.clPrincipalImageFragment?.ivAdd,
                movieTvShow,
                true,
                R.drawable.ic_check,
                R.drawable.ic_add
            )
        }

        //Movies playing
        if (listMoviesNowPlaying != null) setTexViewAndRecyclerView(
            binding?.tvOne,
            binding?.rvOne,
            getString(R.string.moviesNowPlaying),
            listMoviesNowPlaying
        )

        //Movies popular
        if (listMoviesPopular != null) setTexViewAndRecyclerView(
            binding?.tvTwo,
            binding?.rvTwo,
            getString(R.string.moviesPopular),
            listMoviesPopular
        )

        //TV on air
        if (listTvAiringToday != null) setTexViewAndRecyclerView(
            binding?.tvThree,
            binding?.rvThree,
            getString(R.string.tvAiringToday),
            listTvAiringToday
        )

        //TV popular
        if (listTvPopular != null) setTexViewAndRecyclerView(
            binding?.tvFour,
            binding?.rvFour,
            getString(R.string.tvPopular),
            listTvPopular
        )
        binding?.clPrincipalImageFragment?.btPlay?.setOnClickListener {
            Snackbar.make(
                binding?.clFragmentMovies!!,
                "Play any movie",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setTexViewAndRecyclerView(
        textView: TextView?,
        recyclerView: RecyclerView?,
        text: String,
        listResultService: ArrayList<ResultService>?
    ) {
        textView?.visibility = View.VISIBLE
        recyclerView?.visibility = View.VISIBLE
        textView?.text = text
        recyclerView?.adapter = MoviesTvShowAdapter(listResultService, moviesTvShowInterface)
    }

    private fun addOrRemoveMyList(
        ivAdd: ImageView?,
        resultService: ResultService?,
        isSetRoom: Boolean,
        image1: Int,
        image2: Int
    ) {
        val resultServiceDao = appDatabase?.resultServiceDao()
        lifecycleScope.launch {
            val isResultService = setImageAddOrRemoveMyList(
                resultServiceDao?.getResultServiceById(resultService?.id!!),
                ivAdd,
                resources,
                image1,
                image2
            )
            if (isSetRoom)
                if (isResultService) resultServiceDao?.setResultService(resultService!!)
                else resultServiceDao?.deleteResultServiceById(resultService?.id!!)
        }
    }

    private fun onClick(view: View?) {

    }
}