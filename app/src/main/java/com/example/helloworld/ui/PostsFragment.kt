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
import com.example.helloworld.data.Post
import com.example.helloworld.databinding.DialogPostBinding
import com.example.helloworld.databinding.FragmentPostsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

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

        loadPosts()
    }

    private fun loadPosts() {
        binding.progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val posts = api.getPosts()
                adapter.submitList(posts)
            } catch (e: Exception) {
                toast("Failed to load posts: ${e.message}")
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
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
        lifecycleScope.launch {
            try {
                val created = api.createPost(Post(title = title, body = body))
                val current = adapter.currentList.toMutableList()
                // jsonplaceholder always echoes id 101 for new posts; keep list entries unique locally
                val localId = (current.maxOfOrNull { it.id } ?: 100) + 1
                current.add(0, created.copy(id = localId))
                adapter.submitList(current)
                toast("Post created")
            } catch (e: Exception) {
                toast("Failed to create post: ${e.message}")
            }
        }
    }

    private fun updatePost(existing: Post, title: String, body: String) {
        lifecycleScope.launch {
            try {
                val updated = api.updatePost(existing.id, existing.copy(title = title, body = body))
                val current = adapter.currentList.map {
                    if (it.id == existing.id) updated.copy(id = existing.id) else it
                }
                adapter.submitList(current)
                toast("Post updated")
            } catch (e: Exception) {
                toast("Failed to update post: ${e.message}")
            }
        }
    }

    private fun deletePost(post: Post) {
        lifecycleScope.launch {
            try {
                api.deletePost(post.id)
                val current = adapter.currentList.filter { it.id != post.id }
                adapter.submitList(current)
                toast("Post deleted")
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
        _binding = null
    }
}
