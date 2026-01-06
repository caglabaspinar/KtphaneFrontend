package com.example.ktphanemobil.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.databinding.FragmentProfileBinding
import com.example.ktphanemobil.ui.activities.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val PREFS_NAME = "UserPrefs"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val name = prefs.getString(KEY_USER_NAME, "Kullanıcı").orEmpty()
        val email = prefs.getString(KEY_USER_EMAIL, "Email bulunamadı").orEmpty()

        binding.txtProfileName.text = "Hoş geldin, $name"
        binding.txtProfileEmail.text = email

        binding.btnLogout.setOnClickListener {
            prefs.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
