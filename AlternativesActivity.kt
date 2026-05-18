package com.vidyarthibus.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vidyarthibus.app.data.FirebaseRepository
import com.vidyarthibus.app.databinding.ActivityAlternativesBinding
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlternativesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlternativesBinding
    private val repo = FirebaseRepository()
    private lateinit var adapter: SharedAutoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlternativesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Shared Auto Contacts"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = SharedAutoAdapter { phone -> dialNumber(phone) }
        binding.rvAutos.layoutManager = LinearLayoutManager(this)
        binding.rvAutos.adapter = adapter

        loadAutos()
    }

    private fun loadAutos() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            repo.getSharedAutos()
                .catch {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@AlternativesActivity, "Failed to load contacts", Toast.LENGTH_SHORT).show()
                }
                .collect { list ->
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(list)
                    binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
        }
    }

    private fun dialNumber(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
