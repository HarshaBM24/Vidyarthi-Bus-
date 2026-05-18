package com.vidyarthibus.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vidyarthibus.app.databinding.ItemSharedAutoBinding
import com.vidyarthibus.app.model.SharedAuto

class SharedAutoAdapter(
    private val onCall: (String) -> Unit
) : ListAdapter<SharedAuto, SharedAutoAdapter.VH>(Diff()) {

    inner class VH(private val b: ItemSharedAutoBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(auto: SharedAuto) {
            b.tvDriverName.text    = auto.driverName
            b.tvPhone.text         = auto.phoneNumber
            b.tvArea.text          = auto.area
            b.tvCoverage.text      = auto.routeCoverage
            b.btnCall.setOnClickListener { onCall(auto.phoneNumber) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemSharedAutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class Diff : DiffUtil.ItemCallback<SharedAuto>() {
        override fun areItemsTheSame(a: SharedAuto, b: SharedAuto) = a.id == b.id
        override fun areContentsTheSame(a: SharedAuto, b: SharedAuto) = a == b
    }
}
