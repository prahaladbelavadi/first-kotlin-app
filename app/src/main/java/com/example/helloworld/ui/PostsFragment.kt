package com.example.helloworld.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.data.ApiClient
import com.example.helloworld.data.NetworkUtils
import com.example.helloworld.data.Post
import com.example.helloworld.databinding.DialogPostBinding
import com.example.helloworld.databinding.FragmentPostsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostsFragment : Fragment() {
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val api = ApiClient.postsApi
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostsAdapter(
            onEdit = { post -> showPostDialog(existing = post) },
            onDelete = { post -> deletePost(post) }
        )
        binding.recyclerPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPosts.adapter = adapter

        binding.fabAddPost.setOnClickListener { showPostDialog(existing = null) }
        binding.btnRetry.setOnClickListener { loadPosts() }

        loadPosts()
    }

    // Runs on the view's own lifecycle: if the fragment's view is torn down
    // (e.g. user navigates away) while a request is in flight, this scope is
    // cancelled automatically instead of resuming and touching a null binding.
    private val viewScope get() = viewLifecycleOwner.lifecycleScope

    private fun loadPosts() {
        showLoading()
        viewScope.launch {
            try {
                if (!NetworkUtils.isConnected(requireContext())) {
                    showError("No internet connection")
                    return@launch
                }
                val posts = api.getPosts()
                if (posts.isEmpty()) showEmpty() else showPosts(posts)
            } catch (e: IOException) {
                showError("Couldn't reach the server. Check your connection and retry.")
            } catch (e: HttpException) {
                showError("Server error (${e.code()}). Please retry.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.recyclerPosts.visibility = View.GONE
        binding.textEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun showPosts(posts: List<Post>) {
        adapter.submitList(posts)
        binding.progress.visibility = View.GONE
        binding.recyclerPosts.visibility = View.VISIBLE
        binding.textEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.progress.visibility = View.GONE
        binding.recyclerPosts.visibility = View.GONE
        binding.textEmpty.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.progress.visibility = View.GONE
        binding.recyclerPosts.visibility = View.GONE
        binding.textEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.textError.text = message
    }

    private fun showPostDialog(existing: Post?) {
        val dialogBinding = DialogPostBinding.inflate(layoutInflater)
        dialogBinding.inputTitle.setText(existing?.title.orEmpty())
        dialogBinding.inputBody.setText(existing?.body.orEmpty())

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "New post" else "Edit post")
            .setView(dialogBinding.root)
            .setPositiveButton(if (existing == null) "Create" else "Save") { _, _ ->
                val title = dialogBinding.inputTitle.text.toString().trim()
                val body = dialogBinding.inputBody.text.toString().trim()
                if (title.isEmpty()) {
                    toast("Title can't be empty")
                    return@setPositiveButton
                }
                if (existing == null) {
                    createPost(title, body)
                } else {
                    updatePost(existing, title, body)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createPost(title: String, body: String) {
        if (!NetworkUtils.isConnected(requireContext())) {
            toast("No internet connection")
            return
        }
        viewScope.launch {
            try {
                val created = api.createPost(Post(title = title, body = body))
                val current = adapter.currentList.toMutableList()
                // jsonplaceholder is a mock API: it echoes back id 101 for every
                // new post rather than persisting one, so we assign a locally
                // unique id to keep list diffing correct.
                val localId = (current.maxOfOrNull { it.id } ?: 100) + 1
                current.add(0, created.copy(id = localId))
                showPosts(current)
                toast("Post created")
            } catch (e: IOException) {
                toast("Couldn't reach the server. Try again.")
            } catch (e: HttpException) {
                toast("Server error (${e.code()}) creating post.")
            } catch (e: Exception) {
                toast("Failed to create post: ${e.message}")
            }
        }
    }

    private fun updatePost(existing: Post, title: String, body: String) {
        if (!NetworkUtils.isConnected(requireContext())) {
            toast("No internet connection")
            return
        }
        viewScope.launch {
            try {
                val updated = api.updatePost(existing.id, existing.copy(title = title, body = body))
                val current = adapter.currentList.map {
                    if (it.id == existing.id) updated.copy(id = existing.id) else it
                }
                showPosts(current)
                toast("Post updated")
            } catch (e: IOException) {
                toast("Couldn't reach the server. Try again.")
            } catch (e: HttpException) {
                toast("Server error (${e.code()}) updating post.")
            } catch (e: Exception) {
                toast("Failed to update post: ${e.message}")
            }
        }
    }

    private fun deletePost(post: Post) {
        if (!NetworkUtils.isConnected(requireContext())) {
            toast("No internet connection")
            return
        }
        viewScope.launch {
            try {
                api.deletePost(post.id)
                val current = adapter.currentList.filter { it.id != post.id }
                if (current.isEmpty()) showEmpty() else showPosts(current)
                toast("Post deleted")
            } catch (e: IOException) {
                toast("Couldn't reach the server. Try again.")
            } catch (e: HttpException) {
                toast("Server error (${e.code()}) deleting post.")
            } catch (e: Exception) {
                toast("Failed to delete post: ${e.message}")
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerPosts.adapter = null
        _binding = null
    }
}
