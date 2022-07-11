package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartsafe.smartsafe_app.databinding.ItemHistoryBinding
import com.smartsafe.smartsafe_app.domain.entity.DoorAction
import com.smartsafe.smartsafe_app.domain.entity.History
import java.text.SimpleDateFormat
import java.util.*

class HistoryListAdapter(
    private val values: List<History>,
    private val onItemClickListener: ((item: History) -> Unit)? = null
) : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.textAction.text = when (item.doorAction ?: DoorAction.CLOSE) {
            DoorAction.OPEN -> "Abierta"
            DoorAction.CLOSE -> "Cerrada"
        }

        item.date?.let { date ->
            holder.textDate.text =
                SimpleDateFormat("EEEE dd - MMMM - yyyy - hh:mm a").format(date)
                    .split(" ").joinToString(" ") { text ->
                        text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(item) }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val textAction: TextView = binding.historyTextAction
        val textDate: TextView = binding.historyTextDate
    }
}