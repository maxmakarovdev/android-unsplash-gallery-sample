package com.maxmakarov.gallery

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.maxmakarov.core.ui.BaseNavActivity
import com.maxmakarov.gallery.databinding.NavActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavActivity : BaseNavActivity() {

    private lateinit var binding: NavActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NavActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.navView.setupWithNavController(navHostFragment.navController)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideStatusBar()
        }
    }

    private fun hideStatusBar() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun setFavouritesBadge(isVisible: Boolean) {
        binding.navView.getOrCreateBadge(R.id.favorites_nav_graph).also {
            it.isVisible = isVisible
        }
    }

    override fun getBottomNavView() = binding.navView
}