package com.example.ktphanemobil.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.ActivityLoginBinding
import com.example.ktphanemobil.models.LoginRequest
import com.example.ktphanemobil.models.StudentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        private const val PREFS_NAME = "UserPrefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        RetrofitClient.instance.login(loginRequest)
            .enqueue(object : Callback<StudentResponse> {

                override fun onResponse(
                    call: Call<StudentResponse>,
                    response: Response<StudentResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { student ->
                            saveUserToPreferences(
                                student.id,
                                student.fullName,
                                student.email
                            )

                            Toast.makeText(
                                this@LoginActivity,
                                "Hoşgeldiniz!",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "E-posta veya şifre hatalı",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudentResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Bağlantı Hatası: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun saveUserToPreferences(id: Int, name: String, email: String) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("user_id", id)
            putString("user_name", name)
            putString("user_email", email)
            apply()
        }
    }
}
