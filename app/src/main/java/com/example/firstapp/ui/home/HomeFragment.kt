package com.example.firstapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
                    showError(result.message ?: "Невідома помилка")
                }
            }
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
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
