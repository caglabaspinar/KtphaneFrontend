package com.example.ktphanemobil.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.R
import com.example.ktphanemobil.databinding.FragmentProfileBinding
import com.example.ktphanemobil.ui.activities.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val PREFS_NAME = "UserPrefs"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_AUTH_TOKEN = "auth_token"

        private const val KEY_ROLE = "role"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        fillProfileFromPrefs()
        setupLogoutButton()
        setupHistoryButton()
        setupAdminButtons()


        return binding.root
    }

    private fun fillProfileFromPrefs() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val name = prefs.getString(KEY_USER_NAME, "Kullanıcı") ?: "Kullanıcı"
        val email = prefs.getString(KEY_USER_EMAIL, "Email bulunamadı") ?: "Email bulunamadı"

        binding.txtProfileName.text = "Hoş geldin, $name"
        binding.txtProfileEmail.text = email
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun setupHistoryButton() {
        binding.btnHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BorrowHistoryFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun logout() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .apply()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    private fun setupAdminButtons() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val role = prefs.getString(KEY_ROLE, "Student")

        if (role == "Admin") {


            binding.btnAddBook.visibility = View.VISIBLE
            binding.btnAddBook.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddBookFragment())
                    .addToBackStack(null)
                    .commit()
            }


            binding.btnAddLibrary.visibility = View.VISIBLE
            binding.btnAddLibrary.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddLibraryFragment())
                    .addToBackStack(null)
                    .commit()
            }

        } else {

            binding.btnAddBook.visibility = View.GONE
            binding.btnAddLibrary.visibility = View.GONE
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
// Kullanıcının profil ekranıdır; SharedPreferences’tan kullanıcı bilgilerini gösterir,
// logout işlemini yapar, ödünç geçmişine geçişi sağlar ve rol Admin ise kitap/kütüphane ekleme
// butonlarını dinamik olarak görünür yapar.
