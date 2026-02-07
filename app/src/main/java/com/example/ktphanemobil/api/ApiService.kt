package com.example.ktphanemobil.api

import com.example.ktphanemobil.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // ---------- Libraries ----------
    @GET("api/Library")
    fun getLibraries(): Call<List<Library>>

    @GET("api/Reports/library/{libraryId}")
    fun getLibraryBooks(@Path("libraryId") libraryId: Int): Call<List<Book>>

    // ---------- Books ----------
    @GET("api/Books")
    fun getBooks(): Call<List<Book>>

    // Admin
    @POST("api/Books")
    fun addBook(@Body request: AddBookRequest): Call<Book>


    @DELETE("api/Books/{id}")
    fun deleteBook(@Path("id") id: Int): Call<Void>

    // ---------- Auth / Students ----------
    @POST("api/Students/login")
    fun login(@Body request: LoginRequest): Call<StudentResponse>

    @POST("api/Students")
    fun register(@Body request: RegisterRequest): Call<StudentResponse>

    // ---------- StudentBooks (JWT ile studentId token’dan geliyor) ----------
    @POST("api/StudentBooks/borrow")
    fun borrowBook(@Body request: BorrowRequest): Call<BorrowResponse>

    @POST("api/StudentBooks/return")
    fun returnBook(@Body request: BorrowRequest): Call<BorrowResponse>

    @GET("api/StudentBooks/my")
    fun getMyActiveBorrows(): Call<List<BorrowedBookResponse>>

    @GET("api/StudentBooks/my/history")
    fun getMyBorrowHistory(): Call<List<BorrowedBookResponse>>

    @POST("api/Library")
    fun addLibrary(@Body request: AddLibraryRequest): Call<Library>

}
