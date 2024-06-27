package com.example.storyapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Detail of the Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL)
        val createdAt = intent.getStringExtra(EXTRA_CREATED_AT)
        val name = intent.getStringExtra(EXTRA_NAME)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val lon = intent.getDoubleExtra(EXTRA_LON, 0.0)
        val id = intent.getStringExtra(EXTRA_ID)
        val lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)

        detilView(photoUrl, createdAt, name, description, lon, id, lat)
    }

    private fun detilView(
        photoUrl: String?, createdAt: String?, name: String?,
        description: String?, lon: Double, id: String?, lat: Double
    ) {
        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.profileImage)
        }

        binding.nameText.text = name ?: "No Name"
        binding.dateTextView.text = createdAt ?: "No Date"
        binding.descriptionText.text = description ?: "No Description"
    }

    companion object {
        const val EXTRA_PHOTO_URL = "EXTRA_PHOTO_URL"
        const val EXTRA_CREATED_AT = "EXTRA_CREATED_AT"
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_LON = "EXTRA_LON"
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_LAT = "EXTRA_LAT"
    }
}
