package com.example.firstapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapp.api.models.Inverter
import com.example.firstapp.databinding.ItemInverterBinding
import java.text.DecimalFormat

class InverterAdapter(
    private val onItemClick: (Inverter) -> Unit
) : ListAdapter<Inverter, InverterAdapter.InverterViewHolder>(InverterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InverterViewHolder {
        val binding = ItemInverterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InverterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InverterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InverterViewHolder(
        private val binding: ItemInverterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(inverter: Inverter) {
            binding.apply {
                textViewTitle.text = inverter.title
                textViewAddress.text = inverter.address
                textViewOwner.text = "👤 ${inverter.owner}"

                // Format region and city
                val regionText = if (inverter.city != null) {
                    "📍 ${inverter.city.title}, ${inverter.region.title}"
                } else {
                    "📍 ${inverter.region.title}"
                }
                textViewRegion.text = regionText

                // Format power
                val powerText = if (inverter.solarMaxPower != null) {
                    "☀️ ${formatPower(inverter.solarMaxPower)} кВт"
                } else if (inverter.inverterMaxPower != null) {
                    "⚡ ${formatPower(inverter.inverterMaxPower)} кВт"
                } else {
                    "⚡ N/A"
                }
                textViewPower.text = powerText

                // Format battery
                val batteryText = if (inverter.batterySize != null) {
                    "🔋 ${formatPower(inverter.batterySize)} кВт·г"
                } else {
                    "🔋 N/A"
                }
                textViewBattery.text = batteryText
            }
        }

        private fun formatPower(power: Double): String {
            val formatter = DecimalFormat("#.#")
            return formatter.format(power)
        }
    }
}

class InverterDiffCallback : DiffUtil.ItemCallback<Inverter>() {
    override fun areItemsTheSame(oldItem: Inverter, newItem: Inverter): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Inverter, newItem: Inverter): Boolean {
        return oldItem == newItem
    }
}