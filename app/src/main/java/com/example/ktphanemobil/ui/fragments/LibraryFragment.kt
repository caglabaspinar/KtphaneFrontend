package com.example.ktphanemobil.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ktphanemobil.R
import com.example.ktphanemobil.adapter.LibraryAdapter
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentLibraryBinding
import com.example.ktphanemobil.models.Library
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)

        binding.rvLibraries.layoutManager = LinearLayoutManager(requireContext())
        loadLibraries()

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
                        Toast.makeText(
                            requireContext(),
                            "Kütüphaneler alınamadı: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val libraries = response.body().orEmpty()
                    setupAdapter(libraries)
                }

                override fun onFailure(call: Call<List<Library>>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Bağlantı hatası: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setupAdapter(libraries: List<Library>) {
        binding.rvLibraries.adapter = LibraryAdapter(libraries) { selectedLib ->
            val fragment = LibraryBookListFragment().apply {
                arguments = Bundle().apply {
                    putInt("library_id", selectedLib.id)
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
