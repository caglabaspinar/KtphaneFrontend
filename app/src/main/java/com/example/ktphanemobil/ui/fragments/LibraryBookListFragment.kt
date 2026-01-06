package com.example.ktphanemobil.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ktphanemobil.R
import com.example.ktphanemobil.adapter.LibraryBookAdapter
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentLibraryBookListBinding
import com.example.ktphanemobil.models.Book
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryBookListFragment : Fragment() {

    private var _binding: FragmentLibraryBookListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBookListBinding.inflate(inflater, container, false)

        binding.rvLibraryBooks.layoutManager = LinearLayoutManager(requireContext())

        val libraryId = arguments?.getInt("library_id", -1) ?: -1
        if (libraryId == -1) {
            Toast.makeText(requireContext(), "Kütüphane bilgisi bulunamadı.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return binding.root
        }


        loadBooksByLibrary(libraryId)

        return binding.root
    }



    private fun loadBooksByLibrary(libraryId: Int) {
        RetrofitClient.instance.getLibraryBooks(libraryId)
            .enqueue(object : Callback<List<Book>> {

                override fun onResponse(
                    call: Call<List<Book>>,
                    response: Response<List<Book>>
                ) {
                    if (!response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Kitaplar alınamadı: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val books = response.body().orEmpty()
                    if (books.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "Bu kütüphanede kitap yok.", Toast.LENGTH_SHORT).show()
                        return
                    }

                    setupAdapter(books)
                }

                override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "İnternet yok veya sunucuya ulaşılamadı.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun setupAdapter(books: List<Book>) {
        binding.rvLibraryBooks.adapter = LibraryBookAdapter(books) { selectedBook ->
            val fragment = BookBorrowFragment().apply {
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
