package com.example.ktphanemobil.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ktphanemobil.R
import com.example.ktphanemobil.adapter.GeneralBookAdapter
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.FragmentGeneralBookListBinding
import com.example.ktphanemobil.models.Book
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class GeneralBookListFragment : Fragment() {

    private var _binding: FragmentGeneralBookListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GeneralBookAdapter
    private var isAdmin: Boolean = false


    private val allBooks: MutableList<Book> = mutableListOf()
    private val shownBooks: MutableList<Book> = mutableListOf()


    private var shouldRefreshOnResume = false


    private var lastQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneralBookListBinding.inflate(inflater, container, false)

        val prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", null)
            ?: prefs.getString("role", null)
            ?: ""
        isAdmin = role.equals("Admin", ignoreCase = true)

        binding.rvGeneralBooks.layoutManager = LinearLayoutManager(requireContext())


        setupAdapter()


        binding.edtSearch.addTextChangedListener { editable ->
            lastQuery = editable?.toString().orEmpty()
            applyFilter(lastQuery)
        }


        loadAllBooks()

        return binding.root
    }

    override fun onResume() {
        super.onResume()


        if (!shouldRefreshOnResume) {
            shouldRefreshOnResume = true
            return
        }


        loadAllBooks()
    }

    private fun loadAllBooks() {
        RetrofitClient.instance.getBooks()
            .enqueue(object : Callback<List<Book>> {

                override fun onResponse(
                    call: Call<List<Book>>,
                    response: Response<List<Book>>
                ) {
                    if (!isAdded || _binding == null) return

                    if (!response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Kitaplar alınamadı: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val booksFromApi = response.body().orEmpty()

                    allBooks.clear()
                    allBooks.addAll(booksFromApi)


                    applyFilter(lastQuery)
                }

                override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                    if (!isAdded || _binding == null) return

                    Toast.makeText(
                        requireContext(),
                        "Bağlantı hatası: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setupAdapter() {
        adapter = GeneralBookAdapter(
            books = shownBooks,
            isAdmin = isAdmin,
            onBookClick = { selectedBook ->
                val fragment = BookInfoFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("selected_book", selectedBook)
                    }
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { book ->

                if (book.isBorrowed == true) {
                    Toast.makeText(
                        requireContext(),
                        "Bu kitap şu anda ödünç alındı. Silinemez.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@GeneralBookAdapter
                }
                showDeleteConfirmDialog(book)
            }
        )

        binding.rvGeneralBooks.adapter = adapter
    }

    private fun applyFilter(queryRaw: String) {
        val q = normalizeForSearch(queryRaw)

        val filtered = if (q.isBlank()) {
            allBooks
        } else {
            allBooks.filter { b ->
                normalizeForSearch(b.title).contains(q)
            }
        }

        shownBooks.clear()
        shownBooks.addAll(filtered)


        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }


    }


    private fun normalizeForSearch(s: String): String {
        return s
            .lowercase(Locale("tr", "TR"))
            .replace("\\s+".toRegex(), "") // tüm boşlukları kaldır
            .trim()
    }

    private fun showDeleteConfirmDialog(book: Book) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Kitabı Sil")
            .setMessage("“${book.title}” kitabını silmek istediğine emin misin?")
            .setNegativeButton("Hayır") { d, _ -> d.dismiss() }
            .setPositiveButton("Sil") { _, _ -> deleteBook(book) }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
    }

    private fun deleteBook(book: Book) {
        RetrofitClient.instance.deleteBook(book.id)
            .enqueue(object : Callback<Void> {

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (!isAdded || _binding == null) return

                    if (response.isSuccessful) {


                        allBooks.removeAll { it.id == book.id }


                        applyFilter(lastQuery)

                        Toast.makeText(
                            requireContext(),
                            "Kitap silindi.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val backendMessage = response.errorBody()?.string()?.takeIf { it.isNotBlank() }

                    val msg = when (response.code()) {
                        401, 403 -> backendMessage ?: "Yetkin yok. (Admin girişi gerekli)"
                        404 -> backendMessage ?: "Kitap bulunamadı."
                        409 -> backendMessage ?: "Bu kitap daha önce ödünç alındığı için silinemez."
                        else -> backendMessage ?: "Silme başarısız: ${response.code()}"
                    }

                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (!isAdded || _binding == null) return

                    Toast.makeText(
                        requireContext(),
                        "Bağlantı hatası: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
// Tüm kitapları listeleyen ekrandır; backend’den kitapları çeker, arama kutusuna göre filtreler,
// kitaba tıklanınca detay ekranına gider ve Admin rolünde ise silme akışını (ödünç kontrolü + onay dialogu)
// yöneterek kitabı API üzerinden siler.