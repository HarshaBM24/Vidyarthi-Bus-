package com.vidyarthibus.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vidyarthibus.app.databinding.ActivityMainBinding
import com.vidyarthibus.app.model.BusRoute

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private lateinit var adapter: BusRouteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = BusRouteAdapter { route -> openDetail(route) }
        binding.rvRoutes.layoutManager = LinearLayoutManager(this)
        binding.rvRoutes.adapter = adapter

        vm.routes.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.loading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        vm.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }
    }

    private fun openDetail(route: BusRoute) {
        val intent = Intent(this, BusDetailActivity::class.java).apply {
            putExtra(BusDetailActivity.KEY_ROUTE, route)
        }
        startActivity(intent)
    }
}
