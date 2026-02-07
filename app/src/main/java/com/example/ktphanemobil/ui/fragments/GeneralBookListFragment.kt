package com.example.ktphanemobil.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ktphanemobil.R
import com.example.ktphanemobil.adapter.GeneralBookAdapter
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentGeneralBookListBinding
import com.example.ktphanemobil.models.Book
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeneralBookListFragment : Fragment() {

    private var _binding: FragmentGeneralBookListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneralBookListBinding.inflate(inflater, container, false)

        val b = _binding!!
        b.rvGeneralBooks.layoutManager = LinearLayoutManager(requireContext())

        loadAllBooks()

        return b.root
    }

    private fun loadAllBooks() {
        RetrofitClient.instance.getBooks()
            .enqueue(object : Callback<List<Book>> {

                override fun onResponse(
                    call: Call<List<Book>>,
                    response: Response<List<Book>>
                ) {
                    if (!isAdded) return
                    val b = _binding ?: return

                    if (!response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Kitaplar alınamadı: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val books = response.body().orEmpty()
                    if (books.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Kitap bulunamadı.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    setupAdapter(b, books)
                }

                override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                    if (!isAdded) return
                    if (_binding == null) return

                    Toast.makeText(
                        requireContext(),
                        "Bağlantı hatası: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setupAdapter(b: FragmentGeneralBookListBinding, books: List<Book>) {
        b.rvGeneralBooks.adapter = GeneralBookAdapter(books) { selectedBook ->
            val fragment = BookInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("selected_book", selectedBook)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
