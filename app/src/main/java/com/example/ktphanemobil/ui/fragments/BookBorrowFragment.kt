package com.example.ktphanemobil.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentBookBorrowBinding
import com.example.ktphanemobil.models.Book
import com.example.ktphanemobil.models.BorrowRequest
import com.example.ktphanemobil.models.BorrowResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookBorrowFragment : Fragment() {

    private var _binding: FragmentBookBorrowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookBorrowBinding.inflate(inflater, container, false)

        val book = arguments?.getSerializable("selected_book") as? Book
        if (book == null) {
            Toast.makeText(context, "Kitap bilgisi bulunamadı.", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        binding.txtBorrowTitle.text = book.title
        binding.txtBorrowAuthor.text = "Yazar: ${book.author}"
        binding.txtBorrowIsbn.text = "ISBN: ${book.isbn ?: "Belirtilmemiş"}"
        binding.txtBorrowLibrary.text = "Kütüphane: ${book.libraryName ?: "Belirtilmemiş"}"

        binding.btnBorrowAction.setOnClickListener {
            val prefs = requireActivity()
                .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

            val studentId = prefs.getInt("user_id", -1)
            if (studentId == -1) {
                Toast.makeText(context, "Önce giriş yapmalısın.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = BorrowRequest(
                studentId = studentId,
                bookId = book.id
            )

            RetrofitClient.instance.borrowBook(request)
                .enqueue(object : Callback<BorrowResponse> {

                    override fun onResponse(
                        call: Call<BorrowResponse>,
                        response: Response<BorrowResponse>
                    ) {
                        if (response.code() == 409) {
                            Toast.makeText(
                                context,
                                "Bu kitap daha önce ödünç alındı.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }

                        if (response.isSuccessful) {
                            val message =
                                response.body()?.message
                                    ?: "Kitap başarıyla ödünç alındı!"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(
                                context,
                                "Ödünç alma başarısız: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<BorrowResponse>, t: Throwable) {
                        Toast.makeText(
                            context,
                            "Bağlantı hatası: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
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
