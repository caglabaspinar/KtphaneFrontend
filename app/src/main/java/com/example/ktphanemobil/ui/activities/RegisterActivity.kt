package com.example.ktphanemobil.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.ActivityRegisterBinding
import com.example.ktphanemobil.models.RegisterRequest
import com.example.ktphanemobil.models.StudentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    companion object {
        private const val MSG_FILL_ALL = "Tüm alanları doldur."
        private const val MSG_SUCCESS = "Kayıt başarılı. Giriş yapabilirsin."
        private const val MSG_CONN_FAIL_PREFIX = "Bağlantı hatası: "
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtGoLogin.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleRegister() {
        hideError()

        val fullName = binding.edtFullName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showErrorLines(listOf(MSG_FILL_ALL))
            return
        }

        setLoading(true)

        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password
        )

        RetrofitClient.instance.register(request)
            .enqueue(object : Callback<StudentResponse> {

                override fun onResponse(
                    call: Call<StudentResponse>,
                    response: Response<StudentResponse>
                ) {
                    setLoading(false)

                    if (response.isSuccessful) {
                        // Başarı mesajını da txtError'da göstermek istersen aynı mekanizma ile yapılır.
                        finish()
                    } else {
                        val raw = try {
                            response.errorBody()?.string()
                        } catch (e: Exception) {
                            null
                        }

                        val errors = parseBackendErrors(raw)

                        if (errors.isNotEmpty()) {
                            showErrorLines(errors)
                        } else {
                            showErrorLines(listOf("Kayıt başarısız. (HTTP ${response.code()})"))
                        }
                    }
                }

                override fun onFailure(call: Call<StudentResponse>, t: Throwable) {
                    setLoading(false)
                    showErrorLines(listOf(MSG_CONN_FAIL_PREFIX + (t.message ?: "Bilinmeyen hata")))
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }

    // ✅ TextView hata yönetimi
    private fun hideError() {
        binding.txtError.visibility = View.GONE
        binding.txtError.text = ""
    }

    private fun showErrorLines(lines: List<String>) {
        val text = lines
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(separator = "\n") { "• $it" }

        binding.txtError.text = text
        binding.txtError.visibility = View.VISIBLE

        binding.txtError.post {
            binding.txtError.requestFocus()
        }
    }


    // ✅ Backend'den gelen hata body'sini parse eder:
    // 1) ["msg1","msg2"]
    // 2) {"errors":[...]} veya {"message":"..."} veya {"title":"..."}
    // 3) {"errors":{ "Email":["..."], "Password":["..."] }} (ASP.NET/FluentValidation tarzı)
    // 4) JSON değilse düz metin
    private fun parseBackendErrors(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()

        val s = raw.trim()

        // 1) JSON Array: ["msg1","msg2"]
        try {
            val arr = org.json.JSONArray(s)
            return List(arr.length()) { i -> arr.optString(i).trim() }
                .filter { it.isNotBlank() }
        } catch (_: Exception) { /* devam */ }

        // 2) JSON Object: {"errors":[...]} veya {"message":"..."} vb.
        try {
            val obj = org.json.JSONObject(s)

            // {"errors":[...]}
            if (obj.has("errors")) {
                val e = obj.get("errors")
                if (e is org.json.JSONArray) {
                    return List(e.length()) { i -> e.optString(i).trim() }
                        .filter { it.isNotBlank() }
                }
            }

            // {"message":"..."}
            if (obj.has("message")) {
                val msg = obj.optString("message").trim()
                if (msg.isNotBlank()) return listOf(msg)
            }

            // {"title":"..."} ASP.NET problem details'te bazen var
            if (obj.has("title")) {
                val title = obj.optString("title").trim()
                if (title.isNotBlank()) return listOf(title)
            }

            // {"errors":{ "Email":["..."], "Password":["..."] }} gibi olursa:
            if (obj.has("errors") && obj.get("errors") is org.json.JSONObject) {
                val errsObj = obj.getJSONObject("errors")
                val list = mutableListOf<String>()
                val keys = errsObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val valAny = errsObj.get(key)
                    if (valAny is org.json.JSONArray) {
                        for (i in 0 until valAny.length()) {
                            val msg = valAny.optString(i).trim()
                            if (msg.isNotBlank()) list.add(msg)
                        }
                    }
                }
                if (list.isNotEmpty()) return list.distinct()
            }

        } catch (_: Exception) { /* devam */ }

        // 3) Düz metin: tırnakları temizle
        return listOf(s.trim('"'))
    }
}
