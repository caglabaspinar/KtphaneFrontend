package com.example.ktphanemobil.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.ActivityLoginBinding
import com.example.ktphanemobil.models.LoginRequest
import com.example.ktphanemobil.models.StudentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        private const val PREFS_NAME = "UserPrefs"
        private const val MSG_FILL_ALL = "Lütfen tüm alanları doldurun."
        private const val MSG_INVALID = "E-posta veya şifre hatalı."
        private const val MSG_TIMEOUT = "Sunucuya ulaşılamadı (timeout). Backend açık mı?"
        private const val MSG_CONN_FAIL_PREFIX = "Bağlantı hatası: "
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(this)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideError()

        binding.txtGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        hideError()

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showErrorLines(listOf(MSG_FILL_ALL))
            return
        }

        setLoading(true)
        loginUser(email, password)
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        RetrofitClient.instance.login(loginRequest)
            .enqueue(object : Callback<StudentResponse> {

                override fun onResponse(
                    call: Call<StudentResponse>,
                    response: Response<StudentResponse>
                ) {
                    setLoading(false)

                    if (response.isSuccessful) {
                        response.body()?.let { student ->
                            saveUserToPreferences(
                                student.id,
                                student.fullName,
                                student.email,
                                student.token,
                                student.role
                            )

                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                            finish()
                        } ?: run {
                            showErrorLines(listOf("Beklenmeyen hata: boş response."))
                        }
                    } else {

                        showErrorLines(listOf(MSG_INVALID))
                    }
                }

                override fun onFailure(call: Call<StudentResponse>, t: Throwable) {
                    setLoading(false)

                    val msg = when (t) {
                        is SocketTimeoutException -> MSG_TIMEOUT
                        else -> MSG_CONN_FAIL_PREFIX + (t.message ?: "Bilinmeyen hata")
                    }

                    showErrorLines(listOf(msg))
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {

        binding.btnLogin.isEnabled = !isLoading
    }

    private fun saveUserToPreferences(id: Int, name: String, email: String, token: String, role: String) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("user_id", id)
            putString("user_name", name)
            putString("user_email", email)

            // ✅ auth
            putString("auth_token", token)
            putString("role", role)

            apply()
        }
    }



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
}
