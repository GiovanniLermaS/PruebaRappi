package com.example.pruebarappi.view.detail

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pruebarappi.R
import com.example.pruebarappi.databinding.ActivityDetailBinding
import com.example.pruebarappi.db.AppDatabase
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.utils.BASE_URL_IMAGE
import com.example.pruebarappi.utils.RESULT_SERVICE
import com.example.pruebarappi.utils.setImageAddOrRemoveMyList
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.imagepipeline.request.Postprocessor
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.fresco.processors.BlurPostprocessor
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private val resultService by lazy { intent.getSerializableExtra(RESULT_SERVICE) as ResultService }

    private var binding: ActivityDetailBinding? = null

    @Inject
    lateinit var appDatabase: AppDatabase

    private fun showDetailMovie() {
        if (resultService.poster_path != null) {
            val postprocessor: Postprocessor = BlurPostprocessor(this, 50)
            val imageRequest =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(BASE_URL_IMAGE + resultService.poster_path))
                    .setPostprocessor(postprocessor)
                    .build()
            val controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(binding?.ivBackgroundDetail?.controller)
                .build() as PipelineDraweeController
            binding?.ivBackgroundDetail?.controller = controller
            binding?.ivImageDetail?.setImageURI(Uri.parse(BASE_URL_IMAGE + resultService.poster_path))
            binding?.tvDescriptionDetail?.text = resultService.overview
            addOrRemoveMyList(
                binding?.ivAdd,
                false,
                R.drawable.ic_add,
                R.drawable.ic_check,
            )
        }
    }

    private fun addOrRemoveMyList(
        ivAdd: ImageView?,
        isSetRoom: Boolean,
        image1: Int,
        image2: Int
    ) {
        val resultServiceDao = appDatabase.resultServiceDao()
        lifecycleScope.launch {
            val isResultService = setImageAddOrRemoveMyList(
                resultServiceDao.getResultServiceById(resultService.id!!),
                ivAdd,
                resources,
                image1,
                image2
            )
            if (isSetRoom)
                if (isResultService) resultServiceDao.setResultService(resultService)
                else resultServiceDao.deleteResultServiceById(resultService.id!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        showDetailMovie()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivAdd -> addOrRemoveMyList(
                binding?.ivAdd,
                true,
                R.drawable.ic_check,
                R.drawable.ic_add
            )
        }
    }
}