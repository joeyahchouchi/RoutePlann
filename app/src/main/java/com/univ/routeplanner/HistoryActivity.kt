package com.univ.routeplanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.univ.routeplanner.databinding.ActivityHistoryBinding
import com.univ.routeplanner.ui.HistoryViewModel
import com.univ.routeplanner.ui.RouteHistoryAdapter

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModel.Factory(applicationContext)
    }

    private lateinit var adapter: RouteHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadHistory()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()  // back arrow closes this screen, returning to MainActivity
        }
    }

    private fun setupRecyclerView() {
        adapter = RouteHistoryAdapter { clickedRoute ->
            // When a history item is tapped, send its data back to MainActivity
            val resultIntent = Intent().apply {
                putExtra(EXTRA_ORIGIN, clickedRoute.origin)
                putExtra(EXTRA_DESTINATION, clickedRoute.destination)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.routes.observe(this) { list ->
            adapter.updateItems(list)
        }

        viewModel.isEmpty.observe(this) { empty ->
            binding.tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (empty) View.GONE else View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        binding.btnClearAll.setOnClickListener {
            viewModel.clearAll()
        }
    }

    companion object {
        const val EXTRA_ORIGIN = "extra_origin"
        const val EXTRA_DESTINATION = "extra_destination"
    }
}