package com.vidyarthibus.app.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vidyarthibus.app.R
import com.vidyarthibus.app.databinding.ActivityBusDetailBinding
import com.vidyarthibus.app.model.BusRoute
import com.vidyarthibus.app.model.CrowdStatus
import java.util.UUID

class BusDetailActivity : AppCompatActivity() {

    companion object {
        const val KEY_ROUTE = "key_route"
    }

    private lateinit var binding: ActivityBusDetailBinding
    private val vm: BusDetailViewModel by viewModels()

    private lateinit var route: BusRoute
    private val userId: String by lazy { UUID.randomUUID().toString().take(10) }

    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* permissions handled in VM */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        route = intent.getParcelableExtra(KEY_ROUTE)
            ?: run { finish(); return }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = route.busNumber
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tvRouteName.text = route.routeName
        binding.tvStops.text = route.stops.joinToString(" → ")

        requestLocationIfNeeded()

        vm.observeStatus(route.routeId)

        vm.crowdStatus.observe(this) { updateMeter(it) }

        vm.submitting.observe(this) { busy ->
            binding.progressSubmit.visibility = if (busy) View.VISIBLE else View.GONE
            binding.btnEmpty.isEnabled  = !busy
            binding.btnSeated.isEnabled = !busy
            binding.btnFull.isEnabled   = !busy
        }

        vm.toast.observe(this) { msg ->
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                vm.clearToast()
            }
        }

        binding.btnEmpty.setOnClickListener  { vm.submitReport(route.routeId, userId, CrowdStatus.EMPTY) }
        binding.btnSeated.setOnClickListener { vm.submitReport(route.routeId, userId, CrowdStatus.SEATED) }
        binding.btnFull.setOnClickListener   { vm.submitReport(route.routeId, userId, CrowdStatus.FULL) }

        binding.btnAlternatives.setOnClickListener {
            startActivity(Intent(this, AlternativesActivity::class.java))
        }
    }

    private fun updateMeter(status: CrowdStatus) {
        binding.tvCrowdLabel.text = status.label
        binding.progressCrowd.progress = status.progressValue

        val colorRes = when (status) {
            CrowdStatus.EMPTY  -> R.color.crowd_empty
            CrowdStatus.SEATED -> R.color.crowd_seated
            CrowdStatus.FULL   -> R.color.crowd_full
        }
        val color = ContextCompat.getColor(this, colorRes)
        binding.progressCrowd.setIndicatorColor(color)
        binding.tvCrowdLabel.setTextColor(color)

        binding.tvCrowdEmoji.text = when (status) {
            CrowdStatus.EMPTY  -> "🟢"
            CrowdStatus.SEATED -> "🟡"
            CrowdStatus.FULL   -> "🔴"
        }
    }

    private fun requestLocationIfNeeded() {
        val fine = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, fine) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            locationPermission.launch(arrayOf(fine, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
