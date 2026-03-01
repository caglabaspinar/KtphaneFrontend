package com.example.ktphanemobil.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentEditBookBinding
import com.example.ktphanemobil.models.Book
import com.example.ktphanemobil.models.UpdateBookRequest
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditBookFragment : Fragment() {

    private var _binding: FragmentEditBookBinding? = null
    private val binding get() = _binding!!

    private var book: Book? = null
    private var isSaving = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBookBinding.inflate(inflater, container, false)

        book = arguments?.getSerializable("selected_book") as? Book
        val currentBook = book
        if (currentBook == null) {
            Toast.makeText(requireContext(), "Kitap bilgisi bulunamadı.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return binding.root
        }

        binding.edtTitle.setText(currentBook.title)
        binding.edtAuthor.setText(currentBook.author)
        binding.edtIsbn.setText(currentBook.isbn ?: "")
        binding.edtPageCount.setText(currentBook.pageCount?.toString() ?: "")

        binding.btnSave.setOnClickListener {
            if (isSaving) return@setOnClickListener
            saveChanges(currentBook)
        }

        return binding.root
    }

    private fun saveChanges(currentBook: Book) {

        val title = binding.edtTitle.text.toString().trim()
        val author = binding.edtAuthor.text.toString().trim()

        val rawIsbn = binding.edtIsbn.text.toString().trim()
        val normalizedIsbn = rawIsbn
            .filter { it.isDigit() }
            .ifBlank { null }

        val pageCountStr = binding.edtPageCount.text.toString().trim()
        val pageCount = pageCountStr.toIntOrNull()

        if (title.isBlank() || author.isBlank()) {
            showError("Kitap adı ve yazar boş olamaz.")
            return
        }


        if (normalizedIsbn != null) {
            if (normalizedIsbn.length != 13 ||
                (!normalizedIsbn.startsWith("978") && !normalizedIsbn.startsWith("979"))
            ) {
                showError("ISBN 13 haneli olmalı ve 978 veya 979 ile başlamalı")
                return
            }
        }

        val req = UpdateBookRequest(
            title = title,
            author = author,
            isbn = normalizedIsbn,
            pageCount = pageCount,
            libraryId = currentBook.libraryId
        )

        isSaving = true
        binding.btnSave.isEnabled = false

        RetrofitClient.instance.updateBook(currentBook.id, req)
            .enqueue(object : Callback<Void> {

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (!isAdded || _binding == null) return

                    isSaving = false
                    binding.btnSave.isEnabled = true

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Kitap güncellendi.", Toast.LENGTH_SHORT).show()

                        val updatedBook = currentBook.copy(
                            title = title,
                            author = author,
                            isbn = normalizedIsbn,
                            pageCount = pageCount
                        )

                        parentFragmentManager.setFragmentResult(
                            "book_updated",
                            Bundle().apply {
                                putSerializable("updated_book", updatedBook)
                            }
                        )

                        parentFragmentManager.popBackStack()
                        return
                    }


                    if (response.code() == 409) {
                        showError("Bu ISBN numarası zaten kullanılıyor.")
                        return
                    }

                    val backendMessage = response.errorBody()?.string()?.trim()
                    val msg = if (!backendMessage.isNullOrBlank())
                        backendMessage
                    else
                        "Güncelleme başarısız: ${response.code()}"

                    showError(msg)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (!isAdded || _binding == null) return

                    isSaving = false
                    binding.btnSave.isEnabled = true

                    showError("Bağlantı hatası: ${t.message}")
                }
            })
    }

    private fun showError(message: String) {
        if (!isAdded || _binding == null) return
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#D32F2F"))
            .setTextColor(Color.WHITE)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
// Admin’in mevcut bir kitabın bilgilerini güncellediği ekrandır; girilen verileri doğrular,
// update isteğini backend’e gönderir, 409 ISBN çakışmasını kontrol eder ve başarılı olursa
// güncellenmiş kitabı önceki fragmente result olarak iletir.