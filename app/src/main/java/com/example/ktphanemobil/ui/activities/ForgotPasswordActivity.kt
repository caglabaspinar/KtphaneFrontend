package com.example.ktphanemobil.ui.activities

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ktphanemobil.api.RetrofitClient
import com.example.ktphanemobil.databinding.ActivityForgotPasswordBinding
import com.example.ktphanemobil.models.ForgotPasswordRequest
import com.example.ktphanemobil.models.GenericMessageResponse
import com.example.ktphanemobil.models.ResetPasswordRequest
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private var codeSentOnce: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(this)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideAllMessages()
        updateSendButtonText()

        binding.txtBackToLogin.setOnClickListener { finish() }

        binding.btnSendCode.setOnClickListener {
            hideAllMessages()

            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                showBottomMessage("Lütfen e-posta adresinizi girin.", isError = true)
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showBottomMessage("Lütfen geçerli bir e-posta adresi girin.", isError = true)
                return@setOnClickListener
            }

            setLoading(true)

            RetrofitClient.instance
                .forgotPassword(ForgotPasswordRequest(email))
                .enqueue(object : Callback<GenericMessageResponse> {
                    override fun onResponse(
                        call: Call<GenericMessageResponse>,
                        response: Response<GenericMessageResponse>
                    ) {
                        setLoading(false)

                        if (response.isSuccessful) {
                            val msg = if (codeSentOnce) {
                                "Kod tekrar gönderildi. Mail adresinizi kontrol edin."
                            } else {
                                "Kod gönderildi. Mail adresinizi kontrol edin."
                            }

                            codeSentOnce = true
                            updateSendButtonText()
                            binding.resetSection.visibility = View.VISIBLE
                            showTopInfo(msg)
                        } else {

                            showBottomMessage(extractErrorMessage(response) ?: "Kod gönderilemedi. Tekrar deneyin.", true)
                        }
                    }

                    override fun onFailure(call: Call<GenericMessageResponse>, t: Throwable) {
                        setLoading(false)
                        showBottomMessage("Bağlantı hatası: ${t.message ?: "Bilinmeyen hata"}", isError = true)
                    }
                })
        }

        binding.btnResetPassword.setOnClickListener {
            hideAllMessages()

            val email = binding.etEmail.text.toString().trim()
            val code = binding.etCode.text.toString().trim()
            val p1 = binding.etNewPassword.text.toString()
            val p2 = binding.etNewPassword2.text.toString()

            if (email.isEmpty() || code.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                showBottomMessage("Lütfen tüm alanları doldurun.", isError = true)
                return@setOnClickListener
            }

            if (p1 != p2) {
                showBottomMessage("Şifreler uyuşmuyor.", isError = true)
                return@setOnClickListener
            }


            val issues = validatePassword(p1)
            if (issues.isNotEmpty()) {
                val msg = issues.joinToString("\n") { "• $it" }
                showBottomMessage(msg, isError = true)
                return@setOnClickListener
            }

            setLoading(true)

            RetrofitClient.instance
                .resetPassword(ResetPasswordRequest(email, code, p1))
                .enqueue(object : Callback<GenericMessageResponse> {
                    override fun onResponse(
                        call: Call<GenericMessageResponse>,
                        response: Response<GenericMessageResponse>
                    ) {
                        setLoading(false)

                        if (response.isSuccessful) {

                            showBottomMessage(
                                response.body()?.message ?: "Şifre başarıyla güncellendi.",
                                isError = false
                            )
                        } else {

                            val backendMsg = extractErrorMessage(response)
                            showBottomMessage(
                                backendMsg ?: "Şifre güncellenemedi. Kod yanlış veya süresi dolmuş olabilir.",
                                isError = true
                            )
                        }
                    }

                    override fun onFailure(call: Call<GenericMessageResponse>, t: Throwable) {
                        setLoading(false)
                        showBottomMessage("Bağlantı hatası: ${t.message ?: "Bilinmeyen hata"}", isError = true)
                    }
                })
        }
    }

    private fun updateSendButtonText() {
        binding.btnSendCode.text = if (codeSentOnce) "Kodu Tekrar Gönder" else "Kodu Gönder"
    }

    private fun validatePassword(pw: String): List<String> {
        val issues = mutableListOf<String>()
        if (pw.length < 8) issues.add("En az 8 karakter olmalı.")
        if (!pw.any { it.isUpperCase() }) issues.add("En az 1 büyük harf içermeli.")
        if (!pw.any { it.isLowerCase() }) issues.add("En az 1 küçük harf içermeli.")
        if (!pw.any { it.isDigit() }) issues.add("En az 1 sayı içermeli.")
        return issues
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnSendCode.isEnabled = !isLoading
        binding.btnResetPassword.isEnabled = !isLoading
    }



    private fun hideAllMessages() {
        binding.txtInfo.visibility = View.GONE
        binding.txtInfo.text = ""

        binding.txtError.visibility = View.GONE
        binding.txtError.text = ""
    }


    private fun showTopInfo(msg: String) {
        binding.txtInfo.text = msg
        binding.txtInfo.visibility = View.VISIBLE


        binding.txtError.visibility = View.GONE
        binding.txtError.text = ""
    }


    private fun showBottomMessage(msg: String, isError: Boolean) {
        binding.txtError.text = msg
        binding.txtError.visibility = View.VISIBLE

        if (isError) {
            binding.txtError.setTextColor(getColor(android.R.color.holo_red_dark))
            binding.txtError.setBackgroundColor(0x10FF0000.toInt())
        } else {
            binding.txtError.setTextColor(getColor(android.R.color.holo_green_dark))
            binding.txtError.setBackgroundColor(0x102E7D32.toInt())
        }


        binding.txtInfo.visibility = View.GONE
        binding.txtInfo.text = ""
    }


    private fun extractErrorMessage(response: Response<*>): String? {
        return try {
            val raw = response.errorBody()?.string() ?: return null
            val json = JSONObject(raw)
            when {
                json.has("message") -> json.getString("message")
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}
// Şifremi unuttum akışını yöneten ekrandır; kullanıcıdan e-posta alıp backend’e kod gönderme isteği atar,
// ardından gelen kod ve yeni şifreyle şifre sıfırlama isteği yapar ve doğrulama/hata mesajlarını UI’da gösterir.