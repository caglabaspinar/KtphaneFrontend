package com.example.ktphanemobil.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ktphanemobil.databinding.ItemMyBorrowBinding
import com.example.ktphanemobil.models.BorrowedBookResponse

class MyBorrowAdapter(
    private val items: MutableList<BorrowedBookResponse>,
    private val onItemClick: (BorrowedBookResponse) -> Unit,
    private val onReturnClick: (BorrowedBookResponse) -> Unit
) : RecyclerView.Adapter<MyBorrowAdapter.VH>() {

    inner class VH(val binding: ItemMyBorrowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMyBorrowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.binding.txtTitle.text = item.title
        holder.binding.txtAuthor.text = item.author
        holder.binding.txtBorrowDate.text = "Alınma: ${item.borrowDate}"

        holder.binding.root.setOnClickListener { onItemClick(item) }
        holder.binding.btnReturn.setOnClickListener { onReturnClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
