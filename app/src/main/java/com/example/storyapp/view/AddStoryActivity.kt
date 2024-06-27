package com.example.storyapp.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.api.APISettings
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.main.MainActivity
import com.example.storyapp.main.getImageUri
import com.example.storyapp.main.reduceFileImage
import com.example.storyapp.main.uriToFile
import com.example.storyapp.response.AddStoryGuestRespon
import com.example.storyapp.user.PreferensiUser
import com.example.storyapp.user.dataStore
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class AddStoryActivity : AppCompatActivity() {


    private lateinit var userPref: PreferensiUser

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPref = PreferensiUser.getInstance(dataStore)
        setupUI()
        setupWindowInsets()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupUI() {
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.sendStory.setOnClickListener { uploadImage() }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom)
            insets
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launchIntentCamera.launch(currentImageUri!!)
    }

    private val launchIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) showImage()
    }

    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launchGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            showImage()
        } ?: Log.d("Photo Picker", "No Media Selected")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadImage() {
        val description = binding.descText.text.toString()
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

            lifecycleScope.launch {
                userPref.getSession().collect { user ->
                    val token = user.token
                    if (token.isNotEmpty()) {
                        uploadStory(token, multipartBody, requestBody)
                    } else {
                        showToast("False token")
                        showLoading(false)
                    }
                }
            }
        } ?: showToast("Empty Image")
    }

    private suspend fun uploadStory(token: String, multipartBody: MultipartBody.Part, requestBody: RequestBody) {
        try {
            val apiService = APISettings.getApiService(token)
            val successResponse = apiService.uploadStory("Bearer $token", multipartBody, requestBody)
            showToast(successResponse.message)
           showSuccessDialog()
        } catch (e: retrofit2.HttpException) {
            resolveErrors(e)
        } finally {
           showLoading(false)
        }
    }

    private fun resolveErrors(e: retrofit2.HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        val errorResponse = Gson().fromJson(errorBody, AddStoryGuestRespon::class.java)
        showToast(errorResponse.message)
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Success!")
            setMessage("Content uploaded!")
            setPositiveButton("Next") { _, _ ->
                val intent = Intent(this@AddStoryActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ImageResult.setImageURI(it)
        }
    }
}
