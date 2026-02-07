package com.example.ktphanemobil.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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


        setSupportActionBar(binding.toolbar)


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
