package com.vidyarthibus.app.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vidyarthibus.app.model.BusRoute
import com.vidyarthibus.app.model.CrowdReport
import com.vidyarthibus.app.model.CrowdStatus
import com.vidyarthibus.app.model.SharedAuto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseRepository {

    private val db = FirebaseDatabase.getInstance()
    private val routesRef = db.getReference("routes")
    private val reportsRef = db.getReference("crowd_reports")
    private val autosRef = db.getReference("shared_autos")

    companion object {
        // Reports older than 15 minutes are stale
        private const val REPORT_TIMEOUT_MS = 15 * 60 * 1000L
    }

    // ─── Routes ──────────────────────────────────────────────────────────────

    fun getBusRoutes(): Flow<List<BusRoute>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BusRoute>()
                for (child in snapshot.children) {
                    val busNumber = child.child("busNumber").getValue(String::class.java) ?: ""
                    val routeName = child.child("routeName").getValue(String::class.java) ?: ""
                    val stopsSnap = child.child("stops")
                    val stops = mutableListOf<String>()
                    for (s in stopsSnap.children) {
                        s.getValue(String::class.java)?.let { stops.add(it) }
                    }
                    list.add(
                        BusRoute(
                            routeId = child.key ?: "",
                            busNumber = busNumber,
                            routeName = routeName,
                            stops = stops
                        )
                    )
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        routesRef.addValueEventListener(listener)
        awaitClose { routesRef.removeEventListener(listener) }
    }

    // ─── Crowd Status ─────────────────────────────────────────────────────────

    fun getCrowdStatus(routeId: String): Flow<CrowdStatus> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val now = System.currentTimeMillis()
                var latestStatus = CrowdStatus.EMPTY
                var latestTime = 0L

                for (child in snapshot.children) {
                    val rId = child.child("routeId").getValue(String::class.java) ?: continue
                    if (rId != routeId) continue
                    val ts = child.child("timestamp").getValue(Long::class.java) ?: 0L
                    if ((now - ts) > REPORT_TIMEOUT_MS) continue   // stale
                    if (ts > latestTime) {
                        latestTime = ts
                        val statusName = child.child("statusName").getValue(String::class.java)
                            ?: CrowdStatus.EMPTY.name
                        latestStatus = CrowdStatus.fromName(statusName)
                    }
                }
                trySend(latestStatus)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reportsRef.addValueEventListener(listener)
        awaitClose { reportsRef.removeEventListener(listener) }
    }

    fun submitReport(
        routeId: String,
        userId: String,
        status: CrowdStatus,
        lat: Double,
        lon: Double,
        onResult: (Boolean, String) -> Unit
    ) {
        val key = reportsRef.push().key ?: run {
            onResult(false, "Could not generate key")
            return
        }
        val data = mapOf(
            "reportId" to key,
            "routeId" to routeId,
            "userId" to userId,
            "statusName" to status.name,
            "timestamp" to System.currentTimeMillis(),
            "latitude" to lat,
            "longitude" to lon
        )
        reportsRef.child(key).setValue(data)
            .addOnSuccessListener { onResult(true, "✅ Report submitted!") }
            .addOnFailureListener { onResult(false, "❌ ${it.message}") }
    }

    // ─── Shared Autos ────────────────────────────────────────────────────────

    fun getSharedAutos(): Flow<List<SharedAuto>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SharedAuto>()
                for (child in snapshot.children) {
                    list.add(
                        SharedAuto(
                            id = child.key ?: "",
                            driverName = child.child("driverName").getValue(String::class.java) ?: "",
                            phoneNumber = child.child("phoneNumber").getValue(String::class.java) ?: "",
                            area = child.child("area").getValue(String::class.java) ?: "",
                            routeCoverage = child.child("routeCoverage").getValue(String::class.java) ?: ""
                        )
                    )
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        autosRef.addValueEventListener(listener)
        awaitClose { autosRef.removeEventListener(listener) }
    }

    // ─── Seed sample data (called once if DB is empty) ───────────────────────

    fun seedSampleData() {
        routesRef.get().addOnSuccessListener { snap ->
            if (!snap.exists()) {
                val routes = mapOf(
                    "route_01" to mapOf(
                        "busNumber" to "BUS-101",
                        "routeName" to "Village A → College",
                        "stops" to listOf("Village A", "Junction B", "Market", "College Gate")
                    ),
                    "route_02" to mapOf(
                        "busNumber" to "BUS-202",
                        "routeName" to "Village C → College",
                        "stops" to listOf("Village C", "Town Center", "Hospital", "College Gate")
                    ),
                    "route_03" to mapOf(
                        "busNumber" to "BUS-303",
                        "routeName" to "Village D → College",
                        "stops" to listOf("Village D", "Bridge Point", "Park", "College Main")
                    )
                )
                routesRef.setValue(routes)
            }
        }

        autosRef.get().addOnSuccessListener { snap ->
            if (!snap.exists()) {
                val autos = mapOf(
                    "auto_01" to mapOf(
                        "driverName" to "Raju Sharma",
                        "phoneNumber" to "9876543210",
                        "area" to "Village A",
                        "routeCoverage" to "Village A → College"
                    ),
                    "auto_02" to mapOf(
                        "driverName" to "Suresh Kumar",
                        "phoneNumber" to "9865432109",
                        "area" to "Village C",
                        "routeCoverage" to "Village C → Town"
                    ),
                    "auto_03" to mapOf(
                        "driverName" to "Mahesh Reddy",
                        "phoneNumber" to "9754321098",
                        "area" to "Junction B",
                        "routeCoverage" to "Junction B → College"
                    )
                )
                autosRef.setValue(autos)
            }
        }
    }
}
