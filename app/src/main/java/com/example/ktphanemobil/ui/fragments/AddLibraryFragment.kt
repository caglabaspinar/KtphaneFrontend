package com.example.ktphanemobil.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentAddLibraryBinding
import com.example.ktphanemobil.models.AddLibraryRequest
import com.example.ktphanemobil.models.Library
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLibraryFragment : Fragment() {

    private var _binding: FragmentAddLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLibraryBinding.inflate(inflater, container, false)

        binding.btnSaveLibrary.setOnClickListener {
            val name = binding.etLibraryName.text.toString().trim()
            val location = binding.etLibraryLocation.text.toString().trim()

            if (name.isEmpty() || location.isEmpty()) {
                Toast.makeText(requireContext(), "Kütüphane adı ve yer boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = AddLibraryRequest(
                name = name,
                location = location
            )

            RetrofitClient.instance.addLibrary(request)
                .enqueue(object : Callback<Library> {

                    override fun onResponse(call: Call<Library>, response: Response<Library>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Kütüphane eklendi", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Eklenemedi (Kod ${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Library>, t: Throwable) {
                        Toast.makeText(requireContext(), "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
