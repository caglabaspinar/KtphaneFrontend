package com.example.ktphanemobil.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ktphanemobil.adapter.BorrowHistoryAdapter
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentBorrowHistoryBinding
import com.example.ktphanemobil.models.BorrowedBookResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BorrowHistoryFragment : Fragment() {

    private var _binding: FragmentBorrowHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BorrowHistoryAdapter
    private val items = mutableListOf<BorrowedBookResponse>()

    companion object {
        private const val MSG_EMPTY = "Henüz kitap geçmişin yok."
        private const val PREFS = "auth"
        private const val KEY_STUDENT_ID = "studentId"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBorrowHistoryBinding.inflate(inflater, container, false)

        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        adapter = BorrowHistoryAdapter(items)
        binding.recyclerHistory.adapter = adapter

        loadHistory()

        return binding.root
    }

    private fun loadHistory() {
        binding.progressHistory.visibility = View.VISIBLE
        binding.txtEmptyHistory.visibility = View.GONE
        binding.recyclerHistory.visibility = View.GONE

        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val studentId = prefs.getInt(KEY_STUDENT_ID, -1)



        RetrofitClient.instance.getMyBorrowHistory()
            .enqueue(object : Callback<List<BorrowedBookResponse>> {

                override fun onResponse(
                    call: Call<List<BorrowedBookResponse>>,
                    response: Response<List<BorrowedBookResponse>>
                ) {
                    binding.progressHistory.visibility = View.GONE

                    if (!response.isSuccessful) {
                        showEmpty("Veri alınamadı. Kod: ${response.code()}")
                        return
                    }

                    val list = response.body().orEmpty()
                    items.clear()
                    items.addAll(list)
                    adapter.notifyDataSetChanged()

                    if (items.isEmpty()) showEmpty(MSG_EMPTY) else showList()
                }

                override fun onFailure(call: Call<List<BorrowedBookResponse>>, t: Throwable) {
                    binding.progressHistory.visibility = View.GONE
                    showEmpty("Bağlantı hatası: ${t.message}")
                    Toast.makeText(requireContext(), "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showEmpty(msg: String) {
        binding.txtEmptyHistory.visibility = View.VISIBLE
        binding.recyclerHistory.visibility = View.GONE
        binding.txtEmptyHistory.text = msg
    }

    private fun showList() {
        binding.txtEmptyHistory.visibility = View.GONE
        binding.recyclerHistory.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
// Öğrencinin tüm ödünç geçmişini listeleyen ekrandır; backend’den my/history endpoint’ini çağırır,
// gelen veriyi RecyclerView’da gösterir ve boş ya da hata durumlarını UI üzerinde yönetir.