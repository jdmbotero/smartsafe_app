package com.smartsafe.smartsafe_app.presentation.main.boxList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartsafe.smartsafe_app.databinding.ItemBoxBinding
import com.smartsafe.smartsafe_app.domain.entity.Box

class BoxListAdapter(
    private val values: List<Box>,
    private val onItemClickListener: ((item: Box) -> Unit)? = null
) : RecyclerView.Adapter<BoxListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBoxBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.textId.text = item.id
        holder.textName.text = item.name

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(item) }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        val textId: TextView = binding.boxTextId
        val textName: TextView = binding.boxTextName
    }
}