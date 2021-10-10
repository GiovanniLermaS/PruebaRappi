package com.example.pruebarappi.view.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pruebarappi.databinding.FragmentMyListBinding
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.view.home.fragment.adapter.MoviesTvShowAdapter
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import kotlinx.coroutines.launch

class MyListFragment(
    private val moviesTvShowInterface: MoviesTvShowInterface?, private val appDatabase: AppDatabase?
) : Fragment() {

    private var listResultService: List<ResultService>? = null

    private var binding: FragmentMyListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyListBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            listResultService = appDatabase?.resultServiceDao()?.getResultService()
            if (listResultService != null && listResultService!!.isNotEmpty()) {
                binding?.tvMyList?.adapter = MoviesTvShowAdapter(
                    listResultService as ArrayList<ResultService>,
                    moviesTvShowInterface
                )
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}