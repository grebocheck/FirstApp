package com.example.firstapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapp.api.client.NetworkResult
import com.example.firstapp.api.models.Inverter
import com.example.firstapp.databinding.FragmentHomeBinding
import com.example.firstapp.ui.adapters.InverterAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: InverterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSwipeRefresh()
        observeData()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = InverterAdapter { inverter ->
            onInverterClick(inverter)
        }

        binding.recyclerViewInverters.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter

            // Add scroll listener for pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // Check if we're scrolling down
                    if (dy > 0) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                        // Load more when we're near the end of the list (last 5 items)
                        if (viewModel.canLoadMore() &&
                            (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                            viewModel.loadMoreInverters()
                        }
                    }
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshInverters()
        }
    }

    private fun observeData() {
        viewModel.inverters.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    showLoading(true)
                }
                is NetworkResult.Success -> {
                    showLoading(false)
                    result.data?.let { paginatedResponse ->
                        if (paginatedResponse.results.isNotEmpty()) {
                            adapter.submitList(paginatedResponse.results)
                            showContent()
                        } else {
                            showEmpty()
                        }
                    }
                }
                is NetworkResult.Error -> {
                    showLoading(false)

                    // If we already have data (from previous pages), just show a toast
                    val currentList = adapter.currentList
                    if (currentList.isNotEmpty()) {
                        Toast.makeText(context, result.message ?: "Помилка завантаження", Toast.LENGTH_SHORT).show()
                        showContent()
                    } else {
                        showError(result.message ?: "Невідома помилка")
                    }
                }
            }
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }

        viewModel.isLoadingMore.observe(viewLifecycleOwner) { isLoadingMore ->
            if (isLoadingMore) {
                // Show a toast or small indicator that more data is loading
                Toast.makeText(context, "Завантаження...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerViewInverters.visibility = if (show) View.GONE else View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.textViewEmpty.visibility = View.GONE
    }

    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewInverters.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.textViewEmpty.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewInverters.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewEmpty.visibility = View.GONE
        binding.textViewError.text = message

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewInverters.visibility = View.GONE
        binding.textViewError.visibility = View.GONE
        binding.textViewEmpty.visibility = View.VISIBLE
    }

    private fun onInverterClick(inverter: Inverter) {
        Toast.makeText(context, "Обрано: ${inverter.title}", Toast.LENGTH_SHORT).show()
        // Тут можна додати навігацію до детального екрану інвертора
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
