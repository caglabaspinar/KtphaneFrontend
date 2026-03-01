package com.example.ktphanemobil.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ktphanemobil.R
import com.example.ktphanemobil.databinding.ActivityMainBinding
import com.example.ktphanemobil.ui.fragments.GeneralBookListFragment
import com.example.ktphanemobil.ui.fragments.LibraryFragment
import com.example.ktphanemobil.ui.fragments.MyBooksFragment
import com.example.ktphanemobil.ui.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar = binding.toolbar
        val initialTop = toolbar.paddingTop
        val initialLeft = toolbar.paddingLeft
        val initialRight = toolbar.paddingRight
        val initialBottom = toolbar.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                initialLeft,
                initialTop + systemBars.top,
                initialRight,
                initialBottom
            )
            insets
        }
        ViewCompat.requestApplyInsets(toolbar)

        setSupportActionBar(toolbar)


        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            updateBackButton()
        }
        updateBackButton()

        if (savedInstanceState == null) {
            replaceRootFragment(LibraryFragment())
            binding.bottomNavigation.selectedItemId = R.id.nav_libraries
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_libraries -> LibraryFragment()
                R.id.nav_books -> GeneralBookListFragment()
                R.id.nav_my_books -> MyBooksFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> LibraryFragment()
            }

            supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )

            replaceRootFragment(selectedFragment)
            true
        }
    }

    private fun updateBackButton() {
        val canGoBack = supportFragmentManager.backStackEntryCount > 0
        supportActionBar?.setDisplayHomeAsUpEnabled(canGoBack)
        supportActionBar?.setDisplayShowHomeEnabled(canGoBack)
    }

    private fun replaceRootFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
// Uygulamanın ana ekranını yöneten Activity’dir; BottomNavigation ile fragment geçişlerini kontrol eder,
// toolbar ve geri tuşu davranışını back stack’e göre yönetir ve seçilen fragment’i ekrana yerleştirir.