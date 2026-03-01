package com.example.ktphanemobil.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ktphanemobil.databinding.ItemBorrowHistoryBinding
import com.example.ktphanemobil.models.BorrowedBookResponse

class BorrowHistoryAdapter(
    private val items: MutableList<BorrowedBookResponse>
) : RecyclerView.Adapter<BorrowHistoryAdapter.VH>() {

    inner class VH(val binding: ItemBorrowHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemBorrowHistoryBinding.inflate(
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

        holder.binding.txtBorrowDate.text =
            if (item.borrowDate.isNullOrBlank()) "Alınma: -"
            else "Alınma: ${item.borrowDate}"

        holder.binding.txtReturnDate.text =
            if (item.returnDate.isNullOrBlank()) "Teslim: -"
            else "Teslim: ${item.returnDate}"
    }

    override fun getItemCount(): Int = items.size
}
// Öğrencinin ödünç geçmişini RecyclerView üzerinde listelemek için kullanılan adapter sınıfıdır.
// BorrowedBookResponse verilerini alır, her bir item’ı ItemBorrowHistoryBinding ile ekrana bağlar
// ve alınma ile teslim tarihlerini uygun formatta kullanıcıya gösterir.
