package com.example.ktphanemobil.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ktphanemobil.R
import com.example.ktphanemobil.adapter.GeneralBookAdapter
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentMyBooksBinding
import com.example.ktphanemobil.models.Book
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBooksFragment : Fragment() {

    private var _binding: FragmentMyBooksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GeneralBookAdapter
    private val myBooks = mutableListOf<Book>()

    companion object {
        private const val PREFS_NAME = "UserPrefs"
        private const val KEY_USER_ID = "user_id"

        private const val MSG_LOGIN_REQUIRED = "Önce giriş yapmalısın."
        private const val MSG_EMPTY = "Henüz ödünç aldığın kitap yok."
        private const val MSG_FETCH_FAIL_PREFIX = "Kitaplar alınamadı. Kod: "
        private const val MSG_CONN_FAIL_PREFIX = "Bağlantı hatası: "
        private const val MSG_CONN_FAIL_TOAST = "Bağlantı hatası"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBooksBinding.inflate(inflater, container, false)

        binding.recyclerMyBooks.layoutManager = LinearLayoutManager(requireContext())

        adapter = GeneralBookAdapter(myBooks) { clickedBook ->
            val fragment = BookInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("selected_book", clickedBook)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerMyBooks.adapter = adapter

        loadMyBooks()

        return binding.root
    }

    private fun loadMyBooks() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val studentId = prefs.getInt(KEY_USER_ID, -1)

        if (studentId == -1) {
            showEmpty(MSG_LOGIN_REQUIRED)
            return
        }

        showLoading(true)

        RetrofitClient.instance.getStudentBooks(studentId)
            .enqueue(object : Callback<List<Book>> {

                override fun onResponse(
                    call: Call<List<Book>>,
                    response: Response<List<Book>>
                ) {
                    showLoading(false)

                    if (!response.isSuccessful) {
                        showEmpty(MSG_FETCH_FAIL_PREFIX + response.code())
                        return
                    }

                    val list = response.body().orEmpty()
                    myBooks.clear()
                    myBooks.addAll(list)
                    adapter.notifyDataSetChanged()

                    if (myBooks.isEmpty()) {
                        showEmpty(MSG_EMPTY)
                    } else {
                        showList()
                    }
                }

                override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                    showLoading(false)
                    showEmpty(MSG_CONN_FAIL_PREFIX + (t.message ?: "Bilinmeyen hata"))
                    Toast.makeText(requireContext(), MSG_CONN_FAIL_TOAST, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressMyBooks.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            binding.txtEmptyMyBooks.visibility = View.GONE
        }
    }

    private fun showEmpty(message: String) {
        binding.txtEmptyMyBooks.visibility = View.VISIBLE
        binding.recyclerMyBooks.visibility = View.GONE
        binding.txtEmptyMyBooks.text = message
    }

    private fun showList() {
        binding.txtEmptyMyBooks.visibility = View.GONE
        binding.recyclerMyBooks.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
