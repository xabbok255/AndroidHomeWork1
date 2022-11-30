package ru.netology.nmedia.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.adapter.OnPostInteractionListenerImpl
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.data.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.hideKeyboard
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: PostViewModel by lazy {
        ViewModelProvider(
            this, PostViewModel.Factory(PostRepositoryInMemoryImpl())
        ).get(PostViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribe()
        setupListeners()
        binding.editOrigGroup.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
            val content = binding.postContent.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(this, "Введите текст поста!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addOrEditPost(
                Post(
                    author = "me",
                    content = content,
                    published = Date().toString()
                )
            )
        }

        binding.cancelEditingButton.setOnClickListener {
            viewModel.cancelEditPost()
        }
    }

    private fun subscribe() {
        val adapter = PostsAdapter(OnPostInteractionListenerImpl(viewModel))
        binding.postList.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) {
            if (it == null) {
                binding.postContent.setText("")
                binding.editingOrigContent.text = ""
                binding.editOrigGroup.visibility = View.GONE
                this.hideKeyboard()
                binding.postContent.clearFocus()
            } else {
                binding.postContent.setText(it.content)
                binding.postContent.requestFocus()

                binding.editingOrigContent.text = it.content
                binding.editOrigGroup.visibility = View.VISIBLE
            }
        }
    }
}