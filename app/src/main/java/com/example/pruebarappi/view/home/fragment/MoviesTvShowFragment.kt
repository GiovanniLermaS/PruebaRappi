package com.example.pruebarappi.view.home.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebarappi.R
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.utils.BASE_URL_IMAGE
import com.example.pruebarappi.utils.setImageAddOrRemoveMyList
import com.example.pruebarappi.view.home.fragment.adapter.MoviesTvShowAdapter
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import com.facebook.drawee.view.SimpleDraweeView
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_movies_tv_show, container, false)

        val ivAdd = v.findViewById<ImageView>(R.id.ivAdd)

        val tvOne = v.findViewById<TextView>(R.id.tvOne)
        val rvOne = v.findViewById<RecyclerView>(R.id.rvOne)

        val tvTwo = v.findViewById<TextView>(R.id.tvTwo)
        val rvTwo = v.findViewById<RecyclerView>(R.id.rvTwo)

        val tvThree = v.findViewById<TextView>(R.id.tvThree)
        val rvThree = v.findViewById<RecyclerView>(R.id.rvThree)

        val tvFour = v.findViewById<TextView>(R.id.tvFour)
        val rvFour = v.findViewById<RecyclerView>(R.id.rvFour)

        addOrRemoveMyList(ivAdd, movieTvShow, false, R.drawable.ic_add, R.drawable.ic_check)

        //Principal image
        v.findViewById<ConstraintLayout>(R.id.clInfoPoster)
            .setOnClickListener { moviesTvShowInterface?.clickMovieTvShow(movieTvShow) }

        v.findViewById<SimpleDraweeView>(R.id.ivPrincipalImage)
            .setImageURI(Uri.parse(BASE_URL_IMAGE + movieTvShow?.backdrop_path))
        v.findViewById<TextView>(R.id.tvNameMovie).text = movieTvShow?.title
        v.findViewById<ConstraintLayout>(R.id.clAddMyList)
            .setOnClickListener {
                addOrRemoveMyList(
                    ivAdd,
                    movieTvShow,
                    true,
                    R.drawable.ic_check,
                    R.drawable.ic_add
                )
            }

        //Movies playing
        if (listMoviesNowPlaying != null) setTexViewAndRecyclerView(
            tvOne,
            rvOne,
            getString(R.string.moviesNowPlaying),
            listMoviesNowPlaying
        )

        //Movies popular
        if (listMoviesPopular != null) setTexViewAndRecyclerView(
            tvTwo,
            rvTwo,
            getString(R.string.moviesPopular),
            listMoviesPopular
        )

        //TV on air
        if (listTvAiringToday != null) setTexViewAndRecyclerView(
            tvThree,
            rvThree,
            getString(R.string.tvAiringToday),
            listTvAiringToday
        )

        //TV popular
        if (listTvPopular != null) setTexViewAndRecyclerView(
            tvFour,
            rvFour,
            getString(R.string.tvPopular),
            listTvPopular
        )
        return v
    }

    private fun setTexViewAndRecyclerView(
        textView: TextView,
        recyclerView: RecyclerView,
        text: String,
        listResultService: ArrayList<ResultService>?
    ) {
        textView.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        textView.text = text
        recyclerView.adapter = MoviesTvShowAdapter(listResultService, moviesTvShowInterface)
    }

    private fun addOrRemoveMyList(
        ivAdd: ImageView,
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
}