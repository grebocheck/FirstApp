// com/example/firstapp/ui/adapters/InverterAdapter.kt

package com.example.firstapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapp.api.models.Inverter
import com.example.firstapp.databinding.ItemInverterBinding

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

        fun bind(inverter: Inverter) {
            binding.apply {
                textViewTitle.text = inverter.title
                textViewAddress.text = inverter.address
                textViewOwner.text = inverter.owner
                textViewRegion.text = "${inverter.city.title}, ${inverter.region.title}"

                // Показуємо потужність якщо є
                val powerText = when {
                    inverter.solarMaxPower != null -> "☀️ ${inverter.solarMaxPower} кВт"
                    inverter.inverterMaxPower != null -> "⚡ ${inverter.inverterMaxPower} кВт"
                    else -> "Потужність не вказана"
                }
                textViewPower.text = powerText

                // Показуємо батарею якщо є
                if (inverter.batterySize != null && inverter.batterySize > 0) {
                    textViewBattery.text = "🔋 ${inverter.batterySize} кВт·г"
                } else {
                    textViewBattery.text = "🔋 Немає батареї"
                }

                root.setOnClickListener {
                    onItemClick(inverter)
                }
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
}
