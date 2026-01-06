package com.example.ktphanemobil.api

import com.example.ktphanemobil.models.Book
import com.example.ktphanemobil.models.BorrowRequest
import com.example.ktphanemobil.models.BorrowResponse
import com.example.ktphanemobil.models.Library
import com.example.ktphanemobil.models.LoginRequest
import com.example.ktphanemobil.models.RegisterRequest
import com.example.ktphanemobil.models.StudentResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("api/Library")
    fun getLibraries(): Call<List<Library>>

    @GET("api/Reports/library/{libraryId}")
    fun getLibraryBooks(
        @Path("libraryId") libraryId: Int
    ): Call<List<Book>>

    @GET("api/Books")
    fun getBooks(): Call<List<Book>>

    @GET("api/Reports/student/{studentId}")
    fun getStudentBooks(
        @Path("studentId") studentId: Int
    ): Call<List<Book>>

    @POST("api/Students/login")
    fun login(
        @Body request: LoginRequest
    ): Call<StudentResponse>

    @POST("api/Students")
    fun register(
        @Body request: RegisterRequest
    ): Call<StudentResponse>

    @POST("api/StudentBooks")
    fun borrowBook(
        @Body request: BorrowRequest
    ): Call<BorrowResponse>
}
