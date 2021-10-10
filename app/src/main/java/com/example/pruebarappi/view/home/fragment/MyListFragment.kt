package com.example.pruebarappi.view.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebarappi.R
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.view.home.fragment.adapter.MoviesTvShowAdapter
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import kotlinx.coroutines.launch

class MyListFragment(
    private val moviesTvShowInterface: MoviesTvShowInterface?, private val appDatabase: AppDatabase
) : Fragment() {

    private var listResultService: List<ResultService>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_my_list, container, false)
        lifecycleScope.launch {
            listResultService = appDatabase.resultServiceDao().getResultService()
            if (listResultService != null && listResultService!!.isNotEmpty()) {
                v.findViewById<RecyclerView>(R.id.tvMyList).adapter = MoviesTvShowAdapter(
                    listResultService as ArrayList<ResultService>,
                    moviesTvShowInterface
                )
            }
        }
        return v
    }
}