package com.example.ktphanemobil.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        private const val MSG_FAIL_PREFIX = "Kayıt başarısız: "
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
        val fullName = binding.edtFullName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, MSG_FILL_ALL, Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(
                            this@RegisterActivity,
                            MSG_SUCCESS,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            MSG_FAIL_PREFIX + response.code(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudentResponse>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(
                        this@RegisterActivity,
                        MSG_CONN_FAIL_PREFIX + (t.message ?: "Bilinmeyen hata"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }
}
