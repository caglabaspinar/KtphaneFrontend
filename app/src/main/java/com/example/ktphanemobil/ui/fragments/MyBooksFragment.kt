package com.example.ktphanemobil.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ktphanemobil.R
import com.example.ktphanemobil.adapter.MyBorrowAdapter
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentMyBooksBinding
import com.example.ktphanemobil.models.BorrowRequest
import com.example.ktphanemobil.models.BorrowedBookResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBooksFragment : Fragment() {

    private var _binding: FragmentMyBooksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MyBorrowAdapter
    private val myBorrows = mutableListOf<BorrowedBookResponse>()

    companion object {
        private const val MSG_EMPTY = "Henüz ödünç aldığın kitap yok."
        private const val MSG_FETCH_FAIL_PREFIX = "Kitaplar alınamadı. Kod: "
        private const val MSG_CONN_FAIL_PREFIX = "Bağlantı hatası: "
        private const val MSG_CONN_FAIL_TOAST = "Bağlantı hatası"
        private const val MSG_RETURN_SUCCESS = "Kitap teslim edildi."
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBooksBinding.inflate(inflater, container, false)

        binding.recyclerMyBooks.layoutManager = LinearLayoutManager(requireContext())

        adapter = MyBorrowAdapter(
            items = myBorrows,
            onItemClick = { item ->
                val fragment = BookInfoFragment().apply {
                    arguments = Bundle().apply {
                        putInt("book_id", item.bookId)
                        putString("book_title", item.title)
                        putString("book_author", item.author)
                    }
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onReturnClick = { item ->
                returnBook(item.bookId)
            }
        )

        binding.recyclerMyBooks.adapter = adapter

        loadMyActiveBorrows()

        return binding.root
    }

    private fun loadMyActiveBorrows() {
        showLoading(true)

        RetrofitClient.instance.getMyActiveBorrows()
            .enqueue(object : Callback<List<BorrowedBookResponse>> {

                override fun onResponse(
                    call: Call<List<BorrowedBookResponse>>,
                    response: Response<List<BorrowedBookResponse>>
                ) {
                    showLoading(false)

                    if (!response.isSuccessful) {
                        showEmpty(MSG_FETCH_FAIL_PREFIX + response.code())
                        return
                    }

                    val list = response.body().orEmpty()
                    myBorrows.clear()
                    myBorrows.addAll(list)
                    adapter.notifyDataSetChanged()

                    if (myBorrows.isEmpty()) {
                        showEmpty(MSG_EMPTY)
                    } else {
                        showList()
                    }
                }

                override fun onFailure(call: Call<List<BorrowedBookResponse>>, t: Throwable) {
                    showLoading(false)
                    showEmpty(MSG_CONN_FAIL_PREFIX + (t.message ?: "Bilinmeyen hata"))
                    Toast.makeText(requireContext(), MSG_CONN_FAIL_TOAST, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun returnBook(bookId: Int) {
        showLoading(true)

        RetrofitClient.instance.returnBook(BorrowRequest(bookId))
            .enqueue(object : Callback<com.example.ktphanemobil.models.BorrowResponse> {

                override fun onResponse(
                    call: Call<com.example.ktphanemobil.models.BorrowResponse>,
                    response: Response<com.example.ktphanemobil.models.BorrowResponse>
                ) {
                    showLoading(false)

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), MSG_RETURN_SUCCESS, Toast.LENGTH_SHORT).show()
                        loadMyActiveBorrows()
                    } else {
                        showEmpty("Teslim edilemedi. Kod: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<com.example.ktphanemobil.models.BorrowResponse>,
                    t: Throwable
                ) {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Bağlantı hatası: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressMyBooks.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            binding.txtEmptyMyBooks.visibility = View.GONE
        }
    }

    private fun showEmpty(message: String) {
        binding.txtEmptyMyBooks.visibility = View.VISIBLE
        binding.recyclerMyBooks.visibility = View.GONE
        binding.txtEmptyMyBooks.text = message
    }

    private fun showList() {
        binding.txtEmptyMyBooks.visibility = View.GONE
        binding.recyclerMyBooks.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
