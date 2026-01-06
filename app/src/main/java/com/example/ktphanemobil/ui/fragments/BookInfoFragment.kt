package com.example.ktphanemobil.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.databinding.FragmentBookInfoBinding
import com.example.ktphanemobil.models.Book

class BookInfoFragment : Fragment() {

    private var _binding: FragmentBookInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookInfoBinding.inflate(inflater, container, false)

        val book = arguments?.getSerializable("selected_book") as? Book
        if (book == null) {
            Toast.makeText(context, "Kitap bilgisi bulunamadı.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return binding.root
        }

        binding.txtInfoTitle.text = book.title
        binding.txtInfoAuthor.text = "Yazar: ${book.author}"
        binding.txtInfoIsbn.text = "ISBN: ${book.isbn ?: "Belirtilmemiş"}"
        binding.txtInfoLibrary.text = "Bulunduğu Kütüphane: ${book.libraryName ?: "Genel"}"

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
