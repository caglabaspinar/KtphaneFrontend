package com.example.ktphanemobil.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.R
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentAddBookBinding
import com.example.ktphanemobil.models.AddBookRequest
import com.example.ktphanemobil.models.Library
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
            val isbn = binding.etIsbn.text.toString().trim()

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (libraries.isEmpty()) {
                Toast.makeText(requireContext(), "Kütüphane seçilmedi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedIndex = binding.spinnerLibrary.selectedItemPosition
            val libraryId = libraries[selectedIndex].id

            val request = AddBookRequest(
                title = title,
                author = author,
                isbn = isbn,
                libraryId = libraryId
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
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Eklenemedi (Kod ${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<com.example.ktphanemobil.models.Book>, t: Throwable) {
                        Toast.makeText(requireContext(), "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
