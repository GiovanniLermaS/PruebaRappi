package com.example.pruebarappi.view.home

import com.example.pruebarappi.repository.HomeActivityRepository
import com.example.pruebarappi.repository.IHomeActivityRepository
import com.example.pruebarappi.retrofit.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HomeModule {

    @Provides
    fun provideAboutRepository(apiInterface: ApiInterface): IHomeActivityRepository {
        return HomeActivityRepository(apiInterface)
    }
}