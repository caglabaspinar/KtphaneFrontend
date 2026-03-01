package com.example.ktphanemobil.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentAddBookBinding
import com.example.ktphanemobil.models.AddBookRequest
import com.example.ktphanemobil.models.Library
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddBookFragment : Fragment() {

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    private var libraries: List<Library> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookBinding.inflate(inflater, container, false)

        loadLibraries()
        setupSaveButton()

        return binding.root
    }

    private fun loadLibraries() {
        RetrofitClient.instance.getLibraries()
            .enqueue(object : Callback<List<Library>> {
                override fun onResponse(
                    call: Call<List<Library>>,
                    response: Response<List<Library>>
                ) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Kütüphaneler alınamadı", Toast.LENGTH_SHORT).show()
                        return
                    }

                    libraries = response.body().orEmpty()
                    if (libraries.isEmpty()) {
                        Toast.makeText(requireContext(), "Kütüphane yok", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val names = libraries.map { it.name }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        names
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerLibrary.adapter = adapter
                }

                override fun onFailure(call: Call<List<Library>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupSaveButton() {
        binding.btnSaveBook.setOnClickListener {

            val title = binding.etTitle.text.toString().trim()
            val author = binding.etAuthor.text.toString().trim()
            val rawIsbn = binding.etIsbn.text.toString().trim()
            val normalizedIsbn = normalizeIsbn(rawIsbn)

            val pageCountText = binding.etPageCount.text.toString().trim()
            val pageCount = pageCountText.toIntOrNull()

            if (pageCountText.isNotEmpty() && pageCount == null) {
                showError("Sayfa sayısı sayı olmalı")
                return@setOnClickListener
            }

            if (title.isEmpty() || author.isEmpty() || rawIsbn.isEmpty()) {
                showError("Tüm alanları doldurun")
                return@setOnClickListener
            }

            if (!isValidIsbn13(normalizedIsbn)) {
                showError("ISBN 13 haneli olmalı ve 978 veya 979 ile başlamalı")
                return@setOnClickListener
            }

            if (libraries.isEmpty()) {
                showError("Kütüphane seçilmedi")
                return@setOnClickListener
            }

            val selectedIndex = binding.spinnerLibrary.selectedItemPosition
            val libraryId = libraries[selectedIndex].id

            val request = AddBookRequest(
                title = title,
                author = author,
                isbn = normalizedIsbn,
                libraryId = libraryId,
                pageCount = pageCount
            )

            RetrofitClient.instance.addBook(request)
                .enqueue(object : Callback<com.example.ktphanemobil.models.Book> {
                    override fun onResponse(
                        call: Call<com.example.ktphanemobil.models.Book>,
                        response: Response<com.example.ktphanemobil.models.Book>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Kitap eklendi", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                            return
                        }


                        if (response.code() == 409) {
                            showError("Bu ISBN numarası zaten kullanılıyor.")
                            return
                        }


                        val backendMsg = response.errorBody()?.string()?.trim()
                        val msg = if (!backendMsg.isNullOrBlank()) backendMsg else "Eklenemedi (Kod ${response.code()})"
                        showError(msg)
                    }

                    override fun onFailure(call: Call<com.example.ktphanemobil.models.Book>, t: Throwable) {
                        showError("Bağlantı hatası: ${t.message}")
                    }
                })
        }
    }


    private fun showError(message: String) {
        if (!isAdded || _binding == null) return
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#D32F2F"))
            .setTextColor(Color.WHITE)
            .show()
    }


    private fun normalizeIsbn(input: String): String {
        return input.filter { it.isDigit() }
    }


    private fun isValidIsbn13(isbn: String): Boolean {
        if (isbn.length != 13) return false
        if (!isbn.startsWith("978") && !isbn.startsWith("979")) return false
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
// Admin’in yeni kitap eklediği ekrandır; kütüphane listesini API’den çekip spinner’a doldurur,
// girilen kitap bilgilerini (ISBN normalize/format ve sayfa sayısı dahil) doğrular, ardından backend’e
// kitap ekleme isteği gönderir ve hata/başarı durumlarını kullanıcıya gösterir.