package com.vidyarthibus.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vidyarthibus.app.databinding.ItemBusRouteBinding
import com.vidyarthibus.app.model.BusRoute

class BusRouteAdapter(
    private val onClick: (BusRoute) -> Unit
) : ListAdapter<BusRoute, BusRouteAdapter.VH>(Diff()) {

    inner class VH(private val b: ItemBusRouteBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(route: BusRoute) {
            b.tvBusNumber.text = route.busNumber
            b.tvRouteName.text = route.routeName
            b.tvStops.text = route.stops.joinToString(" → ")
            b.root.setOnClickListener { onClick(route) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemBusRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class Diff : DiffUtil.ItemCallback<BusRoute>() {
        override fun areItemsTheSame(a: BusRoute, b: BusRoute) = a.routeId == b.routeId
        override fun areContentsTheSame(a: BusRoute, b: BusRoute) = a == b
    }
}
