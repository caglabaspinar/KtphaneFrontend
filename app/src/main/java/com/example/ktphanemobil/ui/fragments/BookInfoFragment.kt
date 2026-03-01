package com.example.ktphanemobil.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.R
import com.example.ktphanemobil.databinding.FragmentBookInfoBinding
import com.example.ktphanemobil.models.Book
import java.io.Serializable

class BookInfoFragment : Fragment() {

    private var _binding: FragmentBookInfoBinding? = null
    private val binding get() = _binding!!

    private var book: Book? = null
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", null)
            ?: prefs.getString("role", null)
            ?: ""
        isAdmin = role.equals("Admin", ignoreCase = true)


        parentFragmentManager.setFragmentResultListener(
            "book_updated",
            this
        ) { _, bundle ->

            val updatedBook = bundle.getSerializable("updated_book") as? Book
            if (updatedBook != null) {
                book = updatedBook
                if (_binding != null) {
                    bindBookToUi(updatedBook)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookInfoBinding.inflate(inflater, container, false)

        val selectedBook = arguments?.getSerializable("selected_book") as? Book
        if (selectedBook == null) {
            Toast.makeText(requireContext(), "Kitap bilgisi bulunamadı.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return binding.root
        }

        book = selectedBook
        bindBookToUi(selectedBook)


        binding.btnEditBook.visibility = if (isAdmin) View.VISIBLE else View.GONE

        binding.btnEditBook.setOnClickListener {
            val currentBook = book ?: return@setOnClickListener

            val fragment = EditBookFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("selected_book", currentBook)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun bindBookToUi(book: Book) {
        binding.txtInfoTitle.text = book.title
        binding.txtInfoAuthor.text = "Yazar: ${book.author}"
        binding.txtInfoIsbn.text = "ISBN: ${book.isbn ?: "Belirtilmemiş"}"
        binding.txtInfoPageCount.text =
            "Sayfa Sayısı: ${book.pageCount?.toString() ?: "Bilinmiyor"}"
        binding.txtInfoLibrary.text =
            "Bulunduğu Kütüphane: ${book.libraryName ?: "Genel"}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
// Seçilen kitabın detay bilgilerini gösteren ekrandır; kullanıcı rolüne göre düzenleme butonunu
// görünür yapar ve Admin ise kitabı EditBookFragment’e göndererek güncelleme akışını başlatır.