package com.example.storyapp.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.view.StoryAdapter
import com.example.storyapp.view.DetailStoryActivity
import com.example.storyapp.view.AddStoryActivity
import com.example.storyapp.view.WelcomeActivity
import com.example.storyapp.view.SigninActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<ViewModelMain> { FactoryViewModel.getInstance(this) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingView()
        observationSession()
        observationOut()
        joinAllStory()
        observationListStory()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun observationSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun observationOut(){
        binding.bottomNavigationView.setOnItemSelectedListener {  menuItem ->
            when (menuItem.itemId){
                R.id.logoutmenu -> {
                    binding.bottomAppBar.setOnMenuItemClickListener(null)

                    AlertDialog.Builder(this).apply {
                        setTitle("Confirmation to Logout")
                        setMessage("Are you sure want to Logout?")
                        setPositiveButton("Yes") { dialog, _ ->
                            dialog.dismiss()
                            viewModel.out()
                            binding.bottomAppBar.setOnMenuItemClickListener { innerMenu ->
                                observationOut()
                                true
                            }
                            val intent = Intent(this@MainActivity, SigninActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                            binding.bottomAppBar.setOnMenuItemClickListener { innerMenu ->
                                observationOut()
                                true
                            }
                        }
                        create()
                        show()
                    }
                    true
                }
                R.id.action_home -> {
                    recreate()
                    true
                }
                else -> false
            }
        }
    }

    private fun joinAllStory() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                viewModel.getAllStory()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error: ${e.message}")
            }
        }
    }

    private fun observationListStory() {
        viewModel.listStory.observe(this) { storyList ->
            if (storyList.isNotEmpty()) {
                settingStoryAdapter(storyList)
            } else {
                showEmptyStoryToast()
                settingEmptyStoryAdapter()
            }
        }
    }

    private fun settingStoryAdapter(storyList: List<ListStoryItem>) {
        storyAdapter = StoryAdapter(storyList, object : StoryAdapter.OnAdapterListener {
            override fun onClick(story: ListStoryItem) {
                navigateToDetailStory(story)
            }
        })
        binding.rvStory.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun settingEmptyStoryAdapter() {
        storyAdapter = StoryAdapter(mutableListOf(), object : StoryAdapter.OnAdapterListener {
            override fun onClick(story: ListStoryItem) {
            }
        })
        binding.rvStory.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun showEmptyStoryToast() {
        Toast.makeText(this, ("Empty Story"), Toast.LENGTH_SHORT).show()
    }

    private fun settingView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun navigateToDetailStory(story: ListStoryItem) {
        Intent(this, DetailStoryActivity::class.java).apply {
            putExtra(DetailStoryActivity.EXTRA_PHOTO_URL, story.photoUrl)
            putExtra(DetailStoryActivity.EXTRA_CREATED_AT, story.createdAt)
            putExtra(DetailStoryActivity.EXTRA_NAME, story.name)
            putExtra(DetailStoryActivity.EXTRA_DESCRIPTION, story.description)
            putExtra(DetailStoryActivity.EXTRA_LON, story.lon ?: 0.0)
            putExtra(DetailStoryActivity.EXTRA_ID, story.id)
            putExtra(DetailStoryActivity.EXTRA_LAT, story.lat ?: 0.0)
        }.also {
            startActivity(it)
        }
    }
}
